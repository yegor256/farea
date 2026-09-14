/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link DtExecution}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class DtExecutionTest {

    @Test
    void setsManyGoals(@Mktmp final Path dir) throws IOException {
        final Pom pom = new Pom(dir.resolve("pom.xml")).init();
        final Plugin plugin = new DtPlugins(dir, pom).append("g", "a", "0.0.1");
        final Execution exec = plugin.execution("foo");
        exec.goals("abc");
        exec.goals("cde");
        exec.goals("cde");
        MatcherAssert.assertThat(
            "Appends two goals",
            pom.xpath(
                "count(/project/build/plugins/plugin/executions/execution/goals/goal)"
            ).get(0),
            Matchers.equalTo("3")
        );
    }
}
