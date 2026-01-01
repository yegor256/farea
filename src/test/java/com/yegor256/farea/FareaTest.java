/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.jcabi.matchers.XhtmlMatchers;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.WeAreOnline;
import java.io.IOException;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link Farea}.
 *
 * @since 0.1.0
 */
@ExtendWith(WeAreOnline.class)
@ExtendWith(MktmpResolver.class)
final class FareaTest {

    @Test
    void compilesSimpleProject(@Mktmp final Path dir) throws IOException {
        new Farea(dir).together(
            f -> {
                f.files()
                    .file("src/main/java/foo/Hello.java")
                    .write("package foo; import org.cactoos.Input; class Hello {}".getBytes());
                f.dependencies()
                    .append("org.cactoos", "cactoos", "0.55.0");
                MatcherAssert.assertThat(
                    "exit code is zero",
                    f.execQuiet("compile"),
                    Matchers.equalTo(0)
                );
            }
        );
        MatcherAssert.assertThat(
            "Compiles simple project",
            dir.resolve("target/classes/foo/Hello.class").toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void failsGracefully(@Mktmp final Path dir) throws IOException {
        new Farea(dir).together(
            f -> MatcherAssert.assertThat(
                "exit code is NON zero",
                f.execQuiet("wrong-goal"),
                Matchers.not(Matchers.equalTo(0))
            )
        );
    }

    @Test
    void callsSimplePlugin(@Mktmp final Path dir) throws IOException {
        new Farea(dir).together(
            f -> {
                f.build()
                    .plugins()
                    .append("maven-compiler-plugin", "3.11.0")
                    .configuration()
                    .set("skip", "true");
                f.withOpt("--debug");
                f.exec("compile");
                MatcherAssert.assertThat(
                    "Calls simple plugin",
                    f.files().log(),
                    RequisiteMatcher.SUCCESS
                );
            }
        );
    }

    @Test
    void cleans(@Mktmp final Path dir) throws IOException {
        new Farea(dir).together(
            f -> {
                f.dependencies().append("foo", "foo", "0.0.0");
                f.clean();
                f.dependencies().append("foo", "foo", "0.0.0");
                f.exec("initialize");
                MatcherAssert.assertThat(
                    "Builds clean pom.xml",
                    f.files().log(),
                    new RequisiteMatcher().without("WARNING")
                );
            }
        );
    }

    @Test
    void cleansNonExistingDir(@Mktmp final Path dir) throws IOException {
        new Farea(dir.resolve("non-existing")).together(
            f -> {
                f.clean();
                f.properties().set("something-else", "42 42");
                MatcherAssert.assertThat(
                    "Directory exists after cleaning",
                    f.files().file("pom.xml").exists(),
                    Matchers.is(true)
                );
            }
        );
    }

    @Test
    void buildsInManyThreads(@Mktmp final Path dir) throws IOException {
        MatcherAssert.assertThat(
            "the build works in many processes",
            new Jointly<>(
                thread -> {
                    new Farea(dir.resolve(Integer.toString(thread))).together(
                        f -> {
                            f.properties().set(
                                String.format("foo-%d", thread),
                                "foo-bar"
                            );
                            f.files().file("src/main/java/Foo.java")
                                .write("class Foo {}".getBytes());
                            f.dependencies().appendItself();
                            f.exec("compile");
                        }
                    );
                    return 0;
                }
            ).made(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "the pom.xml is correctly created",
            new Farea(dir).files().file("0/pom.xml").content(),
            XhtmlMatchers.hasXPaths("/project/properties[count(*[starts-with(name(), 'foo-')])=1]")
        );
    }
}
