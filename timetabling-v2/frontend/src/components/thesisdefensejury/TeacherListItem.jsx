import React, { useState } from "react";
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
import KeywordChip from "components/common/KeywordChip";

export default function TeacherListItem({ assignedTeacher, teacher, handleSelectTeacher }) {
    return (
        <>
            <ListItem key={teacher?.id} alignItems="flex-start" disablePadding>
                <ListItemButton
                    role={undefined}
                    onClick={(e) => {
                        handleSelectTeacher(teacher);
                    }}
                >
                    <ListItemIcon>
                        <Checkbox
                            edge="start"
                            disableRipple
                            checked={
                                assignedTeacher?.find((item) => item?.id === teacher?.id) !==
                                undefined
                            } // Use local state for checkbox state
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
