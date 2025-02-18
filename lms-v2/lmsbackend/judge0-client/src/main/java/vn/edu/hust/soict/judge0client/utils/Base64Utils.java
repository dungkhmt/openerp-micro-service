package vn.edu.hust.soict.judge0client.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Base64;

public class Base64Utils {

    public static String decodeBase64(String base64Encoded) {
        if (StringUtils.isBlank(base64Encoded)) {
            return base64Encoded;
        }

        return new String(Base64.getDecoder().decode(base64Encoded.replaceAll("\\r\\n|\\r|\\n", "")));
    }

    public static String encodeBase64(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }

        return Base64.getEncoder().encodeToString(input.getBytes());
    }
}
