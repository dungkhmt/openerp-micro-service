import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Button } from '@mui/material'
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';

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
    }
];

export default function TimePerformanceScreen() {
    const [classOpeneds, setClassOpeneds] = useState([]);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [openLoading, setOpenLoading] = React.useState(false);
    const [uploading, setUploading] = useState(false);
    const [semesters, setSemesters] = useState([]); // State to store the list of semesters
    const [selectedSemester, setSelectedSemester] = useState(null); // State to store the selected semester

    useEffect(() => {
        request("get", "/class-opened/get-all", (res) => {
            setClassOpeneds(res.data);
        }).then();

        request("get", "/semester/get-all", (res) => {
            setSemesters(res.data);
        });
    }, [refreshKey])

    const handleImportExcel = () => {
        if (!selectedSemester) {
            // If no semester is selected, show an alert or handle it as needed
            alert("Please select a semester.");
            return;
        }

        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', '.xlsx, .xls');

        input.onchange = async (e) => {
            const file = e.target.files[0];
            const url = "/excel/upload-class-opened"

            if (file) {
                try {
                    setOpenLoading(true);
                    const formData = new FormData();
                    formData.append('file', file);

                    // Extract the relevant value from the selectedSemester object
                    const semesterName = selectedSemester.semester;

                    // Add the semester parameter to the URL
                    const fullUrl = `${url}?semester=${semesterName}`;

                    // Assuming you have an API endpoint for file upload
                    const response = await request(
                        "POST",
                        fullUrl,
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

    const handleSelectSemester = (event, newValue) => {
        // Update the selected semester when the Autocomplete value changes
        setSelectedSemester(newValue);
    };

    function DataGridToolbar() {

        return (
            <div>
                <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
                    <Autocomplete
                        options={semesters}
                        getOptionLabel={(option) => option.semester} // Update with the actual property name for semester name
                        style={{ width: 150, margin: "8px" }}
                        value={selectedSemester}
                        onChange={handleSelectSemester}
                        renderInput={(params) => <TextField {...params} label="Chọn kỳ học" variant="outlined" />}
                    />
                </div>
                <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
                    <Button
                        variant="outlined"
                        color="primary"
                        style={{ marginRight: "8px" }}
                        onClick={handleImportExcel}
                        disabled={!selectedSemester}
                    >
                        Tải lên danh sách lớp
                    </Button>
                </div>
            </div >

        );
    }

    function DataGridTitle() {
        return (
            <Box style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <Typography variant="h5">Danh sách lớp học đã mở</Typography>
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
            />
        </div>
    );
}