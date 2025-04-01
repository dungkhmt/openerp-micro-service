package com.hust.openerp.taskmanagement.algorithm;

import java.util.*;

public class UserSessionMapper {
    private final int numUsers;
    private final int numSessions;
    private final List<List<Integer>> userChoices;
    private final Map<String, Integer> userToIndex;
    private final Map<UUID, Integer> sessionToIndex;
    private final Map<Integer, String> indexToUser;
    private final Map<Integer, UUID> indexToSession;

    public UserSessionMapper(Map<String, List<UUID>> userChoicesMap) {
        if (userChoicesMap == null || userChoicesMap.isEmpty()) {
            throw new IllegalArgumentException("User choices map cannot be null or empty");
        }

        userToIndex = new HashMap<>();
        sessionToIndex = new HashMap<>();
        indexToUser = new HashMap<>();
        indexToSession = new HashMap<>();

        int userIdx = 0;
        for (String userId : userChoicesMap.keySet()) {
            if (userChoicesMap.get(userId) == null || userChoicesMap.get(userId).isEmpty()) {
                throw new IllegalArgumentException("User " + userId + " has no preferences");
            }
            userToIndex.put(userId, userIdx);
            indexToUser.put(userIdx, userId);
            userIdx++;
        }
        numUsers = userIdx;

        int sessionIdx = 0;
        Set<UUID> allSessions = new HashSet<>();
        for (List<UUID> sessions : userChoicesMap.values()) {
            allSessions.addAll(sessions);
        }
        if (allSessions.isEmpty()) {
            throw new IllegalArgumentException("No sessions provided in preferences");
        }
        for (UUID sessionId : allSessions) {
            sessionToIndex.put(sessionId, sessionIdx);
            indexToSession.put(sessionIdx, sessionId);
            sessionIdx++;
        }
        numSessions = sessionIdx;

        userChoices = new ArrayList<>(numUsers);
        for (int i = 0; i < numUsers; i++) {
            userChoices.add(new ArrayList<>());
        }
        for (Map.Entry<String, List<UUID>> entry : userChoicesMap.entrySet()) {
            int u = userToIndex.get(entry.getKey());
            for (UUID sessionId : entry.getValue()) {
                int s = sessionToIndex.get(sessionId);
                userChoices.get(u).add(s);
            }
        }
    }

    public int getNumUsers() { return numUsers; }
    public int getNumSessions() { return numSessions; }
    public List<List<Integer>> getUserChoices() { return userChoices; }
    public String getUserFromIndex(int index) { return indexToUser.get(index); }
    public UUID getSessionFromIndex(int index) { return indexToSession.get(index); }
    public int getExpandedSessionIndex(int sessionIndex, int copy, int extraCapacity) {
        return sessionIndex * (1 + extraCapacity) + copy;
    }
    public int getOriginalSessionIndex(int expandedIndex, int extraCapacity) {
        return expandedIndex / (1 + extraCapacity);
    }
}