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
import java.nio.file.Path;

/**
 * Fake Maven Reactor.
 *
 * <p>Run it like this to test a simple Java compilation:</p>
 *
 * <code><pre> </pre></code>
 *
 * @since 0.0.1
 */
public final class Farea {

    /**
     * Home.
     */
    private final Path home;

    /**
     * Ctor.
     * @param dir The home dir
     */
    public Farea(final Path dir) {
        this.home = dir;
    }

    /**
     * Access to files.
     * @return Files in home
     */
    public Files files() {
        return new Files(this.home);
    }

    /**
     * Get access to build.
     * @return Build
     * @throws IOException If fails
     */
    public Build build() throws IOException {
        return new Build(this.pom());
    }

    /**
     * Execute with command line arguments.
     * @param args Command line arguments
     * @throws IOException If fails
     */
    public void exec(final String... args) throws IOException {
        this.pom().init();
        new Jaxec()
            .with("mvn")
            .with(args)
            .withHome(this.home)
            .withCheck(false)
            .exec();
    }

    /**
     * Execute with command line arguments.
     * @return POM
     * @throws IOException If fails
     */
    public Pom pom() throws IOException {
        return new Pom(this.home.resolve("pom.xml")).init();
    }

}
