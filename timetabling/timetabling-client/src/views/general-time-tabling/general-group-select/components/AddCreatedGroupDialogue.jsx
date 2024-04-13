import React, { useState } from "react";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import GeneralGroupAutoComplete from "views/general-time-tabling/common-components/GeneralGroupAutoComplete";
import { request } from "api";
import { toast } from "react-toastify";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";

const AddCreatedGroupDialogue = ({ open, setOpen, setClasses, selectedClasses }) => {
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [isLoading, setLoading] = useState(false);

  const handleClose = (e) => {
    setOpen(false);
  };

  const handleSubmitAddToGroup = () => {
    const ids = Array.from(new Set(selectedClasses.map((gClass) => gClass?.split("-")?.[0]).filter((id) => id !== null)));
    console.log(ids);
    setLoading(true);
    request(
      "post",
      "/general-classes/update-classes-group",
      (res) => {
        let generalClasses = [];
        res.data?.forEach((classObj) => {
          if (classObj?.classCode !== null && classObj?.timeSlots) {
            classObj.timeSlots.forEach((timeSlot, index) => {
              const cloneObj = JSON.parse(
                JSON.stringify({
                  ...classObj,
                  ...timeSlot,
                  classCode: classObj.classCode,
                  id: classObj.id + `-${index + 1}`,
                })
              );
              delete cloneObj.timeSlots;
              generalClasses.push(cloneObj);
            });
          }
        });
        setClasses(generalClasses);
        setLoading(false);
        toast.info("Thêm nhóm thành công!")
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
