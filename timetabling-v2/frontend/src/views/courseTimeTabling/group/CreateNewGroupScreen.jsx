import React, { useState, useEffect } from "react";
import { useGroupData } from 'services/useGroupData';
import { useClassroomData } from 'services/useClassroomData';
import { 
  Autocomplete, 
  Button, 
  Dialog, 
  DialogActions, 
  DialogContent, 
  DialogTitle, 
  TextField,

} from "@mui/material";

const CreateNewGroupScreen = React.memo(({ open, handleClose, onSuccess, selectedGroup }) => {
  const [formData, setFormData] = useState({
    id: null,
    groupName: "",
    buildingId: "",  // For filtering rooms
    roomName: "",    // For API
    priority: 1
  });

  const [priorityError, setPriorityError] = useState("");

  const { buildings, classrooms, isBuildingsLoading, isClassroomsLoading } = useClassroomData();
  const { createGroup, updateGroup, isCreating, isUpdating } = useGroupData();

  // Filter classrooms based on selected building
  const filteredClassrooms = classrooms?.filter(
    classroom => classroom.building?.id === formData.buildingId
  ) || [];

  const handlePriorityChange = (e) => {
    const value = e.target.value;
    const numberValue = parseInt(value);
    
    if (value === '') {
      setFormData({ ...formData, priority: '' });
      setPriorityError('');
      return;
    }

    if (isNaN(numberValue)) {
      setPriorityError("Vui lòng nhập số");
      return;
    }
    if (numberValue < 1 || numberValue > 10) {
      setPriorityError("Độ ưu tiên phải từ 1 đến 10");
      return;
    }
    setPriorityError("");
    setFormData({ ...formData, priority: numberValue });
  };

  const handleSubmit = async () => {
    const payload = {
      groupName: formData.groupName,
      roomName: formData.roomName,
      priority: formData.priority,
      id: selectedGroup?.id // Always include the id (parent groupId for new groups)
    };

    try {
      if (selectedGroup?.isNewGroup) {
        await createGroup(payload);
        onSuccess && await onSuccess();
      } else if (selectedGroup) {
        await updateGroup({ 
          ...payload,
          oldRoomName: selectedGroup.roomName
        });
        onSuccess && await onSuccess();
      }
      handleClose();
    } catch (error) {
      console.error(error);
    }
  };

  const getBuildingFromRoomName = (roomName) => {
    if (!roomName) return null;
    const buildingCode = roomName.split('-')[0]; // Get "D6" from "D6-101"
    return buildingCode;
  };

  useEffect(() => {
    if (selectedGroup) {
      const buildingCode = getBuildingFromRoomName(selectedGroup.roomName);
      setFormData({
        id: selectedGroup.id,
        groupName: selectedGroup.groupName,
        buildingId: buildingCode,
        roomName: selectedGroup.roomName,
        priority: selectedGroup.priority || 1
      });
    } else {
      setFormData({
        id: null,
        groupName: "",
        buildingId: "",
        roomName: "",
        priority: 1
      });
    }
  }, [selectedGroup, open]);

  const isFormValid = () => {
    const { groupName, roomName, priority } = formData;
    
    // All fields are required regardless of create/update
    if (!groupName || !roomName || !priority) {
      return false;
    }

    // Priority must be valid
    if (priorityError || priority < 1 || priority > 10) {
      return false;
    }

    return true;
  };

  console.log(buildings)
  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="xs"
      fullWidth
    >
      <DialogTitle style={{ paddingBottom: "8px" }}>
        {selectedGroup ? "Chỉnh sửa thông tin" : "Thêm nhóm mới"}
      </DialogTitle>
      <DialogContent style={{ paddingTop: "8px" }}>
        <TextField
          size="medium"
          margin="dense"
          label="Tên nhóm"
          fullWidth
          value={formData.groupName}
          onChange={(e) =>
            setFormData({ ...formData, groupName: e.target.value })
          }
          disabled={!!selectedGroup} 
        />

        <Autocomplete
          size="medium"
          options={buildings || []} 
          loading={isBuildingsLoading}
          value={formData.buildingId || ""}
          getOptionLabel={(option) => option || ""}
          isOptionEqualToValue={(option, value) => option === value}
          onChange={(_, newValue) =>
            setFormData({
              ...formData,
              buildingId: newValue || "",
              roomName: "", // Reset room when building changes
            })
          }
          renderInput={(params) => (
            <TextField {...params} label="Tòa nhà" margin="dense" required />
          )}
        />

        <Autocomplete
          size="medium"
          options={filteredClassrooms}
          loading={isClassroomsLoading}
          getOptionLabel={(option) => option.classroom}
          value={classrooms?.find(c => c.classroom === formData.roomName) || null}
          isOptionEqualToValue={(option, value) => option.classroom === value.classroom}
          onChange={(_, newValue) =>
            setFormData({
              ...formData,
              roomName: newValue?.classroom || "",
            })
          }
          renderInput={(params) => (
            <TextField {...params} label="Phòng học ưu tiên" margin="dense" required />
          )}
        />

        <TextField
          size="medium"
          fullWidth
          margin="dense"
          label="Độ ưu tiên"
          value={formData.priority}
          onChange={handlePriorityChange}
          error={!!priorityError}
          helperText={priorityError || "Nhập số từ 1 đến 10"}
          inputProps={{
            maxLength: 2
          }}
        />
      </DialogContent>
      <DialogActions>
        <Button size="small" onClick={handleClose}>
          Hủy
        </Button>
        <Button
          size="small"
          onClick={handleSubmit}
          disabled={!isFormValid() || isCreating || isUpdating}
        >
          {selectedGroup ? "Cập nhật" : "Tạo mới"}
        </Button>
      </DialogActions>
    </Dialog>
  );
});

export default CreateNewGroupScreen;
