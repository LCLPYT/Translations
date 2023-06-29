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

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UrlLanguageLoader implements LanguageLoader {

    private final URL[] urls;
    private final Iterable<String> resourceDirectories;
    private final Executor executor;
    private final Logger logger;

    public UrlLanguageLoader(URL url, Iterable<String> resourceDirectories, Logger logger) {
        this(new URL[]{ url }, resourceDirectories, logger);
    }

    public UrlLanguageLoader(URL[] urls, Iterable<String> resourceDirectories, Logger logger) {
        this(urls, resourceDirectories, logger, ForkJoinPool.commonPool());
    }

    public UrlLanguageLoader(URL[] urls, Iterable<String> resourceDirectories, Logger logger, Executor executor) {
        this.urls = urls;
        this.resourceDirectories = resourceDirectories;
        this.logger = logger;
        this.executor = executor;
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

        for (URL url : urls) {
            try {
                parseUrl(url, builder);
            } catch (IOException e) {
                logger.error("Failed to parse resource url {}", url, e);
            }
        }

        return builder.build();
    }

    private void parseUrl(URL url, JsonLanguageCollectionBuilder builder) throws IOException {
        String path = url.getPath();
        if (path == null) return;

        if (path.endsWith("/")) {
            parseDirectory(url, builder);
        } else {
            parseJar(url, builder);
        }
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

    private void parseDirectory(URL url, JsonLanguageCollectionBuilder builder) throws IOException {
        if (!"file".equals(url.getProtocol())) {
            throw new IllegalStateException(String.format("Cannot read directory for protocol %s", url.getProtocol()));
        }

        final URI uri;

        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        final Path dir = Paths.get(uri);

        if (!Files.isDirectory(dir)) {
            throw new IOException(String.format("Not a directory %s", dir));
        }

        try (Stream<Path> files = Files.walk(dir, 256)) {
            files.filter(path -> {
                        String rel = dir.relativize(path).toString();
                        String normalized = rel.replace(File.separatorChar, '/');
                        return isTranslationFile(normalized);
                    })
                    .sequential()
                    .forEach(path -> readFile(path, builder));
        }
    }

    private void readFile(Path path, JsonLanguageCollectionBuilder builder) {
        final String json;

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

    @Nonnull
    public static URL[] getResourceLocations(Object o) {
        if (o == null) return new URL[0];

        if (o instanceof URL[]) {
            return (URL[]) o;
        }

        if (o instanceof URL) {
            return new URL[] { (URL) o };
        }

        if (o instanceof ClassLoader) {
            return getResourceLocations((ClassLoader) o);
        }

        final Class<?> c;
        if (o instanceof Class<?>) {
            c = (Class<?>) o;
        } else {
            c = o.getClass();
        }

        final URL[] clUrls = getResourceLocations(c.getClassLoader());

        final ProtectionDomain protectionDomain;

        try {
            protectionDomain = c.getProtectionDomain();
        } catch (SecurityException e) {
            return clUrls;
        }

        final CodeSource codeSource = protectionDomain.getCodeSource();

        if (codeSource == null) {
            return clUrls;
        }

        final URL url = codeSource.getLocation();
        if (url == null) return clUrls;

        final URL[] merged = new URL[clUrls.length + 1];
        System.arraycopy(clUrls, 0, merged, 0, clUrls.length);
        merged[merged.length - 1] = url;

        return merged;
    }

    private static URL[] getResourceLocations(ClassLoader loader) {
        if (!(loader instanceof URLClassLoader)) {
            // cannot safely determine resource location
            return new URL[0];
        }

        URLClassLoader cl = (URLClassLoader) loader;
        return cl.getURLs();
    }
}
