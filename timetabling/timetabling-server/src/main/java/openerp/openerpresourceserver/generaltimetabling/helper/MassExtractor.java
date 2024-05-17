package openerp.openerpresourceserver.generaltimetabling.helper;

import java.util.regex.Pattern;

public class MassExtractor {
    public static int extract(String mass) {
        String a = mass.replaceAll("\\s", "");
        if (Pattern.compile("\\d+\\(\\d+(?:-\\d+){3}\\)").matcher(a.trim()).find()) {
            String numbersString = a.trim().substring(2, a.indexOf(')'));
            String[] numbersArray = numbersString.split("-");
            return Integer.parseInt(numbersArray[0]) + Integer.parseInt(numbersArray[1]);
        } else {
            System.out.println("Pattern doesn't match\n" + a.trim());
            return 0;
        }
    }


    public static int extractExercisePeriods(String mass) {
        String a = mass.replaceAll("\\s", "");
        if (Pattern.compile("\\d+\\(\\d+(?:-\\d+){3}\\)").matcher(a.trim()).find()) {
            String numbersString = a.trim().substring(2, a.indexOf(')'));
            String[] numbersArray = numbersString.split("-");
            return Integer.parseInt(numbersArray[1]);
        } else {
            System.out.println("Pattern doesn't match\n" + a.trim());
            return 0;
        }
    }
}
