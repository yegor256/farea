/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;

/**
 * Configuration provides access to Maven plugin configuration elements.
 * 
 * <p>This interface allows you to set configuration parameters for Maven plugins
 * in the POM file. You can configure any plugin-specific parameters needed for 
 * the plugin to function correctly during testing.</p>
 * 
 * <p>The configuration is immediately persisted to the underlying POM file when
 * modifications are made.</p>
 * 
 * <p>The following types of values are supported:</p>
 * <ul>
 *   <li>Simple values (String, Number, Boolean) - stored as XML text content</li>
 *   <li>{@link Iterable} - stored as a sequence of XML elements</li>
 *   <li>{@link java.util.Map} - stored as a nested XML structure</li>
 *   <li>Arrays - stored as a sequence of XML elements</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 * // Configure a plugin
 * farea.build()
 *     .plugins()
 *     .append("org.apache.maven.plugins", "maven-compiler-plugin", "3.8.1")
 *     .configuration()
 *     .set("source", "11")
 *     .set("target", "11");
 *     
 * // Configure with complex values
 * plugin.configuration()
 *     .set("includes", new String[] {"**/*.java"})
 *     .set("excludes", Arrays.asList("**/generated/**"));
 * </pre>
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
