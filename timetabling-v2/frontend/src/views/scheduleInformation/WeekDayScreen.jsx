import { Box, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useWeekDayData } from "services/useWeekDayData";

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

export default function WeekDayScreen() {
    const { weekDays, isLoading } = useWeekDayData();

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
                loading={isLoading}
                components={{
                    Toolbar: DataGridTitle,
                }}
                rows={weekDays}
                columns={columns}
                pageSize={10}
            />
        </div>
    );
}