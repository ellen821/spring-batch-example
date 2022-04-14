package com.assadev.batch.core.utils;

public class CrawlerUtils {

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}
