/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Local}.
 *
 * @since 0.1.0
 */
final class LocalTest {

    @Test
    void returnsRepositoryPathIfExists() {
        final Local local = new Local();
        final Path repo = local.path();
        MatcherAssert.assertThat(
            "Repository path should end with .m2/repository",
            repo.toString().replace("\\", "/"),
            Matchers.endsWith("/.m2/repository")
        );
    }

}
