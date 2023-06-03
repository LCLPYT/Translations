/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.io;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class locates translation files inside of jar files.
 * The jar file to search is the jar file containing the given class.
 *
 * @author LCLP
 */
public class JarTranslationLocator implements ITranslationLocator {

    private final Class<?> classInJar;
    private final Logger logger;
    private final Predicate<String> fileNamePredicate;

    /**
     * Create a new locator which will locate .json files inside of given directories.
     *
     * @param classInJar This class determines the jar file to search; the jar file containing this class will be used.
     * @param logger A logger to receive feedback.
     * @param fileNamePredicate A predicate to filter file names for translation files.
     */
    public JarTranslationLocator(Class<?> classInJar, Logger logger, Predicate<String> fileNamePredicate) {
        this.classInJar = classInJar;
        this.logger = logger;
        this.fileNamePredicate = fileNamePredicate;
    }

    /**
     * Create a new locator which will locate .json files inside of given directories.
     *
     * @param classInJar This class determines the jar file to search; the jar file containing this class will be used.
     * @param logger A logger to receive feedback.
     * @param resourceDirectories A list of file path prefixes relative to the jar root.
     *                            Only files starting with one of those prefixes will be located.
     */
    public JarTranslationLocator(Class<?> classInJar, Logger logger, List<String> resourceDirectories) {
        this(classInJar, logger, name -> name.endsWith(".json") && resourceDirectories.stream().anyMatch(name::startsWith));
    }

    @Override
    public List<String> locate() throws IOException {
        CodeSource src = classInJar.getProtectionDomain().getCodeSource();
        if (src == null) throw new NullPointerException("code source is null");

        List<String> translationFiles = new ArrayList<>();

        URL jar = src.getLocation();
        try (ZipInputStream zip = new ZipInputStream(jar.openStream())) {
            while (true) {
                ZipEntry entry = zip.getNextEntry();
                if(entry == null) break;

                String name = entry.getName();
                if (!fileNamePredicate.test(name)) continue;

                translationFiles.add(name);
                logger.info(String.format("Located translation file '%s'.", name));
            }
        }

        return translationFiles;
    }

}
