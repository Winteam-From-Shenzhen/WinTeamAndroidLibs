package net.yt.libs.test.wifi;

public class Utils {
    public static String removeDoubleQuotes(String string) {
        if (string == null){
            return null;
        }
        final int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
    }

    public static String addQuotationMarks(String content) {
        if (content == null) {
            return null;
        }
        final int length = content.length();
        if ((length > 1) && (content.charAt(0) == '"') && (content.charAt(length - 1) == '"')) {
            return content;
        }
        content = "\"" + content + "\"";
        return content;
    }
}
