import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Button } from '@mui/material'
import CreateNewGroupScreen from "./CreateNewGroupScreen";
import SelectSemester from "./SelectSemesterScreen";
import GroupListScreen from './GroupListScreen';

const columns = [
    {
        headerName: "Class Opened ID",
        field: "id",
        width: 170
    },
    {
        headerName: "Nhóm",
        field: "group",
        width: 170
    },
    {
        headerName: "SL thực",
        field: "quantity",
        width: 100
    },
    {
        headerName: "Loại lớp",
        field: "classType",
        width: 100
    },
    {
        headerName: "Mã học phần",
        field: "moduleCode",
        width: 100
    },
    {
        headerName: "Tên học phần",
        field: "moduleName",
        width: 100
    },
    {
        headerName: "Thời lượng",
        field: "mass",
        width: 100
    },
    {
        headerName: "SL MAX",
        field: "quantityMax",
        width: 100
    },
    {
        headerName: "Lớp học",
        field: "studyClass",
        width: 100
    },
    {
        headerName: "Trạng thái",
        field: "state",
        width: 100
    },
    {
        headerName: "Mã lớp",
        field: "classCode",
        width: 100
    },
    {
        headerName: "Kíp",
        field: "crew",
        width: 100
    },
    {
        headerName: "Đợt",
        field: "openBatch",
        width: 100
    },
    {
        headerName: "Khóa",
        field: "course",
        width: 100
    },
    {
        headerName: "Tiết BĐ",
        field: "start",
        width: 100
    },
    {
        headerName: "Thứ",
        field: "weekday",
        width: 100
    },
    {
        headerName: "Phòng",
        field: "classroom",
        width: 100
    }
];

export default function ScheduleScreen() {
    const [timePerformances, setTimePerformances] = useState([]);
    const [selectionModel, setSelectionModel] = useState([]);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [isSelectSemester, setSelectSemester] = useState(false);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [isGroupListOpen, setGroupListOpen] = useState(false);
    const [existingGroupData, setExistingGroupData] = useState([]); // Replace with your actual existing group data
    const [classOpeneds, setClassOpeneds] = useState([]);

    useEffect(() => {
        request("get", "/class-opened/get-all", (res) => {
            setClassOpeneds(res.data);
        }).then();
    }, [refreshKey])

    const handleOpenGroupList = () => {
        setGroupListOpen(true);
    };

    const handleCloseGroupList = () => {
        setGroupListOpen(false);
    };

    const handleSelectExistingGroup = (selectedItem) => {
        // Implement your logic to handle the selected item from the dialog
        console.log('Selected item from dialog:', selectedItem);
    };


    useEffect(() => {
        request("get", "/time-performance/get-all", (res) => {
            setTimePerformances(res.data);
        }).then();
    }, [refreshKey])

    function handleDeleteSelected() {
        // Implement your logic to delete selected items using an API
        // You can use the selectedIds state to get the IDs of selected items
        console.log("Delete selected items:", selectionModel);
    }

    const handleUpdateData = ({ semester }) => {
        // Implement your logic to update data using an API
        console.log("Update data", { semester });

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

    const handleOpenSelectSemester = () => {
        setSelectSemester(true);
    };

    const handleCloseSelectSemester = () => {
        setSelectSemester(false);
    };

    function DataGridToolbar() {

        const isDeleteButtonDisabled = selectionModel.length < 2;

        return (
            <div>
                <div style={{ display: "flex", gap: 16, justifyContent: "flex-start" }}>
                    <Button
                        variant="outlined"
                        color="secondary"
                        style={{ marginLeft: "8px" }}
                        onClick={handleOpenSelectSemester}
                    // disabled={isDeleteButtonDisabled}
                    >
                        Chọn kỳ học
                    </Button>
                </div>

                <SelectSemester
                    open={isSelectSemester}
                    handleClose={handleCloseSelectSemester}
                    handleUpdate={handleUpdateData}
                    handleRefreshData={handleRefreshData}
                />

                <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
                    <Button
                        variant="outlined"
                        color="secondary"
                        onClick={handleOpenGroupList}
                    // disabled={isDeleteButtonDisabled}
                    >
                        Thêm vào nhóm đã có
                    </Button>
                    <GroupListScreen
                        open={isGroupListOpen}
                        handleClose={handleCloseGroupList}
                        existingData={classOpeneds}
                        handleSelect={handleSelectExistingGroup}
                    />
                    <Button
                        variant="outlined"
                        color="primary"
                        style={{ marginRight: "8px" }}
                        onClick={handleOpenDialog}
                    >
                        Thêm vào nhóm mới
                    </Button>
                </div>

                <CreateNewGroupScreen
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
                <Typography variant="h5">Danh sách lớp mở thời khóa biểu</Typography>
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
                rows={classOpeneds}
                columns={columns}
                pageSize={10}
                checkboxSelection
                onSelectionModelChange={(el) => {
                    console.log("New selection model:", el);
                    setSelectionModel(el.selectionModel);
                }}
                selectionModel={selectionModel}
            />
        </div>
    );
}