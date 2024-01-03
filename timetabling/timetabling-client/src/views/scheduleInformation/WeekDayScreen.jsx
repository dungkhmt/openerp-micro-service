import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../api";
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography } from '@mui/material'

const columns = [
    {
        headerName: "Weekday ID",
        field: "id",
        width: 150
    },
    {
        headerName: "Thứ",
        field: "weekDay",
        width: 150
    },
];

export default function TimePerformanceScreen() {
    const [weekdays, setWeekdays] = useState([]);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);

    useEffect(() => {
        request("get", "/weekday/get-all", (res) => {
            setWeekdays(res.data);
        }).then();
    }, [refreshKey])

    function DataGridTitle() {
        return (
            <Box style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <Typography variant="h5">Danh sách Ngày học trong tuần</Typography>
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
                        </>
                    ),
                }}
                rows={weekdays}
                columns={columns}
                pageSize={10}
            />
        </div>
    );
}