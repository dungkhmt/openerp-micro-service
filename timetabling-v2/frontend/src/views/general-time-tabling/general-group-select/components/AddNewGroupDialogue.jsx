import { useState } from "react";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import { useGeneralSchedule } from "services/useGeneralScheduleData";

const AddNewGroupDialogue = ({ open, setOpen, selectedClasses, setClasses }) => {
  const [groupName, setGroupName] = useState("");
  
  const { handlers, states } = useGeneralSchedule();
  const { isUpdatingClassesGroup } = states;

  const handleClose = () => {
    setOpen(false);
  };

  const handleRequestUpdateClassesGroupName = async () => {
    const ids = Array.from(new Set(selectedClasses.filter((id) => id !== null)));
    
    try {
      const response = await handlers.updateClassesGroup({
        ids,
        groupName,
      });
      
      let generalClasses = [];
      response?.data?.forEach((classObj) => {
        if (classObj?.classCode !== null && classObj?.timeSlots) {
          delete classObj.timeSlots;
          generalClasses.push(classObj);
        }
      });
      
      setClasses(generalClasses);
      handleClose();
    } catch (error) {
      handleClose();
    }
  };

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>Thêm vào nhóm mới</DialogTitle>
      <DialogContent>
        <div className="flex gap-2 flex-col">
          <TextField
            value={groupName}
            label="Nhập tên nhóm mới"
            onChange={(e) => {
              setGroupName(e.target.value);
            }}
          />
        </div>
      </DialogContent>
      <DialogActions>
        {isUpdatingClassesGroup ? <FacebookCircularProgress/> : null}
        <Button onClick={handleClose}>Hủy</Button>
        <Button
          type="submit"
          disabled={isUpdatingClassesGroup}
          onClick={handleRequestUpdateClassesGroupName}
        >
          Xác nhận
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddNewGroupDialogue;
