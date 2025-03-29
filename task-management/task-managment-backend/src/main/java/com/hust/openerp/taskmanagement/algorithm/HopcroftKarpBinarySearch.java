package com.hust.openerp.taskmanagement.algorithm;

import java.util.*;

public class HopcroftKarpBinarySearch {
    private final int numUsers;
    private final int numSessions;
    private final List<List<Integer>> userChoices;
    private final UserSessionMapper mapper;
    private int[] lastPairU;

    public HopcroftKarpBinarySearch(Map<String, List<UUID>> userChoicesMap) {
        this.mapper = new UserSessionMapper(userChoicesMap);
        this.numUsers = mapper.getNumUsers();
        this.numSessions = mapper.getNumSessions();
        this.userChoices = mapper.getUserChoices();
    }

    public int findMinExtraCapacity() {
        int low = 0;
        int high = numUsers;
        int result = high;
        while (low <= high) {
            int mid = (low + high) / 2;
            int matching = runHopcroftKarpForCapacity(mid);
            if (matching == numUsers) {
                result = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return result;
    }

    private int runHopcroftKarpForCapacity(int extraCapacity) {
        int expandedRightCount = numSessions * (1 + extraCapacity);
        @SuppressWarnings("unchecked")
        List<Integer>[] expandedGraph = new List[numUsers];
        for (int i = 0; i < numUsers; i++) {
            expandedGraph[i] = new ArrayList<>();
            List<Integer> preferredSessions = userChoices.get(i);
            for (Integer session : preferredSessions) {
                for (int copy = 0; copy < (1 + extraCapacity); copy++) {
                    int expandedSessionIndex = mapper.getExpandedSessionIndex(session, copy, extraCapacity);
                    expandedGraph[i].add(expandedSessionIndex);
                }
            }
        }

        HopcroftKarp hk = new HopcroftKarp(numUsers, expandedRightCount, expandedGraph);
        int matching = hk.run();
        if (matching == numUsers) {
            lastPairU = hk.getPairU().clone();
        }
        return matching;
    }

    public Map<String, UUID> getAssignment() {
        int minExtraCapacity = findMinExtraCapacity();
        int matching = runHopcroftKarpForCapacity(minExtraCapacity);
        if (matching != numUsers) {
            throw new IllegalStateException("Could not assign all users");
        }
        Map<String, UUID> assignment = new HashMap<>();
        for (int u = 0; u < numUsers; u++) {
            if (lastPairU[u] == -1) {
                throw new IllegalStateException("User " + mapper.getUserFromIndex(u) + " not assigned");
            }
            int expandedSessionIdx = lastPairU[u];
            int originalSessionIdx = mapper.getOriginalSessionIndex(expandedSessionIdx, minExtraCapacity);
            String userId = mapper.getUserFromIndex(u);
            UUID sessionId = mapper.getSessionFromIndex(originalSessionIdx);
            assignment.put(userId, sessionId);
        }
        return assignment;
    }
}