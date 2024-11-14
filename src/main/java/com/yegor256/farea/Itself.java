/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 Yegor Bugayenko
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

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.xembly.Directives;

/**
 * This classpath classes packaged as a plugin.
 *
 * @since 0.0.1
 */
final class Itself {

    /**
     * Directory of Maven project.
     */
    private final Path home;

    /**
     * Original POM, of the current project.
     */
    private final Base base;

    /**
     * Should it fail if plugin.xml is not in the classpath?
     */
    private final boolean careful;

    /**
     * Ctor.
     * @param dir The directory of the Maven project
     */
    Itself(final Path dir) {
        this(dir, new Base());
    }

    /**
     * Ctor.
     * @param dir The directory of the Maven project
     * @param pom Original pom.xml
     */
    Itself(final Path dir, final Base pom) {
        this(dir, pom, true);
    }

    /**
     * Ctor.
     * @param dir The directory of the Maven project
     * @param pom Original pom.xml
     * @param crf Careful?
     */
    Itself(final Path dir, final Base pom, final boolean crf) {
        this.home = dir;
        this.base = pom;
        this.careful = crf;
    }

    /**
     * Deploy it to local Maven repository.
     * @param local Path of local Maven repo, usually "~/.m2/repository"
     * @return The {@code version} of a new Maven dependency just deployed
     * @throws IOException If fails
     */
    public String deploy(final Path local) throws IOException {
        final String version = this.next();
        final Path place = local.resolve(
            String.format(
                "%s/%s/%s",
                this.base.groupId().replace(".", "/"),
                this.base.artifactId(), version
            )
        );
        final String name = String.format(
            "%s-%s", this.base.artifactId(), version
        );
        synchronized (Itself.class) {
            this.assembleJar(
                version,
                place.resolve(
                    String.format("%s.jar", name)
                )
            );
            Files.write(
                place.resolve(
                    String.format("%s.pom", name)
                ),
                this.base.xml().toString().getBytes(StandardCharsets.UTF_8)
            );
        }
        return version;
    }

    /**
     * Zip the entire module into a JAR.
     * @param version Version of the JAR to use
     * @param zip The location of the JAR
     * @throws IOException If fails
     */
    private void assembleJar(final String version, final Path zip) throws IOException {
        if (zip.toFile().exists()) {
            Files.delete(zip);
        }
        if (zip.toFile().getParentFile().mkdirs()) {
            Logger.debug(
                this, "Directory created at %[file]s",
                zip.toFile().getParentFile()
            );
        }
        Files.createFile(zip);
        final Set<String> seen = new HashSet<>(0);
        final String classpath = System.getProperty("java.class.path");
        final String[] jars = classpath.split(File.pathSeparator);
        if (!this.zip(version, zip, seen, jars) && this.careful) {
            throw new IllegalStateException(
                String.join(
                    " ",
                    "There is no 'META-INF/maven/plugin.xml' file in the classpath.",
                    "Most probably you haven't configured maven-plugin-plugin",
                    "or the 'packaging' of your Maven project is not set to 'maven-plugin'.",
                    "Normally, the 'plugin.xml' file is in the 'target/' directory.",
                    "This error may also be caused by the fact that you haven't",
                    "executed the full Maven build, but only running a test.",
                    "The 'plugin.xml' file is generated by the 'maven-plugin-plugin'",
                    "plugin right before the execution of unit tests."
                )
            );
        }
        Logger.debug(
            Itself.class,
            "JAR saved to %[file]s (%[size]s, %d files, %d jars)",
            zip, zip.toFile().length(), seen.size(), jars.length
        );
    }

    /**
     * Package it in a single ZIP.
     * @param version Version of the JAR to use
     * @param zip The ZIP file
     * @param seen Seen already
     * @param jars JARs to package
     * @return TRUE if descripted (has "plugin.xml" file inside)
     * @throws IOException If fails
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    private boolean zip(final String version, final Path zip, final Set<String> seen,
        final String... jars) throws IOException {
        boolean descripted = false;
        try (ZipOutputStream stream = new ZipOutputStream(Files.newOutputStream(zip))) {
            for (final String path : jars) {
                final File jar = new File(path);
                if (!jar.isDirectory()) {
                    continue;
                }
                final Path src = jar.toPath();
                for (final Path file : Files.walk(src).collect(Collectors.toList())) {
                    if (file.toFile().isDirectory()) {
                        continue;
                    }
                    final String name = src.relativize(file).toString().replace("\\", "/");
                    if (seen.contains(name)) {
                        continue;
                    }
                    final ZipEntry entry = new ZipEntry(name);
                    stream.putNextEntry(entry);
                    if ("META-INF/maven/plugin.xml".equals(name)) {
                        final Path descriptor = this.temp(version).resolve("plugin.xml");
                        Files.copy(file, descriptor);
                        new Pom(descriptor).modify(
                            new Directives()
                                .xpath("/plugin/version")
                                .strict(1)
                                .set(version)
                        );
                        Files.copy(descriptor, stream);
                        descripted = true;
                    } else {
                        Files.copy(file, stream);
                    }
                    stream.closeEntry();
                    seen.add(name);
                }
            }
        }
        return descripted;
    }

    /**
     * Create new version.
     * @return The version
     * @throws IOException If fails
     */
    private String next() throws IOException {
        while (true) {
            final String version = String.format(
                "%s-farea-%08x", this.base.version(), System.nanoTime()
            );
            synchronized (Itself.class) {
                if (this.temp(version).toFile().mkdirs()) {
                    return version;
                }
            }
        }
    }

    /**
     * Make a temp directory for this version.
     * @param version The version
     * @return The path
     */
    private Path temp(final String version) {
        final Path temp = this.home
            .resolve("target")
            .resolve("farea")
            .resolve(version);
        Runtime.getRuntime().addShutdownHook(
            new Thread(
                () -> {
                    try (Stream<Path> files = Files.walk(temp)) {
                        files
                            .map(Path::toFile)
                            .sorted(Comparator.reverseOrder())
                            .forEach(File::delete);
                    } catch (final IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                }
            )
        );
        return temp;
    }

}
