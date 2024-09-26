package com.example.shared.utils;

import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtil extends StringUtils {

    private static final LinkedList<Replacer> replacerList = new LinkedList<>();

    public static final String SPACE = " ";

    public static final Character AND = '&';

    public static final Character STAR = '*';

    public static final Character SLASH = '/';

    public static final Character QUESTION_MASK = '?';

    public static final Character COMMA = ',';

    public static final Character UNDERSCORE = '_';

    public static final Character DOT = '.';

    public static final Character AT = '@';

    public static final Character PLUS = '+';

    public static final String UTF8_BOM = "\uFEFF";

    public static final String CODE = "$.code";

    static {
        final String aa = "[áàảãạâấầẩẫậăắằẳẵặ]";
        final String a = "a";
        replacerList.add(new Replacer(aa, a));

        final String oo = "[óòỏõọôốồổỗộơớờởỡợ]";
        final String o = "o";
        replacerList.add(new Replacer(oo, o));

        final String ee = "[éèẻẽẹêếềểễệ]";
        final String e = "e";
        replacerList.add(new Replacer(ee, e));

        final String uu = "[uúùủũụưứừửữự]";
        final String u = "u";
        replacerList.add(new Replacer(uu, u));

        final String ii = "[iíìỉĩị]";
        final String i = "i";
        replacerList.add(new Replacer(ii, i));

        final String yy = "[ýỳỷỹỵ]";
        final String y = "y";
        replacerList.add(new Replacer(yy, y));

        final String dd = "[đ]";
        final String d = "d";
        replacerList.add(new Replacer(dd, d));
    }

    public static String compound2Unicode(String str) {
        str = str.replaceAll("\u0065\u0309", "\u1EBB"); //ẻ
        str = str.replaceAll("\u0065\u0301", "\u00E9"); //é
        str = str.replaceAll("\u0065\u0300", "\u00E8"); //è
        str = str.replaceAll("\u0065\u0323", "\u1EB9"); //ẹ
        str = str.replaceAll("\u0065\u0303", "\u1EBD"); //ẽ
        str = str.replaceAll("\u00EA\u0309", "\u1EC3"); //ể
        str = str.replaceAll("\u00EA\u0301", "\u1EBF"); //ế
        str = str.replaceAll("\u00EA\u0300", "\u1EC1"); //ề
        str = str.replaceAll("\u00EA\u0323", "\u1EC7"); //ệ
        str = str.replaceAll("\u00EA\u0303", "\u1EC5"); //ễ
        str = str.replaceAll("\u0079\u0309", "\u1EF7"); //ỷ
        str = str.replaceAll("\u0079\u0301", "\u00FD"); //ý
        str = str.replaceAll("\u0079\u0300", "\u1EF3"); //ỳ
        str = str.replaceAll("\u0079\u0323", "\u1EF5"); //ỵ
        str = str.replaceAll("\u0079\u0303", "\u1EF9"); //ỹ
        str = str.replaceAll("\u0075\u0309", "\u1EE7"); //ủ
        str = str.replaceAll("\u0075\u0301", "\u00FA"); //ú
        str = str.replaceAll("\u0075\u0300", "\u00F9"); //ù
        str = str.replaceAll("\u0075\u0323", "\u1EE5"); //ụ
        str = str.replaceAll("\u0075\u0303", "\u0169"); //ũ
        str = str.replaceAll("\u01B0\u0309", "\u1EED"); //ử
        str = str.replaceAll("\u01B0\u0301", "\u1EE9"); //ứ
        str = str.replaceAll("\u01B0\u0300", "\u1EEB"); //ừ
        str = str.replaceAll("\u01B0\u0323", "\u1EF1"); //ự
        str = str.replaceAll("\u01B0\u0303", "\u1EEF"); //ữ
        str = str.replaceAll("\u0069\u0309", "\u1EC9"); //ỉ
        str = str.replaceAll("\u0069\u0301", "\u00ED"); //í
        str = str.replaceAll("\u0069\u0300", "\u00EC"); //ì
        str = str.replaceAll("\u0069\u0323", "\u1ECB"); //ị
        str = str.replaceAll("\u0069\u0303", "\u0129"); //ĩ
        str = str.replaceAll("\u006F\u0309", "\u1ECF"); //ỏ
        str = str.replaceAll("\u006F\u0301", "\u00F3"); //ó
        str = str.replaceAll("\u006F\u0300", "\u00F2"); //ò
        str = str.replaceAll("\u006F\u0323", "\u1ECD"); //ọ
        str = str.replaceAll("\u006F\u0303", "\u00F5"); //õ
        str = str.replaceAll("\u01A1\u0309", "\u1EDF"); //ở
        str = str.replaceAll("\u01A1\u0301", "\u1EDB"); //ớ
        str = str.replaceAll("\u01A1\u0300", "\u1EDD"); //ờ
        str = str.replaceAll("\u01A1\u0323", "\u1EE3"); //ợ
        str = str.replaceAll("\u01A1\u0303", "\u1EE1"); //ỡ
        str = str.replaceAll("\u00F4\u0309", "\u1ED5"); //ổ
        str = str.replaceAll("\u00F4\u0301", "\u1ED1"); //ố
        str = str.replaceAll("\u00F4\u0300", "\u1ED3"); //ồ
        str = str.replaceAll("\u00F4\u0323", "\u1ED9"); //ộ
        str = str.replaceAll("\u00F4\u0303", "\u1ED7"); //ỗ
        str = str.replaceAll("\u0061\u0309", "\u1EA3"); //ả
        str = str.replaceAll("\u0061\u0301", "\u00E1"); //á
        str = str.replaceAll("\u0061\u0300", "\u00E0"); //à
        str = str.replaceAll("\u0061\u0323", "\u1EA1"); //ạ
        str = str.replaceAll("\u0061\u0303", "\u00E3"); //ã
        str = str.replaceAll("\u0103\u0309", "\u1EB3"); //ẳ
        str = str.replaceAll("\u0103\u0301", "\u1EAF"); //ắ
        str = str.replaceAll("\u0103\u0300", "\u1EB1"); //ằ
        str = str.replaceAll("\u0103\u0323", "\u1EB7"); //ặ
        str = str.replaceAll("\u0103\u0303", "\u1EB5"); //ẵ
        str = str.replaceAll("\u00E2\u0309", "\u1EA9"); //ẩ
        str = str.replaceAll("\u00E2\u0301", "\u1EA5"); //ấ
        str = str.replaceAll("\u00E2\u0300", "\u1EA7"); //ầ
        str = str.replaceAll("\u00E2\u0323", "\u1EAD"); //ậ
        str = str.replaceAll("\u00E2\u0303", "\u1EAB"); //ẫ
        str = str.replaceAll("\u0045\u0309", "\u1EBA"); //Ẻ
        str = str.replaceAll("\u0045\u0301", "\u00C9"); //É
        str = str.replaceAll("\u0045\u0300", "\u00C8"); //È
        str = str.replaceAll("\u0045\u0323", "\u1EB8"); //Ẹ
        str = str.replaceAll("\u0045\u0303", "\u1EBC"); //Ẽ
        str = str.replaceAll("\u00CA\u0309", "\u1EC2"); //Ể
        str = str.replaceAll("\u00CA\u0301", "\u1EBE"); //Ế
        str = str.replaceAll("\u00CA\u0300", "\u1EC0"); //Ề
        str = str.replaceAll("\u00CA\u0323", "\u1EC6"); //Ệ
        str = str.replaceAll("\u00CA\u0303", "\u1EC4"); //Ễ
        str = str.replaceAll("\u0059\u0309", "\u1EF6"); //Ỷ
        str = str.replaceAll("\u0059\u0301", "\u00DD"); //Ý
        str = str.replaceAll("\u0059\u0300", "\u1EF2"); //Ỳ
        str = str.replaceAll("\u0059\u0323", "\u1EF4"); //Ỵ
        str = str.replaceAll("\u0059\u0303", "\u1EF8"); //Ỹ
        str = str.replaceAll("\u0055\u0309", "\u1EE6"); //Ủ
        str = str.replaceAll("\u0055\u0301", "\u00DA"); //Ú
        str = str.replaceAll("\u0055\u0300", "\u00D9"); //Ù
        str = str.replaceAll("\u0055\u0323", "\u1EE4"); //Ụ
        str = str.replaceAll("\u0055\u0303", "\u0168"); //Ũ
        str = str.replaceAll("\u01AF\u0309", "\u1EEC"); //Ử
        str = str.replaceAll("\u01AF\u0301", "\u1EE8"); //Ứ
        str = str.replaceAll("\u01AF\u0300", "\u1EEA"); //Ừ
        str = str.replaceAll("\u01AF\u0323", "\u1EF0"); //Ự
        str = str.replaceAll("\u01AF\u0303", "\u1EEE"); //Ữ
        str = str.replaceAll("\u0049\u0309", "\u1EC8"); //Ỉ
        str = str.replaceAll("\u0049\u0301", "\u00CD"); //Í
        str = str.replaceAll("\u0049\u0300", "\u00CC"); //Ì
        str = str.replaceAll("\u0049\u0323", "\u1ECA"); //Ị
        str = str.replaceAll("\u0049\u0303", "\u0128"); //Ĩ
        str = str.replaceAll("\u004F\u0309", "\u1ECE"); //Ỏ
        str = str.replaceAll("\u004F\u0301", "\u00D3"); //Ó
        str = str.replaceAll("\u004F\u0300", "\u00D2"); //Ò
        str = str.replaceAll("\u004F\u0323", "\u1ECC"); //Ọ
        str = str.replaceAll("\u004F\u0303", "\u00D5"); //Õ
        str = str.replaceAll("\u01A0\u0309", "\u1EDE"); //Ở
        str = str.replaceAll("\u01A0\u0301", "\u1EDA"); //Ớ
        str = str.replaceAll("\u01A0\u0300", "\u1EDC"); //Ờ
        str = str.replaceAll("\u01A0\u0323", "\u1EE2"); //Ợ
        str = str.replaceAll("\u01A0\u0303", "\u1EE0"); //Ỡ
        str = str.replaceAll("\u00D4\u0309", "\u1ED4"); //Ổ
        str = str.replaceAll("\u00D4\u0301", "\u1ED0"); //Ố
        str = str.replaceAll("\u00D4\u0300", "\u1ED2"); //Ồ
        str = str.replaceAll("\u00D4\u0323", "\u1ED8"); //Ộ
        str = str.replaceAll("\u00D4\u0303", "\u1ED6"); //Ỗ
        str = str.replaceAll("\u0041\u0309", "\u1EA2"); //Ả
        str = str.replaceAll("\u0041\u0301", "\u00C1"); //Á
        str = str.replaceAll("\u0041\u0300", "\u00C0"); //À
        str = str.replaceAll("\u0041\u0323", "\u1EA0"); //Ạ
        str = str.replaceAll("\u0041\u0303", "\u00C3"); //Ã
        str = str.replaceAll("\u0102\u0309", "\u1EB2"); //Ẳ
        str = str.replaceAll("\u0102\u0301", "\u1EAE"); //Ắ
        str = str.replaceAll("\u0102\u0300", "\u1EB0"); //Ằ
        str = str.replaceAll("\u0102\u0323", "\u1EB6"); //Ặ
        str = str.replaceAll("\u0102\u0303", "\u1EB4"); //Ẵ
        str = str.replaceAll("\u00C2\u0309", "\u1EA8"); //Ẩ
        str = str.replaceAll("\u00C2\u0301", "\u1EA4"); //Ấ
        str = str.replaceAll("\u00C2\u0300", "\u1EA6"); //Ầ
        str = str.replaceAll("\u00C2\u0323", "\u1EAC"); //Ậ
        str = str.replaceAll("\u00C2\u0303", "\u1EAA"); //Ẫ
        return str;
    }

    /**
     * Transform a Vietnamese toneString to noToneString.
     *
     * @param toneString
     * @return
     */
    public static String castToNoTone(String toneString) {
        String result = toneString;
        for (Replacer r : replacerList) {
            result = result.replaceAll(r.regex, r.replacement);
            result = result.replaceAll(r.regex.toUpperCase(), r.replacement.toUpperCase());
        }
        return result;
    }

    private static class Replacer {

        public String regex;

        public String replacement;

        public Replacer(String regex, String replacement) {
            this.regex = regex;
            this.replacement = replacement;
        }
    }

    public static String trim(String value) {
        if (value == null)
            return null;
        int len = value.length();
        int start = 0;
        while (start < len) {
            char c = value.charAt(start);
            if (Character.isWhitespace(c) || Character.isSpaceChar(c)) {
                start++;
                continue;
            }
            break;
        }

        while (len > start) {
            char c = value.charAt(len - 1);
            if (Character.isWhitespace(c) || Character.isSpaceChar(c)) {
                len--;
                continue;
            }
            break;
        }

        return ((start > 0) || (len < value.length())) ? value.substring(start, len) : value;
    }

    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        return cs1.equals(cs2);
    }

    public static String getRandomString(int length) {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";

        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
        SecureRandom random = new SecureRandom();

        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            sb.append(rndChar);
        }

        return sb.toString();
    }

    public static String arrayConcat(Object[] a) {
        if (a == null) return "";

        if (a.length == 0) return "";

        int iMax = a.length - 1;
        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) return b.toString();
            b.append(", ");
        }
    }

    public static boolean isNumeric(String s) {
        Pattern p = Pattern.compile("^[0-9]*$");
        return p.matcher(s).find();
    }

    public static String getNormalFileName(String filename) {
        LocalDate now = LocalDate.now();
        return String.format("%s_%02d_%02d_%04d", filename, now.getDayOfMonth(), now.getMonthValue(), now.getYear());
    }

    public static List<String> splitStr(String input, String character) {
        List<String> result = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            result.add("");
            return result;
        }
        String[] splitCodes = input.split(character);
        for (String temp : splitCodes) {
            String add = trim(temp);
            if (add.length() > 0) {
                result.add(add);
            }
        }
        return result;
    }

    public static List<String> splitBySpace(String input) {
        return splitStr(input, SPACE);
    }

    public static List<String> splitByComma(String input) {
        return splitStr(input, String.valueOf(COMMA));
    }

    public static boolean equalsIgnoreCaseAndSpace(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }

        if (str1 == null || str2 == null) {
            return false;
        }

        str1 = str1.replaceAll("\\s+","");
        str2 = str2.replaceAll("\\s+","");
        return str1.equalsIgnoreCase(str2);
    }

    public static String formatStringForLikeQuery(String s) {
        if(s == null || s.isEmpty()) {
            return "%%";
        }
        return "%" + s + "%";
    }

}
