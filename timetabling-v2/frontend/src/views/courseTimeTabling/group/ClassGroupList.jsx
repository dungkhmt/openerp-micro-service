import { useState } from "react";
import { useGroupData } from 'services/useGroupData';
import { Button } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import GroupToolbar from "./components/GroupToolbar";
import CreateNewGroupScreen from "./CreateNewGroupScreen";
import DeleteConfirmDialog from "./components/DeleteConfirmDialog";

export default function ClassGroupList() {
    const [selectedGroup, setSelectedGroup] = useState(null);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
    const [deleteGroupId, setDeleteGroupId] = useState(null);

    const { groups, isLoading, deleteGroup } = useGroupData();

    if (isLoading) {
        return <div>Loading...</div>;
    }

    const columns = [
        {
            headerName: "Group ID",
            field: "id",
            width: 150
        },
        {
            headerName: "Tên nhóm",
            field: "groupName",
            width: 150
        },
        {
            headerName: "Tòa nhà ưu tiên",
            field: "priorityBuilding",
            width: 120
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
                        onClick={() => handleDeleteClick(params.row.id)}
                    >
                        Xóa
                    </Button>
                </div>
            ),
        },
    ];

    const handleEdit = (group) => {
        console.log(group)
        setSelectedGroup(group);
        setDialogOpen(true);
    };

    const handleDeleteClick = (id) => {    
        setDeleteGroupId(id);
        setConfirmDeleteOpen(true);
    };

    const handleConfirmDelete = async () => {
        if (deleteGroupId) {
            await deleteGroup(deleteGroupId);
            setConfirmDeleteOpen(false);
            setDeleteGroupId(null);
        }
    };

    return (
        <div style={{ height: 500, width: '100%' }}>
            <DataGrid
                loading={isLoading}
                rows={groups}
                columns={columns}
                pageSize={10}
                components={{
                    Toolbar: () => <GroupToolbar onAdd={() => setDialogOpen(true)} />,
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
        </div>
    );
}