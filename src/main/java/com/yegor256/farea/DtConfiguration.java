/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import java.util.Map;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Configuration of an Execution.
 *
 * @since 0.0.1
 */
final class DtConfiguration implements Configuration {

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * XPath for the "configuration" element.
     */
    private final String xpath;

    /**
     * Ctor.
     * @param file The POM
     * @param xpth The location of the parent element
     */
    DtConfiguration(final Pom file, final String xpth) {
        this.pom = file;
        this.xpath = xpth;
    }

    @Override
    public Configuration set(final String key, final Object value) throws IOException {
        this.pom.modify(
            new Directives()
                .xpath(this.xpath)
                .strict(1)
                .addIf("configuration")
                .addIf(key)
                .append(DtConfiguration.dirs(value))
        );
        return this;
    }

    private static Iterable<Directive> dirs(final Object value) {
        final Directives dirs = new Directives();
        if (value instanceof Iterable) {
            for (final Object item : (Iterable<?>) value) {
                dirs.add("item").append(DtConfiguration.dirs(item)).up();
            }
        } else if (value instanceof String[]) {
            for (final String item : (String[]) value) {
                dirs.add("item").append(DtConfiguration.dirs(item)).up();
            }
        } else if (value instanceof Map) {
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                dirs
                    .add(entry.getKey().toString())
                    .append(DtConfiguration.dirs(entry.getValue()))
                    .up();
            }
        } else {
            dirs.set(Xembler.escape(value.toString()));
        }
        return dirs;
    }

}
