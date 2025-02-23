/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Local maven repository, usually in "~/.m2/repository".
 *
 * @since 0.0.1
 */
final class Local {

    /**
     * User home.
     */
    private final Path user;

    /**
     * Ctor.
     */
    Local() {
        this.user = Paths.get(System.getProperty("user.home"));
    }

    /**
     * Get its path.
     *
     * @return The absolute path of it
     */
    public Path path() {
        final Path home = this.user.resolve(".m2");
        if (!home.toFile().exists()) {
            throw new IllegalStateException(
                String.format(
                    "Maven home is not found at this location: %s",
                    home.toAbsolutePath()
                )
            );
        }
        final Path local = home.resolve("repository");
        if (!local.toFile().exists()) {
            throw new IllegalStateException(
                String.format(
                    "Maven home repository is not found at this location: %s",
                    local.toAbsolutePath()
                )
            );
        }
        return local;
    }

}
