import React from "react";
import { useGeneralTableColumns } from "./useScheduleTableConfig";
import { DataGrid } from "@mui/x-data-grid";
import { useClasses } from "../../hooks/useClasses";
import { Box } from "@mui/material";
import { DATA_GRID_DEFAULT_SLOTS_COMPONENTS } from "@mui/x-data-grid/internals";

const GeneralScheduleTable = ({ group, semester }) => {
  console.log(semester);
  const { loading, error, classes, setClasses } = useClasses(group, semester);
  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        className="text-xs"
        columns={useGeneralTableColumns(setClasses)}
        rows={classes}
        pageSize={10}
      />
    </Box>
  );
};

export default GeneralScheduleTable;
