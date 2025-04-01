import { Box } from "@mui/material";
import { useWeekAcademicTableConfig } from "../hooks/useWeekAcademicTableConfig";
import { DataGrid } from "@mui/x-data-grid";

const WeekAcademicTable = ({ weeks, isLoading }) => {
  return (
    <Box sx={{ width: "100%", height: 500 }}>
      <DataGrid
        rows={weeks}
        loading={isLoading}
        columns={useWeekAcademicTableConfig()}
      />
    </Box>
  );
};

export default WeekAcademicTable;