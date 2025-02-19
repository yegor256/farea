/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 Yegor Bugayenko
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

import java.io.IOException;
import java.nio.file.Path;

/**
 * Dependencies.
 *
 * @since 0.1.0
 */
public interface Dependencies {
    /**
     * Ctor.
     * @param group The group ID
     * @param artifact The artifact ID
     * @param version The version
     * @return Deps
     * @throws IOException If fails
     */
    Dependency append(String group, String artifact,
        String version) throws IOException;

    /**
     * Add itself (the code in this classpath) to the Maven reactor.
     * @return Itself as a dependency
     * @throws IOException If fails
     */
    Dependency appendItself() throws IOException;

    /**
     * Add itself (the code in this classpath) to the Maven reactor.
     * @param local Path of local Maven repo, usually "~/.m2/repository"
     * @return Itself as a plugin
     * @throws IOException If fails
     */
    Dependency appendItself(Path local) throws IOException;
}
