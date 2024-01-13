import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Button } from '@mui/material'
import CreateNewClassroomScreen from "./CreateNewClassroomScreen";

export default function TimePerformanceScreen() {
    const [classrooms, setClassrooms] = useState([]);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [openLoading, setOpenLoading] = React.useState(false);
    const [uploading, setUploading] = useState(false);

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

    const handleOpenDialog = () => {
        setDialogOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
    };

    const handleUpdate = (selectedRow) => {
        console.log("Update semester", selectedRow);
    };

    const handleDelete = (semesterId) => {
        console.log("Delete semester with ID", semesterId);
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
                        onClick={handleOpenDialog}
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
        </div>
    );
}