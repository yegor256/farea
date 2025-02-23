/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import java.util.Map;
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

    @SuppressWarnings("unchecked")
    @Override
    public Configuration set(final String key, final Object value) throws IOException {
        final Directives dirs = new Directives()
            .xpath(this.xpath)
            .strict(1)
            .addIf("configuration")
            .addIf(key);
        if (value instanceof Iterable) {
            for (final Object item : Iterable.class.cast(value)) {
                dirs.add("item").set(Xembler.escape(item.toString())).up();
            }
        } else if (value instanceof Map) {
            for (final Object entry : Map.class.cast(value).entrySet()) {
                final Map.Entry<Object, Object> ent = Map.Entry.class.cast(entry);
                dirs.add(ent.getKey()).set(Xembler.escape(ent.getValue().toString())).up();
            }
        } else if (value instanceof Object[]) {
            for (final Object item : Object[].class.cast(value)) {
                dirs.add("item").set(Xembler.escape(item.toString())).up();
            }
        } else {
            dirs.set(Xembler.escape(value.toString()));
        }
        this.pom.modify(dirs);
        return this;
    }

}
