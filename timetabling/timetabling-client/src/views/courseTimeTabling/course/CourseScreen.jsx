import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { toast } from 'react-toastify';
import { Box, Typography, Button, Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material'
import CreateNewCourse from "./CreateNewCourseScreen";

export default function CourseScreen() {
    const [courses, setCourses] = useState([]);
    const [selectedCourse, setSelectedCourse] = useState(null);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
    const [deleteCourseId, setDeleteCourseId] = useState(null);

    const columns = [
        {
            headerName: "Mã môn học",
            field: "id",
            width: 170
        },
        {
            headerName: "Tên môn học",
            field: "courseName",
            width: 400
        },
        {
            headerName: "Số tín chỉ",
            field: "credit",
            width: 170
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
        request("get", "/course/get-all", (res) => {
            setCourses(res.data);
        }).then();
    }, [refreshKey])

    const handleUpdateData = ({ classrooms, course }) => {
        setDataChanged(true);

        setRefreshKey((prevKey) => prevKey + 1);
    };

    const handleRefreshData = () => {
        setDataChanged(true);
        setRefreshKey((prevKey) => prevKey + 1);
    };

    const handleCreate = () => {
        setSelectedCourse(null);
        setDialogOpen(true);
    };

    const handleUpdate = (selectedRow) => {
        setSelectedCourse(selectedRow);
        setDialogOpen(true);
    };

    const handleConfirmDelete = () => {
        if (deleteCourseId) {
            request("delete", `/course/delete?id=${deleteCourseId}`, (res) => {
                handleRefreshData();
            },
                (error) => {
                    toast.error(error.response.data);
                }).then();
        }

        setConfirmDeleteOpen(false);
        setDeleteCourseId(null);
    };

    const handleCancelDelete = () => {
        setConfirmDeleteOpen(false);
        setDeleteCourseId(null);
    };

    const handleDelete = (courseId) => {
        setDeleteCourseId(courseId);
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

                <CreateNewCourse
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
                <Typography variant="h5">Danh sách môn học</Typography>
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
                rows={courses}
                columns={columns}
                pageSize={10}
            />

            <CreateNewCourse
                open={isDialogOpen}
                handleClose={() => {
                    setDialogOpen(false);
                    setSelectedCourse(null);
                }}
                handleUpdate={handleUpdateData}
                handleRefreshData={handleRefreshData}
                selectedCourse={selectedCourse}
            />
        </div>
    );
}