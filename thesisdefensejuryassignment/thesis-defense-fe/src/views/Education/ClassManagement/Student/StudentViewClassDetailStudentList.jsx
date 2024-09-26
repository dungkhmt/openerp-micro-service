import React from 'react';
import {Card, CardContent} from "@mui/material";
import StudentListOfClass from "../../../../component/education/classmanagement/student/classdetail/StudentListOfClass";

export default function StudentViewClassDetailStudentList(props) {
  const classId = props.classId;

  return (
    <Card>
      <CardContent>
        <StudentListOfClass classId={classId}/>
      </CardContent>
    </Card>
  );
}