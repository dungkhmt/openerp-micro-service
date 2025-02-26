import { useState } from "react";
import { Dialog, DialogTitle, DialogContent, Button, TextField, MenuItem } from "@mui/material";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
// Replace axios import with request from api
import { request } from "../../../../api";

const ViewClassDetailDialog = ({ classData, isOpen, closeDialog }) => {
  const [subClasses, setSubClasses] = useState([]);
  const [openNewDialog, setOpenNewDialog] = useState(false);
  const [newSubClass, setNewSubClass] = useState({
    studentCount: "",
    classType: "",
    classCount: ""
  });

  const handleAddSubClass = async () => {
    const payload = {
      fromParentClassId: classData?.id,
      classType: newSubClass.classType,
      numberStudents: parseInt(newSubClass.studentCount, 10),
      duration: classData?.duration, // use duration from parent data
      numberClasses: parseInt(newSubClass.classCount, 10)
    };
    try {
      await request(
        "post",
        "/plan-general-classes/make-subclass",
        (res) => setSubClasses([...subClasses, res.data]),
        (err) => { throw err; },
        payload
      );
    } catch (error) {
      console.error("Failed to add subclass", error);
    }
    setOpenNewDialog(false);
    setNewSubClass({ studentCount: "", classType: "", classCount: "" });
  };

  return (
    <>
      <Dialog open={isOpen} onClose={closeDialog} maxWidth="md" fullWidth>
        <DialogTitle>
          Thông tin các lớp con của lớp: {classData?.id}
        </DialogTitle>
        <DialogContent>
          <div style={{ margin: "16px 0" }}>
            <Button variant="contained" onClick={() => setOpenNewDialog(true)}>Thêm mới</Button>
          </div>
          <div style={{ height: 300, width: "100%" }}>
            <DataGrid
              rows={subClasses}
              columns={[
                { field: "id", headerName: "ID", width: 70 },
                { field: "studentCount", headerName: "Số lượng sinh viên", width: 200 },
                { field: "classType", headerName: "Loại lớp", width: 150 },
                { field: "classCount", headerName: "SL lớp", width: 100 },
              ]}
              components={{ Toolbar: GridToolbar }}
              pageSize={5}
              rowsPerPageOptions={[5]}
            />
          </div>
        </DialogContent>
      </Dialog>

      <Dialog open={openNewDialog} onClose={() => setOpenNewDialog(false)}>
        <DialogTitle>Thêm lớp con</DialogTitle>
        <DialogContent>
          <div className="flex flex-col gap-2 p-4 items-end">
            <TextField
              label="Số lượng sinh viên"
              type="number"
              value={newSubClass.studentCount}
              onChange={(e) =>
                setNewSubClass({ ...newSubClass, studentCount: e.target.value })
              }
              fullWidth
            />
            <TextField
              select
              label="Loại lớp"
              value={newSubClass.classType}
              onChange={(e) =>
                setNewSubClass({ ...newSubClass, classType: e.target.value })
              }
              fullWidth
            >
              <MenuItem value="LT">LT</MenuItem>
              <MenuItem value="NT">NT</MenuItem>
              <MenuItem value="TH">TH</MenuItem>
              <MenuItem value="TN">TN</MenuItem>
            </TextField>
            <TextField
              label="SL lớp"
              type="number"
              value={newSubClass.classCount}
              onChange={(e) =>
                setNewSubClass({ ...newSubClass, classCount: e.target.value })
              }
              fullWidth
            />
            <Button onClick={handleAddSubClass} variant="contained" color="primary">
              Xác nhận
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default ViewClassDetailDialog;
