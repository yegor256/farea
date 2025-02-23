/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

/**
 * Plugin inside Plugin.
 *
 * @since 0.0.1
 */
final class DtPlugin implements Plugin {

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * Position in "plugins".
     */
    private final int pos;

    /**
     * Ctor.
     * @param file The POM
     * @param position The position
     */
    DtPlugin(final Pom file, final int position) {
        this.pom = file;
        this.pos = position;
    }

    @Override
    public Execution execution() {
        return this.execution("default");
    }

    @Override
    public Execution execution(final String name) {
        return new DtExecution(this.pom, this.pos, name);
    }

    @Override
    public Configuration configuration() {
        return new DtConfiguration(
            this.pom,
            String.format(
                "/project/build/plugins/plugin[position()=%d]",
                this.pos
            )
        );
    }

}
