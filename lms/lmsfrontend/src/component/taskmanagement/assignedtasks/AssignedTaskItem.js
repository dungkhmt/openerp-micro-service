import React, {useState} from "react";
import {Box, Grid} from "@material-ui/core";
import {boxChildComponent} from "../ultis/constant";
import {Typography} from '@mui/material';
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';
import CategoryElement from "../common/CategoryElement";
import StatusElement from "../common/StatusElement";
import {Link} from "react-router-dom";

const AssignedTaskItem = ({ task }) => {
    const [statusId, setStatusId] = useState(task.statusItem?.statusId);

    const category = task.taskCategory.categoryName;
    const taskName = task.name;
    const description = task.description;
    const project = task.project.name;
    const dueDate = task.dueDate;
    const outOfDate = task.outOfDate;
    const timeRemaining = task.timeRemaining;

    return (
        <>
            <Box sx={boxChildComponent} mb={3}>
                <Box display={'flex'} justifyContent={'space-between'} alignItems={'center'}>
                    <Box display={'flex'} alignItems={'center'} mb={2}>
                        <Box>
                            <CategoryElement categoryId={task.taskCategory.categoryId} value={task.taskCategory.categoryName} />
                        </Box>
                        <Link to={`/taskmanagement/tasks/${task.id}`} style={{ textDecoration: 'none' }}>
                            <Typography variant="body1" >
                                {taskName}
                            </Typography>
                        </Link>
                    </Box>
                    <Box>
                        <StatusElement statusId={task.statusItem?.statusId} value={task.statusItem?.description} />
                    </Box>
                </Box>
                {/* <Box>
                        <TextField
                            select
                            variant="outlined"
                            defaultValue=""
                            value={statusId}
                            onChange={(e) => setStatusId(e.target.value)}
                            sx={{ backgroundColor: "#eee" }}
                        >
                            {taskStatus.map((item) => (
                                <MenuItem key={item.statusId} value={item.statusId}>{item.statusCode}</MenuItem>
                            ))}
                        </TextField>
                        <IconButton aria-label="delete" size="medium" onClick={() => handleUpdateStatus(task.id, statusId)}>
                            <SyncSharpIcon fontSize="inherit" />
                        </IconButton>
                    </Box> */}
                <Box mb={2}>
                    <Typography paragraph={true} variant="caption" color="primary">
                        Dự án: {project}
                    </Typography>
                </Box>
                <Box>
                    <Grid container>
                        <Grid item={true} xs={9}>
                            <Box display={'flex'} alignItems={'center'}>
                                <Box>
                                    <Typography color="warning" variant="body2">
                                        Thời hạn: {dueDate}
                                    </Typography>
                                </Box>
                                <Box>
                                    {outOfDate && <LocalFireDepartmentIcon color="error" />}
                                </Box>
                            </Box>
                            <Typography variant="caption" sx={{ color: "red" }}>
                                {timeRemaining}
                            </Typography>
                        </Grid>
                        <Grid item={true} xs={3}>
                        </Grid>
                    </Grid>
                </Box>
            </Box>
        </>
    );
}

export default AssignedTaskItem;