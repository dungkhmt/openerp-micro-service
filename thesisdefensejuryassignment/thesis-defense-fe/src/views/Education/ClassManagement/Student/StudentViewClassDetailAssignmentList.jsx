import React from 'react';
import {Card, CardContent} from "@mui/material";
import AssignmentListOfClass from "../../../../component/education/classmanagement/teacher/AssignmentListOfClass";

export default function StudentViewClassDetailAssignmentList(props) {
  const classId = props.classId;

  return (
    <Card>
      <CardContent>
        <AssignmentListOfClass classId={classId}
                               userRole="student"/>
      </CardContent>
    </Card>
  );
}