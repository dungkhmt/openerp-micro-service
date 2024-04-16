import React, { useEffect, useState } from "react";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import { Autocomplete } from "@mui/material";
import { request } from "api";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import { toast } from "react-toastify";


const AddNewGroupDialogue = ({ open, setOpen, selectedClasses, setClasses }) => {
  const [groupName, setGroupName] = useState("");
  const [buildings, setBuildings] = useState([]);
  const [selectedBuildingName, setSelectedBuildingName] = useState("");
  const [isLoading, setLoading] = useState(false);


  const handleClose = (e) => {
    setOpen(false);
  };

  useEffect(() => {
    request("get", "/classroom/get-all-building", (res) => {
      setBuildings(res.data);
    });
  }, []);

  const handleBuildingpriorityChange = (event, newValue) => {
    setSelectedBuildingName(newValue);
  };

  const handleRequestUpdateClassesGroupName = () => {
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
        console.log(generalClasses);
        setClasses(generalClasses);
        setLoading(false);
        toast.info("Thêm nhóm thành công!")
        handleClose();
      },
      (error) => {
        setLoading(false);
        console.log(error);
        handleClose();
        toast.error("Thêm nhóm lỗi, nhóm đã có hoặc mã lớp không tồn tại!");
      },
      { ids, groupName, priorityBuilding: selectedBuildingName }
    );
  };

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>Thêm vào nhóm đã tạo</DialogTitle>
      <DialogContent>
        <div className="flex gap-2 flex-col">
          <TextField
            value={groupName}
            label="Nhập tên nhóm mới"
            onChange={(e) => {
              setGroupName(e.target.value);
            }}
          />
          <Autocomplete
            options={buildings}
            getOptionLabel={(option) => option}
            style={{ width: 250, marginTop: "8px" }}
            value={selectedBuildingName}
            renderInput={(params) => (
              <TextField {...params} label="Tòa nhà ưu tiên" />
            )}
            onChange={handleBuildingpriorityChange}
          />
        </div>
      </DialogContent>
      <DialogActions>
        {isLoading ? <FacebookCircularProgress/> : null}
        <Button onClick={handleClose}>Hủy</Button>
        <Button
          type="submit"
          disabled={isLoading}
          onClick={() => {
            handleRequestUpdateClassesGroupName();
          }}
        >
          Xác nhận
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddNewGroupDialogue;
