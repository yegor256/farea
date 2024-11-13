/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
import org.cactoos.experimental.Threads;
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
            () -> {
                pom.init();
                pom.modify(
                    new Directives()
                        .xpath("/project")
                        .add("properties")
                        .add(String.format("foo-%d", count.addAndGet(1)))
                        .set("yes!")
                );
                return 0;
            }
        ).forEach(x -> Assertions.assertEquals(0, x));
        MatcherAssert.assertThat(
            "the pom.xml has all modifications",
            new String(Files.readAllBytes(xml), StandardCharsets.UTF_8),
            XhtmlMatchers.hasXPaths(
                String.format("/project/properties[count(*)=%d]", count.get())
            )
        );
    }
}
