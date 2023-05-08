import React, {useState} from 'react';
import {boxChildComponent, centerBox} from "../ultis/constant";
import {Box, IconButton, Menu, MenuItem, Typography} from "@mui/material";
import {Link} from 'react-router-dom';
import SettingsOutlinedIcon from '@mui/icons-material/SettingsOutlined';
import ApartmentIcon from '@mui/icons-material/Apartment';

const ProjectItem = ({ name, id }) => {
    const [anchorEl, setAnchorEl] = useState(null);
    const open = Boolean(anchorEl);
    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };
    return (
        <>
            <Box sx={boxChildComponent} mb={3}>
                <Box sx={{ display: 'flex' }}>
                    <Box sx={centerBox}>
                        <ApartmentIcon />
                    </Box>
                    <Box px={3} sx={{ width: '100%' }}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                            <Link to={`/taskmanagement/project/${id}/tasks`} style={{ textDecoration: 'none', color: "#000" }}>
                                <Typography variant="h6" component={'h6'}>
                                    {name}
                                </Typography>
                            </Link>
                            <IconButton aria-label="delete" size="large" onClick={handleClick}>
                                <SettingsOutlinedIcon />
                            </IconButton>
                        </Box>
                        <Box>
                            <Link to={`/taskmanagement/project/tasks/create/${id}`} style={{ textDecoration: 'none', marginRight: "15px" }}>Thêm n.vụ</Link>
                            <Link to={`/taskmanagement/project/${id}/tasks#list-task`} style={{ textDecoration: 'none', marginRight: "15px" }}>Danh sách n.vụ</Link>
                            <Link to={`/taskmanagement/project/${id}/board`} style={{ textDecoration: 'none', marginRight: "15px" }}>Board</Link>
                        </Box>
                    </Box>
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
                <MenuItem onClick={handleClose}><Link to={`/taskmanagement/project/type/update/${id}`} style={{ textDecoration: 'none', color: "#000"}}>Chỉnh sửa dự án</Link></MenuItem>
                <MenuItem onClick={handleClose}>Xóa dự án</MenuItem>
            </Menu>
        </>
    );
}

export default ProjectItem;