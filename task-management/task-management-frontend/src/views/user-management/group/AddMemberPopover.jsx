import { Popover, Box, Typography, Button } from "@mui/material";
import { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import PropTypes from "prop-types";
import toast from "react-hot-toast";
import { Icon } from "@iconify/react";
import { GroupedAvatars } from "../../../components/common/avatar/GroupedAvatars";
import ItemSelector from "../../../components/mui/dialog/ItemSelector";
import { addUserToGroup } from "../../../store/user-management/group";
import UserInfo from "../../../components/common/avatar/UserInfo";
import { useParams } from "react-router";

const AddMemberPopover = ({ anchorEl, setAnchorEl }) => {
  const { groupId } = useParams();
  const dispatch = useDispatch();
  const { usersCache } = useSelector((state) => state.userManagement);
  const { groupMembers } = useSelector((state) => state.userGroup);
  const [filteredUsers, setFilteredUsers] = useState(usersCache);
  const [searchedUsers, setSearchedUsers] = useState(usersCache);
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [loading, setLoading] = useState(false);

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
  const onSubmit = async () => {
    try {
      setLoading(true);
      const data = {
        userIds: selectedMembers.map((member) => member.id),
      };
      await dispatch(addUserToGroup({ id: groupId, data })).unwrap();
      toast.success("Thêm thành viên thành công!");
    } catch (error) {
      toast.error("Lỗi khi thêm thành viên!");
      console.error(error);
    } finally {
      setLoading(false);
      handleClose();
    }
  };

  useEffect(() => {
    if (!usersCache) return;
    setFilteredUsers(
      usersCache.filter(
        (user) =>
          !groupMembers?.some((member) => member.user.id === user.id) &&
          (user.firstName || user.lastName)
      )
    );
  }, [usersCache, groupMembers]);

  return (
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
          renderItem={(item) => <UserInfo user={item} />}
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
  );
};

AddMemberPopover.propTypes = {
  anchorEl: PropTypes.object,
  setAnchorEl: PropTypes.func,
};

export default AddMemberPopover;
