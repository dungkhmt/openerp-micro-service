import { useCallback, useMemo, useState } from "react";
import { useSemesterData } from "services/useSemesterData";
import { COLUMNS } from "./utils/gridColumns";
import { 
  Autocomplete, 
  Box, 
  Button, 
  CircularProgress, 
  Dialog, 
  DialogActions, 
  DialogContent, 
  DialogTitle, 
  TextField, 
  Typography 
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useExamClassData } from "services/useExamClassData"
import EditExamClassModal from './utils/EditExamClassModal';

export default function TimePerformanceScreen() {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [successDialogOpen, setSuccessDialogOpen] = useState(false);
  const [conflictDialogOpen, setConflictDialogOpen] = useState(false);
  const [conflictList, setConflictList] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [selectedRow, setSelectedRow] = useState(null);
  const [selectedDeletedRows, setSelectedDeletedRows] = useState(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState(false);
  const [editFormData, setEditFormData] = useState({
    examClassId: '',
    classId: '',
    courseId: '',
    courseName: '',
    description: '',
    groupId: '',
    numberOfStudents: '',
    school: '',
    period: '',
    managementCode: '',
  });

  let {
    examClasses,
    isLoading: isLoadingClasses,
    importExcel,
    exportClasses,
    exportConflicts,
    isExportingClasses,
    isExportingConflicts,
    deleteExamClasses,
    isClearing,
    updateExamClass,
  } = useExamClassData();

  examClasses = examClasses.map((examClass, index) => ({
    ...examClass,
    id: examClass.examClassId
  }));


  // Filter examClasses based on keyword using useMemo
  const filteredExamClasses = useMemo(() => {
    if (!keyword.trim()) return examClasses;
    
    const searchTerm = keyword.toLowerCase().trim();
    return examClasses.filter(examClass => {
      // Add all the fields you want to search through
      const searchableFields = [
        examClass.examClassId,
        examClass.classId,
        examClass.courseId,
        examClass.courseName,
        examClass.description,
      ];
      
      return searchableFields.some(field => 
        String(field).toLowerCase().includes(searchTerm)
      );
    });
  }, [examClasses, keyword]);

  const { semesters } = useSemesterData();

  const handledeleteExamClasses = async () => {
    await deleteExamClasses(selectedDeletedRows);
  };

  const handleImportExcel = () => {
    const input = document.createElement("input");
    input.setAttribute("type", "file");
    input.setAttribute("accept", ".xlsx, .xls");

    input.onchange = async (e) => {
      const file = e.target.files[0];
      if (file && selectedSemester) {
        const formData = new FormData();
        formData.append("file", file);

        try {
          const result = await importExcel(formData, selectedSemester.semester);
          if (result.data.length === 0) {
            setSuccessDialogOpen(true);
          } else {
            setConflictList(result.data.map(examClass => examClass.examClassId));
            setConflictDialogOpen(true);
          }
        } catch (error) {
          console.error("Error uploading file", error);
        }
      }
    };

    input.click();
  };

  const handleSelectSemester = (event, newValue) => {
    setSelectedSemester(newValue);
  };

  const handleDialogClose = () => {
    setSuccessDialogOpen(false);
    setConflictDialogOpen(false);
    window.location.reload();
  };

  const handleDownloadConflictList = async () => {
    try {
      await exportConflicts(conflictList);
    } catch (error) {
      console.error("Error exporting conflicts:", error);
    }
  };

  const handleExportExamClasses = async () => {
    try {
      await exportClasses(examClasses.map(examClass => examClass.examClassId), );
    } catch (error) {
      console.error("Error exporting conflicts:", error);
    }
  };

  const handleRowClick = (params) => {
    setSelectedRow(params.row);
    setEditFormData({
      examClassId: params.row.examClassId || '',
      classId: params.row.classId || '',
      courseId: params.row.courseId || '',
      courseName: params.row.courseName || '',
      description: params.row.description || '',
      groupId: params.row.groupId || '',
      numberOfStudents: params.row.numberOfStudents || '',
      school: params.row.school || '',
      period: params.row.period || '',
      managementCode: params.row.managementCode || '',
    });
    setIsEditModalOpen(true);
  };
  
  const handleCloseEditModal = () => {
    setIsEditModalOpen(false);
    setSelectedRow(null);
    setEditFormData({
      examClassId: '',
      classId: '',
      courseId: '',
      courseName: '',
      description: '',
      groupId: '',
      numberOfStudents: '',
      school: '',
      period: '',
      managementCode: '',
    });
  };
  
  const handleFormChange = (event) => {
    const { name, value } = event.target;
    setEditFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };
  
  const handleSubmitEdit = async () => {
    await updateExamClass(editFormData);
    handleCloseEditModal();
  };

  const handleDeleteClick = () => {
    setIsDeleteConfirmOpen(true);
  };
  
  const handleConfirmDelete = async () => {
    try {
      await deleteExamClasses(selectedDeletedRows);
      setIsDeleteConfirmOpen(false);
    } catch (error) {
      console.error("Error deleting classes:", error);
    }
  };
  
  const handleCancelDelete = () => {
    setIsDeleteConfirmOpen(false);
  };

  function DataGridToolbar() {
    return (
      <Box sx={{ p: 2 }}>
        <Box sx={{ display: "flex", justifyContent: "flex-end", mb: 2 }}>
          {/* Semester selection on the right */}
          <Autocomplete
            options={semesters}
            getOptionLabel={(option) => option.semester}
            style={{ width: 150 }}
            value={selectedSemester}
            onChange={handleSelectSemester}
            renderInput={(params) => (
              <TextField {...params} label="Chọn kỳ học" variant="outlined" size="small" />
            )}
          />
        </Box>
        
        {/* Buttons aligned to the right */}
        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 2 }}>
          <Button variant="outlined" color="primary" onClick={handleDeleteClick}>
            Xóa lớp
          </Button>
          <Button
            variant="outlined"
            color="primary"
            onClick={handleImportExcel}
            disabled={!selectedSemester}
          >
            Tải lên DS lớp
          </Button>
          {examClasses.length > 0 && (
            <Button 
              onClick={handleExportExamClasses}
              color="primary"
              variant="outlined"
            >
              Tải xuống DS lớp
            </Button>
          )}
        </Box>

        <Dialog
          open={isDeleteConfirmOpen}
          onClose={handleCancelDelete}
        >
          <DialogTitle>Xác nhận xóa</DialogTitle>
          <DialogContent>
            <Typography>
              Bạn có chắc chắn muốn xóa các lớp đã chọn không?
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCancelDelete} color="primary">
              Hủy
            </Button>
            <Button onClick={handleConfirmDelete} color="error" variant="contained" autoFocus>
              Xóa
            </Button>
          </DialogActions>
        </Dialog>
        
        <Dialog
          open={successDialogOpen || conflictDialogOpen}
          onClose={handleDialogClose}
        >
          <DialogTitle>
            {successDialogOpen
              ? "Tải lên danh sách thành công"
              : "Danh sách lớp bị trùng"}
          </DialogTitle>
          <DialogContent>
            {conflictDialogOpen ? (
              <ul>
                {conflictList.map((conflict) => (
                  <li key={conflict.id}>{conflict.moduleName}</li>
                ))}
              </ul>
            ) : null}
          </DialogContent>
          <DialogActions>
            <Button onClick={handleDialogClose} color="primary" autoFocus>
              OK
            </Button>
            {conflictDialogOpen && (
              <Button onClick={handleDownloadConflictList} color="primary">
                Tải xuống
              </Button>
            )}
          </DialogActions>
        </Dialog>
      </Box>
    );
  }

  function DataGridTitle() {
    return (
      <Box
        sx={{
          width: "100%",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          py: 2
        }}
      >
        <Typography variant="h5">Danh sách lớp thi</Typography>
      </Box>
    );
  }

  return (
    <div style={{ height: 600, width: "100%" }}>
      {(isLoadingClasses || isClearing || isExportingConflicts || isExportingClasses) && (
        <CircularProgress
          style={{ position: "absolute", top: "50%", left: "50%" }}
        />
      )}
      <DataGrid
        components={{
          Toolbar: () => (
            <>
              <DataGridTitle />
              <DataGridToolbar />
            </>
          ),
        }}
        rows={filteredExamClasses}
        columns={COLUMNS}
        pageSize={10}
        onRowClick={handleRowClick}
        checkboxSelection disableRowSelectionOnClick
        onRowSelectionModelChange={(ids) => setSelectedDeletedRows(ids)}
      />

      <EditExamClassModal 
        open={isEditModalOpen}
        onClose={handleCloseEditModal}
        formData={editFormData}
        onChange={handleFormChange}
        onSubmit={handleSubmitEdit}
      />
    </div>
  );
}
