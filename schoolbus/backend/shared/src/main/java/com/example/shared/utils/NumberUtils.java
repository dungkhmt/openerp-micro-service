package com.example.shared.utils;

import java.math.BigDecimal;

public class NumberUtils {

    public static boolean isEqual(BigDecimal pNumber1, BigDecimal pNumber2) {
        if (pNumber1 == null) {
            return pNumber2 == null;
        }
        if (pNumber2 == null)
            return false;
        return pNumber1.compareTo(pNumber2) == 0;
    }

    public static boolean isEqual(Integer pNumber1, Integer pNumber2) {
        if (pNumber1 == null) {
            return pNumber2 == null;
        }
        if (pNumber2 == null)
            return false;
        return pNumber1.compareTo(pNumber2) == 0;
    }

    public static boolean isEqual(Long pNumber1, Long pNumber2) {
        if (pNumber1 == null) {
            return pNumber2 == null;
        }
        if (pNumber2 == null)
            return false;
        return pNumber1.compareTo(pNumber2) == 0;
    }
}