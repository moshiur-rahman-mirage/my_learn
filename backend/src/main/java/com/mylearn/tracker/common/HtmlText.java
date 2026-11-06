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
        String text = TAG.matcher(html).replaceAll(" ");
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
        text = sb.toString().replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">")
                .replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&");
        return text.replaceAll("\\s+", " ").strip();
    }
}
