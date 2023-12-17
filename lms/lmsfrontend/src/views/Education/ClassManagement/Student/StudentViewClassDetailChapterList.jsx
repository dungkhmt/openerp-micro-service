import React from "react";
import { Card, CardContent } from "@mui/material";
import ChapterListOfClass from "../../../../component/education/classmanagement/student/classdetail/ChapterListOfClass";

export default function StudentViewClassDetailChapterList(props) {
  const classId = props.classId;

  return (
    <Card>
      <CardContent>
        <ChapterListOfClass classId={classId} />
      </CardContent>
    </Card>
  );
}
