package wms.utils;

/**
 * Chuan hoa charset CP1258 ve UTF8.
 *
 * @author huyennv
 * @version 1.0
 * @since 1.0
 */
public class CharsetConverter {

    private static char[] MARK = new char[]{
            '̀', '́', '̃', '̉', '̣', 'x'
    };
    private static char[] VOWEL = new char[]{
            'a',
            'ă',
            'â',
            'e',
            'ê',
            'i',
            'o',
            'ô',
            'ơ',
            'u',
            'ư',
            'y',
            'A',
            'Ă',
            'Â',
            'E',
            'Ê',
            'I',
            'O',
            'Ô',
            'Ơ',
            'U',
            'Ư',
            'Y'
    };
    private static char[] CP1258_TO_UTF8 = new char[]{
            'à', 'á', 'ã', 'ả', 'ạ',
            'ằ', 'ắ', 'ẵ', 'ẳ', 'ặ',
            'ầ', 'ấ', 'ẫ', 'ẩ', 'ậ',
            'è', 'é', 'ẽ', 'ẻ', 'ẹ',
            'ề', 'ế', 'ễ', 'ể', 'ệ',
            'ì', 'í', 'ĩ', 'ỉ', 'ị',
            'ò', 'ó', 'õ', 'ỏ', 'ọ',
            'ồ', 'ố', 'ỗ', 'ổ', 'ộ',
            'ờ', 'ớ', 'ỡ', 'ở', 'ợ',
            'ù', 'ú', 'ũ', 'ủ', 'ụ',
            'ừ', 'ứ', 'ữ', 'ử', 'ự',
            'ỳ', 'ý', 'ỹ', 'ỷ', 'ỵ',
            'À', 'Á', 'Ã', 'Ả', 'Ạ',
            'Ằ', 'Ắ', 'Ẵ', 'Ẳ', 'Ặ',
            'Ầ', 'Ấ', 'Ẫ', 'Ẩ', 'Ậ',
            'È', 'É', 'Ẽ', 'Ẻ', 'Ẹ',
            'Ề', 'Ế', 'Ễ', 'Ể', 'Ệ',
            'Ì', 'Í', 'Ĩ', 'Ỉ', 'Ị',
            'Ò', 'Ó', 'Õ', 'Ỏ', 'Ọ',
            'Ồ', 'Ố', 'Ỗ', 'Ổ', 'Ộ',
            'Ờ', 'Ớ', 'Ỡ', 'Ở', 'Ợ',
            'Ù', 'Ú', 'Ũ', 'Ủ', 'Ụ',
            'Ừ', 'Ứ', 'Ữ', 'Ử', 'Ự',
            'Ỳ', 'Ý', 'Ỹ', 'Ỷ', 'Ỵ'
    };

    public static char[] getMarkCharacters() {
        char[] a = new char[MARK.length - 1];
        System.arraycopy(MARK, 0, a, 0, a.length);
        return a;
    }

    public static String convertCp1258ToUTF8(String input) {
        int i = 1;
        while (i < input.length()) {
            char currentChar = input.charAt(i);
            int markIndex = MARK.length - 2;
            while ((markIndex >= 0) && (MARK[markIndex] != currentChar)) {
                markIndex--;
            }
            if (markIndex >= 0) {
                char previousChar = input.charAt(i - 1);
                for (int vowelIndex = 0; vowelIndex < VOWEL.length; vowelIndex++) {
                    if (previousChar == VOWEL[vowelIndex]) {
                        input = input.substring(0, i - 1) + CP1258_TO_UTF8[vowelIndex * (MARK.length - 1) + markIndex] + input.substring(i + 1);
                        break;
                    }
                }
            }
            i++;
        }
        return input;
    }
}
