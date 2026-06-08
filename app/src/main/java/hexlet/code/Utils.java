package hexlet.code;

public class Utils {
    public static String truncate(String text) {
        if (text == null) {
            return "";
        }
        if (text.length() <= 200) {
            return text;
        }
        return text.substring(0, 197) + "...";
    }
}
