import React from "react";
import {
  ListItem,
  ListItemText,
  Checkbox,
  Typography,
  ListItemButton,
  ListItemIcon,
  Divider,
  Stack,
  Box,
} from "@mui/material";
import KeywordChip from "./KeywordChip";
export default function TeacherListItem({
  assignedTeacher,
  handleSelectTeacher,
  teacher,
}) {
  return (
    <>
      <ListItem key={teacher?.id} alignItems="flex-start" disablePadding>
        <ListItemButton
          role={undefined}
          onClick={() => {
            handleSelectTeacher(teacher);
          }}
        >
          <ListItemIcon>
            <Checkbox
              edge="start"
              disableRipple
              checked={
                assignedTeacher.find((item) => item?.id === teacher?.id) !==
                undefined
              }
              tabIndex={-1}
            />
          </ListItemIcon>
          <Stack spacing={1}>
            <ListItemText
              id={`teacher-name-${teacher?.teacherName}`}
              primary={teacher?.teacherName}
              color="text.primary"
            />
            <Typography component="span" variant="body2" color={"GrayText"}>
              {teacher?.id}
            </Typography>
            <Box>
              {teacher?.academicKeywordList.map((item) => (
                <KeywordChip key={item.keyword} keyword={item.keyword} />
              ))}
            </Box>
          </Stack>
        </ListItemButton>
      </ListItem>
      <Divider />
    </>
  );
}
