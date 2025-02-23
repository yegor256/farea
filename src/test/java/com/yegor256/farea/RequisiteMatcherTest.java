/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RequisiteMatcher}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class RequisiteMatcherTest {

    @Test
    void matchesSimpleRequisite(@Mktmp final Path dir) throws IOException {
        new Farea(dir).together(
            f -> {
                f.files()
                    .log()
                    .write("hello world BUILD SUCCESS maybe".getBytes());
                MatcherAssert.assertThat(
                    "matches the log",
                    f.files().log(),
                    RequisiteMatcher.SUCCESS
                );
            }
        );
    }

    @Test
    void matchesNegativeLog(@Mktmp final Path dir) throws IOException {
        new Farea(dir).together(
            f -> {
                f.files()
                    .log()
                    .write("hello world BUILD FAILURE maybe".getBytes());
                MatcherAssert.assertThat(
                    "matches the log",
                    f.files().log(),
                    RequisiteMatcher.FAILURE
                );
            }
        );
    }
}
