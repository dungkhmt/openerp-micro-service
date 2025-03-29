import { useEffect, useState } from "react";
import {
  Button,
  Card,
  CardContent,
  Typography,
  Popover,
  Box,
  IconButton,
} from "@mui/material";
import { Icon } from "@iconify/react";
import { useSelector, useDispatch } from "react-redux";
import ItemSelector from "../../../components/mui/dialog/ItemSelector";
import toast from "react-hot-toast";
import { useParams } from "react-router";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { GroupedAvatars } from "../../../components/common/avatar/GroupedAvatars";
import ExpandableSearch from "../../../components/mui/search/ExpandableSearch";
import ConfirmationDialog from "../../../components/mui/dialog/ConfirmationDialog";
import {
  addMemberToMeetingPlan,
  removeMemberFromMeetingPlan,
} from "../../../store/meeting-plan";

const MeetingMembersView = () => {
  const { pid } = useParams();
  const dispatch = useDispatch();
  const { usersCache } = useSelector((state) => state.userManagement);
  const { members, currentPlan, isCreator } = useSelector(
    (state) => state.meetingPlan
  );
  const [filteredUsers, setFilteredUsers] = useState(usersCache);
  const [searchedUsers, setSearchedUsers] = useState(usersCache);
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [filteredMembers, setFilteredMembers] = useState(members);
  const [deletedMembers, setDeletedMembers] = useState(null);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

  const handleOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
    setSelectedMembers([]);
    setSearchedUsers(filteredUsers);
  };

  const handleSelectChange = (user) => {
    setSelectedMembers((prev) => {
      const isSelected = prev.some((member) => member.id === user.id);
      return isSelected
        ? prev.filter((member) => member.id !== user.id)
        : [...prev, user];
    });
  };

  const handleSearch = (search) => {
    const results = filteredUsers.filter((user) =>
      `${user.firstName} ${user.lastName}`
        .toLowerCase()
        .includes(search.toLowerCase())
    );
    setSearchedUsers(results);
  };

  const handleMemberSearch = (searchTerm) => {
    if (!searchTerm) {
      setFilteredMembers(members);
    } else {
      const filtered = members.filter((member) => {
        const fullName = `${member.firstName ?? ""} ${member.lastName ?? ""}`
          .trim()
          .toLowerCase();
        const email = member.email?.toLowerCase() || "";
        return (
          fullName.includes(searchTerm.toLowerCase()) ||
          email.includes(searchTerm.toLowerCase())
        );
      });
      setFilteredMembers(filtered);
    }
  };

  const handleDelete = (user) => {
    setDeletedMembers(user);
    setOpenDeleteDialog(true);
  };

  const handleDeleteDialogClose = () => {
    setOpenDeleteDialog(false);
    setDeletedMembers(null);
  };

  const onConfirmDelete = async () => {
    try {
      await dispatch(
        removeMemberFromMeetingPlan({
          meetingPlanId: pid,
          userId: deletedMembers.id,
        })
      ).unwrap();
      toast.success("Xóa thành viên thành công!");
      setOpenDeleteDialog(false);
      setDeletedMembers(null);
    } catch (error) {
      console.error(error);
      toast.error("Xóa thành viên thất bại!");
    }
  };

  const onSubmit = async () => {
    try {
      setLoading(true);
      const data = {
        userId: selectedMembers.map((member) => member.id),
      };
      await dispatch(
        addMemberToMeetingPlan({ meetingPlanId: pid, data })
      ).unwrap();
      toast.success("Thêm thành viên thành công!");
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
      handleClose();
    }
  };

  useEffect(() => {
    setFilteredUsers(
      usersCache.filter(
        (user) =>
          !members?.some((member) => member.id === user.id) &&
          user.id !== currentPlan?.createdBy &&
          (user.firstName || user.lastName)
      )
    );
  }, [usersCache, members, currentPlan]);

  useEffect(() => {
    setFilteredMembers(members);
  }, [members]);

  return (
    <>
      {/* Members */}
      <Card sx={{ borderRadius: 3 }}>
        <CardContent>
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 3,
            }}
          >
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <Typography variant="h6">Thành viên</Typography>
              <Typography
                variant="body2"
                sx={{ fontWeight: 600, color: "text.secondary" }}
              >
                ({members?.length})
              </Typography>
            </Box>

            <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
              <ExpandableSearch onSearchChange={handleMemberSearch} />
              {isCreator &&
                (currentPlan?.statusId === "PLAN_DRAFT" ||
                  currentPlan?.statusId === "PLAN_REG_OPEN") && (
                  <IconButton
                    onClick={handleOpen}
                    sx={{
                      bgcolor: "grey.100",
                      "&:hover": { bgcolor: "grey.300" },
                    }}
                  >
                    <Icon icon="stash:plus-solid" fontSize={20} />
                  </IconButton>
                )}
            </Box>
          </Box>
          <Box
            sx={{
              overflowX: "auto",
              display: "flex",
              flexDirection: "column",
              overflowY: "auto",
              maxHeight: "60vh",
            }}
          >
            {filteredMembers && filteredMembers.length > 0 ? (
              filteredMembers.map((member, index) => {
                const { firstName, lastName, email } = member;
                const fullName =
                  firstName || lastName
                    ? `${firstName ?? ""} ${lastName ?? ""}`.trim()
                    : " - ";
                return (
                  <Box
                    key={index}
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      gap: 2,
                      mb: 2,
                      justifyContent: "space-between",
                    }}
                  >
                    <Box
                      sx={{
                        display: "flex",
                        alignItems: "center",
                        gap: 2,
                        mb: 1,
                      }}
                    >
                      <UserAvatar user={member} />
                      <Box
                        sx={{
                          display: "flex",
                          alignItems: "flex-start",
                          flexDirection: "column",
                        }}
                      >
                        <Typography
                          sx={{
                            fontWeight: 600,
                            fontSize: "0.9rem",
                            color: "grey.A700",
                          }}
                        >
                          {fullName}
                        </Typography>
                        <Typography noWrap variant="caption">
                          {email || "No email provided"}
                        </Typography>
                      </Box>
                    </Box>
                    {isCreator &&
                      (currentPlan?.statusId === "PLAN_DRAFT" ||
                        currentPlan?.statusId === "PLAN_REG_OPEN") && (
                        <IconButton
                          onClick={() => handleDelete(member)}
                          sx={{
                            color: "secondary.light",
                            "&:hover": {
                              color: "secondary.dark",
                            },
                          }}
                        >
                          <Icon icon="mdi:delete" fontSize={20} />
                        </IconButton>
                      )}
                  </Box>
                );
              })
            ) : (
              <Typography
                variant="body2"
                color="textSecondary"
                sx={{ fontStyle: "italic" }}
              >
                Không có thành viên.
              </Typography>
            )}
          </Box>
        </CardContent>
      </Card>

      {/* Add Member Popover */}
      <Popover
        open={Boolean(anchorEl)}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        transformOrigin={{ vertical: "bottom", horizontal: "right" }}
        PaperProps={{
          sx: { minWidth: 250, width: "30%" },
        }}
      >
        <Box sx={{ px: 5, py: 3 }}>
          <Typography variant="h6" sx={{ mb: 2 }}>
            Thêm Thành Viên
          </Typography>
          <ItemSelector
            items={searchedUsers}
            selectedItems={selectedMembers}
            onSelectChange={handleSelectChange}
            handleSearch={handleSearch}
            renderItem={(item) => (
              <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
                <UserAvatar user={item} skin="light" />
                <Typography variant="subtitle2">
                  {`${item.firstName} ${item.lastName}`}
                </Typography>
              </Box>
            )}
            renderSelectedItem={(items) => <GroupedAvatars users={items} />}
            placeholder="Tìm kiếm..."
            label="Chọn thành viên"
            startIcon={<Icon icon="mdi:account-plus" />}
            idPopover="add-member-selector"
          />
          <Box mt={2} display="flex" justifyContent="flex-end">
            <Button onClick={handleClose}>Hủy</Button>
            <Button
              onClick={() => onSubmit(selectedMembers)}
              variant="contained"
              disabled={loading}
              sx={{ ml: 1 }}
            >
              {loading ? "Đang thêm..." : "Thêm"}
            </Button>
          </Box>
        </Box>
      </Popover>

      {/* Delete Confirmation Dialog */}
      <ConfirmationDialog
        open={openDeleteDialog}
        onClose={handleDeleteDialogClose}
        onConfirm={onConfirmDelete}
        title="Xác nhận xóa thành viên"
        content={
          <>
            Bạn có chắc chắn muốn xóa{" "}
            <strong>
              {deletedMembers?.firstName} {deletedMembers?.lastName}
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

export default MeetingMembersView;
