package com.kimkihwan.me.imagelist.util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by jamie on 1/15/17.
 */

public class FileUtil {

    private final static String TEMP_FILENAME = "TEMP";

    public static File createTempFile(File ori) throws IOException {
        return File.createTempFile(FilenameUtils.getBaseName(ori.getName()) + TEMP_FILENAME,
                FilenameUtils.EXTENSION_SEPARATOR_STR.concat(FilenameUtils.getExtension(ori.getName())),
                ori.getParentFile());
    }
}
