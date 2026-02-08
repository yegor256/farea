/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.jcabi.matchers.XhtmlMatchers;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link Itself}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class ItselfTest {

    @Test
    void deploysJar(@Mktmp final Path dir, @Mktmp final Path repos) throws IOException {
        MatcherAssert.assertThat(
            "Resolves",
            repos.resolve(
                String.format(
                    "g1/g2/a/%s/a-%1$s.jar",
                    new Itself(
                        dir,
                        new Base(Paths.get("src/test/resources/fake-pom.xml")),
                        false
                    ).deploy(repos)
                )
            ).toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void deploysJarWithNoPluginXml(@Mktmp final Path dir,
        @Mktmp final Path repos) throws IOException {
        dir.resolve("target").toFile().mkdirs();
        Files.write(
            dir.resolve("target/plugin.xml"),
            "<plugin><version>0</version></plugin>".getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "Resolves",
            repos.resolve(
                String.format(
                    "g1/g2/a/%s/a-%1$s.jar",
                    new Itself(
                        dir,
                        new Base(Paths.get("src/test/resources/fake-pom.xml")),
                        false,
                        ""
                    ).deploy(repos)
                )
            ).toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void deploysJarAndPom(@Mktmp final Path dir, @Mktmp final Path repos) throws IOException {
        MatcherAssert.assertThat(
            "Deploys JAR and POM",
            XhtmlMatchers.xhtml(
                new String(
                    Files.readAllBytes(
                        repos.resolve(
                            String.format(
                                "g1/g2/a/%s/a-%1$s.pom",
                                new Itself(
                                    dir,
                                    new Base(Paths.get("src/test/resources/fake-pom.xml")),
                                    false
                                ).deploy(repos)
                            )
                        )
                    ),
                    StandardCharsets.UTF_8
                )
            ),
            XhtmlMatchers.hasXPath(
                "/ns1:project/ns1:groupId[.='g1.g2']",
                "http://maven.apache.org/POM/4.0.0"
            )
        );
    }

    @Test
    void deploysInManyThreads(@Mktmp final Path dir, @Mktmp final Path repos) {
        final Itself itself = new Itself(
            dir,
            new Base(Paths.get("src/test/resources/fake-pom.xml")),
            false
        );
        MatcherAssert.assertThat(
            "the JAR deploys in many threads",
            new Jointly<>(
                thread -> {
                    itself.deploy(repos);
                    return itself;
                }
            ).made(),
            Matchers.notNullValue()
        );
    }
}
