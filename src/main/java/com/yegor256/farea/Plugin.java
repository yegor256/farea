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
package com.yegor256.farea;

/**
 * Plugin inside Plugin.
 *
 * @since 0.0.1
 */
public final class Plugin {

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * Position in "plugins".
     */
    private final int pos;

    /**
     * Ctor.
     * @param file The POM
     * @param position The position
     */
    Plugin(final Pom file, final int position) {
        this.pom = file;
        this.pos = position;
    }

    /**
     * Append new execution or get access to existing one.
     * @param name The "id" of it
     * @return Execution found or added
     */
    public Execution execution(final String name) {
        return new Execution(this.pom, this.pos, name);
    }

    /**
     * Get config.
     * @return Config
     */
    public Configuration configuration() {
        return new Configuration(
            this.pom,
            String.format(
                "/project/build/plugins/plugin[position()=%d]",
                this.pos
            )
        );
    }

}
