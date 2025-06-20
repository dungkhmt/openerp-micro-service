import { Box, Card, Typography, IconButton } from "@mui/material";
import { Icon } from "@iconify/react";
import UserInfo from "../../../components/common/avatar/UserInfo";
import GroupDialog from "../../../components/groups/GroupDialog";
import { useState } from "react";
import { useSelector } from "react-redux";
import { updateGroup } from "../../../store/user-management/group";
import toast from "react-hot-toast";
import { useParams } from "react-router";
import { useDispatch } from "react-redux";
import DescriptionText from "../../../components/common/text/DescriptionText";

const GroupHeader = () => {
  const { groupId } = useParams();
  const dispatch = useDispatch();
  const { currentGroup: group, groupMembers: members } = useSelector(
    (state) => state.userGroup
  );
  const [updateDialogOpen, setUpdateDialogOpen] = useState(false);

  const admin = members?.find((member) => member?.user?.id === group.createdBy)
    ?.user ?? { firstName: "Không xác định", lastName: "" };

  const onSubmit = async (data) => {
    try {
      await dispatch(
        updateGroup({
          id: groupId,
          data: { ...data, organizationId: group.organizationId },
        })
      ).unwrap();
      toast.success("Cập nhật nhóm thành công!");
    } catch (error) {
      toast.error("Lỗi khi cập nhật nhóm!");
      console.error(error);
    } finally {
      setUpdateDialogOpen(false);
    }
  };

  return (
    <Card
      sx={{
        p: 5,
        borderRadius: 3,
        boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
      }}
    >
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          mb: 2,
        }}
      >
        <Typography variant="h6">{group.name}</Typography>
        <IconButton
          onClick={() => setUpdateDialogOpen(true)}
          sx={{
            color: "primary.main",
            "&:hover": {
              color: "primary.main",
              backgroundColor: "primary.background",
            },
          }}
        >
          <Icon icon="mdi:pencil" fontSize={20} />
        </IconButton>
      </Box>
      <DescriptionText text={group.description} sx={{ mb: 5, ml: 1 }} />
      <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
        <Typography variant="subtitle2">Quản trị viên:</Typography>
        <UserInfo
          user={admin}
          variant="body2"
          typographySx={{ color: "text.primary", fontWeight: 500 }}
        />
      </Box>

      <GroupDialog
        initialValues={group}
        openDialog={updateDialogOpen}
        onSubmit={onSubmit}
        onClose={() => setUpdateDialogOpen(false)}
      />
    </Card>
  );
};

export default GroupHeader;
