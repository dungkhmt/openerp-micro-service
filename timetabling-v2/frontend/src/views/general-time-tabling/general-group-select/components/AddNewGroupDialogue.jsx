import { useEffect, useState } from "react";
import { request } from "api";
import { toast } from "react-toastify";
import { Autocomplete, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";


const AddNewGroupDialogue = ({ open, setOpen, selectedClasses, setClasses }) => {
  const [groupName, setGroupName] = useState("");
  const [buildings, setBuildings] = useState([]);
  const [selectedBuildingName, setSelectedBuildingName] = useState([]);
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
    console.log(newValue.join(","))
    setSelectedBuildingName(newValue);
  };

  const handleRequestUpdateClassesGroupName = () => {
    const ids = Array.from(new Set(selectedClasses.filter((id) => id !== null)));
    console.log(ids);
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
        console.log(generalClasses);
        setClasses(generalClasses);
        setLoading(false);
        toast.success("Thêm nhóm thành công!")
        handleClose();
      },
      (error) => {
        setLoading(false);
        console.log(error);
        handleClose();
        toast.error("Thêm nhóm lỗi, nhóm đã có hoặc mã lớp không tồn tại!");
      },
      { ids, groupName, priorityBuilding: selectedBuildingName.join(",") }
    );
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
          <Autocomplete
            multiple
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
