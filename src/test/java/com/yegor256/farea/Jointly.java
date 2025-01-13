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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.cactoos.Scalar;
import org.cactoos.experimental.Threads;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

/**
 * Run piece of code in many threads.
 *
 * @param <T> Type of result
 * @since 0.11.0
 */
final class Jointly<T> {

    /**
     * The block.
     */
    private final Jointly.Block<T> block;

    /**
     * Ctor.
     * @param blk The code block to run.
     */
    Jointly(final Jointly.Block<T> blk) {
        this.block = blk;
    }

    /**
     * Build the result.
     * @return The result built
     */
    T made() {
        return this.made(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Build the result.
     * @param threads How many threads to use
     * @return The result built
     */
    T made(final int threads) {
        final AtomicInteger started = new AtomicInteger();
        final AtomicReference<T> ret = new AtomicReference<>();
        final Collection<Scalar<T>> bodies = new ArrayList<>(threads);
        final AtomicInteger finished = new AtomicInteger();
        for (int idx = 0; idx < threads; ++idx) {
            bodies.add(
                () -> {
                    final T out = this.block.exec(started.getAndIncrement());
                    finished.incrementAndGet();
                    return out;
                }
            );
        }
        new Threads<>(threads, bodies).forEach(ret::set);
        MatcherAssert.assertThat(
            "all threads counted",
            finished.get(),
            Matchers.equalTo(threads)
        );
        return ret.get();
    }

    /**
     * The block of code to run, in many threads.
     *
     * @param <R> Type of result
     * @since 0.11.0
     */
    public interface Block<R> {
        /**
         * The method to run.
         * @param thread The number of thread running
         * @return The result
         * @throws Exception If fails
         */
        R exec(int thread) throws Exception;
    }
}
