import React, { useEffect } from "react";
import { useGeneralTableColumns } from "./useScheduleTableConfig";
import { DataGrid } from "@mui/x-data-grid";
import { useClasses } from "../../hooks/useClasses";
import { Box } from "@mui/material";
import { toast } from "react-toastify";

const GeneralScheduleTable = ({
  isLoading,
  setSelectedRows,
  isDataLoading,
  setClasses,
  setLoading,
  classes,
  semester
}) => {
  

  return (
    <Box style={{ height: 550, width: "100%" }}>
      <DataGrid
        onRowSelectionModelChange={(rows) => setSelectedRows(rows)}
        checkboxSelection
        className="text-xs"
        loading={isDataLoading || isLoading}
        columns={useGeneralTableColumns(setClasses, setLoading, semester)}
        rows={classes}
        pageSize={10}
      />
    </Box>
  );
};

export default GeneralScheduleTable;
