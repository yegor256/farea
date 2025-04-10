/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256.farea;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Hamcrest matcher for a requisite.
 *
 * <p>Use it like this:</p>
 *
 * <code><pre> new Farea(dir).together(f -> {
 *   f.build()
 *     .plugins()
 *     .appendItself()
 *     .goal("my-goal")
 *     .phase("test")
 *     .configuration()
 *     .set("message", "Hello, world!");
 *   f.exec("test");
 *   assertThat(
 *     f.files().log(),
 *     new RequisiteMatcher.OnSuccess()
 *   );
 * });</pre></code>
 *
 * @since 0.0.1
 */
public final class RequisiteMatcher extends BaseMatcher<Requisite> {

    /**
     * Successful log.
     */
    public static final Matcher<Requisite> SUCCESS = new RequisiteMatcher()
        .with("BUILD SUCCESS")
        .without("BUILD FAILURE");

    /**
     * Failed log.
     */
    public static final Matcher<Requisite> FAILURE = new RequisiteMatcher()
        .with("BUILD FAILURE");

    /**
     * The log we've seen in the {@link #matches(Object)}.
     */
    private String seen;

    /**
     * Explanation of the failure.
     */
    private final Collection<String> failures = new LinkedList<>();

    /**
     * Expect these regular expressions to be present there.
     */
    private final Collection<Pattern> positive = new LinkedList<>();

    /**
     * Expect these expressions to be absent.
     */
    private final Collection<Pattern> negative = new LinkedList<>();

    @Override
    public boolean matches(final Object log) {
        try {
            this.seen = Requisite.class.cast(log).content();
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        boolean match = true;
        for (final Pattern ptn : this.positive) {
            if (!ptn.matcher(this.seen).find()) {
                this.failures.add(String.format("there is no \"%s\"", ptn));
                match = false;
            }
        }
        for (final Pattern ptn : this.negative) {
            if (ptn.matcher(this.seen).find()) {
                this.failures.add(String.format("there is \"%s\"", ptn));
                match = false;
            }
        }
        return match;
    }

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(
            String.format(
                "Maven log with %d mandatory part(s) inside and without %d part(s), but ",
                this.positive.size(), this.negative.size()
            )
        );
        int idx = 0;
        for (final String msg : this.failures) {
            if (idx > 0) {
                desc.appendText(" and ");
            }
            desc.appendText(msg);
            ++idx;
        }
    }

    @Override
    public void describeMismatch(final Object log, final Description desc) {
        desc.appendText("\n").appendText(this.seen);
    }

    /**
     * With this pattern.
     * @param regex The pattern
     * @return Itself
     */
    public RequisiteMatcher with(final String regex) {
        return this.with(Pattern.compile(Pattern.quote(regex)));
    }

    /**
     * With this pattern.
     * @param regex The pattern
     * @return Itself
     */
    public RequisiteMatcher with(final Pattern regex) {
        this.positive.add(regex);
        return this;
    }

    /**
     * Without this pattern.
     * @param regex The pattern
     * @return Itself
     */
    public RequisiteMatcher without(final String regex) {
        return this.without(Pattern.compile(Pattern.quote(regex)));
    }

    /**
     * Without this pattern.
     * @param regex The pattern
     * @return Itself
     */
    public RequisiteMatcher without(final Pattern regex) {
        this.negative.add(regex);
        return this;
    }
}
