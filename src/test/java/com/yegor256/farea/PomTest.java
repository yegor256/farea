/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XMLDocument;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import org.cactoos.Scalar;
import org.cactoos.experimental.Threads;
import org.cactoos.iterable.Repeated;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xembly.Directives;

/**
 * Test case for {@link Pom}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class PomTest {

    @Test
    void printsCorrectly(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        new Pom(xml).init();
        MatcherAssert.assertThat(
            "Prints correctly",
            new XMLDocument(xml).toString(),
            Matchers.containsString("<modelVersion>")
        );
    }

    @Test
    void createsDirectoryIfAbsent(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("a/b/c/pom.xml");
        new Pom(xml).init();
        MatcherAssert.assertThat(
            "Directory created",
            dir.resolve("a/b/c").toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void rendersWithoutExtraSpaces(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        new Pom(xml).init().modify(new Directives().xpath("/project").add("properties"));
        MatcherAssert.assertThat(
            "Prints without spaces",
            new XMLDocument(xml).toString(),
            Matchers.containsString("<name>test</name>\n   <properties>\n      <maven")
        );
    }

    @Test
    void simplyModifies(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-1.xml");
        final Pom pom = new Pom(xml);
        pom.modify(
            new Directives()
                .xpath("/project")
                .add("properties")
                .add("test")
                .set("привет!")
        );
        MatcherAssert.assertThat(
            "Simply modifies",
            pom.xpath("/project/properties/test/text()").get(0),
            Matchers.containsString("привет")
        );
    }

    @Test
    void modifiesInManyThreads(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-1.xml");
        final Pom pom = new Pom(xml);
        final AtomicInteger count = new AtomicInteger();
        final int threads = Runtime.getRuntime().availableProcessors();
        new Threads<>(
            threads,
            new Repeated<Scalar<?>>(
                threads,
                () -> {
                    pom.init();
                    pom.modify(
                        new Directives()
                            .xpath("/project")
                            .addIf("bar")
                            .strict(1)
                            .add(String.format("foo-%d", count.addAndGet(1)))
                            .set("yes!")
                    );
                    return 0;
                }
            )
        ).forEach(x -> Assertions.assertEquals(0, x));
        MatcherAssert.assertThat(
            "all threads counted",
            count.get(),
            Matchers.equalTo(threads)
        );
        MatcherAssert.assertThat(
            "the pom.xml has all modifications",
            XhtmlMatchers.xhtml(new String(Files.readAllBytes(xml), StandardCharsets.UTF_8)),
            XhtmlMatchers.hasXPaths(
                String.format("/project/bar[count(*)=%d]", threads)
            )
        );
    }
}
