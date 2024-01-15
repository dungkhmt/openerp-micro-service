import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { toast } from 'react-toastify';
import { Box, Typography, Button, Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material'
import CreateNewSemester from "./CreateNewSemesterScreen";

export default function SemesterScreen() {
    const [semesters, setSemesters] = useState([]);
    const [selectedSemester, setSelectedSemester] = useState(null);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
    const [deleteSemesterId, setDeleteSemesterId] = useState(null);

    const columns = [
        {
            headerName: "Semester ID",
            field: "id",
            width: 170
        },
        {
            headerName: "Kỳ học",
            field: "semester",
            width: 170
        },
        {
            headerName: "Mô tả",
            field: "description",
            width: 300
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
        request("get", "/semester/get-all", (res) => {
            setSemesters(res.data);
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
        setSelectedSemester(null);
        setDialogOpen(true);
    };

    const handleUpdate = (selectedRow) => {
        setSelectedSemester(selectedRow);
        setDialogOpen(true);
    };

    const handleConfirmDelete = () => {
        if (deleteSemesterId) {
            request("delete", `/semester/delete?id=${deleteSemesterId}`, (res) => {
                console.log("Semester deleted successfully");
                handleRefreshData();
            },
                (error) => {
                    toast.error(error.response.data);
                }).then();
        }

        setConfirmDeleteOpen(false);
        setDeleteSemesterId(null);
    };

    const handleCancelDelete = () => {
        setConfirmDeleteOpen(false);
        setDeleteSemesterId(null);
    };

    const handleDelete = (semesterId) => {
        setDeleteSemesterId(semesterId);
        setConfirmDeleteOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
    };

    function DataGridToolbar() {

        return (
            <div>
                <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
                    <Button
                        variant="outlined"
                        color="primary"
                        style={{ marginRight: "8px" }}
                        onClick={handleCreate}
                    >
                        Thêm mới
                    </Button>
                </div>

                <CreateNewSemester
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
                    <DialogTitle id="confirm-delete-dialog-title">Xác nhận xóa kỳ học</DialogTitle>
                    <DialogContent>
                        <Typography>Bạn có chắc chắn muốn xóa kỳ học này?</Typography>
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
                <Typography variant="h5">Danh sách kỳ học</Typography>
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
                rows={semesters}
                columns={columns}
                pageSize={10}
            />

            <CreateNewSemester
                open={isDialogOpen}
                handleClose={() => {
                    setDialogOpen(false);
                    setSelectedSemester(null);
                }}
                handleUpdate={handleUpdateData}
                handleRefreshData={handleRefreshData}
                selectedSemester={selectedSemester}
            />
        </div>
    );
}