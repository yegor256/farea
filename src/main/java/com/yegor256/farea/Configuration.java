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
     * Set one config element.
     *
     * <p>The value could either be an object with the {@code toString()}
     * method implemented, or a {@link Iterable}, or
     * a {@link java.util.Map}.</p>
     *
     * <p>It will be saved to the file system immediately.</p>
     *
     * @param key The key
     * @param value The value (could be Iterable or Map)
     * @return Config
     * @throws IOException If fails
     */
    Configuration set(String key, Object value) throws IOException;
}
