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
     * Ctor.
     * @param file The POM
     */
    DtProperties(final Pom file) {
        this.pom = file;
    }

    @Override
    public Properties set(final String name, final String value) throws IOException {
        this.pom.modify(
            new Directives()
                .xpath("/project")
                .addIf("properties")
                .strict(1)
                .xpath(name)
                .remove()
                .xpath("/project/properties")
                .strict(1)
                .add(name)
                .set(value)
        );
        return this;
    }

}
