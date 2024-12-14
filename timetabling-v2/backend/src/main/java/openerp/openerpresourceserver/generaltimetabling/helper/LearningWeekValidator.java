package openerp.openerpresourceserver.generaltimetabling.helper;

import openerp.openerpresourceserver.generaltimetabling.model.entity.AcademicWeek;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LearningWeekValidator {
    public static boolean isCorrectFormat(String week) {
        String regex = "\\d+-\\d+";

        // Compile the regular expression
        Pattern patternObj = Pattern.compile(regex);

        // Match the pattern against the input
        Matcher matcher = patternObj.matcher(week);

        // If the pattern matches
        if (matcher.matches()) {
            // Extract the numbers
            String[] parts = week.split("-");
            int a = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);
            // Return true if a <= b, false otherwise
            return a <= b;
        } else {
            // If the pattern doesn't match, return false
            return false;
        }
    }

    public static boolean validate(String weekString, List<AcademicWeek> repoWeeks) {
        List<Integer> updateWeeks = LearningWeekExtractor.extractArray(weekString);
        List<Integer> existedWeeks = repoWeeks.stream().map(AcademicWeek::getWeekIndex).toList();
        for (int i = 0; i < updateWeeks.size(); i++) {
            if (!existedWeeks.contains(updateWeeks.get(i))) {
                return false;
            }
        }
        return true;
    }
}
