import { useState } from "react";
import { 
  Dialog, DialogTitle, DialogContent, Button, TextField, MenuItem,
  Tabs, Tab, Box, Checkbox
} from "@mui/material";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { request } from "../../../../api";

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ pt: 2 }}>
          {children}
        </Box>
      )}
    </div>
  );
}

const ViewClassDetailDialog = ({ classData, isOpen, closeDialog }) => {
  const [subClasses, setSubClasses] = useState([]);
  const [openNewDialog, setOpenNewDialog] = useState(false);
  const [tabValue, setTabValue] = useState(0);
  const [groupName, setGroupName] = useState("");
  const [groups, setGroups] = useState([
    { id: 1, name: "Nhóm A", selected: false },
    { id: 2, name: "Nhóm B", selected: false },
    { id: 3, name: "Nhóm C", selected: false },
  ]);

  const [newSubClass, setNewSubClass] = useState({
    studentCount: "",
    classType: "",
    classCount: ""
  });

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleAddSubClass = async () => {
    const payload = {
      fromParentClassId: classData?.id,
      classType: newSubClass.classType,
      numberStudents: parseInt(newSubClass.studentCount, 10),
      duration: classData?.duration,
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

  const handleGroupSelectionChange = (id) => {
    setGroups(groups.map(group => 
      group.id === id ? { ...group, selected: !group.selected } : group
    ));
  };

  const handleUpdateGroups = () => {
    if (!groupName.trim()) return;
    
    // Add new group with the name from input
    const newGroup = {
      id: Math.max(...groups.map(g => g.id), 0) + 1,
      name: groupName,
      selected: false
    };
    
    setGroups([...groups, newGroup]);
    setGroupName(""); // Clear input after adding
  };

  const groupColumns = [
    {
      field: 'select',
      headerName: 'Select',
      width: 100,
      renderCell: (params) => (
        <Checkbox
          checked={params.row.selected}
          onChange={() => handleGroupSelectionChange(params.row.id)}
        />
      ),
    },
    { field: 'name', headerName: 'Nhóm', width: 200 },
  ];

  return (
    <>
      <Dialog open={isOpen} onClose={closeDialog} maxWidth="md" fullWidth>
        <DialogTitle>
          Thông tin của lớp: {classData?.id}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs value={tabValue} onChange={handleTabChange} aria-label="class details tabs">
              <Tab label="Thông tin lớp con" />
              <Tab label="Nhóm" />
            </Tabs>
          </Box>
          
          <TabPanel value={tabValue} index={0}>
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
                slots={{ toolbar: GridToolbar }}
                initialState={{
                  pagination: {
                    paginationModel: { pageSize: 5 }
                  }
                }}
                pageSizeOptions={[5]}
              />
            </div>
          </TabPanel>
          
          <TabPanel value={tabValue} index={1}>
            <div className="flex flex-row gap-2 mb-3">
              <TextField 
                label="Tên nhóm" 
                value={groupName}
                onChange={(e) => setGroupName(e.target.value)}
                size="small"
              />
              <Button 
                variant="contained" 
                onClick={handleUpdateGroups}
              >
                Update
              </Button>
            </div>
            <div style={{ height: 300, width: "100%" }}>
              <DataGrid
                rows={groups}
                columns={groupColumns}
                slots={{ toolbar: GridToolbar }}
                initialState={{
                  pagination: {
                    paginationModel: { pageSize: 5 }
                  }
                }}
                pageSizeOptions={[5]}
                disableRowSelectionOnClick
              />
            </div>
          </TabPanel>
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
