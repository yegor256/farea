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
 * Test case for {@link DtProperties}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class DtPropertiesTest {

    @Test
    void setsUniqueProperty(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-1.xml");
        final Pom pom = new Pom(xml);
        new DtProperties(pom)
            .set("foo", "bar1")
            .set("foo", "bar2")
            .set("another.property", "привет");
        MatcherAssert.assertThat(
            "Sets unique property",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "//properties/foo[.='bar2']",
                "//properties[count(foo)=1]",
                "//properties[count(*)=2]",
                "//properties/another.property[.='привет']"
            )
        );
    }

    @Test
    void setsInManyThreads(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-2.xml");
        final Pom pom = new Pom(xml);
        MatcherAssert.assertThat(
            "the pom.xml has all modifications",
            XhtmlMatchers.xhtml(
                new Jointly<>(
                    thread -> {
                        new DtProperties(pom).set(String.format("foo-%d", thread), "bar");
                        return pom;
                    }
                ).made(10).xml()
            ),
            XhtmlMatchers.hasXPaths("/project/properties[count(*[starts-with(name(), 'foo-')])=10]")
        );
    }

    @Test
    void setsCollection(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-8.xml");
        final Pom pom = new Pom(xml);
        new DtProperties(pom)
            .set("noise", "boom")
            .set("xxx", Arrays.asList("foo", "bar"));
        MatcherAssert.assertThat(
            "Collection is not set",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "//properties/xxx[count(item) = 2]",
                "//properties/xxx[item='foo']",
                "//properties/xxx[item='bar']"
            )
        );
    }

    @Test
    void setsMap(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom-8.xml");
        final Pom pom = new Pom(xml);
        final Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        new DtProperties(pom)
            .set("xxx", map)
            .set("another", "foo");
        MatcherAssert.assertThat(
            "Collection is not set",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "//properties/xxx[count(*) = 2]",
                "//properties/xxx[one='1']",
                "//properties/xxx[two='2']"
            )
        );
    }
}
