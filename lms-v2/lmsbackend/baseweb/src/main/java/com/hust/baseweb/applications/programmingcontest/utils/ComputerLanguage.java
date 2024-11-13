package com.hust.baseweb.applications.programmingcontest.utils;

public class ComputerLanguage {

    public enum Languages {
        C,
        CPP,
        CPP11,
        CPP14,
        CPP17,
        PYTHON3,
        JAVA
    }

    public static String mapLanguageToExtension(ComputerLanguage.Languages language) {
        switch (language) {
            case C:
                return ".c";
            case PYTHON3:
                return ".py";
            case JAVA:
                return ".java";
            default:
                return ".cpp";
        }
    }
}
