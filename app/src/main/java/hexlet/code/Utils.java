package hexlet.code;

public class Utils {
    public static String truncate(String text) {
        if (text == null) {
            return "";
        }
        int maxLength = 100;
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
