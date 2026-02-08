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
 * Test case for {@link DtBuild}.
 *
 * @since 0.15.0
 */
@ExtendWith(MktmpResolver.class)
final class DtBuildTest {

    @Test
    void appendsBuildProperties(@Mktmp final Path dir) throws IOException {
        final Pom pom = new Pom(dir.resolve("pom.xml")).init();
        new DtBuild(dir, pom).properties().set("foo", "bar");
        MatcherAssert.assertThat(
            "Sets build propertiees",
            pom.xpath("/project/build/foo/text()").get(0),
            Matchers.equalTo("bar")
        );
    }
}
