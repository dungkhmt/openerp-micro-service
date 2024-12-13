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
  const [deleteClassroomId, setDeleteClassroomId] = useState(null);
  const [openLoading, setOpenLoading] = useState(false);

  const columns = [
    {
      headerName: "Classroom ID",
      field: "id",
      width: 120
    },
    {
      headerName: "Lớp học",
      field: "classroom",
      width: 120
    },
    {
      headerName: "Tòa nhà",
      field: "building",
      width: 120
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
            onClick={() => handleDelete(params.row.id)}
          >
            Xóa
          </Button>
        </div>
      ),
    },
  ];

  const handleConfirmDelete = async () => {
    if (deleteClassroomId) {
      await deleteClassroom(deleteClassroomId);
      await refetchClassrooms();
    }
    setConfirmDeleteOpen(false);
    setDeleteClassroomId(null);
  };

  const handleUpdate = (selectedRow) => {
    setSelectedClassroom(selectedRow);
    setDialogOpen(true);
  };

  const handleDelete = (semesterId) => {
    setDeleteClassroomId(semesterId);
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
        components={{
          Toolbar: () => (
            <ClassroomToolbar
              onCreateNew={() => setDialogOpen(true)}
              onImportExcel={handleImportExcel}
            />
          ),
        }}
        rows={classrooms}
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