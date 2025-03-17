/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
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
    public Properties set(final String name, final String value) throws IOException {
        this.pom.modify(
            new Directives()
                .xpath("/project")
                .addIf(this.element)
                .strict(1)
                .xpath(name)
                .remove()
                .xpath("/project")
                .xpath(this.element)
                .strict(1)
                .add(name)
                .set(value)
        );
        return this;
    }
}
