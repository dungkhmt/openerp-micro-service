import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { toast } from 'react-toastify';
import { Box, Typography, Button, Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material'
import CreateNewClassroomScreen from "./CreateNewClassroomScreen";

export default function TimePerformanceScreen() {
    const [classrooms, setClassrooms] = useState([]);
    const [selectedClassroom, setSelectedClassroom] = useState(null);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [openLoading, setOpenLoading] = React.useState(false);
    const [uploading, setUploading] = useState(false);
    const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
    const [deleteClassroomId, setDeleteClassroomId] = useState(null);

    const columns = [
        {
            headerName: "Classroom ID",
            field: "id",
            width: 150
        },
        {
            headerName: "Lớp học",
            field: "classroom",
            width: 150
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
        request("get", "/classroom/get-all", (res) => {
            setClassrooms(res.data);
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
        setSelectedClassroom(null);
        setDialogOpen(true);
    };

    const handleUpdate = (selectedRow) => {
        setSelectedClassroom(selectedRow);
        setDialogOpen(true);
    };

    const handleConfirmDelete = () => {
        if (deleteClassroomId) {
            request("delete", `/classroom/delete?id=${deleteClassroomId}`, (res) => {
                handleRefreshData();
            },
                (error) => {
                    toast.error(error.response.data);
                }).then();
        }

        setConfirmDeleteOpen(false);
        setDeleteClassroomId(null);
    };

    const handleCancelDelete = () => {
        setConfirmDeleteOpen(false);
        setDeleteClassroomId(null);
    };

    const handleDelete = (semesterId) => {
        setDeleteClassroomId(semesterId);
        setConfirmDeleteOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
    };

    const handleImportExcel = () => {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', '.xlsx, .xls');

        input.onchange = async (e) => {
            const file = e.target.files[0];
            const url = "/excel/upload-classroom"

            if (file) {
                try {
                    setOpenLoading(true);
                    const formData = new FormData();
                    formData.append('file', file);

                    const response = await request(
                        "POST",
                        url,
                        (res) => {
                            console.log(res.data);
                            setUploading(false);
                            // You may want to update the table data here
                            window.location.reload();
                        }, {

                    }
                        , formData,
                        {
                            "Content-Type": "multipart/form-data",
                        });

                    // Handle the response as needed
                    console.log(response);
                } catch (error) {
                    console.error("Error uploading file", error);
                } finally {
                    setOpenLoading(false);
                }
            }
        };

        input.click();
    };

    function DataGridToolbar() {

        // const isDeleteButtonDisabled = selectionModel.length < 2;

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

                <CreateNewClassroomScreen
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
                    <DialogTitle id="confirm-delete-dialog-title">Xác nhận xóa phòng học</DialogTitle>
                    <DialogContent>
                        <Typography>Bạn có chắc chắn muốn xóa phòng học này?</Typography>
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

                <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
                    <Button
                        variant="outlined"
                        color="primary"
                        style={{ marginRight: "8px", marginTop: "8px" }}
                        onClick={handleImportExcel}
                    >
                        Tải lên danh sách phòng
                    </Button>
                </div>
            </div>

        );
    }

    function DataGridTitle() {
        return (
            <Box style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <Typography variant="h5">Danh sách phòng học</Typography>
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
                handleUpdate={handleUpdateData}
                handleRefreshData={handleRefreshData}
                selectedClassroom={selectedClassroom}
            />
        </div>
    );
}