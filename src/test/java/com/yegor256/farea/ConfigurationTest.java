/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Yegor Bugayenko
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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Configuration}.
 *
 * @since 0.1.0
 */
final class ConfigurationTest {

    @Test
    void setsListAsParam(final @TempDir Path dir) throws IOException {
        final Path xml = dir.resolve("pom-1.xml");
        final Pom pom = new Pom(xml);
        new Plugins(pom).append("a", "0.0.0");
        new Configuration(pom, 1).set("foo", Arrays.asList("<one>", "two"));
        MatcherAssert.assertThat(
            "Sets list as param",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPath("//configuration/foo/item[.='<one>']")
        );
    }

    @Test
    void setsArrayAsParam(final @TempDir Path dir) throws IOException {
        final Path xml = dir.resolve("pom-2.xml");
        final Pom pom = new Pom(xml);
        new Plugins(pom).append("xyz", "1.1.1");
        new Configuration(pom, 1).set("bar", new String[] {"\u0000", "beta"});
        MatcherAssert.assertThat(
            "Sets array as param",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPath("//configuration/bar/item[.='\\u0000']")
        );
    }

    @Test
    void setsMapAsParam(final @TempDir Path dir) throws IOException {
        final Path xml = dir.resolve("pom-3.xml");
        final Pom pom = new Pom(xml);
        new Plugins(pom).append("bbb", "1.2.3");
        final Map<String, Integer> map = new HashMap<>();
        map.put("test", 42);
        new Configuration(pom, 1).set("hello", map);
        MatcherAssert.assertThat(
            "Sets map as param",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPath("//configuration/hello/test[.='42']")
        );
    }

}
