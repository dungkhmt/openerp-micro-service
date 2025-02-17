import { Button } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { useState } from 'react';
import { useClassroomData } from 'services/useClassroomData';
import { ClassroomToolbar } from './components/ClassroomToolbar';
import CreateNewClassroomScreen from './CreateNewClassroomScreen';
import { DeleteConfirmDialog } from './components/DeleteConfirmDialog';

export default function ClassroomListScreen() {
  const { classrooms, deleteClassroom, importExcel, isLoading, isImporting, refetchClassrooms } = useClassroomData();
  const [selectedClassroom, setSelectedClassroom] = useState(null);
  const [isDialogOpen, setDialogOpen] = useState(false);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [deleteClassroomCode, setDeleteClassroomCode] = useState(null);
  const [openLoading, setOpenLoading] = useState(false);

  console.log(classrooms);
  const columns = [
    {
      headerName: "STT",
      field: "index",
      width: 120,
      renderCell: (params) => params.api.getRowIndex(params.row.id) + 1,
      renderCell: (params) => {
        return params.id;
      }
    },
    {
      headerName: "Lớp học",
      field: "classroom",
      width: 120
    },
    {
      headerName: "Tòa nhà",
      field: "building",
      width: 120,
      valueGetter: (params) => params.row.building?.name
    },
    {
      headerName: "Số lượng chỗ ngồi",
      field: "quantityMax",
      width: 170
    },
    {
      headerName: "Mô tả",
      field: "description",
      width: 180
    },
    {
      headerName: "Hành động",
      field: "actions",
      width: 200,
      renderCell: (params) => (
        <div>
          <Button
            variant="outlined"
            color="primary"
            onClick={() => handleUpdate(params.row)}
            style={{ marginRight: "8px" }}
          >
            Sửa
          </Button>
          <Button
            variant="outlined"
            color="secondary"
            onClick={() => handleDelete(params.row.classroom)}
          >
            Xóa
          </Button>
        </div>
      ),
    },
  ];

  const handleConfirmDelete = async () => {
    if (deleteClassroomCode) {
      await deleteClassroom(deleteClassroomCode.toString());  // Ensure string type
      await refetchClassrooms();
    }
    setConfirmDeleteOpen(false);
    setDeleteClassroomCode(null);
  };

  const handleUpdate = (selectedRow) => {
    setSelectedClassroom(selectedRow);
    setDialogOpen(true);
  };

  const handleDelete = (classroom) => {
    setDeleteClassroomCode(classroom.toString());  // Convert to string explicitly
    setConfirmDeleteOpen(true);
  };

  const handleImportExcel = () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', '.xlsx, .xls');

    input.onchange = async (e) => {
      const file = e.target.files[0];
      if (file) {
        try {
          setOpenLoading(true);
          const formData = new FormData();
          formData.append('file', file);
          await importExcel(formData);
          await refetchClassrooms();
        } catch (error) {
          console.error("Error uploading file", error);
        } finally {
          setOpenLoading(false);
        }
      }
    };

    input.click();
  };

  return (
    <div style={{ height: 500, width: '100%' }}>
      <DataGrid
        loading={isLoading || isImporting || openLoading}
        getRowId={(row) => row.id}
        getRowSpacing={params => ({
          top: params.isFirstVisible ? 0 : 5,
          bottom: params.isLastVisible ? 0 : 5,
        })}
        rows={classrooms.map((row, index) => ({
          ...row,
          id: index + 1
        }))}
        components={{
          Toolbar: () => (
            <ClassroomToolbar
              onCreateNew={() => setDialogOpen(true)}
              onImportExcel={handleImportExcel}
            />
          ),
        }}
        columns={columns}
        pageSize={10}
      />

      <CreateNewClassroomScreen
        open={isDialogOpen}
        handleClose={() => {
          setDialogOpen(false);
          setSelectedClassroom(null);
        }}
        selectedClassroom={selectedClassroom}
      />

      <DeleteConfirmDialog
        open={confirmDeleteOpen}
        onClose={() => setConfirmDeleteOpen(false)}
        onConfirm={handleConfirmDelete}
      />
    </div>
  );
}