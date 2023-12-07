import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Button, IconButton } from '@mui/material'
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import SearchIcon from '@mui/icons-material/Search';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export default function ScheduleScreen() {
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [classOpeneds, setClassOpeneds] = useState([]);
    const [semesters, setSemesters] = useState([]);
    const [groups, setGroups] = useState([]);
    
    const [selectedSemester, setSelectedSemester] = useState(null);
    const [selectedGroup, setSelectedGroup] = useState(null);

    const [classPeriods, setClassPeriods] = useState([]);
    const [weekdays, setWeekdays] = useState([]);
    const [classrooms, setClassrooms] = useState([]);

    const columns = [
        {
            headerName: "Class Opened ID",
            field: "id",
            width: 170
        },
        {
            headerName: "Kỳ học",
            field: "semester",
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
            width: 80,
        },
        {
            headerName: "Đổi tiết",
            field: "setPeriod",
            width: 100,
            renderCell: (params) => (
                <Autocomplete
                    options={classPeriods}
                    getOptionLabel={(option) => option.classPeriod}
                    style={{ width: 100 }}
                    renderInput={(params) => (
                        <TextField
                            {...params}
                            label=""
                            value={params.row ? params.row.classPeriod : null}
                        />
                    )}
                    onChange={(event, selectedValue) => handleClassPeriodChange(event, params.row, selectedValue)}
                />
            ),
        },
        {
            headerName: "Thứ",
            field: "weekday",
            width: 80,
        },
        {
            headerName: "Đổi thứ",
            field: "setWeekday",
            width: 100,
            renderCell: (params) => (
                <Autocomplete
                    options={weekdays}
                    getOptionLabel={(option) => option.weekDay}
                    style={{ width: 100 }}
                    renderInput={(params) => (
                        <TextField
                            {...params}
                            label=""
                            value={params.row ? params.row.weekDay : null}
                        />
                    )}
                    onChange={(event, selectedValue) => handleWeekdayChange(event, params.row, selectedValue)}
                />
            ),
        },
        {
            headerName: "Phòng",
            field: "classroom",
            width: 120,
        },
        {
            headerName: "Đổi phòng",
            field: "setClassroom",
            width: 150,
            renderCell: (params) => (
                <Autocomplete
                    options={classrooms}
                    getOptionLabel={(option) => option.classroom}
                    style={{ width: 150 }}
                    renderInput={(params) => (
                        <TextField
                            {...params}
                            label=""
                            value={params.row ? params.row.classroom : null}
                        />
                    )}
                    onChange={(event, selectedValue) => handleClassroomChange(event, params.row, selectedValue)}
                />
            ),
        },
    ];

    useEffect(() => {
        request("get", "/class-opened/get-all", (res) => {
            setClassOpeneds(res.data);
        }).then();

        request("get", "/semester/get-all", (res) => {
            setSemesters(res.data);
        });

        request("get", "/group/get-all", (res) => {
            setGroups(res.data);
        });

        request("get", "/class-period/get-all", (res) => {
            setClassPeriods(res.data);
        });

        request("get", "/weekday/get-all", (res) => {
            setWeekdays(res.data);
        });

        request("get", "/classroom/get-all", (res) => {
            setClassrooms(res.data);
        });
    }, [refreshKey])

    const handleSemesterChange = (event, newValue) => {
        setSelectedSemester(newValue);
    };

    const handleGroupChange = (event, newValue) => {
        setSelectedGroup(newValue);
    };

    const handleAutoMakeSchedule = () => {

    }

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
            //set data filter
            setClassOpeneds(res.data);
        },
            {},
            requestSearch
        ).then();

        setDataChanged(true);
    };

    const handleClassPeriodChange = (event, row, selectedValue) => {
        if (selectedValue) {
            const startPeriod = selectedValue.classPeriod
            const id = row.id

            const url = "/class-opened/make-schedule";
            const requestData = {
                id: id,
                startPeriod: startPeriod,
            };

            request("post", url, (res) => {
                handleRefreshData();
            },
            (error) => {
                toast.error(error.response.data);
            },
            requestData
            ).then();;
        }
    };

    const handleWeekdayChange = (event, row, selectedValue) => {
        if (selectedValue) {
            const weekDay = selectedValue.weekDay
            const id = row.id

            const url = "/class-opened/make-schedule";
            const requestData = {
                id: id,
                weekday: weekDay,
            };

            request("post", url, (res) => {
                handleRefreshData();
            },
            (error) => {
                toast.error(error.response.data);
            },
                requestData
            ).then();
        }
    };

    const handleClassroomChange = (event, row, selectedValue) => {
        if (selectedValue) {
            const classroom = selectedValue.classroom
            const id = row.id

            const url = "/class-opened/make-schedule";
            const requestData = {
                id: id,
                classroom: classroom,
            };

            request("post", url, (res) => {
                handleRefreshData();
            },
            (error) => {
                toast.error(error.response.data);
            },
                requestData
            ).then();
        }
    };

    const handleRefreshData = () => {
        setDataChanged(true);
        // Tăng giá trị của refreshKey để làm mới useEffect và fetch dữ liệu mới
        setRefreshKey((prevKey) => prevKey + 1);
    };

    function DataGridToolbar() {

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
                    <IconButton
                        variant="contained"
                        onClick={handleFilterData}
                        aria-label="search"
                        size="large"
                    >
                        <SearchIcon fontSize="inherit" />
                    </IconButton>
                </div>
                <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
                    <Button
                        variant="outlined"
                        color="secondary"
                        onClick={handleAutoMakeSchedule}
                        disabled={!selectedSemester && !selectedGroup}
                    >
                        Sắp xếp tự động
                    </Button>
                </div>
            </div>

        );
    }

    function DataGridTitle() {
        return (
            <Box style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <Typography variant="h5">Sắp xếp thời khóa biểu</Typography>
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
            />
            {/* <ToastContainer /> */}
        </div>
    );
}