import { useState } from "react";
import { request } from "api";
import { toast } from "react-toastify";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import FirstYearGroupAutoComplete from "views/first-year-time-tabling/common-components/FirstYearGroupAutoComplete";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";

const AddCreatedGroupDialogue = ({ open, setOpen, selectedClasses, onSuccess }) => {
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
        setLoading(false);
        onSuccess(); // Call refresh instead of setting classes directly
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
        <FirstYearGroupAutoComplete
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
