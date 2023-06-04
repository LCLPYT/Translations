/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.translations.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class IOUtil {

    private IOUtil() {}

    public static void transfer(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int len;

        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    public static String readString(InputStream in, Charset charset) throws IOException {
        return readString(in, charset.name());
    }

    public static String readString(InputStream in, String charsetName) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        transfer(in, out);
        return out.toString(charsetName);
    }

    public static String basename(String path) {
        String[] parts = path.split("/");
        String fileName = parts[parts.length - 1];

        parts = fileName.split("\\.");
        if (parts.length == 1) return fileName;  // filename does not contain dots

        String extension = parts[parts.length - 1];

        return fileName.substring(0, fileName.length() - extension.length() - 1);  // remove file extension
    }
}
