import { useState } from "react";
import { useSemesterData } from "services/useSemesterData";
import { getColumns } from "./utils/gridColumns";
import { DataGrid } from "@mui/x-data-grid";
import { DataGridTitle, DataGridToolbar } from "./components/DataGridComponents";
import { CreateEditDialog, DeleteConfirmDialog } from "./components/SemesterDialogs";

export default function SemesterScreen() {
  const { semesters, isLoading, deleteSemester, isDeleting } = useSemesterData();
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [isDialogOpen, setDialogOpen] = useState(false);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [deleteSemesterId, setDeleteSemesterId] = useState(null);

  const handleCreate = () => {
    setSelectedSemester(null);
    setDialogOpen(true);
  };

  const handleUpdate = (selectedRow) => {
    setSelectedSemester(selectedRow);
    setDialogOpen(true);
  };

  const handleDelete = (semesterId) => {
    setDeleteSemesterId(semesterId);
    setConfirmDeleteOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (deleteSemesterId) {
      try {
        await deleteSemester(deleteSemesterId);
        setConfirmDeleteOpen(false);
        setDeleteSemesterId(null);
      } catch (error) {
        // Errors are handled by the mutation callbacks
      }
    }
  };

  const columns = getColumns({ handleUpdate, handleDelete });

  return (
    <div style={{ height: "500px", width: "100%" }}>
      <DataGrid
        loading={isLoading}
        components={{
          Toolbar: () => (
            <>
              <DataGridTitle />
              <DataGridToolbar
                isDialogOpen={isDialogOpen}
                handleCreate={handleCreate}
                handleCloseDialog={() => setDialogOpen(false)}
                selectedSemester={selectedSemester}
              />
            </>
          ),
        }}
        rows={semesters}
        columns={columns}
        pageSize={10}
      />

      <CreateEditDialog
        open={isDialogOpen}
        handleClose={() => {
          setDialogOpen(false);
          setSelectedSemester(null);
        }}
        selectedSemester={selectedSemester}
      />

      <DeleteConfirmDialog
        open={confirmDeleteOpen}
        onClose={() => {
          setConfirmDeleteOpen(false);
          setDeleteSemesterId(null);
        }}
        onConfirm={handleConfirmDelete}
        isDeleting={isDeleting}
      />
    </div>
  );
}
