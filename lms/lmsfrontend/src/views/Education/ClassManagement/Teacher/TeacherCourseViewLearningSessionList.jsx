import React, { useEffect, useState } from "react";
import { request } from "../../../../api";
import { Button, Link, Tooltip } from "@material-ui/core/";
import AddIcon from "@material-ui/icons/Add";
import MaterialTable from "material-table";
import { Link as RouterLink, useHistory } from "react-router-dom";
import TeacherCreateSessionForm from "./TeacherCreateSessionForm";
import { Card, CardContent } from "@mui/material";
import LearningSessionListOfCourse from "component/education/course/teacher/LearningSessionListOfCourse";

export default function TeacherCourseViewLearningSessionList(props) {
  const courseId = props.courseId;
  const [sessions, setSessions] = useState([]);
  const history = useHistory();
  const [open, setOpen] = useState(false);

  function handleAddSession() {
    setOpen(true);
  }
  return (
    <div>
      <Card>
        <CardContent>
          <LearningSessionListOfCourse
            courseId={courseId}
            enableCreateSession
            viewDetailOnRowClick
          />
        </CardContent>
      </Card>
      {/* <TeacherCreateSessionForm
        open={open}
        setOpen={setOpen}
        classId={classId}
      /> */}
    </div>
  );
}
