/**
 * Utility functions for checking conflicts in exam timetable assignments
 */

/**
 * Checks for conflicts in room and time assignments
 * 
 * @param {Array} classesData - List of class data objects
 * @param {Object} assignmentChanges - Key-value pairs of class ID to changed assignments
 * @returns {Array} - List of detected conflicts
 */
export const checkForConflicts = (classesData, assignmentChanges) => {
  const conflicts = [];
  const assignmentMap = new Map();
  
  // First, create a map of all assignments including the changes
  const updatedClassesData = classesData.map(classItem => {
    const classId = classItem.id;
    const changes = assignmentChanges[classId] || {};
    
    return {
      ...classItem,
      // Override properties with any changes
      roomId: changes.roomId !== undefined ? changes.roomId : classItem.roomId,
      weekId: changes.weekId !== undefined ? changes.weekId : classItem.weekId,
      dateId: changes.dateId !== undefined ? changes.dateId : classItem.dateId,
      slotId: changes.slotId !== undefined ? changes.slotId : classItem.slotId
    };
  });
  
  // Now check for conflicts
  updatedClassesData.forEach(classItem => {
    const roomId = classItem.roomId;
    const weekId = classItem.weekId;
    const dateId = classItem.dateId;
    const slotId = classItem.slotId;
    
    // Skip if any required assignment is missing
    if (!roomId || !weekId || !dateId || !slotId) {
      return;
    }
    
    // Create a unique key for this room-time combination
    const assignmentKey = `${roomId}_${weekId}_${dateId}_${slotId}`;
    
    // Check if this combination already exists
    if (assignmentMap.has(assignmentKey)) {
      // We found a conflict
      const existingClass = assignmentMap.get(assignmentKey);
      conflicts.push({
        class1: existingClass,
        class2: classItem
      });
    } else {
      // No conflict, add to our map
      assignmentMap.set(assignmentKey, classItem);
    }
  });
  
  return conflicts;
};

/**
 * Groups conflicts by type for better display
 * 
 * @param {Array} conflicts - List of detected conflicts
 * @returns {Object} - Object with conflicts grouped by type
 */
export const groupConflictsByType = (conflicts) => {
  const roomConflicts = conflicts.filter(conflict => 
    conflict.class1.roomId === conflict.class2.roomId &&
    conflict.class1.weekId === conflict.class2.weekId &&
    conflict.class1.dateId === conflict.class2.dateId &&
    conflict.class1.slotId === conflict.class2.slotId
  );
  
  return {
    roomConflicts,
    totalConflicts: conflicts.length
  };
};

/**
 * Format a conflict for display
 * 
 * @param {Object} conflict - A conflict object with class1 and class2 properties
 * @returns {Object} - Formatted conflict information
 */
export const formatConflict = (conflict) => {
  return {
    type: 'room_time',
    classIds: [conflict.class1.examClassId, conflict.class2.examClassId],
    classNames: [conflict.class1.className, conflict.class2.className],
    roomId: conflict.class1.roomId,
    time: {
      weekId: conflict.class1.weekId,
      dateId: conflict.class1.dateId,
      slotId: conflict.class1.slotId
    }
  };
};

/**
 * Check if a proposed assignment would create conflicts
 * 
 * @param {string} classId - ID of the class being changed
 * @param {Object} newAssignment - New assignment values
 * @param {Array} classesData - List of class data objects
 * @param {Object} currentAssignments - Current assignment state
 * @returns {Array} - List of conflicts that would be created
 */
export const checkProposedAssignment = (classId, newAssignment, classesData, currentAssignments) => {
  // Create a temporary assignments object with the proposed change
  const tempAssignments = {
    ...currentAssignments,
    [classId]: newAssignment
  };
  
  return checkForConflicts(classesData, tempAssignments);
};
