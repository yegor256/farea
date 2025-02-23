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
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link DtRequisites}.
 *
 * @since 0.1.0
 */
@ExtendWith(MktmpResolver.class)
final class DtRequisitesTest {

    @Test
    void deletesOneFile(@Mktmp final Path dir) throws IOException {
        final String name = "foo.txt";
        Files.write(dir.resolve(name), "".getBytes());
        new DtRequisites(dir).file(name).delete();
        MatcherAssert.assertThat(
            "Deletes one file",
            dir.resolve(name).toFile().exists(),
            Matchers.is(false)
        );
    }

    @Test
    void showsAllAsTree(@Mktmp final Path dir) throws IOException {
        final String name = "foo.txt";
        Files.write(dir.resolve(name), "".getBytes());
        new DtRequisites(dir).show();
        MatcherAssert.assertThat(
            "the file stays",
            dir.resolve(name).toFile().exists(),
            Matchers.is(true)
        );
    }
}
