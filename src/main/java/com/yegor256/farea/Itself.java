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

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This classpath classes packaged as a plugin.
 *
 * @since 0.0.1
 */
final class Itself {

    /**
     * Original POM, of the current project.
     */
    private final Base base;

    /**
     * Ctor.
     */
    Itself() {
        this(new Base());
    }

    /**
     * Ctor.
     * @param pom Original pom.xml
     */
    Itself(final Base pom) {
        this.base = pom;
    }

    /**
     * Deploy it to local Maven repository.
     * @param local Path of local Maven repo, usually "~/.m2/repository"
     * @throws IOException If fails
     */
    public void deploy(final Path local) throws IOException {
        final Path place = local.resolve(
            String.format(
                "%s/%s/%s",
                this.base.groupId().replace(".", "/"),
                this.base.artifactId(), this.base.version()
            )
        );
        final String name = String.format(
            "%s-%s",
            this.base.artifactId(), this.base.version()
        );
        this.assembleJar(
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

    /**
     * Zip the entire module into a JAR.
     * @param zip The location of the JAR
     * @throws IOException If fails
     */
    private void assembleJar(final Path zip) throws IOException {
        if (zip.toFile().exists()) {
            Files.delete(zip);
        }
        if (zip.toFile().getParentFile().mkdirs()) {
            Logger.debug(
                this, "Directory created at %s",
                zip.toFile().getParentFile()
            );
        }
        Files.createFile(zip);
        final Set<String> seen = new HashSet<>();
        final String classpath = System.getProperty("java.class.path");
        final String[] jars = classpath.split(File.pathSeparator);
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
                    Files.copy(file, stream);
                    stream.closeEntry();
                    seen.add(name);
                }
            }
        }
        Logger.debug(
            Itself.class,
            "JAR saved to %s (%d bytes)",
            zip, zip.toFile().length()
        );
    }

}
