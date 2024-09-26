import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { toast } from 'react-toastify';
import { Box, Typography, Button, Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material'
import CreateNewGroupScreen from "./CreateNewGroupScreen";

export default function ClassGroupList() {
    const [groups, setGroups] = useState([]);
    const [selectedGroup, setSelectedGroup] = useState(null);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
    const [deleteGroupId, setDeleteGroupId] = useState(null);

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

    useEffect(() => {
        request("get", "/group/get-all", (res) => {
            setGroups(res.data);
        }).then();
    }, [refreshKey])

    const handleUpdateData = ({ classrooms, semester }) => {
        setDataChanged(true);
        setRefreshKey((prevKey) => prevKey + 1);
    };

    const handleRefreshData = () => {
        setDataChanged(true);
        setRefreshKey((prevKey) => prevKey + 1);
    };

    const handleCreate = () => {
        setSelectedGroup(null);
        setDialogOpen(true);
    };

    const handleUpdate = (selectedRow) => {
        setSelectedGroup(selectedRow);
        setDialogOpen(true);
    };

    const handleConfirmDelete = () => {
        if (deleteGroupId) {
            request("delete", `/group/delete?id=${deleteGroupId}`, (res) => {
                handleRefreshData();
            },
                (error) => {
                    toast.error(error.response.data);
                }).then();
        }

        setConfirmDeleteOpen(false);
        setDeleteGroupId(null);
    };

    const handleCancelDelete = () => {
        setConfirmDeleteOpen(false);
        setDeleteGroupId(null);
    };

    const handleDelete = (semesterId) => {
        setDeleteGroupId(semesterId);
        setConfirmDeleteOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
    };

    function DataGridToolbar() {

        return (
            <div>
                <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
                    {/* <Button
                        variant="outlined"
                        color="primary"
                        style={{ marginRight: "8px" }}
                        onClick={handleCreate}
                    >
                        Thêm mới
                    </Button> */}
                </div>

                <CreateNewGroupScreen
                    open={isDialogOpen}
                    handleClose={handleCloseDialog}
                    handleUpdate={handleUpdateData}
                    handleRefreshData={handleRefreshData}
                />

                <Dialog
                    open={confirmDeleteOpen}
                    onClose={handleCancelDelete}
                    aria-labelledby="confirm-delete-dialog"
                >
                    <DialogTitle id="confirm-delete-dialog-title">Xác nhận xóa nhóm</DialogTitle>
                    <DialogContent>
                        <Typography>Bạn có chắc chắn muốn xóa nhóm này?</Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleCancelDelete} color="secondary">
                            Hủy
                        </Button>
                        <Button onClick={handleConfirmDelete} color="primary">
                            Xóa
                        </Button>
                    </DialogActions>
                </Dialog>
            </div>

        );
    }

    function DataGridTitle() {
        return (
            <Box style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <Typography variant="h5">Danh sách nhóm</Typography>
            </Box>
        )
    }

    return (
        <div style={{ height: 500, width: '100%' }}>
            <DataGrid
                key={dataChanged}
                components={{
                    Toolbar: () => (
                        <>
                            <DataGridTitle />
                            <DataGridToolbar />
                        </>
                    ),
                }}
                rows={groups}
                columns={columns}
                pageSize={10}
            />

            <CreateNewGroupScreen
                open={isDialogOpen}
                handleClose={() => {
                    setDialogOpen(false);
                    setSelectedGroup(null);
                }}
                handleUpdate={handleUpdateData}
                handleRefreshData={handleRefreshData}
                selectedGroup={selectedGroup}
            />
        </div>
    );
}