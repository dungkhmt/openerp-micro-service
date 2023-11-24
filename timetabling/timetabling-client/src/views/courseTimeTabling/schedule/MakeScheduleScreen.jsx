import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Button, IconButton } from '@mui/material'
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import SearchIcon from '@mui/icons-material/Search';

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
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [classOpeneds, setClassOpeneds] = useState([]);
    const [semesters, setSemesters] = useState([]); // State to store the list of semesters
    const [groups, setGroups] = useState([]); // State to store the list of semesters
    const [selectedSemester, setSelectedSemester] = useState(null); // State to store the selected semester
    const [selectedGroup, setSelectedGroup] = useState(null); // State to store the selected semester
    
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
    }, [refreshKey])

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
            //set data filter
            setClassOpeneds(res.data);
        },
            {},
            requestSearch
        ).then();

        // Set dataChanged to true to trigger a re-render
        setDataChanged(true);
    };

    // const handleRefreshData = () => {
    //     setDataChanged(true);
    //     // Tăng giá trị của refreshKey để làm mới useEffect và fetch dữ liệu mới
    //     setRefreshKey((prevKey) => prevKey + 1);
    // };

    function DataGridToolbar() {

        // const isButtonDisabled = rowSelectionModel.length < 1;

        return (
            <div>
                <div style={{ display: "flex", gap: 16, justifyContent: "flex-start" }}>
                    <Autocomplete
                        options={semesters}
                        getOptionLabel={(option) => option.semester} // Update with the actual property name for semester name
                        style={{ width: 135, marginLeft: "8px" }}
                        value={selectedSemester}
                        renderInput={(params) => <TextField {...params} label="Chọn kỳ học" />}
                        onChange={handleSemesterChange}
                    />
                    <Autocomplete
                        options={groups}
                        getOptionLabel={(option) => option.groupName} // Update with the actual property name for semester name
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
        </div>
    );
}