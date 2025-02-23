/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;

/**
 * Execution.
 *
 * @since 0.1.0
 */
public interface Execution {
    /**
     * Set phase.
     * @param value The Maven execution
     * @return Itself
     * @throws IOException If fails
     */
    Execution phase(String value) throws IOException;

    /**
     * Set goals.
     * @param values The Maven goals (non-empty list)
     * @return Itself
     * @throws IOException If fails
     */
    Execution goals(String... values) throws IOException;

    /**
     * Get config.
     * @return Config
     */
    Configuration configuration();
}
