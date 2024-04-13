import React from "react";
import AssignedTeacherListItem from "component/common/AssignedTeacherListItem";
import { useAssignTeacherRole } from "action";
export default function AssignedTeacherList() {
  const assignedTeacher = useAssignTeacherRole(
    (state) => state.assignedTeacher
  );
  return (
    <>
      {assignedTeacher?.map((item) => (
        <AssignedTeacherListItem assignedTeacher={item} />
      ))}
    </>
  );
}
