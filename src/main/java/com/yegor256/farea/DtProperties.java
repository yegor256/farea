/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import java.util.Map;
import org.xembly.Directives;

/**
 * Properties of a project.
 *
 * @since 0.0.1
 */
final class DtProperties implements Properties {

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * Element.
     */
    private final String element;

    /**
     * Ctor.
     * @param file The POM
     */
    DtProperties(final Pom file) {
        this(file, "properties");
    }

    /**
     * Ctor.
     * @param file The POM
     * @param elm The name of the XML element
     */
    DtProperties(final Pom file, final String elm) {
        this.pom = file;
        this.element = elm;
    }

    @Override
    public Properties set(final String name, final Object value) throws IOException {
        final Directives dirs = new Directives()
            .xpath("/project")
            .addIf(this.element)
            .strict(1)
            .xpath(name)
            .remove()
            .xpath("/project")
            .xpath(this.element)
            .strict(1)
            .add(name);
        if (value instanceof Iterable) {
            for (final Object item : (Iterable<?>) value) {
                dirs.add("item").set(item.toString()).up();
            }
        } else if (value instanceof String[]) {
            for (final String item : (String[]) value) {
                dirs.add("item").set(item).up();
            }
        } else if (value instanceof Map) {
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                dirs
                    .add(entry.getKey().toString())
                    .set(entry.getValue().toString())
                    .up();
            }
        } else {
            dirs.set(value.toString());
        }
        this.pom.modify(dirs);
        return this;
    }

}
