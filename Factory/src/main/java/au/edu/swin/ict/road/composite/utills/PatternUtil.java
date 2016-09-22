package au.edu.swin.ict.road.composite.utills;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TODO documentation
 */
public class PatternUtil {
    private static String pattern = "[[|][*][(][)]\\s+]+";
    private static Pattern splitter = Pattern.compile(pattern);

    public static List<String> splitEP(String eventPattern) {
        String[] result = splitter.split(eventPattern);
        List<String> tobeReturn = new ArrayList<String>();
        for (String s : result) {
            tobeReturn.add(s.trim());
        }
        return tobeReturn;
    }
}
