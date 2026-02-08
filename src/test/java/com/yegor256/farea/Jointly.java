/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import org.cactoos.Scalar;
import org.cactoos.experimental.Threads;

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
        final AtomicReference<T> ret = new AtomicReference<>();
        final Collection<Scalar<T>> bodies = new ArrayList<>(threads);
        for (int idx = 0; idx < threads; ++idx) {
            final int thread = idx;
            bodies.add(() -> this.block.exec(thread));
        }
        new Threads<>(threads, bodies).forEach(ret::set);
        return ret.get();
    }

    /**
     * The block of code to run, in many threads.
     *
     * @param <R> Type of result
     * @since 0.11.0
     */
    @FunctionalInterface
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
