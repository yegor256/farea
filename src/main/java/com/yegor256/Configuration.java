/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.yegor256;

import java.io.IOException;
import org.xembly.Directives;

/**
 * Configuration of a Plugin.
 *
 * @since 0.0.1
 */
public final class Configuration {

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * Position of the plugin inside plugins.
     */
    private final int pos;

    /**
     * Ctor.
     * @param file The POM
     * @param position The position
     */
    Configuration(final Pom file, final int position) {
        this.pom = file;
        this.pos = position;
    }

    /**
     * Get config.
     * @param key The key
     * @param value The value
     * @return Config
     * @throws IOException If fails
     */
    public Configuration set(final String key, final Object value) throws IOException {
        this.pom.modify(
            new Directives()
                .xpath(
                    String.format(
                        "/project/build/plugins/plugin[position()=%d]",
                        this.pos
                    )
                )
                .strict(1)
                .addIf("configuration")
                .addIf(key)
                .set(value)
        );
        return this;
    }

}
