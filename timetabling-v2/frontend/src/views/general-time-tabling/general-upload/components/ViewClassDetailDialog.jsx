import { useState, useEffect } from "react";
import { 
  Dialog, DialogTitle, DialogContent, Button, TextField, MenuItem,
  Tabs, Tab, Box, Checkbox, CircularProgress
} from "@mui/material";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { request } from "api";
import { useGeneralSchedule } from "services/useGeneralScheduleData";

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
        <Box sx={{ pt: 1 }}>
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
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [filteredGroups, setFilteredGroups] = useState([]);

  const { handlers } = useGeneralSchedule();

  const [newSubClass, setNewSubClass] = useState({
    studentCount: "",
    classType: "",
    classCount: ""
  });

  useEffect(() => {
    if (isOpen && classData?.id && tabValue === 1) {
      fetchClassGroups();
    }
  }, [isOpen, classData?.id, tabValue]);

  const fetchClassGroups = async () => {
    if (!classData?.id) return;
    
    setLoading(true);
    try {
      const data = await handlers.getClassGroups(classData.id);
      setGroups(data || []);
      console.log(data);
    } catch (error) {
      console.error("Failed to fetch class groups", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (searchText.trim() === "") {
      setFilteredGroups(groups);
    } else {
      const filtered = groups.filter(group => 
        group.groupName.toLowerCase().includes(searchText.toLowerCase())
      );
      setFilteredGroups(filtered);
    }
  }, [groups, searchText]);

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

  const handleGroupSelectionChange = async (id) => {
    const group = groups.find(g => g.id === id);
    if (!group) return;
    try {
      if (!group.assigned) {
        await handlers.updateClassGroup(classData?.id, id);
      } else {
        await handlers.deleteClassGroup(classData?.id, id);
      }
      setGroups(groups.map(g => g.id === id ? { ...g, assigned: !g.assigned } : g));
    } catch (error) {
      console.error("Failed to update group", error);
    }
  };

  const handleSelectAllGroups = (event) => {
    const checked = event.target.checked;
    setGroups(groups.map(group => ({
      ...group,
      assigned: checked
    })));
  };

  const areAllGroupsSelected = groups.length > 0 && groups.every(group => group.assigned);

  const handleUpdateGroups = () => {
    if (!groupName.trim()) return;
    
    const newGroup = {
      id: Math.max(...groups.map(g => g.id || 0), 0) + 1,
      groupName: groupName,
      assigned: false
    };
    
    setGroups([...groups, newGroup]);
    setGroupName(""); // Clear input after adding
  };

  const groupColumns = [
    {
      field: 'assigned',
      headerName: 'Đã chọn',
      width: 130, 
      renderHeader: () => (
        <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
          <Checkbox 
            checked={areAllGroupsSelected}
            onChange={handleSelectAllGroups}
            indeterminate={groups.some(g => g.assigned) && !areAllGroupsSelected}
            size="small"
          />
          <span className="MuiDataGrid-columnHeaderTitle font-medium">Đã chọn</span>
        </div>
      ),
      renderCell: (params) => (
        <Checkbox
          checked={params.row.assigned}
          onChange={() => handleGroupSelectionChange(params.row.id)}
        />
      ),
    },
    { field: 'groupName', headerName: 'Nhóm', width: 300 },
  ];

  return (
    <>
      <Dialog 
        open={isOpen} 
        onClose={closeDialog} 
        maxWidth="md" 
        fullWidth
      >
        <DialogTitle 
          sx={{ 
            pb: 0, 
            pt: 1, 
            fontSize: '1rem',
            minHeight: '40px',
            display: 'flex',
            alignItems: 'center' 
          }}
        >
          Thông tin của lớp: {classData?.id}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 1, mt: 0 }}>
            <Tabs value={tabValue} onChange={handleTabChange} aria-label="class details tabs">
              <Tab label="Thông tin lớp con" />
              <Tab label="Nhóm" />
            </Tabs>
          </Box>
          
          <TabPanel value={tabValue} index={0}>
            <div style={{ margin: "8px 0" }}>
              <Button variant="contained" onClick={() => setOpenNewDialog(true)}>Thêm mới</Button>
            </div>
            <div style={{ height: 250, width: "100%" }}>
              <DataGrid
                rows={subClasses}
                columns={[
                  { field: "id", headerName: "ID", width: 70 },
                  { field: "studentCount", headerName: "Số lượng sinh viên", width: 200 },
                  { field: "classType", headerName: "Loại lớp", width: 150 },
                  { field: "classCount", headerName: "SL lớp", width: 100 },
                ]}
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
            <div className="flex flex-row gap-1 mb-1 items-center justify-end">
              <TextField 
                label="Tìm kiếm theo tên nhóm" 
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
                size="small"
                sx={{ width: '250px' }}
                placeholder="Nhập tên nhóm để tìm kiếm..."
              />
            </div>
            <div style={{ height: 350, width: "100%" }}>
              {loading ? (
                <div className="flex justify-center items-center h-full">
                  <CircularProgress />
                </div>
              ) : (
                <DataGrid
                  rows={filteredGroups}
                  columns={groupColumns}
                  disableRowSelectionOnClick
                  paginationModel={{ page: 0, pageSize: 10 }}
                  pageSizeOptions={[10, 25, 100]}
                />
              )}
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
