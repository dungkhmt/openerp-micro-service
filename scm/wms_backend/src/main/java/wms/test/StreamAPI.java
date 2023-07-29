package wms.test;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class StreamAPI {
    public static void main(String[] args) {
        List < Integer > nums = Arrays.asList(10, 23, 22, 23, 24, 24, 33, 15, 26, 15);
        List <Integer> distinctNums = nums.stream().distinct().collect(Collectors.toList());
        System.out.println(distinctNums);
    }
}
