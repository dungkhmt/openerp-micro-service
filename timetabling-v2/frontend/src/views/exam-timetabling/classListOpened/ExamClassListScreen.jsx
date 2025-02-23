import { useCallback, useMemo, useState } from "react";
import { useSemesterData } from "services/useSemesterData";
import { COLUMNS } from "./utils/gridColumns";
import { Autocomplete, Box, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useExamClassOpenedData } from "services/useExamClassOpenedData"


export default function TimePerformanceScreen() {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [successDialogOpen, setSuccessDialogOpen] = useState(false);
  const [conflictDialogOpen, setConflictDialogOpen] = useState(false);
  const [conflictList, setConflictList] = useState([]);
  const [keyword, setKeyword] = useState("");
  

  let {
    examClasses,
    isLoading: isLoadingClasses,
    importExcel,
    exportConflicts,
    isExporting,
    clearAll,
    isClearing,
  } = useExamClassOpenedData();

  examClasses = examClasses.map((examClass, index) => ({
    ...examClass,
    id: index
  }));

  const handleKeywordChange = useCallback((e) => {
    setKeyword(e.target.value);
  }, []);
  

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

  const handleClearAll = async () => {
    await clearAll();
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

  function DataGridToolbar() {
    return (
      <div>
        <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
          <Autocomplete
            options={semesters}
            getOptionLabel={(option) => option.semester}
            style={{ width: 150, margin: "8px" }}
            value={selectedSemester}
            onChange={handleSelectSemester}
            renderInput={(params) => (
              <TextField {...params} label="Chọn kỳ học" variant="outlined" />
            )}
          />
        </div>
        <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
          <Button variant="outlined" color="primary" onClick={handleClearAll}>
            Xóa danh sách lớp
          </Button>
          <Button
            variant="outlined"
            color="primary"
            style={{ marginRight: "8px" }}
            onClick={handleImportExcel}
            disabled={!selectedSemester}
          >
            Tải lên danh sách lớp
          </Button>
        </div>
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
      </div>
    );
  }

  function DataGridTitle() {
    return (
      <Box
        style={{
          width: "100%",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        <Typography variant="h5">Danh sách lớp thi</Typography>
      </Box>
    );
  }

  return (
    <div style={{ height: 600, width: "100%" }}>
      {(isLoadingClasses || isClearing || isExporting) && (
        <CircularProgress
          style={{ position: "absolute", top: "50%", left: "50%" }}
        />
      )}
      <DataGrid
        components={{
          Toolbar: () => (
            <>
              <DataGridTitle />
              <DataGridToolbar/>
            </>
          ),
        }}
        rows={filteredExamClasses}
        columns={COLUMNS}
        pageSize={10}
      />
    </div>
  );
}
