package com.hust.openerp.taskmanagement.algorithm;

import java.util.Map;
import java.util.UUID;

public interface AssignmentStrategy{
    int findMinExtraCapacity();
    Map<String, UUID> getAssignment();
}
