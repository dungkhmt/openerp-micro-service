import {Box, Typography} from "@material-ui/core";
import {Pagination, Stack} from '@mui/material';
import AssignedTaskItem from "./AssignedTaskItem";
import {request} from "../../../api";
import {useEffect, useState} from "react";
import {boxComponentStyle} from "../ultis/constant";

const ListAssignedTasks = () => {
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(1);
    const [total, setTotal] = useState(1);
    const [taskAssigned, setTaskAssigned] = useState([]);

    const url = `/assigned-tasks-user-login/page=${page - 1}/size=5`;

    useEffect(() => {
        request('get', url, res => {
            setLoading(false);
            setTaskAssigned(res.data.data);
            setTotal(res.data.totalPage);
        }, err => {
            console.log(err);
        });
    }, [url]);

    return (
        <>
            <Box sx={boxComponentStyle}>
                <Box mb={4}>
                    <Typography variant="h4" mb={4} component={'h4'}>
                        Danh sách các nhiệm vụ được giao
                    </Typography>
                </Box>
                {loading && 
                    <Typography variant="body2">
                        Loading......
                    </Typography>
                }
                {taskAssigned.map(task => (
                    <AssignedTaskItem
                        key={task.id}
                        task={task}
                    />
                ))}
            </Box>
            <Box display={'flex'} justifyContent="center">
                <Stack spacing={2}>
                    <Pagination
                        count={total}
                        page={page}
                        onChange={(e, value) => setPage(value)}
                        color="primary"
                    />
                </Stack>
            </Box>
        </>
    );
}

export default ListAssignedTasks;