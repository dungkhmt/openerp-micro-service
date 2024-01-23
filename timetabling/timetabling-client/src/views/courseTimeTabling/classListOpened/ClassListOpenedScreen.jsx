import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../../api";
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Button, CircularProgress, Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material'
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
    const [semesters, setSemesters] = useState([]);
    const [selectedSemester, setSelectedSemester] = useState(null);
    const [successDialogOpen, setSuccessDialogOpen] = useState(false);
    const [conflictDialogOpen, setConflictDialogOpen] = useState(false);
    const [conflictList, setConflictList] = useState([]);

    useEffect(() => {
        request("get", "/class-opened/get-all", (res) => {
            setClassOpeneds(res.data);
        }).then();

        request("get", "/semester/get-all", (res) => {
            setSemesters(res.data);
        });
    }, [refreshKey])

    const handleImportExcel = () => {

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

                    const semesterName = selectedSemester.semester;

                    const fullUrl = `${url}?semester=${semesterName}`;

                    await request(
                        "POST",
                        fullUrl,
                        (res) => {
                            if (res.data.length === 0) {
                                // No conflicts, show success dialog
                                setSuccessDialogOpen(true);
                            } else {
                                // Conflicts exist, show conflict dialog
                                setConflictList(res.data);
                                setConflictDialogOpen(true);
                            }
                        }, {
                        //handle when error
                    }
                        , formData,
                        {
                            "Content-Type": "multipart/form-data",
                        });
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
        setSelectedSemester(newValue);
    };

    const handleDialogClose = () => {
        setSuccessDialogOpen(false);
        setConflictDialogOpen(false);
        window.location.reload();
    };

    const handleDownloadConflictList = () => {
        const url = "/excel/export/class-opened-conflict";
        console.log("data conflict: ", conflictList)
        const requestData = conflictList;

        request("post", url, (res) => {
            const blob = new Blob([res.data], { type: res.headers['content-type'] });
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = 'Class_Conflict_List.xlsx';

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            },
            (error) => {
                console.error('Error exporting Excel:', error);
            },
            requestData,
            {responseType: 'arraybuffer'}).then();
    };

    function DataGridToolbar() {

        return (
            <div>
                <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
                    <Autocomplete
                        options={semesters}
                        getOptionLabel={(option) => option.semester}
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
                <Dialog open={successDialogOpen || conflictDialogOpen} onClose={handleDialogClose}>
                    <DialogTitle>
                        {successDialogOpen ? "Tải lên danh sách thành công" : "Danh sách lớp bị trùng"}
                    </DialogTitle>
                    <DialogContent>
                        {conflictDialogOpen ? (
                            <ul>
                                {conflictList.map((conflict) => (
                                    <li key={conflict.id}>{conflict.moduleName}</li>
                                ))}
                            </ul>
                        ) : null}
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleDialogClose} color="primary" autoFocus>
                            OK
                        </Button>
                        {conflictDialogOpen && (
                            <Button onClick={handleDownloadConflictList} color="primary">
                                Tải xuống
                            </Button>
                        )}
                    </DialogActions>
                </Dialog>
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
            {openLoading && <CircularProgress style={{ position: 'absolute', top: '50%', left: '50%' }} />}
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