import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Grid,
  FormControl,
  IconButton,
  FormHelperText,
  Typography,
  Box,
} from "@mui/material";
import { Icon } from "@iconify/react";
import PropTypes from "prop-types";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Controller, useForm } from "react-hook-form";
import { useParams } from "react-router";
import { createEvent } from "../../../store/project/events";
import toast from "react-hot-toast";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { GroupedAvatars } from "../../../components/common/avatar/GroupedAvatars";
import ItemSelector from "../../../components/mui/dialog/ItemSelector";
import { DatePicker } from "@mui/x-date-pickers";

const DialogNewEvent = ({ openDialog, setOpenDialog }) => {
  const dispatch = useDispatch();
  const { id } = useParams();

  const { members } = useSelector((state) => state.project);
  const [createLoading, setCreateLoading] = useState(false);
  const [selectedUsers, setSelectedUsers] = useState([]);

  const {
    handleSubmit,
    formState: { errors },
    control,
  } = useForm();

  const handleSelectChange = (member) => {
    setSelectedUsers((prevSelectedUsers) =>
      prevSelectedUsers.includes(member)
        ? prevSelectedUsers.filter((selected) => selected.id !== member.id)
        : [...prevSelectedUsers, member]
    );
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedUsers([]);
  };

  const onSubmit = async (data) => {
    try {
      setCreateLoading(true);
      data.name = data.name?.trim();
      data.description = data.description?.trim();
      await dispatch(
        createEvent({
          ...data,
          projectId: id,
          userIds: selectedUsers.map((user) => user.id),
        })
      );
      toast.success("Tạo sự kiện thành công");
    } catch (e) {
      console.error(e);
      toast.error("Lỗi khi tạo sự kiện");
    } finally {
      setCreateLoading(false);
      setOpenDialog(false);
      setSelectedUsers([]);
    }
  };

  const [filteredMembers, setFilteredMembers] = useState(members);

  const handleSearch = (search) => {
    setFilteredMembers(
      members.filter(({ member }) =>
        `${member.firstName} ${member.lastName}`
          .toLowerCase()
          .includes(search.toLowerCase())
      )
    );
  };

  return (
    <Dialog open={openDialog} maxWidth="sm" fullWidth>
      <DialogTitle>Tạo Sự Kiện</DialogTitle>
      <DialogContent>
        <IconButton
          size="small"
          onClick={handleCloseDialog}
          sx={{ position: "absolute", right: "1rem", top: "1rem" }}
        >
          <Icon icon="mdi:close" />
        </IconButton>
        <Grid container spacing={4}>
          <Grid item xs={12} sx={{ mt: 2 }}>
            <FormControl fullWidth>
              <Controller
                name="name"
                control={control}
                rules={{ required: { value: true, message: "Required" } }}
                render={({ value, onChange }) => (
                  <TextField
                    value={value}
                    onChange={onChange}
                    label="Tên"
                    variant="outlined"
                    fullWidth
                    autoFocus
                    error={Boolean(errors.name)}
                    spellCheck={false}
                  />
                )}
              />
              {errors.name && (
                <FormHelperText
                  sx={{ color: "error.main" }}
                  id="validation-name"
                >
                  Tên không được để trống
                </FormHelperText>
              )}
            </FormControl>
          </Grid>
          <Grid item xs={12}>
            <FormControl fullWidth>
              <Controller
                name="description"
                control={control}
                render={({ value, onChange }) => (
                  <TextField
                    value={value}
                    onChange={onChange}
                    label="Mô tả"
                    multiline
                    fullWidth
                    spellCheck={false}
                    minRows={3}
                  />
                )}
              />
            </FormControl>
          </Grid>
          <Grid item sm={4} xs={12}>
            <FormControl fullWidth>
              <Controller
                name="dueDate"
                control={control}
                defaultValue={new Date()}
                as={
                  <DatePicker
                    label="Ngày diễn ra"
                    format="dd/MM/yyyy"
                    renderInput={(params) => <TextField {...params} />}
                  />
                }
              />
            </FormControl>
          </Grid>
          <Grid item sm={4} xs={12}>
            <ItemSelector
              items={filteredMembers.map(({ member }) => member)}
              selectedItems={selectedUsers}
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
              placeholder="Tìm kiếm thành viên"
              label="Thành viên"
              startIcon={<Icon icon="tdesign:member" />}
              idPopover="member-popover"
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={handleCloseDialog}
          variant="outlined"
          sx={{
            color: "#696969",
            borderColor: "#D3D3D3",
            textTransform: "none",
            "&:hover": {
              borderColor: "#B0B0B0",
              backgroundColor: "rgba(211, 211, 211, 0.5)",
            },
          }}
        >
          Cancel
        </Button>
        <Button
          onClick={handleSubmit(onSubmit)}
          color="primary"
          variant="contained"
          disabled={createLoading}
          sx={{
            textTransform: "none",
          }}
        >
          {createLoading ? "Creating..." : "Create"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

DialogNewEvent.propTypes = {
  openDialog: PropTypes.bool.isRequired,
  setOpenDialog: PropTypes.func.isRequired,
};

export { DialogNewEvent };
