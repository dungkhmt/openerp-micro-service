import { useState } from "react";
import { useGroupData } from 'services/useGroupData';
import { Button } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import GroupToolbar from "./components/GroupToolbar";
import CreateNewGroupScreen from "./CreateNewGroupScreen";
import DeleteConfirmDialog from "./components/DeleteConfirmDialog";
import ManageGroupScreen from './components/ManageGroupScreen';

export default function ClassGroupList() {
    const [selectedGroup, setSelectedGroup] = useState(null);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
    const [deleteGroupId, setDeleteGroupId] = useState(null);
    const [isManageDialogOpen, setManageDialogOpen] = useState(false);

    const { groups, isLoading, deleteGroup } = useGroupData();

    if (isLoading) {
        return <div>Loading...</div>;
    }

    console.log(groups);
    const columns = [
      {
        headerName: "STT",
        field: "index",
        width: 120,
        renderCell: (params) => params.row.index
      },
      {
        headerName: "Tên nhóm",
        field: "groupName",
        width: 320,
      },
      {
        headerName: "Phòng học",
        field: "roomName",
        width: 150,
      },
      {
        headerName: "Độ ưu tiên",
        field: "priority",
        width: 150,
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
              onClick={() => handleEdit(params.row)}
              style={{ marginRight: "8px" }}
            >
              Sửa
            </Button>
            <Button
              variant="outlined"
              color="secondary"
              onClick={() => handleDeleteClick(params.row)}
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
            id: group.groupId // Use the original group ID for API
        });
        setDialogOpen(true);
    };

    const handleDeleteClick = (row) => {    
        setDeleteGroupId({
            id: row.groupId, // Use the original group ID for API
            roomName: row.roomName
        });
        setConfirmDeleteOpen(true);
    };

    const handleConfirmDelete = async () => {
        if (deleteGroupId) {
            await deleteGroup({
                id: deleteGroupId.id,
                roomId: deleteGroupId.roomName
            });
            setConfirmDeleteOpen(false);
            setDeleteGroupId(null);
        }
    };

    return (
        <div style={{ height: 500, width: '100%' }}>
            <DataGrid
                loading={isLoading}
                getRowId={(row) => row.index} // Use index as unique row identifier
                rows={groups.map((group, index) => ({
                    ...group,
                    groupId: group.id, // Store original group ID
                    index: index + 1, // Use index for unique row identification
                    id: group.id // Keep original ID
                }))}
                columns={columns}
                pageSize={10}
                components={{
                    Toolbar: () => (
                        <GroupToolbar 
                            onAdd={() => setDialogOpen(true)}
                            onManage={() => setManageDialogOpen(true)}
                        />
                    ),
                }}
            />

            <CreateNewGroupScreen
                open={isDialogOpen}
                handleClose={() => {
                    setDialogOpen(false);
                    setSelectedGroup(null);
                }}
                selectedGroup={selectedGroup}
            />

            <DeleteConfirmDialog
                open={confirmDeleteOpen}
                onClose={() => setConfirmDeleteOpen(false)}
                onConfirm={handleConfirmDelete}
            />

            <ManageGroupScreen
                open={isManageDialogOpen}
                handleClose={() => setManageDialogOpen(false)}
            />
        </div>
    );
}