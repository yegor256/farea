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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link DtPlugin}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class DtPluginTest {

    @Test
    void appendsManyExecutions(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        final Pom pom = new Pom(xml).init();
        final Plugin plugin = new DtPlugins(dir, pom).append("g", "a", "0.0.1");
        plugin.execution("foo").phase("first");
        plugin.execution("bar").phase("second");
        MatcherAssert.assertThat(
            "Appends two executions",
            pom.xpath("/project/build/plugins/plugin/executions/execution/id/text()").size(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void appendsGoalsExecutions(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        final Pom pom = new Pom(xml).init();
        final Plugin plugin = new DtPlugins(dir, pom).append("g", "a", "0.0.1");
        plugin.execution().phase("first").goals("x");
        plugin.execution("bar").phase("second").goals("y");
        MatcherAssert.assertThat(
            "Appends only two executions",
            pom.xpath("count(/project/build/plugins/plugin/executions/execution)").get(0),
            Matchers.equalTo("2")
        );
    }

    @Test
    void addsConfiguration(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        final Pom pom = new Pom(xml).init();
        new DtPlugins(dir, pom)
            .append("z", "z", "0.0.2")
            .configuration()
            .set("hey", "you");
        new DtPlugins(dir, pom)
            .append("z", "z", "0.0.2")
            .configuration()
            .set("hey", "me");
        MatcherAssert.assertThat(
            "Sets plugin configuration",
            pom.xpath("/project/build/plugins/plugin/configuration/hey/text()").get(0),
            Matchers.equalTo("me")
        );
    }

    @Test
    void addsGoalsAndConfiguration(@Mktmp final Path dir) throws IOException {
        final Path xml = dir.resolve("pom.xml");
        final Pom pom = new Pom(xml).init();
        new DtPlugins(dir, pom)
            .appendItself()
            .execution("default")
            .phase("process-classes")
            .goals("build", "optimize")
            .goals("test")
            .configuration()
            .set("foo", "bar");
        MatcherAssert.assertThat(
            "Sets plugin configuration and goals",
            XhtmlMatchers.xhtml(pom.xml()),
            XhtmlMatchers.hasXPaths(
                "/project/build/plugins/plugin/executions/execution[count(goals) = 1]",
                "/project/build/plugins/plugin/executions/execution/goals[count(goal) = 3]",
                "/project/build/plugins/plugin/executions/execution/configuration/foo[text() = 'bar']"
            )
        );
    }

}
