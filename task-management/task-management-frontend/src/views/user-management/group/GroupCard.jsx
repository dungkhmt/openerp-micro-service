import {
  Box,
  Card,
  CircularProgress,
  IconButton,
  Menu,
  MenuItem,
  Tooltip,
  Typography,
} from "@mui/material";
import { GroupedAvatars } from "../../../components/common/avatar/GroupedAvatars";
import PropTypes from "prop-types";
import { useState } from "react";
import { Link } from "react-router-dom";
import { Icon } from "@iconify/react";
import { useDispatch, useSelector } from "react-redux";
import GroupDialog from "../../../components/groups/GroupDialog";
import { updateGroup } from "../../../store/user-management/group";
import toast from "react-hot-toast";

const GroupCard = ({ group }) => {
  const dispatch = useDispatch();
  const { user } = useSelector((state) => state.myProfile);
  const [anchorEl, setAnchorEl] = useState(null);
  const [editDialog, setEditDialog] = useState(false);
  const [selectedGroup, setSelectedGroup] = useState(null);

  const handleClick = (group, e) => {
    setAnchorEl(e.currentTarget);
    setSelectedGroup(group);
  };

  const handleClose = () => {
    setAnchorEl(null);
    setSelectedGroup(null);
  };

  const handleOpenInNewTab = (groupId) => {
    handleClose();
    const groupUrl = `groups/${groupId}`;
    window.open(groupUrl, "_blank", "noopener,noreferrer");
  };

  const handleEdit = () => {
    setEditDialog(true);
    setAnchorEl(null);
  };

  const onSubmit = async (data) => {
    try {
      await dispatch(
        updateGroup({
          id: group.id,
          data: { ...data, organizationId: group.organizationId },
        })
      ).unwrap();
      toast.success("Cập nhật nhóm thành công!");
    } catch (error) {
      toast.error("Lỗi khi cập nhật nhóm!");
      console.error(error);
    } finally {
      setEditDialog(false);
    }
  };

  if (!user) return <CircularProgress />;

  const isAdmin = group.createdBy === user.id;
  const roleText = isAdmin ? "Admin" : "Member";

  return (
    <>
      <Card
        sx={{
          borderRadius: 3,
          padding: 5,
          boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
        }}
      >
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 1,
            mb: 2,
          }}
        >
          <Tooltip title={group.name} placement="top-start">
            <Typography
              variant="h6"
              component={Link}
              to={`${group.id}`}
              sx={{
                color: "grey.800",
                flexGrow: 1,
                overflow: "hidden",
                whiteSpace: "nowrap",
                textOverflow: "ellipsis",
                mr: 1,
                textDecoration: "none",
                textTransform: "capitalize",
              }}
            >
              {group.name}
            </Typography>
          </Tooltip>
          <Typography
            variant="caption"
            sx={{
              backgroundColor: isAdmin ? "primary.light" : "grey.200",
              color: isAdmin ? "grey.50" : "text.secondary",
              px: 2,
              py: 0.5,
              borderRadius: 3,
              fontWeight: 500,
            }}
          >
            {roleText}
          </Typography>
        </Box>

        <Box
          sx={{
            mb: 2,
            height: "14vh",
            overflowY: group.description ? "auto" : "hidden",
            overflowX: "hidden",
            "&::-webkit-scrollbar": { display: "none" },
            scrollbarWidth: "none",
            msOverflowStyle: "none",
            p: 1,
            wordBreak: "break-word",
            whiteSpace: "pre-wrap",
          }}
        >
          <Typography
            variant="body1"
            gutterBottom
            sx={{ fontSize: "0.95rem", color: "text.primary" }}
          >
            {group.description ? (
              group.description.split("\n").map((line, index) => (
                <Box key={index} component="span">
                  {line}
                  <br />
                </Box>
              ))
            ) : (
              <Box
                sx={{
                  backgroundColor: "grey.100",
                  borderRadius: "10px",
                  height: "13vh",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                <Typography variant="body2" sx={{ color: "text.secondary" }}>
                  Không có mô tả.
                </Typography>
              </Box>
            )}
          </Typography>
        </Box>

        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            mb: 2,
          }}
        >
          {group.members && group.members.length > 0 ? (
            <GroupedAvatars users={group.members} />
          ) : (
            <Typography
              variant="body2"
              sx={{
                color: "text.secondary",
                height: 30,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              Không có thành viên
            </Typography>
          )}
          <IconButton
            onClick={(e) => handleClick(group, e)}
            sx={{
              color: "text.primary",
              transition: "background-color 0.5s",
              "&:hover": {
                backgroundColor: "grey.300",
                "& svg": { color: "text.primary" },
              },
            }}
          >
            <Icon fontSize={18} icon="tabler:dots" />
          </IconButton>
        </Box>

        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleClose}
          anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
          transformOrigin={{ vertical: "top", horizontal: "right" }}
          slotProps={{
            paper: {
              style: {
                minWidth: "10rem",
                marginTop: "8px",
                padding: "3px 10px",
                boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.4)",
                borderRadius: "8px",
              },
            },
          }}
        >
          <MenuItem
            onClick={() => handleOpenInNewTab(group.id)}
            sx={{ gap: 3 }}
          >
            <Icon fontSize={18} icon="ic:round-open-in-new" />
            <Typography variant="body1">Mở trong tab mới</Typography>
          </MenuItem>
          <MenuItem onClick={handleEdit} sx={{ gap: 3 }}>
            <Icon fontSize={18} icon="mingcute:pencil-line" />
            <Typography variant="body1">Chỉnh sửa</Typography>
          </MenuItem>
        </Menu>
      </Card>

      {selectedGroup && (
        <GroupDialog
          initialValues={selectedGroup}
          openDialog={editDialog}
          onClose={() => {
            setEditDialog(false);
            setSelectedGroup(null);
          }}
          onSubmit={onSubmit}
        />
      )}
    </>
  );
};

GroupCard.propTypes = {
  group: PropTypes.object.isRequired,
};

export default GroupCard;
