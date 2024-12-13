import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../api";
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Button } from '@mui/material'
import UpdateDialog from "./UpdateDialog";

const columns = [
    {
        headerName: "Time Performance ID",
        field: "id",
        width: 170
    },
    {
        headerName: "Kỳ học",
        field: "semester",
        width: 170
    },
    {
        headerName: "Phòng học",
        field: "classRoom",
        width: 170
    },
    {
        headerName: "Tuần học",
        field: "studyWeek",
        width: 120
    },
    {
        headerName: "Thứ",
        field: "weekDay",
        width: 120
    },
    {
        headerName: "Hiệu suất sử dụng thời gian",
        field: "performanceTime",
        width: 200
    }
];

export default function TimePerformanceScreen() {
    const [timePerformances, setTimePerformances] = useState([]);
    // const [selectionModel, setSelectionModel] = useState([]);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);

    useEffect(() => {
        request("get", "/time-performance/get-all", (res) => {
            setTimePerformances(res.data);
        }).then();
    }, [refreshKey])

    // function handleDeleteSelected() {
    //     // Implement your logic to delete selected items using an API
    //     // You can use the selectedIds state to get the IDs of selected items
    //     console.log("Delete selected items:", selectionModel);
    // }

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
                    {/* <Button
                    variant="outlined"
                    color="secondary"
                    onClick={handleDeleteSelected}
                    disabled={isDeleteButtonDisabled}
                >
                    Xóa đã chọn
                </Button> */}
                    <Button
                        variant="outlined"
                        color="primary"
                        style={{ marginRight: "8px" }}
                        onClick={handleOpenDialog}
                    >
                        Cập nhật dữ liệu
                    </Button>
                </div>

                <UpdateDialog
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
                <Typography variant="h5">Danh sách hiệu suất sử dụng thời gian</Typography>
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
                rows={timePerformances}
                columns={columns}
                pageSize={10}
            // onSelectionModelChange={(el) => {
            //     console.log("New selection model:", el);
            //     setSelectionModel(el.selectionModel);
            // }}
            // selectionModel={selectionModel}
            />
        </div>
    );
}