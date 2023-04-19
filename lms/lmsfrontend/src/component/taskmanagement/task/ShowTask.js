import * as React from 'react';
import {useEffect, useState} from 'react';
import {
  Avatar,
  Box,
  Breadcrumbs,
  Button,
  Grid,
  IconButton,
  Menu,
  MenuItem,
  Skeleton,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import {boxChildComponent, boxComponentStyle} from '../ultis/constant';
import CategoryElement from '../common/CategoryElement';
import StatusElement from '../common/StatusElement';
import {Link, useHistory, useParams} from 'react-router-dom';
import {request} from "../../../api";
import DownloadIcon from '@mui/icons-material/Download';
import SettingsOutlinedIcon from '@mui/icons-material/SettingsOutlined';
import ChangeStatusModal from './ChangeStatusModal';
import {useForm} from "react-hook-form";
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';
import CommentSection from './CommentSection';
import {successNoti} from "utils/notification";

const ShowTask = () => {
    const { taskId } = useParams();
    const [task, setTask] = useState(null);
    const history = useHistory();

    const { register, errors, handleSubmit, watch, setValue } = useForm();

    const [listComment, setListComment] = useState([]);
    const [loadComments, setLoadComments] = useState(false);
    const [isLoadTask, setIsLoadTask] = useState(false);

    const [fileName, setFileName] = useState(null);
    const [fileId, setFileId] = useState(null);

    const [anchorEl, setAnchorEl] = useState(null);
    const openMenu = Boolean(anchorEl);
    const handleClickMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };
    const handleCloseMenu = () => {
        setAnchorEl(null);
    };


    const [openModal, setOpenModal] = useState(false);
    const handleClickOpenModal = () => {
        setOpenModal(true);
    };
    const handleCloseModal = () => {
        setOpenModal(false);
    };

    const onLoadTask = () => {
        setIsLoadTask(!isLoadTask);
    }

    const onSubmitComment = (data) => {
        let dataForm = {
            ...data,
            projectId: task.project.id
        };
        request(
            'post',
            `tasks/${taskId}/comment`,
            (res) => {
                console.log(res.data);
                successNoti("Đã thêm mới bình luận thành công!", true);
                setLoadComments(!loadComments);
                setValue('comment', '');
            },
            (err) => {
                console.log(err);
            },
            dataForm
        );
    }

    const onHandleDownload = () => {
        request(
            "GET",
            `/content/get/${fileId}`,
            (res) => {
                saveFile(fileName, res.data);
            },
            {},
            {},
            { responseType: "blob" }
        );
    }

    const saveFile = (fileName, data) => {
        let blob = new Blob([data]);

        //IE11 support
        if (window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveBlob(blob, fileName);
        } else {
            let link = window.document.createElement("a");

            link.href = window.URL.createObjectURL(blob);
            link.setAttribute("download", fileName);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    };

    const handleClick = () => {
        setOpen(true);
    };

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }

        setOpen(false);
    };

    useEffect(() => {
        request('get', `/tasks/${taskId}`, res => {
            setTask(res.data);
            setFileId(res.data.fileId);
            setFileName(res.data.fileName);
            console.log(res.data);
        }, err => {
            console.log(err);
        });
    }, [isLoadTask]);

    useEffect(() => {
        request('get', `tasks/${taskId}/comments`, res => {
            setListComment(res.data);
            console.log(res.data);
        }, err => {
            console.log(err);
        });
    }, [loadComments]);

    return (
        <>
            {task ?
                <>
                    <Box mb={2}>
                        <Breadcrumbs aria-label="breadcrumb">
                            <Link style={{ textDecoration: 'none' }} to={`/taskmanagement/project/${task.project.id}/tasks`}>
                                {task.project.name}
                            </Link>
                            <Typography color="text.primary">{task.name.substr(0, 30)}...</Typography>
                        </Breadcrumbs>
                    </Box>
                    <Box sx={boxComponentStyle}>
                        <Box sx={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            mb: 2
                        }}>
                            <CategoryElement categoryId={task.taskCategory.categoryId} value={task.taskCategory.categoryName} />
                            <Box display={'flex'} alignItems="center">
                                <Box>
                                    {task.outOfDate && <LocalFireDepartmentIcon color="error" />}
                                </Box>
                                <Box display={'flex'} mr={2}>
                                    <Typography variant='caption' color="secondary" sx={{ mr: 1 }}>
                                        Thời hạn:
                                    </Typography>
                                    <Typography variant='body2'>
                                        {task.dueDate}
                                    </Typography>
                                </Box>
                                <Box>
                                    <StatusElement statusId={task.statusItem?.statusId} value={task.statusItem?.description} />
                                </Box>
                            </Box>
                        </Box>
                        <Box mb={2} display={'flex'} justifyContent={'space-between'}>
                            <Typography variant='h6' color={"secondary"}>
                                {task.name}
                            </Typography>
                            <IconButton aria-label="delete" size="large" onClick={handleClickMenu}>
                                <SettingsOutlinedIcon />
                            </IconButton>
                        </Box>
                        <Box mb={3} sx={boxChildComponent}>
                            <Box mb={3}>
                                <Box display={'flex'} alignItems={"center"}>
                                    <Avatar>{task.createdByUserLoginId != null ? task.createdByUserLoginId.charAt(0).toUpperCase() : "N"}</Avatar>
                                    <Box px={2} display={'flex'} flexDirection={"column"} >
                                        <Typography variant='body2' color={'secondary'}>
                                            {task.createdByUserLoginId != null ? task.createdByUserLoginId : "Không có dữ liệu"}
                                        </Typography>
                                        <Box display={'flex'}>
                                            <Typography variant='caption' color="secondary" sx={{ marginRight: "5px" }}>
                                                Ngày tạo:
                                            </Typography>
                                            <Typography variant='body2'>
                                                {task.createdStamp}
                                            </Typography>
                                        </Box>
                                    </Box>
                                </Box>
                            </Box>
                            <Box mb={3}>
                                <Typography variant="body1" paragraph={true}>
                                    {task.description}
                                </Typography>
                            </Box>
                            <Box mb={3}>
                                <Grid container columnSpacing={2}>
                                    <Grid item={true} xs={6}>
                                        <Box borderTop={1} borderBottom={1} borderColor={"#cdb8b8"} py={2}>
                                            <Grid container>
                                                <Grid item={true} xs={5}>
                                                    Mức ưu tiên
                                                </Grid>
                                                <Grid item={true} xs={7}>
                                                    {task.taskPriority.priorityName}
                                                </Grid>
                                            </Grid>
                                        </Box>
                                    </Grid>
                                    <Grid item={true} xs={6}>
                                        <Box borderTop={1} borderBottom={1} borderColor={"#cdb8b8"} py={2}>
                                            <Grid container>
                                                <Grid item={true} xs={5}>
                                                    Nhiệm vụ được gán cho
                                                </Grid>
                                                <Grid item={true} xs={7}>
                                                    {task.assignee}
                                                </Grid>
                                            </Grid>
                                        </Box>
                                    </Grid>
                                    <Grid item={true} xs={6}>
                                        <Box borderBottom={1} borderColor={"#cdb8b8"} py={2}>
                                            <Grid container>
                                                <Grid item={true} xs={5}>
                                                    Dự án
                                                </Grid>
                                                <Grid item={true} xs={7}>
                                                    <Typography variant="body2">
                                                        {task.project.name}
                                                    </Typography>
                                                </Grid>
                                            </Grid>
                                        </Box>
                                    </Grid>
                                    <Grid item={true} xs={6}>

                                    </Grid>
                                    <Grid item={true} xs={6}>
                                        <Box py={2}>
                                            <Grid container sx={{ display: 'flex', alignItems: 'center' }}>
                                                <Grid item={true} xs={5}>
                                                    Tệp đính kèm
                                                </Grid>
                                                <Grid item={true} xs={7} sx={{ display: 'flex', alignItems: 'center' }}>
                                                    <Typography variant="body2">
                                                        {task.fileName}
                                                    </Typography>
                                                    {fileId &&
                                                        <IconButton color="primary" aria-label="add an alarm" onClick={onHandleDownload}>
                                                            <DownloadIcon />
                                                        </IconButton>
                                                    }
                                                </Grid>
                                            </Grid>
                                        </Box>
                                    </Grid>
                                </Grid>
                            </Box>
                            {task.timeRemaining != "" &&
                                <Box display={'flex'} justifyContent={'flex-end'}>
                                    <Box display={'flex'} mr={2}>
                                        <Typography variant='caption' color="secondary" sx={{ mr: 1 }}>
                                            Thời gian còn lại:
                                        </Typography>
                                        <Typography variant='body2'>
                                            {task.timeRemaining}
                                        </Typography>
                                    </Box>
                                </Box>
                            }
                        </Box>
                        <CommentSection listComment={listComment} loadComments={loadComments} setLoadCommentsCallBack={(cmt) => setLoadComments(cmt)} projectId={task.project.id}/>
                        <Box>
                            <TextField
                                variant='standard'
                                label="Thêm bình luận"
                                placeholder='...'
                                fullWidth={true}
                                name="comment"
                                inputRef={register({ required: "Thiếu nội dung!" })}
                                error={!!errors.comment}
                                helperText={errors.comment?.message}
                            />
                            <Box display={'flex'} justifyContent={"flex-end"} mt={2}>
                                <Button variant="contained" color="primary" onClick={handleSubmit(onSubmitComment)}>Thêm bình luận</Button>
                            </Box>
                        </Box>
                    </Box>
                    <Menu
                        anchorEl={anchorEl}
                        open={openMenu}
                        onClose={handleCloseMenu}
                        anchorOrigin={{
                            vertical: 'top',
                            horizontal: 'left',
                        }}
                        transformOrigin={{
                            vertical: 'top',
                            horizontal: 'left',
                        }}
                    >
                        <MenuItem onClick={() => { handleCloseMenu(); handleClickOpenModal() }}>Thay đổi trạng thái</MenuItem>
                        <MenuItem onClick={() => { history.push(`/taskmanagement/tasks/${task.id}/edit`) }}>Chỉnh sửa nhiệm vụ</MenuItem>
                    </Menu>
                    <ChangeStatusModal
                        open={openModal}
                        handleClose={handleCloseModal}
                        taskId={task.id}
                        projectId={task.project.id}
                        statusIdDf={task.statusItem != null ? task.statusItem.statusId : ""}
                        dueDateDf={task.dueDateOrigin}
                        partyIdDf={task.partyId}
                        onLoadTask={onLoadTask}
                    />
                </>
                :
                <Stack spacing={1}>
                    <Skeleton variant="text" />
                    <Skeleton variant="circular" width={40} height={40} />
                    <Skeleton variant="rectangular" height={200} />
                </Stack>
            }
        </>
    );
}

export default ShowTask;