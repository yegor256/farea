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

import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.xembly.Directives;

/**
 * Test case for {@link Pom}.
 *
 * @since 0.1.0
 */
final class PomTest {

    @Test
    void printsCorrectly(@TempDir final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        new Pom(xml).init();
        MatcherAssert.assertThat(
            "Prints correctly",
            new XMLDocument(xml).toString(),
            Matchers.containsString("<modelVersion>")
        );
    }

    @Test
    void rendersWithoutExtraSpaces(@TempDir final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        new Pom(xml).init().modify(new Directives().xpath("/project").add("properties"));
        MatcherAssert.assertThat(
            "Prints without spaces",
            new XMLDocument(xml).toString(),
            Matchers.containsString("<name>test</name>\n   <properties>\n      <maven")
        );
    }

    @Test
    void simplyModifies(@TempDir final Path dir) throws IOException {
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

}
