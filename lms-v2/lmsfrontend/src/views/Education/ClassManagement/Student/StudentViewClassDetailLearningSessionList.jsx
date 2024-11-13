import React from 'react';
import {Card, CardContent} from "@mui/material";
import LearningSessionListOfClass
  from "../../../../component/education/classmanagement/teacher/LearningSessionListOfClass";

export default function StudentViewClassDetailLearningSessionList(props) {
  const classId = props.classId;

  return (
    <Card>
      <CardContent>
        <LearningSessionListOfClass classId={classId}/>
      </CardContent>
    </Card>
  );
}