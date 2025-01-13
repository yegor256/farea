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
import org.xembly.Directives;

/**
 * Execution inside Plugin.
 *
 * @since 0.0.1
 */
final class DtExecution implements Execution {

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * Position in "plugins".
     */
    private final int pos;

    /**
     * The "id" of the "execution".
     */
    private final String eid;

    /**
     * Ctor.
     * @param file The POM
     * @param position The position
     * @param name The "id" of it
     */
    DtExecution(final Pom file, final int position, final String name) {
        this.pom = file;
        this.pos = position;
        this.eid = name;
    }

    @Override
    public Execution phase(final String value) throws IOException {
        this.pom.modify(
            this.into()
                .addIf("phase")
                .set(value)
        );
        return this;
    }

    @Override
    public Execution goals(final String... values) throws IOException {
        final Directives dirs = this.into()
            .addIf("goals")
            .up()
            .xpath("goals");
        for (final String goal : values) {
            dirs.add("goal").set(goal).up();
        }
        this.pom.modify(dirs);
        return this;
    }

    @Override
    public Configuration configuration() {
        return new DtConfiguration(
            this.pom,
            String.format(
                "/project/build/plugins/plugin[position()=%d]/executions/execution[id='%s']",
                this.pos, this.eid
            )
        );
    }

    private Directives into() {
        final String xpath = String.format(
            "/project/build/plugins/plugin[position()=%d]",
            this.pos
        );
        return new Directives()
            .xpath(xpath)
            .strict(1)
            .addIf("executions")
            .xpath(
                String.format(
                    "../executions[not(execution[id='%s'])]",
                    this.eid
                )
            )
            .add("execution")
            .add("id")
            .set(this.eid)
            .xpath(xpath)
            .xpath(
                String.format(
                    "executions/execution[id='%s']",
                    this.eid
                )
            );
    }

}
