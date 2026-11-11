package com.mylearn.tracker.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Plain text of the (sanitized) HTML the note editor produces, for searching. */
public final class HtmlText {

    private static final Pattern TAG = Pattern.compile("<[^>]*>");
    private static final Pattern NUMERIC_ENTITY = Pattern.compile("&#(x?)([0-9a-fA-F]+);");

    private HtmlText() {
    }

    public static String toPlainText(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        return decode(TAG.matcher(html).replaceAll(" ")).replaceAll("\\s+", " ").strip();
    }

    private static final Pattern BLOCK_END = Pattern.compile("(?i)<br\\s*/?>|</(p|div|li|h[1-6]|pre|blockquote|tr)>");

    /**
     * Plain text that keeps the note's line structure: one line per paragraph, list item or
     * heading, blank lines between blocks (for line-based syntax such as flashcards).
     */
    public static String toLines(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        String marked = BLOCK_END.matcher(html).replaceAll(m -> m.group().toLowerCase().startsWith("<br") ? "\n" : "\n\n");
        String text = decode(TAG.matcher(marked).replaceAll(""));
        return text.replaceAll("[ \\t\\x0B\\f\\r]+\n", "\n").replaceAll("\n{3,}", "\n\n").strip();
    }

    private static String decode(String text) {
        Matcher m = NUMERIC_ENTITY.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            int code;
            try {
                code = Integer.parseInt(m.group(2), m.group(1).isEmpty() ? 10 : 16);
            } catch (NumberFormatException e) {
                code = ' ';
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(Character.isValidCodePoint(code)
                    ? new String(Character.toChars(code)) : " "));
        }
        m.appendTail(sb);
        return sb.toString().replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">")
                .replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&");
    }
}
