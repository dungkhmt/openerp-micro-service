import React from "react";
import {
  ListItem,
  ListItemText,
  Divider,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from "@mui/material";
const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;

const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 180,
    },
  },
};
const ROLES = [
  {
    id: 2,
    role: "President",
    name: "Chủ tịch",
  },
  {
    id: 3,
    role: "Secretary",
    name: "Thư ký",
  },
  {
    id: 4,
    role: "Commissioner",
    name: "Ủy viên",
  },
];
export default function AssignTeacherListItem({ assignedTeacher, register }) {
  return (
    <React.Fragment>
      <ListItem
        key={assignedTeacher?.id}
        alignItems="flex-start"
        sx={{ paddingBottom: 3, paddingTop: 1 }}
      >
        <ListItemText color="text.primary">
          {assignedTeacher?.teacherName}
        </ListItemText>
        <FormControl sx={{ width: MenuProps.PaperProps.style.width }} required>
          <InputLabel id={`role-${assignedTeacher?.id}`}>Role</InputLabel>
          <Select
            MenuProps={MenuProps}
            label="Role"
            name="role"
            {...register(`${assignedTeacher?.teacherName}`)}
          >
            {ROLES.map((item) => (
              <MenuItem key={item.id} value={item.id}>
                {item.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </ListItem>
      <Divider />
    </React.Fragment>
  );
}
