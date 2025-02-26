import { useEffect, useState } from "react"
import { useExamPlanData } from "services/useExamPlanData"
import { COLUMNS } from "./utils/gridColumns"
import {
  Autocomplete,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField,
  Tooltip,
  Typography
} from "@mui/material"
import { HelpOutline } from "@mui/icons-material"
import { DataGrid } from "@mui/x-data-grid"
import { useExamClassData } from "services/useExamClassData"
import EditExamClassModal from './utils/EditExamClassModal'
import AddExamClassModal from "./utils/AddExamClassModal"
import localText from "./utils/LocalText"

export default function TimePerformanceScreen() {
  // const [ examClasses, setExamClasses ] = useState([]);
  const [selectedExamPlan, setSelectedExamPlan] = useState(null)
  const [successDialogOpen, setSuccessDialogOpen] = useState(false)
  const [conflictDialogOpen, setConflictDialogOpen] = useState(false)
  const [conflictList, setConflictList] = useState([])
  const [selectedRow, setSelectedRow] = useState(null)
  const [selectedRows, setSelectedRows] = useState([])
  const [isEditModalOpen, setIsEditModalOpen] = useState(false)
  const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState(false)
  const [isAddModalOpen, setIsAddModalOpen] = useState(false)
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
    id: '',
    examPlanId: '',
  })

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
    createExamClass,
    downloadSample,
    isImporting,
  } = useExamClassData(selectedExamPlan?.id)

  const { examPlans } = useExamPlanData();

  useEffect(() => {
    if (examPlans && examPlans.length > 0 && !selectedExamPlan) {
      setSelectedExamPlan(examPlans[0]);
    }
  }, [examPlans, selectedExamPlan]);


  const handleImportExcel = () => {
    const input = document.createElement("input")
    input.setAttribute("type", "file")
    input.setAttribute("accept", ".xlsx, .xls")

    input.onchange = async (e) => {
      const file = e.target.files[0]
      if (file && selectedExamPlan) {
        const formData = new FormData()
        formData.append("file", file)

        try {
          const result = await importExcel(formData)
          if (result.data.length === 0) {
            setSuccessDialogOpen(true)
          } else {
            setConflictList(result.data.map(examClass => examClass.id))
            setConflictDialogOpen(true)
          }
        } catch (error) {
          console.error("Error uploading file", error)
        }
      }
    }

    input.click()
  }

  const handleSelectExamPlan = (event, examPlan) => {
    setSelectedExamPlan(examPlan);
    // Clear selected rows when changing exam plan
    setSelectedRows([]);
  }

  const handleDialogClose = () => {
    setSuccessDialogOpen(false)
    setConflictDialogOpen(false)
    window.location.reload()
  }

  const handleDownloadConflictList = async () => {
    try {
      await exportConflicts(conflictList)
      setSuccessDialogOpen(false)
    } catch (error) {
      console.error("Error exporting conflicts:", error)
    }
  }

  const handleExportExamClasses = async () => {
    try {
      await exportClasses(selectedRows)
    } catch (error) {
      console.error("Error exporting conflicts:", error)
    }
  }

  const handleRowClick = (params) => {
    setSelectedRow(params.row)
    setEditFormData({
      id: params.row.id || '',
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
      examPlanId: params.row.examPlanId || '',
    })
    setIsEditModalOpen(true)
  }

  const handleCloseEditModal = () => {
    setIsEditModalOpen(false)
    setSelectedRow(null)
    setEditFormData({
      id: '',
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
      examPlanId: '',
    })
  }

  const handleFormChange = (event) => {
    const { name, value } = event.target
    setEditFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmitEdit = async () => {
    await updateExamClass(editFormData)
    handleCloseEditModal()
  }

  const handleDeleteClick = () => {
    setIsDeleteConfirmOpen(true)
  }

  const handleConfirmDelete = async () => {
    try {
      setIsDeleteConfirmOpen(false)
      await deleteExamClasses(selectedRows)
      setSelectedRows([])
    } catch (error) {
      console.error("Error deleting classes:", error)
      setIsDeleteConfirmOpen(false)
    }
  }

  const handleCancelDelete = () => {
    setIsDeleteConfirmOpen(false)
  }

  const handleAddClick = () => {
    setIsAddModalOpen(true)
  }

  const handleCloseAddModal = () => {
    setIsAddModalOpen(false)
  }

  const handleAddSubmit = async (formData) => {
    try {
      setIsAddModalOpen(false) // Close the modal first

      await createExamClass(formData)
    } catch (error) {
      console.error("Error adding exam class:", error)
      setIsAddModalOpen(false) // Still close modal even if error occurs
    }
  }

  const handleDownloadSample = () => {
    try {
      downloadSample()
    } catch (error) {
      console.error("Error downloading sample file:", error)
    }
  }

  function DataGridToolbar() {
    return (
      <Box sx={{ px: 2, pb: 2 }}>
        <Box sx={{ display: "flex", justifyContent: "flex-end", mb: 2 }}>
          {/* ExamPlan selection on the right */}
          <Autocomplete
            options={examPlans}
            getOptionLabel={(option) => option.name}
            style={{ width: 230 }}
            value={selectedExamPlan}
            onChange={handleSelectExamPlan}
            renderInput={(params) => (
              <TextField {...params} label="Chọn kế hoạch thi" variant="outlined" size="small" />
            )}
          />
        </Box>

        {/* Buttons aligned to the right */}
        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 2 }}>
          <Button
            variant="outlined"
            color="primary"
            onClick={handleDeleteClick}
            disabled={selectedRows.length === 0 || isClearing || isImporting || isExportingClasses || isExportingConflicts || !selectedExamPlan}
          >
            Xóa lớp
          </Button>
          <Button
            variant="outlined"
            color="primary"
            onClick={handleAddClick}
            disabled={isClearing || isImporting || isExportingClasses || isExportingConflicts || !selectedExamPlan}
          >
            Thêm lớp
          </Button>

          <Button
            onClick={handleExportExamClasses}
            color="primary"
            variant="outlined"
            disabled={selectedRows.length === 0 || isClearing || isImporting || isExportingClasses || isExportingConflicts || !selectedExamPlan}
          >
            Tải xuống DS lớp
          </Button>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Button
              variant="outlined"
              color="primary"
              onClick={handleImportExcel}
              disabled={!selectedExamPlan || isClearing || isImporting || isExportingClasses || isExportingConflicts}
            >
              Tải lên DS lớp
            </Button>
            <Tooltip title="Tải xuống file mẫu">
              <IconButton
                onClick={handleDownloadSample}
                size="small"
                sx={{ ml: 0 }}
              >
                <HelpOutline fontSize="small" />
              </IconButton>
            </Tooltip>
          </Box>
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
    )
  }

  function DataGridTitle() {
    return (
      <Box
        sx={{
          width: "100%",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          pt: 2,
        }}
      >
        <Typography
          variant="h4"
          sx={{
            fontWeight: 700,
            color: '#1976d2',
            position: 'relative',
          }}
        >
          Danh Sách Lớp Thi
        </Typography>
      </Box>
    )
  }

  return (
    <div style={{ height: 600, width: "100%" }}>
      {(isLoadingClasses || isClearing || isExportingConflicts || isExportingClasses || isImporting) && (
        <CircularProgress
          style={{ position: "absolute", top: "50%", left: "50%" }}
        />
      )}
      <DataGrid
        localeText={localText}
        components={{
          Toolbar: () => (
            <>
              <DataGridTitle />
              <DataGridToolbar />
            </>
          ),
        }}
        autoHeight
        rows={examClasses}
        columns={COLUMNS}
        pageSizeOptions={[10, 20, 50, 100]}
        onRowClick={handleRowClick}
        pageSize={10}  // Make sure this matches one of the options
        initialState={{
          pagination: { paginationModel: { pageSize: 10 } },
        }}
        checkboxSelection
        disableRowSelectionOnClick
        onRowSelectionModelChange={(ids) => setSelectedRows(ids)}
        sx={{
          '& .MuiDataGrid-columnHeaders': {
            backgroundColor: '#5495e8',  // Theme primary color
            color: '#fff',
            fontSize: '15px',
            fontWeight: 'bold',
          },
          '& .MuiDataGrid-row:nth-of-type(even)': {
            backgroundColor: '#f9f9f9',
          },
          '& .MuiDataGrid-columnHeader': {
            '&:focus': {
              outline: 'none',
            },
          },
          '& .MuiDataGrid-columnHeaderTitle': {
            fontWeight: 'bold',
          },
        }}
      />

      <EditExamClassModal
        open={isEditModalOpen}
        onClose={handleCloseEditModal}
        formData={editFormData}
        onChange={handleFormChange}
        onSubmit={handleSubmitEdit}
      />


      <AddExamClassModal
        open={isAddModalOpen}
        onClose={handleCloseAddModal}
        onSubmit={(editFormData) => {
          handleAddSubmit({
            ...editFormData,
            examPlanId: selectedExamPlan.id
          })
        }}
      />
    </div>
  )
}
