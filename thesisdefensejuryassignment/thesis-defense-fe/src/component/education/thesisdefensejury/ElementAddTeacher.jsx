import React, { useState } from "react";
import { Grid, List, ListSubheader } from "@mui/material";
import TeacherListItem from "component/common/TeacherListItem";
import { useAssignTeacherThesis } from "context/AssignTeacherThesisContext";
import AssignedTeacherListItem from "component/common/AssignedTeacherListItem";

export default function ElementAddTeacher({ teacherList, register }) {
  const { assignedTeacher, handleSelectTeacher } = useAssignTeacherThesis();
  return (
    <Grid container spacing={2}>
      <Grid item xs={6}>
        <List
          dense
          sx={{
            width: "100%",
            bgcolor: "background.paper",
            position: "relative",
            overflow: "auto",
            maxHeight: 400,
          }}
          subheader={<ListSubheader>Danh sách giáo viên</ListSubheader>}
        >
          {teacherList?.map((item) => (
            <TeacherListItem
              key={item?.id}
              assignedTeacher={assignedTeacher}
              handleSelectTeacher={handleSelectTeacher}
              teacher={item}
            />
          ))}
        </List>
      </Grid>
      <Grid item xs={6}>
        <List
          dense
          sx={{
            width: "100%",
            bgcolor: "background.paper",
            position: "relative",
            overflow: "auto",
            maxHeight: 400,
          }}
          subheader={
            <ListSubheader>Danh sách giáo viên hội đồng</ListSubheader>
          }
        >
          {assignedTeacher.map((item) => (
            <AssignedTeacherListItem
              assignedTeacher={item}
              register={register}
            />
          ))}
        </List>
      </Grid>
    </Grid>
  );
}
