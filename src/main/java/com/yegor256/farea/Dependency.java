/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;

/**
 * Dependency represents a Maven dependency in the project's POM file.
 * 
 * <p>This interface allows you to configure a Maven dependency, including
 * its scope and classifier. A Dependency object represents a single dependency
 * entry in the POM's dependencies section.</p>
 * 
 * <p>Usage examples:</p>
 * 
 * <p>1. Setting dependency scope:</p>
 * <pre>
 * // Add a test-scoped dependency
 * farea.dependencies()
 *     .append("org.mockito", "mockito-core", "4.0.0")
 *     .scope("test");
 * </pre>
 * 
 * <p>2. Setting dependency classifier:</p>
 * <pre>
 * // Add a dependency with classifier
 * farea.dependencies()
 *     .append("com.example", "example-lib", "1.0.0")
 *     .classifier("sources");
 * </pre>
 * 
 * <p>3. Configuring both scope and classifier:</p>
 * <pre>
 * farea.dependencies()
 *     .append("org.apache.commons", "commons-lang3", "3.12.0")
 *     .classifier("javadoc")
 *     .scope("provided");
 * </pre>
 *
 * @since 0.0.1
 * @see Dependencies
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
     * @param classifier The classifier
     * @return Itself
     * @throws IOException If fails
     */
    Dependency classifier(String classifier) throws IOException;
}
