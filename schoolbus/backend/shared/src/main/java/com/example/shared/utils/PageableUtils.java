package com.example.shared.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtils {

    public static Pageable generate(Integer page, Integer size, String sort) {
        return generate(page, size, sort, null);
    }

    public static Pageable generate(Integer page, Integer size, String reqSort, String defaultSort) {
        if (page == null) {
            page = 0;
        }
        if (size == null || size == 0) {
            size = 20;
        }

        var sort = defaultSort;
        if (reqSort != null) {
            sort = reqSort;
        }

        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }

        sort = sort.split(",")[0];

        if (sort.charAt(0) == '-') {
            return PageRequest.of(page, size, Sort.by(snakeToCamel(sort.substring(1))).descending());
        }
        return PageRequest.of(page, size, Sort.by(snakeToCamel(sort)).ascending());
    }

    public static Pageable generateForNativeQuery(Integer page, Integer size, String reqSort, String defaultSort){
        if (page == null) {
            page = 0;
        }
        if (size == null || size == 0) {
            size = 20;
        }

        var sort = defaultSort;
        if (reqSort != null) {
            sort = reqSort;
        }

        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }

        sort = sort.split(",")[0];

        if (sort.charAt(0) == '-') {
            return PageRequest.of(page, size, Sort.by(camelToSnake(sort.substring(1))).descending());
        }
        return PageRequest.of(page, size, Sort.by(camelToSnake(sort)).ascending());
    }

    static String snakeToCamel(String str)
    {
        // Convert to StringBuilder
        StringBuilder builder = new StringBuilder(str);

        // Traverse the string character by
        // character and remove underscore
        // and capitalize next letter
        for (int i = 0; i < builder.length(); i++) {

            // Check char is underscore
            if (builder.charAt(i) == '_') {

                builder.deleteCharAt(i);
                builder.replace(
                        i, i + 1,
                        String.valueOf(
                                Character.toUpperCase(
                                        builder.charAt(i))));
            }
        }

        // Return in String type
        return builder.toString();
    }

    static String camelToSnake(String str)
    {
        // Convert to StringBuilder
        StringBuilder builder = new StringBuilder(str);

        // Traverse the string character by
        // character and remove underscore
        // and capitalize next letter
        for (int i = 0; i < builder.length(); i++) {

            // Check char is underscore
            if (Character.isUpperCase(builder.charAt(i))) {

                builder.replace(
                        i, i + 1,
                        String.valueOf(
                                Character.toLowerCase(
                                        builder.charAt(i))));
                builder.insert(i, "_");
            }
        }

        // Return in String type
        return builder.toString();
    }

    public static int calculateTotalPages(Number size, int pageSize) {
        return (int) Math.ceil(size.doubleValue() / pageSize);
    }

}