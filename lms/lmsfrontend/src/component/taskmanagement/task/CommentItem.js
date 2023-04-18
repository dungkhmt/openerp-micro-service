import {Avatar, Box, IconButton, Menu, MenuItem, Typography} from '@mui/material';
import React, {useState} from 'react';
import SettingsOutlinedIcon from '@mui/icons-material/SettingsOutlined';

const CommentItem = ({ comment, onBottom, onDeleteComment, onUpdateComment }) => {
    const [anchorEl, setAnchorEl] = useState(null);
    const open = Boolean(anchorEl);
    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };

    const onDelete = (id) => {
        onDeleteComment(id);
        setAnchorEl(null);
    }

    const onUpdate = (id, comment) => {
        onUpdateComment(id, comment);
        setAnchorEl(null);
    }

    return (
        <>
            <Box py={2} borderBottom={!onBottom ? 1 : 0} borderColor={!onBottom ? "#cdb8b8" : "#fff"}>
                <Box display={'flex'} justifyContent={'space-between'} alignItems={'center'}>
                    <Box display={'flex'} alignItems={"center"} mb={2}>
                        <Avatar>{comment.createdByUserId != null ? comment.createdByUserId.charAt(0).toUpperCase() : "N"}</Avatar>
                        <Box px={2} display={'flex'} flexDirection={"column"} >
                            <Typography variant='body2' color={'secondary'}>
                                {comment.createdByUserId != null ? comment.createdByUserId : "Không có dữ liệu"}
                            </Typography>
                            <Box display={'flex'}>
                                <Typography variant='caption' color="secondary" sx={{ marginRight: "5px" }}>
                                    Ngày tạo:
                                </Typography>
                                <Typography variant='body2'>
                                    {comment.createdDate}
                                </Typography>
                            </Box>
                        </Box>
                    </Box>
                    {comment.modify &&
                        <IconButton aria-label="delete" size="large" onClick={handleClick}>
                            <SettingsOutlinedIcon />
                        </IconButton>
                    }
                </Box>
                <Box>
                    <Typography variant="body2">
                        {comment.comment}
                    </Typography>

                    <Typography variant="caption" sx={{ color: "#9a9191" }}>
                        {comment.status ? <>[edited]</> : <></>}
                    </Typography>
                </Box>
            </Box>
            <Menu
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                anchorOrigin={{
                    vertical: 'top',
                    horizontal: 'left',
                }}
                transformOrigin={{
                    vertical: 'top',
                    horizontal: 'left',
                }}
            >
                <MenuItem onClick={() => onUpdate(comment.id, comment.comment)}>Chỉnh sửa bình luận</MenuItem>
                <MenuItem onClick={() => onDelete(comment.id)}>Xóa bình luận</MenuItem>
            </Menu>
        </>
    );
}

export default CommentItem;