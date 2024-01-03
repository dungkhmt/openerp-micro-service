import * as React from 'react';
import { useEffect, useState } from "react";
import { request } from "../../api";
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography } from '@mui/material'

const columns = [
    {
        headerName: "Class Period ID",
        field: "id",
        width: 150
    },
    {
        headerName: "Tiết học",
        field: "classPeriod",
        width: 150
    },
];

export default function TimePerformanceScreen() {
    const [classPeriods, setClassPeriods] = useState([]);
    const [dataChanged, setDataChanged] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);

    useEffect(() => {
        request("get", "/class-period/get-all", (res) => {
            setClassPeriods(res.data);
        }).then();
    }, [refreshKey])

    function DataGridTitle() {
        return (
            <Box style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center" }}>
                <Typography variant="h5">Danh sách Tiết học</Typography>
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
                rows={classPeriods}
                columns={columns}
                pageSize={10}
            />
        </div>
    );
}