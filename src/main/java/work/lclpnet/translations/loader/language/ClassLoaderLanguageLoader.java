/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.loader.language;

import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import work.lclpnet.translations.model.LanguageCollection;
import work.lclpnet.translations.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassLoaderLanguageLoader implements LanguageLoader {

    private final ClassLoader classLoader;
    private final Iterable<String> resourceDirectories;
    private final Executor executor;
    private final Logger logger;

    public ClassLoaderLanguageLoader(ClassLoader classLoader, Iterable<String> resourceDirectories, Logger logger) {
        this(classLoader, resourceDirectories, ForkJoinPool.commonPool(), logger);
    }

    public ClassLoaderLanguageLoader(ClassLoader classLoader, Iterable<String> resourceDirectories, Executor executor, Logger logger) {
        this.classLoader = classLoader;
        this.resourceDirectories = resourceDirectories;
        this.executor = executor;
        this.logger = logger;
    }

    @Override
    public CompletableFuture<? extends LanguageCollection> loadLanguages() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadSync();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load languages", e);
            }
        }, executor);
    }

    private LanguageCollection loadSync() throws IOException {
        final JsonLanguageCollectionBuilder builder = new JsonLanguageCollectionBuilder(logger);

        for (String resourceDirectory : resourceDirectories) {
            Enumeration<URL> resources = classLoader.getResources(resourceDirectory);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                try {
                    parseUrl(resource, resourceDirectory, builder);
                } catch (IOException e) {
                    logger.error("Failed to parse resource url {}", resource);
                }
            }
        }

        return builder.build();
    }

    private void parseUrl(URL url, String resourceDirectory, JsonLanguageCollectionBuilder builder) throws IOException {
        String[] parts = url.toString().split("!");

        if (parts.length > 1) {
            // file or directory in a jar file
            URL jarUrl = new URL(parts[0]);

            parseJar(jarUrl, builder);
            return;
        }

        // file or directory on the file system

        if (!"file".equals(url.getProtocol())) {
            throw new IllegalStateException(String.format("Unsupported url %s", url));
        }

        parseLocalUrl(url, resourceDirectory, builder);
    }

    private void parseJar(URL url, JsonLanguageCollectionBuilder builder) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(url.openStream())) {
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                if (!isTranslationFile(name)) continue;

                logger.debug("Reading zipped translation file {} ...", name);

                try {
                    String json = IOUtil.readString(zip, StandardCharsets.UTF_8);

                    builder.parse(json, IOUtil.basename(name));
                } catch (IOException | JsonSyntaxException e) {
                    logger.error("Failed to read translation file {}", name, e);
                }
            }
        }
    }

    private void parseLocalUrl(URL url, String resourceDirectory, JsonLanguageCollectionBuilder builder) throws IOException {
        final URI uri;

        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        final Path dir = Paths.get(uri);

        if (!Files.isDirectory(dir)) throw new IOException(String.format("Not a directory %s", dir));

        try (Stream<Path> files = Files.walk(dir, 256)) {
            files.filter(path -> {
                        String rel = dir.relativize(path).toString();
                        String normalized = resourceDirectory + (resourceDirectory.endsWith("/") ? "" : "/") + rel.replace(File.separatorChar, '/');
                        return isTranslationFile(normalized);
                    })
                    .sequential()
                    .forEach(path -> readFile(path, builder));
        }
    }

    private void readFile(Path path, JsonLanguageCollectionBuilder builder) {
        String json;
        try (InputStream in = Files.newInputStream(path)) {
            json = IOUtil.readString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to read translation file {}", path, e);
            return;
        }

        try {
            builder.parse(json, IOUtil.basename(path.getFileName().toString()));
        } catch (JsonSyntaxException e) {
            logger.error("Invalid json file {}", path, e);
        }
    }

    protected boolean isTranslationFile(String fileName) {
        if (!fileName.endsWith(".json")) return false;

        for (String directory : resourceDirectories) {
            if (fileName.startsWith(directory)) {
                return true;
            }
        }

        return false;
    }
}
