/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;

/**
 * Configuration.
 *
 * @since 0.1.0
 */
public interface Configuration {
    /**
     * Get config.
     * @param key The key
     * @param value The value
     * @return Config
     * @throws IOException If fails
     */
    Configuration set(String key, Object value) throws IOException;
}
