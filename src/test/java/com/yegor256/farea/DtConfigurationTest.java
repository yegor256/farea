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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link DtConfiguration}.
 *
 * @since 0.1.0
 */
final class DtConfigurationTest {

    @Test
    void setsUtfStringAsParam(@TempDir final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-fake.xml");
        final Pom pom = new Pom(xml);
        pom.init();
        new DtConfiguration(pom, "/project")
            .set("something", "привет!");
        MatcherAssert.assertThat(
            "Sets unicode as value",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPath("/project/configuration/something[.='привет!']")
        );
    }

    @Test
    void setsListAsParam(@TempDir final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-1.xml");
        final Pom pom = new Pom(xml);
        new DtPlugins(pom).append("a", "0.0.0")
            .execution("foo")
            .phase("boom")
            .configuration()
            .set("foo", Arrays.asList("<one>", "two"));
        MatcherAssert.assertThat(
            "Sets list as param",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPath("//configuration/foo/item[.='<one>']")
        );
    }

    @Test
    void setsArrayAsParam(@TempDir final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-2.xml");
        final Pom pom = new Pom(xml);
        new DtPlugins(pom).append("xyz", "1.1.1")
            .execution("foo")
            .phase("boom")
            .configuration()
            .set("bar", new String[] {"\u0000", "beta"});
        MatcherAssert.assertThat(
            "Sets array as param",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPath("//configuration/bar/item[.='\\u0000']")
        );
    }

    @Test
    void setsMapAsParam(@TempDir final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-3.xml");
        final Pom pom = new Pom(xml);
        final Map<String, Integer> map = new HashMap<>(0);
        map.put("test", 42);
        new DtPlugins(pom).append("bbb", "1.2.3")
            .execution("foo")
            .phase("boom")
            .configuration()
            .set("hello", map);
        MatcherAssert.assertThat(
            "Sets map as param",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPath("//configuration/hello/test[.='42']")
        );
    }

}
