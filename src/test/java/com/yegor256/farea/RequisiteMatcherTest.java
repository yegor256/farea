/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RequisiteMatcher}.
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class RequisiteMatcherTest {

    @Test
    void matchesSimpleRequisite(@Mktmp final Path dir) throws IOException {
        RequisiteMatcherTest.check(
            dir, "hello world BUILD SUCCESS maybe", RequisiteMatcher.SUCCESS
        );
    }

    @Test
    void matchesNegativeLog(@Mktmp final Path dir) throws IOException {
        RequisiteMatcherTest.check(
            dir, "hello world BUILD FAILURE maybe", RequisiteMatcher.FAILURE
        );
    }

    /**
     * Write this text to the log and match it against this matcher.
     * @param dir The home directory of Farea
     * @param text The text to write to the log
     * @param matcher The matcher the log must satisfy
     * @throws IOException If fails
     */
    private static void check(final Path dir, final String text,
        final Matcher<Requisite> matcher) throws IOException {
        new Farea(dir).together(
            f -> {
                f.files()
                    .log()
                    .write(text.getBytes(StandardCharsets.UTF_8));
                MatcherAssert.assertThat(
                    "matches the log",
                    f.files().log(),
                    matcher
                );
            }
        );
        MatcherAssert.assertThat(
            "farea ran without exception",
            dir.toFile().exists(),
            Matchers.is(true)
        );
    }
}
