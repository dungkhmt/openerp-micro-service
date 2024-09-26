import React, { useState, useMemo } from "react";
import { Grid, List, ListSubheader, TextField, InputAdornment } from "@mui/material";
import TeacherListItem from "component/common/TeacherListItem";
import AssignedTeacherList from "./AssignTeacherList";
import SearchIcon from "@mui/icons-material/Search";

export default function ElementAddTeacher({ teacherList }) {
  const containsText = (text, searchText) =>
    text.toLowerCase().indexOf(searchText.toLowerCase()) > -1;
  const [searchTeacher, setSearchTeacher] = useState("");
  const displayedTeacherOptions = useMemo(
    () =>
      searchTeacher !== "" ? teacherList?.filter((option) =>
        containsText(option.teacherName, searchTeacher)
      ) : teacherList, [searchTeacher]
  );

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
          subheader={<ListSubheader>
            <TextField
              size="small"
              autoFocus
              placeholder="Type to search..."
              fullWidth
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
              onChange={(e) => setSearchTeacher(e.target.value)}
              onKeyDown={(e) => {
                if (e.key !== "Escape") {
                  e.stopPropagation();
                }
              }}
            />
          </ListSubheader>}
        >
          {displayedTeacherOptions?.map((item) => (
            <TeacherListItem key={item?.id} teacher={item} />
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
          <AssignedTeacherList />
        </List>
      </Grid>
    </Grid>
  );
}
