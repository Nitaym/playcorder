package com.playcorder.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class PlaycorderUtils {
    public static String GetFilename(String _prefix, String _suffix, String extension) {
        String prefix = "";
        if (!Objects.equals(_prefix, ""))
            prefix = _prefix + "-";
        String suffix = "";
        if (!Objects.equals(_suffix, ""))
            suffix = "-" + _suffix;

        return  prefix +
                (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(new Date()) +
                suffix +
                "." + extension;
    }
    public static String GetFilename(String prefix, String extension) {
        return GetFilename(prefix, "", extension);
    }
    public static String GetFilename(String prefix) {
        return GetFilename(prefix, "raw");
    }
    public static String GetFilename() {
        return GetFilename("");
    }
}
