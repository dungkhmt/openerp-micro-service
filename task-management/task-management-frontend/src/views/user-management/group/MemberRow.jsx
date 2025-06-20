import { TableCell, TableRow, Box, IconButton } from "@mui/material";
import PropTypes from "prop-types";
import UserInfo from "../../../components/common/avatar/UserInfo";
import { Icon } from "@iconify/react";
import { useDispatch } from "react-redux";
import { useParams } from "react-router";
import { removeUserFromGroup } from "../../../store/user-management/group";
import ConfirmationDialog from "../../../components/mui/dialog/ConfirmationDialog";
import { useState } from "react";
import toast from "react-hot-toast";

const MemberRow = ({ member }) => {
  const { groupId } = useParams();
  const dispatch = useDispatch();
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

  const handleDeleteDialogClose = () => {
    setOpenDeleteDialog(false);
  };

  const onConfirmDelete = async () => {
    try {
      await dispatch(
        removeUserFromGroup({ id: groupId, userId: member.user.id })
      ).unwrap();
      toast.success("Xóa thành viên thành công.");
      setOpenDeleteDialog(false);
    } catch (error) {
      toast.error("Lỗi khi xóa thành viên!");
      console.error(error);
    }
  };

  return (
    <>
      <TableRow>
        <TableCell>
          <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
            <UserInfo
              user={member.user}
              typographySx={{ color: "text.primary", fontWeight: 500 }}
              showEmail={true}
            />
          </Box>
        </TableCell>
        <TableCell align="center">{member.totalTasks || 0}</TableCell>
        <TableCell align="center">{member.completedTasks || 0}</TableCell>
        <TableCell align="center">{member.inProgressTasks || 0}</TableCell>
        <TableCell align="center">{member.uncompletedTasks || 0}</TableCell>
        <TableCell align="center">
          <IconButton
            onClick={() => setOpenDeleteDialog(true)}
            sx={{
              color: "error.main",
              "&:hover": {
                color: "error.main",
                backgroundColor: "error.background",
              },
            }}
          >
            <Icon icon="mdi:account-remove" />
          </IconButton>
        </TableCell>
      </TableRow>

      <ConfirmationDialog
        open={openDeleteDialog}
        onClose={handleDeleteDialogClose}
        onConfirm={onConfirmDelete}
        title="Xác nhận xóa thành viên"
        content={
          <>
            Bạn có chắc chắn muốn xóa{" "}
            <strong>
              {member?.user?.firstName} {member?.user?.lastName}
            </strong>{" "}
            khỏi kế hoạch cuộc họp này không? Hành động này không thể hoàn tác.
          </>
        }
        confirmText="Xóa"
        cancelText="Hủy"
        variant="error"
      />
    </>
  );
};

MemberRow.propTypes = {
  member: PropTypes.object.isRequired,
};

export default MemberRow;
