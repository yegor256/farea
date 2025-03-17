/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.jcabi.matchers.XhtmlMatchers;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link DtConfiguration}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class DtConfigurationTest {

    @Test
    void setsUtfStringAsParam(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-fake.xml");
        final Pom pom = new Pom(xml);
        pom.init();
        new DtConfiguration(pom, "/project")
            .set("something", "привет! <test");
        MatcherAssert.assertThat(
            "Sets unicode as value",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPath("/project/configuration/something[.='привет! <test']")
        );
    }

    @Test
    void setsListAsParam(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-1.xml");
        final Pom pom = new Pom(xml);
        new DtPlugins(dir, pom).append("a", "0.0.0")
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
    void setsArrayAsParam(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-2.xml");
        final Pom pom = new Pom(xml);
        new DtPlugins(dir, pom).append("xyz", "1.1.1")
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
    void setsMapAsParam(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-3.xml");
        final Pom pom = new Pom(xml);
        final Map<String, Integer> map = new HashMap<>(0);
        map.put("test", 42);
        new DtPlugins(dir, pom).append("bbb", "1.2.3")
            .execution("foo-0")
            .phase("boom")
            .configuration()
            .set("hello", map);
        MatcherAssert.assertThat(
            "Sets map as param",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPath("//configuration/hello/test[.='42']")
        );
    }

    @Test
    void setsCollection(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-8.xml");
        final Pom pom = new Pom(xml).init();
        new DtConfiguration(pom, "/project")
            .set("noise", "boom")
            .set("xxx", Arrays.asList("foo1", "bar1"));
        MatcherAssert.assertThat(
            "Collection is not set",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "/project/configuration/xxx[count(item) = 2]",
                "/project/configuration/xxx[item='foo1']",
                "/project/configuration/xxx[item='bar1']"
            )
        );
    }

    @Test
    void setsMap(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-8.xml");
        final Pom pom = new Pom(xml).init();
        final Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        new DtConfiguration(pom, "/project")
            .set("xxx", map)
            .set("another", "привет");
        MatcherAssert.assertThat(
            "Map is not set",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "/project/configuration/xxx[count(*) = 2]",
                "/project/configuration/xxx[one='1']",
                "/project/configuration/xxx[two='2']"
            )
        );
    }

    @Test
    void setsMapOfMaps(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-9.xml");
        final Pom pom = new Pom(xml).init();
        final Map<String, Object> lower = new HashMap<>();
        lower.put("alpha", 1);
        lower.put("beta", 2);
        final Map<String, Object> upper = new HashMap<>();
        upper.put("blue", 88);
        upper.put("orange", lower);
        new DtConfiguration(pom, "/project")
            .set("ttt", upper)
            .set("something", "foo-bar");
        MatcherAssert.assertThat(
            "Map of maps is not set",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "/project/configuration/ttt[blue='88']",
                "/project/configuration/ttt/orange[count(*) = 2]",
                "/project/configuration/ttt/orange[alpha='1']",
                "/project/configuration/ttt/orange[beta='2']"
            )
        );
    }
}
