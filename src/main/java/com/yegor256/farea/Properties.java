/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;

/**
 * Properties.
 *
 * @since 0.1.0
 */
public interface Properties {
    /**
     * Set one property.
     *
     * <p>It will be saved to the file system immediately.</p>
     *
     * @param name The name
     * @param value The value
     * @return Properties
     * @throws IOException If fails
     */
    Properties set(String name, String value) throws IOException;
}
