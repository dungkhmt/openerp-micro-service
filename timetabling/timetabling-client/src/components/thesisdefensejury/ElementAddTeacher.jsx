import React, { useState, useMemo } from "react";
import { Grid, List, ListSubheader, TextField, InputAdornment } from "@mui/material";
import TeacherListItem from "./TeacherListItem";
import AssignedTeacherListItem from './AssignedTeacherListItem'
import SearchIcon from "@mui/icons-material/Search";
import ModalLoading from "components/common/ModalLoading";

export default function ElementAddTeacher({ loading, teacherList, assignedTeacher, handleSelectTeacher, handleAssignRole }) {
    const containsText = (text, searchText) =>
        text.toLowerCase().indexOf(searchText.toLowerCase()) > -1;
    const [searchTeacher, setSearchTeacher] = useState("");
    const displayedTeacherOptions = useMemo(
        () =>
            searchTeacher !== "" ? teacherList?.filter((option) =>
                containsText(option.teacherName, searchTeacher)
            ) : teacherList, [searchTeacher, teacherList]);

    return (
        <Grid container spacing={2}>
            {loading && <ModalLoading />}
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
                        <TeacherListItem key={item?.id} assignedTeacher={assignedTeacher} teacher={item} handleSelectTeacher={handleSelectTeacher} />
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
                    {assignedTeacher?.map((item) => (
                        <AssignedTeacherListItem assignedTeacher={item} handleAssignRole={handleAssignRole} handleSelectTeacher={handleSelectTeacher} />
                    ))}
                </List>
            </Grid>
        </Grid>
    );
}
