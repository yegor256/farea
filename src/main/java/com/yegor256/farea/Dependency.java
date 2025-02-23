/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;

/**
 * Dependency.
 *
 * @since 0.1.0
 */
public interface Dependency {
    /**
     * Set scope of it.
     * @param scope The scope
     * @return Itself
     * @throws IOException If fails
     */
    Dependency scope(String scope) throws IOException;

    /**
     * Set classifier of it.
     * @param classifier The scope
     * @return Itself
     * @throws IOException If fails
     */
    Dependency classifier(String classifier) throws IOException;
}
