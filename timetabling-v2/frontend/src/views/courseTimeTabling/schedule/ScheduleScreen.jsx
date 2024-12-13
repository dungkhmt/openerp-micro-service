import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import {Box, Autocomplete, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Typography } from '@mui/material';
import GroupListScreen from './GroupListScreen';
import CreateNewGroupScreen from './CreateNewGroupScreen';
import { DataGrid } from '@mui/x-data-grid';

const columns = [
    {
        headerName: "Class Opened ID",
        field: "id",
        width: 170
    },
    {
        headerName: "Nhóm",
        field: "groupName",
        width: 120
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
        width: 150
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
        width: 150
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
        field: "startPeriod",
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
    },
    {
        headerName: "Tiết BĐ thứ hai",
        field: "secondStartPeriod",
        width: 100
    },
    {
        headerName: "Thứ",
        field: "secondWeekday",
        width: 100
    },
    {
        headerName: "Phòng",
        field: "secondClassroom",
        width: 100
    }
];

export default function ScheduleScreen() {
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [groups, setGroups] = useState([]);
    const [isGroupListOpen, setGroupListOpen] = useState(false);
    const [classOpeneds, setClassOpeneds] = useState([]);
    const [semesters, setSemesters] = useState([]);
    const [selectedSemester, setSelectedSemester] = useState(null);
    const [selectedGroup, setSelectedGroup] = useState(null);
    const [rowSelectionModel, setRowSelectionModel] = React.useState([]);
    const [isDeleteDialogOpen, setDeleteDialogOpen] = useState(false);


    useEffect(() => {
        request("get", "/semester/get-all", (res) => {
            setSemesters(res.data);
        });

        request("get", "/group/get-all", (res) => {
            setGroups(res.data);
        });
    }, [refreshKey])

    useEffect(()=>{
        var semesterName = null
        var groupName = null

        if(!selectedSemester && !selectedGroup) {
            setClassOpeneds([]);
            return;
        }


        if (selectedSemester != null) {
            semesterName = selectedSemester.semester;
        }
        if (selectedGroup != null) {
            groupName = selectedGroup.groupName
        }

        const requestSearch = {
            semester: semesterName,
            groupName: groupName
        };

        request("post", "/class-opened/search", (res) => {
            setClassOpeneds(res.data);
        },
            {},
            requestSearch
        ).then();
    }, [selectedGroup, selectedSemester])

    const handleOpenGroupList = () => {
        setGroupListOpen(true);
    };

    const handleCloseGroupList = () => {
        setGroupListOpen(false);
    };

    const handleSemesterChange = (event, newValue) => {
        setSelectedSemester(newValue);
    };

    const handleGroupChange = (event, newValue) => {
        setSelectedGroup(newValue);
    };

    const handleFilterData = () => {
        const url = "/class-opened/search"

        var semesterName = null
        var groupName = null

        if (selectedSemester != null) {
            semesterName = selectedSemester.semester;
        }
        if (selectedGroup != null) {
            groupName = selectedGroup.groupName
        }

        const requestSearch = {
            semester: semesterName,
            groupName: groupName
        };

        request("post", url, (res) => {
            setClassOpeneds(res.data);
        },
            {},
            requestSearch
        ).then();

        setDataChanged(true);
    };

    const handleRefreshData = () => {
        
        var semesterName = null
        var groupName = null
        if (selectedSemester != null) {
            semesterName = selectedSemester.semester;
        }
        if (selectedGroup != null) {
            groupName = selectedGroup.groupName
        }

        const requestSearch = {
            semester: semesterName,
            groupName: groupName
        };
        request("post", "/class-opened/search", (res) => {
            setClassOpeneds(res.data);
        },
            {},
            requestSearch
        );
        setDataChanged(true);
        setRefreshKey((prevKey) => prevKey + 1);
    };

    const handleOpenDialog = () => {
        setDialogOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
    };

    const handleDeleteClick = () => {
        setDeleteDialogOpen(true);
    };

    const handleDeleteConfirmation = () => {
        setDeleteDialogOpen(false);
        request("delete", `/class-opened/delete-ids?ids=${rowSelectionModel}`, (res) => {
            handleRefreshData();
        }, {});
    };

    const handleDeleteCancel = () => {
        setDeleteDialogOpen(false);
    };


    function DataGridToolbar() {

        const isButtonDisabled = rowSelectionModel.length < 1;

        return (
            <div>
                <div style={{ display: "flex", gap: 16, justifyContent: "flex-start" }}>
                    <Autocomplete
                        options={semesters}
                        getOptionLabel={(option) => option.semester}
                        style={{ width: 135, marginLeft: "8px" }}
                        value={selectedSemester}
                        renderInput={(params) => <TextField {...params} label="Chọn kỳ học" />}
                        onChange={handleSemesterChange}
                    />
                    <Autocomplete
                        options={groups}
                        getOptionLabel={(option) => option.groupName}
                        style={{ width: 160 }}
                        value={selectedGroup}
                        renderInput={(params) => <TextField {...params} label="Chọn nhóm học" />}
                        onChange={handleGroupChange}
                    />
                </div>

                <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
                    <Button
                        variant="outlined"
                        color="secondary"
                        onClick={handleDeleteClick}
                        disabled={isButtonDisabled}
                    >
                        Xóa
                    </Button>
                    <Button
                        variant="outlined"
                        color="secondary"
                        onClick={handleOpenGroupList}
                        disabled={isButtonDisabled}
                    >
                        Thêm vào nhóm đã có
                    </Button>
                    <GroupListScreen
                        open={isGroupListOpen}
                        handleClose={handleCloseGroupList}
                        existingData={[classOpeneds, rowSelectionModel]}
                        handleRefreshData={handleRefreshData}
                    />
                    <Button
                        variant="outlined"
                        color="primary"
                        style={{ marginRight: "8px" }}
                        onClick={handleOpenDialog}
                        disabled={isButtonDisabled}
                    >
                        Thêm vào nhóm mới
                    </Button>
                    <CreateNewGroupScreen
                        open={isDialogOpen}
                        handleClose={handleCloseDialog}
                        existingData={rowSelectionModel}
                        handleRefreshData={handleRefreshData}
                    />


                    {/* Delete Confirmation Dialog */}
                    <Dialog open={isDeleteDialogOpen} onClose={handleDeleteCancel}>
                        <DialogTitle>Xác nhận xóa</DialogTitle>
                        <DialogContent>
                            <Typography>Bạn có chắc chắn muốn xóa các lớp học đã chọn?</Typography>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={handleDeleteCancel} color="primary">
                                Hủy
                            </Button>
                            <Button onClick={handleDeleteConfirmation} color="secondary">
                                Xóa
                            </Button>
                        </DialogActions>
                    </Dialog>
                </div>
            </div>
        );
    }

    function DataGridTitle() {
        return (
            <Box style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <Typography variant="h5">Nhóm các lớp học</Typography>
            </Box>
        )
    }

    return (
        <div style={{ height: 600, width: '100%' }}>
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
                onRowSelectionModelChange={(newRowSelectionModel) => {
                    setRowSelectionModel(newRowSelectionModel);
                }}
                rowSelectionModel={rowSelectionModel}
            />
        </div>
    );
}