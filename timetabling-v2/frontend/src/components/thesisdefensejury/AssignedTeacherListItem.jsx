import React, { useEffect, useState } from "react";
import {
    ListItem,
    ListItemText,
    Divider,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    IconButton,
} from "@mui/material";
import ClearIcon from "@mui/icons-material/Clear";
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
]; // Các role của giáo viên hội đồng

/**
 * Component các giáo viên được chọn
 * 
 */
export default function AssignTeacherListItem({ assignedTeacher, handleSelectTeacher, handleAssignRole }) {
    const [role, setRole] = useState("");
    const handleChange = (e) => {
        setRole(e.target.value);
        handleAssignRole(e);
    };
    useEffect(() => {
        if (assignedTeacher?.role) {
            return setRole(assignedTeacher?.role)
        }
    }, [assignedTeacher])
    return (
        <React.Fragment>
            <ListItem
                key={assignedTeacher?.id}
                alignItems="flex-start"
                sx={{ paddingBottom: 3, paddingTop: 1 }}
            >
                <ListItemText color="text.primary" sx={{ padding: "8px 8px 8px 8px" }}>
                    {assignedTeacher?.teacherName}
                </ListItemText>
                <FormControl sx={{ width: MenuProps.PaperProps.style.width }} required>
                    <InputLabel id={`role-${assignedTeacher?.id}`}>Role</InputLabel>
                    <Select
                        labelId={`role-${assignedTeacher?.id}`}
                        MenuProps={MenuProps}
                        label="Role"
                        name={`${assignedTeacher?.id}`}
                        value={role}
                        onChange={handleChange}
                    >
                        {ROLES.map((item) => (
                            <MenuItem key={item.id} value={item.id}>
                                {item.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <IconButton
                    aria-label="Delete"
                    onClick={(e) => {
                        handleSelectTeacher(assignedTeacher);
                    }}
                    size="small"
                    color="error"
                >
                    <ClearIcon />
                </IconButton>
            </ListItem>
            <Divider />
        </React.Fragment>
    );
}
