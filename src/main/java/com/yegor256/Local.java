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

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Local maven repository, usually in "~/.m2/repository".
 *
 * @since 0.0.1
 */
public final class Local {

    /**
     * User home.
     */
    private final Path user;

    /**
     * Ctor.
     */
    public Local() {
        this.user = Paths.get(System.getProperty("user.home"));
    }

    /**
     * Get its path.
     *
     * @return The absolute path of it
     */
    public Path path() {
        final Path home = this.user.resolve(".m2");
        if (!home.toFile().exists()) {
            throw new IllegalStateException(
                String.format(
                    "Maven home is not found at this location: %s",
                    home.toAbsolutePath()
                )
            );
        }
        final Path local = home.resolve("repository");
        if (!local.toFile().exists()) {
            throw new IllegalStateException(
                String.format(
                    "Maven home repository is not found at this location: %s",
                    local.toAbsolutePath()
                )
            );
        }
        return local;
    }

}
