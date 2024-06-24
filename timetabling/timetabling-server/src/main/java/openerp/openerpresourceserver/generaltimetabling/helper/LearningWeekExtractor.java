package openerp.openerpresourceserver.generaltimetabling.helper;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Log4j2
public class LearningWeekExtractor {
    public static List<Integer> extract(String weekString) {
        List<Integer> weekIntList = Arrays.stream(weekString.split("-")).map(Integer::parseInt).toList();
        if (weekIntList.get(0) != null && Objects.equals(weekIntList.get(0), weekIntList.get(1))) {
            return List.of(weekIntList.get(0));
        } else {
            return IntStream.rangeClosed(weekIntList.get(0), weekIntList.get(1))
                    .boxed()
                    .collect(Collectors.toList());
        }
    }

    public static List<Integer> extractArray(String weekArrayString) {
        List<String> weekStrings = Arrays.stream(weekArrayString.split(",")).toList();
        List<Integer> weeksArray = new ArrayList<>();
        for(String weekString : weekStrings) {
            List<Integer> weekIntList = Arrays.stream(weekString.split("-")).map(Integer::parseInt).toList();
            if (weekIntList.get(0) != null && weekIntList.get(0).equals(weekIntList.get(1))) {
                weeksArray.add(weekIntList.get(0));
            } else {
                weeksArray.addAll(IntStream.rangeClosed(weekIntList.get(0), weekIntList.get(1))
                        .boxed()
                        .toList());
            }
        }
        return weeksArray;
    }
}
