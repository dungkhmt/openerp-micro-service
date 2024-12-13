import { Box, Typography } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { useClassPeriodData } from 'services/useClassPeriodData';

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

export default function ClassPeriodScreen() {
    const { 
        classPeriods, 
        isLoading,
    } = useClassPeriodData();

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
                loading={isLoading}
                components={{
                    Toolbar: DataGridTitle,
                }}
                rows={classPeriods}
                columns={columns}
                pageSize={10}
            />
        </div>
    );
}