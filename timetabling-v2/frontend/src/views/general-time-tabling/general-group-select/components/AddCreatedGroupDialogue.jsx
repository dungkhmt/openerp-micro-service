import { useState } from "react";
import { request } from "api";
import { toast } from "react-toastify";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import GeneralGroupAutoComplete from "views/general-time-tabling/common-components/GeneralGroupAutoComplete";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";

const AddCreatedGroupDialogue = ({ open, setOpen, setClasses, selectedClasses }) => {
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [isLoading, setLoading] = useState(false);

  const handleClose = (e) => {
    setOpen(false);
  };

  const handleSubmitAddToGroup = () => {
    const ids = Array.from(new Set(selectedClasses.map((gClass) => gClass).filter((id) => id !== null)));
    
    setLoading(true);
    request(
      "post",
      "/general-classes/update-classes-group",
      (res) => {
        let generalClasses = [];
        res.data?.forEach((classObj) => {
          if (classObj?.classCode !== null && classObj?.timeSlots) {
            delete classObj.timeSlots;
            generalClasses.push(classObj);
          }
        });
        setClasses(generalClasses);
        setLoading(false);
        toast.success("Thêm nhóm thành công!")
        handleClose();
      },
      (error) => {
        setLoading(false);
        console.log(error);
        handleClose();
        toast.error("Thêm nhóm lỗi!");
      },
      { ids, groupName: selectedGroup?.groupName, priorityBuilding: null }
    );
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
    >
      <DialogTitle>Thêm vào nhóm đã tạo</DialogTitle>
      <DialogContent>
        <GeneralGroupAutoComplete
          selectedGroup={selectedGroup}
          setSelectedGroup={setSelectedGroup}
        />
      </DialogContent>
      <DialogActions>
        {isLoading ? <FacebookCircularProgress/> : null}
        <Button onClick={handleClose}>Hủy</Button>
        <Button disabled={isLoading || selectedGroup === null} onClick={handleSubmitAddToGroup} type="submit">
          Xác nhận
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddCreatedGroupDialogue;
