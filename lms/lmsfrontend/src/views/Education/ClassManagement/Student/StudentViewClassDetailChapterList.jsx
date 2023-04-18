import React from 'react';
import {Card, CardContent} from "@mui/material";
import ChapterListOfCourse
  from "../../../../component/education/classmanagement/student/classdetail/ChapterListOfCourse";

export default function StudentViewClassDetailChapterList(props) {
  const classId = props.classId;

  return (
    <Card>
      <CardContent>
        <ChapterListOfCourse classId={classId}/>
      </CardContent>
    </Card>
  );
}