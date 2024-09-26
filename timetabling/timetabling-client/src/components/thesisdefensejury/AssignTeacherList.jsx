import React from "react";
import AssignedTeacherListItem from './AssignedTeacherListItem'
import { useAssignTeacherRole } from "action";
export default function AssignedTeacherList({ assignedTeacher, handleAssignRole }) {
    // const assignedTeacher = useAssignTeacherRole(
    //     (state) => state.assignedTeacher
    // );
    return (
        <>
            {assignedTeacher?.map((item) => (
                <AssignedTeacherListItem assignedTeacher={item} handleAssignRole />
            ))}
        </>
    );
}
