/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

/**
 * Build.
 *
 * @since 0.1.0
 */
public interface Build {
    /**
     * Get access to build.
     * @return Build
     */
    Plugins plugins();

    /**
     * Get properties of the build.
     * @return Properties
     * @since 0.15.0
     */
    Properties properties();
}
