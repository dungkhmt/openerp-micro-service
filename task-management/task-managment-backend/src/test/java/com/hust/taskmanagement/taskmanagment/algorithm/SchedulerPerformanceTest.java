package com.hust.taskmanagement.taskmanagment.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.openerp.taskmanagement.algorithm.HopcroftKarpBinarySearch;
import com.hust.openerp.taskmanagement.algorithm.ILP;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SchedulerPerformanceTest {
    private static List<TestCase> testCases;
    private static final int ITERATIONS = 10; // Number of runs for averaging

    @BeforeAll
    public static void loadTestCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = SchedulerPerformanceTest.class.getClassLoader()
            .getResourceAsStream("test-cases.json");
        if (is == null) {
            throw new IllegalStateException("Could not find test-cases.json in resources");
        }
        testCases = Arrays.asList(mapper.readValue(is, TestCase[].class));
    }

    private static Stream<TestCase> provideTestCases() {
        return testCases.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testHopcroftKarp(TestCase testCase) {
        Map<String, List<UUID>> userChoices = convertToUUID(testCase.getUserChoicesRaw());
        HopcroftKarpBinarySearch hkbs = new HopcroftKarpBinarySearch(userChoices);

        double totalTime = 0;
        Map<String, UUID> assignment = null;
        int minExtraCapacity = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long startTime = System.nanoTime();
            minExtraCapacity = hkbs.findMinExtraCapacity();
            assignment = hkbs.getAssignment();
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime) / 1_000_000.0;
        }
        double avgTimeMs = totalTime / ITERATIONS;
        int maxLoad = 1 + minExtraCapacity;

        System.out.printf("Hopcroft-Karp [%s] Avg Time: %.2f ms, Max Load: %d, Assignment: %s%n",
            testCase.getName(), avgTimeMs, maxLoad, assignment);

        assertEquals(userChoices.size(), assignment.size(), "All users should be assigned");
        int computedMaxLoad = computeMaxLoad(assignment);
        assertEquals(maxLoad, computedMaxLoad, "Max load should match calculated capacity");
        verifyPreferences(assignment, userChoices);
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testILP(TestCase testCase) {
        Map<String, List<UUID>> userChoices = convertToUUID(testCase.getUserChoicesRaw());
        ILP ilp = new ILP(userChoices);

        double totalTime = 0;
        ILP.ILPResult result = null;
        for (int i = 0; i < ITERATIONS; i++) {
            long startTime = System.nanoTime();
            result = ilp.schedule();
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime) / 1_000_000.0;
        }
        double avgTimeMs = totalTime / ITERATIONS;

        System.out.printf("ILP [%s] Avg Time: %.2f ms, Max Load: %d, Assignment: %s%n",
            testCase.getName(), avgTimeMs, result.getMinimalMaxLoad(), result.getAssignment());

        assertEquals(userChoices.size(), result.getAssignment().size(), "All users should be assigned");
        int computedMaxLoad = computeMaxLoad(result.getAssignment());
        assertEquals(result.getMinimalMaxLoad(), computedMaxLoad, "Max load should match ILP result");
        verifyPreferences(result.getAssignment(), userChoices);
    }

    private Map<String, List<UUID>> convertToUUID(Map<String, List<String>> rawChoices) {
        Map<String, UUID> slotToUUID = new HashMap<>();
        Map<String, List<UUID>> converted = new HashMap<>();

        for (List<String> slots : rawChoices.values()) {
            for (String slot : slots) {
                slotToUUID.computeIfAbsent(slot, k -> UUID.randomUUID());
            }
        }

        for (Map.Entry<String, List<String>> entry : rawChoices.entrySet()) {
            List<UUID> uuidSlots = entry.getValue().stream()
                .map(slotToUUID::get)
                .collect(Collectors.toList());
            converted.put(entry.getKey(), uuidSlots);
        }

        return converted;
    }

    private int computeMaxLoad(Map<String, UUID> assignment) {
        return assignment.values().stream()
            .collect(Collectors.groupingBy(uuid -> uuid, Collectors.counting()))
            .values().stream().mapToInt(Long::intValue).max().orElse(0);
    }

    private void verifyPreferences(Map<String, UUID> assignment, Map<String, List<UUID>> userChoices) {
        for (Map.Entry<String, UUID> entry : assignment.entrySet()) {
            String user = entry.getKey();
            UUID assignedSlot = entry.getValue();
            List<UUID> preferredSlots = userChoices.get(user);
            assertTrue(preferredSlots.contains(assignedSlot),
                "User " + user + " assigned to non-preferred slot " + assignedSlot);
        }
    }
}