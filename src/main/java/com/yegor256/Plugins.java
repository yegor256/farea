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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.xembly.Directives;

/**
 * Plugins inside Build.
 *
 * @since 0.0.1
 */
final class Plugins {

    /**
     * Location.
     */
    private final Pom pom;

    /**
     * Ctor.
     * @param file The POM
     */
    Plugins(final Pom file) {
        this.pom = file;
    }

    /**
     * Ctor.
     * @param group The group ID
     * @param artifact The artifact ID
     * @param version The version
     * @return Plugin just added
     * @throws IOException If fails
     */
    Plugin append(final String group, final String artifact,
        final String version) throws IOException {
        this.pom.modify(
            new Directives()
                .xpath("/project")
                .addIf("build")
                .addIf("plugins")
                .add("plugin")
                .add("groupId").set(group).up()
                .add("artifactId").set(artifact).up()
                .add("version").set(version).up()
        );
        return new Plugin(
            this.pom,
            Integer.parseInt(this.pom.xpath("count(/project/build/plugins/plugin)").get(0))
        );
    }

    /**
     * Ctor.
     * @return Itself as a plugin
     * @throws IOException If fails
     */
    Plugin append() throws IOException {
        final String classpath = System.getProperty("java.class.path");
        final Path zip = Paths.get("/tmp/farea.jar");
        if (zip.toFile().exists()) {
            Files.delete(zip);
        }
        Files.createFile(zip);
        final Set<String> seen = new HashSet<>();
        try (ZipOutputStream stream = new ZipOutputStream(Files.newOutputStream(zip))) {
            for (final String ent : classpath.split(java.io.File.pathSeparator)) {
                final File dir = new File(ent);
                if (!dir.isDirectory()) {
                    continue;
                }
                final Path src = Paths.get(dir.getAbsolutePath());
                for (final Path file : Files.walk(src).collect(Collectors.toList())) {
                    if (file.toFile().isDirectory()) {
                        continue;
                    }
                    final String name = src.relativize(file).toString();
                    if (seen.contains(name)) {
                        continue;
                    }
                    ZipEntry entry = new ZipEntry(name);
                    stream.putNextEntry(entry);
                    Files.copy(file, stream);
                    stream.closeEntry();
                    seen.add(name);
                }
            }
        }
        return this.append("com.yegor256", "farea-stub", "0.0.0");
    }

}
