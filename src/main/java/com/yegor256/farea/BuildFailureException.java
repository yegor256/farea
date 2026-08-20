/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;

/**
 * When build fails.
 * @since 0.9.0
 */
public final class BuildFailureException extends IOException {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 5188162404688529763L;

    /**
     * The exit code.
     */
    private final int exit;

    /**
     * Ctor.
     * @param code The exit code of Maven build
     */
    public BuildFailureException(final int code) {
        this(String.format("build failed with exit code 0x%04x", code), code);
    }

    /**
     * Ctor.
     * @param message The exception message
     * @param code The exit code of Maven build
     */
    private BuildFailureException(final String message, final int code) {
        super(message);
        this.exit = code;
    }

    /**
     * Get the code.
     * @return The code
     */
    public int getCode() {
        return this.exit;
    }
}
