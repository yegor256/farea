/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

/**
 * Plugin.
 *
 * @since 0.0.1
 */
public interface Plugin {
    /**
     * Append new execution or get access to existing one (using default name).
     * @return Execution found or added
     */
    Execution execution();

    /**
     * Append new execution or get access to existing one.
     * @param name The "id" of it
     * @return Execution found or added
     */
    Execution execution(String name);

    /**
     * Get config.
     * @return Config
     */
    Configuration configuration();
}
