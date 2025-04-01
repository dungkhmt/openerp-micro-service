import { useState } from "react";
import { useGroupData } from "services/useGroupData";
import { Button } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import GroupToolbar from "./components/GroupToolbar";
import DeleteConfirmDialog from "./components/DeleteConfirmDialog";
import ManageGroupScreen from "./components/ManageGroupScreen";
import GroupDetailsDialog from "./components/GroupDetailsDialog";
import SimpleCreateGroupDialog from "./components/SimpleCreateGroupDialog";

export default function ClassGroupList() {
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [isDialogOpen, setDialogOpen] = useState(false);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [deleteGroupId, setDeleteGroupId] = useState(null);
  const [isManageDialogOpen, setManageDialogOpen] = useState(false);
  const [selectedDetailId, setSelectedDetailId] = useState(null);
  const [detailDialogOpen, setDetailDialogOpen] = useState(false);
  const [selectedParentGroup, setSelectedParentGroup] = useState(null);

  const { allGroups: groups, isLoading, deleteGroupById } = useGroupData();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  const columns = [
    {
      headerName: "STT",
      field: "index",
      width: 120,
      renderCell: (params) => params.row.index,
    },
    {
      headerName: "Tên nhóm",
      field: "groupName",
      width: 320,
    },
    {
      headerName: "Hành động",
      field: "actions",
      width: 200,
      renderCell: (params) => (
        <div data-action-buttons>
          <Button
            variant="outlined"
            color="primary"
            onClick={() => {
              handleEdit(params.row)
            }}
            style={{ marginRight: "8px" }}
          >
            Sửa
          </Button>
          <Button
            variant="outlined"
            color="error"
            onClick={() => handleDeleteClick(params.row.id)}
          >
            Xóa
          </Button>
        </div>
      ),
    },
  ];

  const handleEdit = (group) => {
    setSelectedGroup({
      ...group,
      id: group.groupId, // Use the original group ID for API
    });
    setManageDialogOpen(true); // Open ManageGroupScreen instead
    console.log(group)
  };

  const handleDeleteClick = (id) => {
    setDeleteGroupId(id);
    setConfirmDeleteOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (deleteGroupId) {
      try {
        await deleteGroupById(deleteGroupId);
        setConfirmDeleteOpen(false);
        setDeleteGroupId(null);
      } catch (error) {
        console.error(error);
      }
    }
  };

  const handleRowClick = (params, event) => {
    setSelectedDetailId(params.row.id); // Use groupId instead of id
    setSelectedParentGroup(params.row.groupName); // Store the parent group name
    setDetailDialogOpen(true);
  };

  return (
    <div style={{ height: 500, width: "100%" }}>
      <DataGrid
        loading={isLoading}
        getRowId={(row) => row.index} // Use index as unique row identifier
        rows={groups.map((group, index) => ({
          ...group,
          groupId: group.id, // Store original group ID
          index: index + 1, // Use index for unique row identification
          id: group.id, // Keep original ID
        }))}
        disableColumnSelector
        disableDensitySelector
        columns={columns}
        pageSize={10}
        components={{
          Toolbar: () => (
            <GroupToolbar onAdd={() => setDialogOpen(true)} />
          ),
        }}
        onRowDoubleClick={handleRowClick}
      />

      <DeleteConfirmDialog
        open={confirmDeleteOpen}
        onClose={() => setConfirmDeleteOpen(false)}
        onConfirm={handleConfirmDelete}
      />

      <ManageGroupScreen
        open={isManageDialogOpen}
        handleClose={() => {
          setManageDialogOpen(false);
          setSelectedGroup(null);
        }}
        initialGroup={selectedGroup} // Pass selected group data
      />

      <GroupDetailsDialog
        open={detailDialogOpen}
        onClose={() => {
          setDetailDialogOpen(false);
          setSelectedParentGroup(null);
        }}
        groupId={selectedDetailId}
        parentGroupName={selectedParentGroup}
      />

      <SimpleCreateGroupDialog
        open={isDialogOpen}
        handleClose={() => setDialogOpen(false)}
      />
    </div>
  );
}
