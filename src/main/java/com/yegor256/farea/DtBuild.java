/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.nio.file.Path;

/**
 * Build inside POM.
 *
 * @since 0.0.1
 */
final class DtBuild implements Build {

    /**
     * Home of the project.
     */
    private final Path home;

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * Ctor.
     * @param dir The location of the project directory
     * @param file The POM
     */
    DtBuild(final Path dir, final Pom file) {
        this.home = dir;
        this.pom = file;
    }

    @Override
    public Plugins plugins() {
        return new DtPlugins(this.home, this.pom);
    }

    @Override
    public Properties properties() {
        return new DtProperties(this.pom, "build");
    }
}
