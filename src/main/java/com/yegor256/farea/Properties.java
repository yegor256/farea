/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
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
     * <p>The value could either be an object with the {@code toString()}
     * method implemented, or a {@link Iterable}, or
     * a {@link java.util.Map}.</p>
     *
     * <p>It will be saved to the file system immediately.</p>
     *
     * @param name The name
     * @param value The value (it could be a collection or a map too)
     * @return Properties
     * @throws IOException If fails
     */
    Properties set(String name, Object value) throws IOException;
}
