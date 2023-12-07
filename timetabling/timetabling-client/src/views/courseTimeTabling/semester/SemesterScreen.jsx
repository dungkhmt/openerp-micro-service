import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Button } from '@mui/material'
import CreateNewSemester from "./CreateNewSemesterScreen";

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
    }
];

export default function TimePerformanceScreen() {
    const [semesters, setSemesters] = useState([]);
    // const [selectionModel, setSelectionModel] = useState([]);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);

    useEffect(() => {
        request("get", "/semester/get-all", (res) => {
            setSemesters(res.data);
        }).then();
    }, [refreshKey])

    const handleUpdateData = ({ classrooms, semester }) => {
        // Implement your logic to update data using an API
        console.log("Update data", { classrooms, semester });

        // Set dataChanged to true to trigger a re-render
        setDataChanged(true);

        // Tăng giá trị của refreshKey để làm mới useEffect và fetch dữ liệu mới
        setRefreshKey((prevKey) => prevKey + 1);
    };

    const handleRefreshData = () => {
        setDataChanged(true);
        // Tăng giá trị của refreshKey để làm mới useEffect và fetch dữ liệu mới
        setRefreshKey((prevKey) => prevKey + 1);
    };

    const handleOpenDialog = () => {
        setDialogOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
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

                <CreateNewSemester
                    open={isDialogOpen}
                    handleClose={handleCloseDialog}
                    handleUpdate={handleUpdateData}
                    handleRefreshData={handleRefreshData}
                />
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
        </div>
    );
}