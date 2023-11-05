import ApartmentIcon from "@mui/icons-material/Apartment";
import SettingsOutlinedIcon from "@mui/icons-material/SettingsOutlined";
import { Box, IconButton, Menu, MenuItem, Typography } from "@mui/material";
import { useState } from "react";
import { Link } from "react-router-dom";
import { boxChildComponent, centerBox } from "../utils/constant";
import PropTypes from "prop-types";

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
        <Box sx={{ display: "flex" }}>
          <Box sx={centerBox}>
            <ApartmentIcon />
          </Box>
          <Box px={3} sx={{ width: "100%" }}>
            <Box sx={{ display: "flex", justifyContent: "space-between" }}>
              <Link
                to={`/project/${id}/tasks`}
                style={{ textDecoration: "none", color: "#000" }}
              >
                <Typography variant="h6" component={"h6"}>
                  {name}
                </Typography>
              </Link>
              <IconButton
                aria-label="delete"
                size="large"
                onClick={handleClick}
              >
                <SettingsOutlinedIcon />
              </IconButton>
            </Box>
            <Box>
              <Link
                to={`/project/tasks/create/${id}`}
                style={{ textDecoration: "none", marginRight: "15px" }}
              >
                Thêm n.vụ
              </Link>
              <Link
                to={`/project/${id}/tasks#list-task`}
                style={{ textDecoration: "none", marginRight: "15px" }}
              >
                Danh sách n.vụ
              </Link>
              <Link
                to={`/project/${id}/board`}
                style={{ textDecoration: "none", marginRight: "15px" }}
              >
                Board
              </Link>
            </Box>
          </Box>
        </Box>
      </Box>
      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "top",
          horizontal: "left",
        }}
        transformOrigin={{
          vertical: "top",
          horizontal: "left",
        }}
      >
        <MenuItem onClick={handleClose}>
          <Link
            to={`/project/type/update/${id}`}
            style={{ textDecoration: "none", color: "#000" }}
          >
            Chỉnh sửa dự án
          </Link>
        </MenuItem>
        <MenuItem onClick={handleClose}>Xóa dự án</MenuItem>
      </Menu>
    </>
  );
};

ProjectItem.propTypes = {
  name: PropTypes.string.isRequired,
  id: PropTypes.number.isRequired,
};

export default ProjectItem;
