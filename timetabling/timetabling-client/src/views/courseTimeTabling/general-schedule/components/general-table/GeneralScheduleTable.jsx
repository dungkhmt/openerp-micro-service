import React from "react";
import { useGeneralTableColumns } from "./tableConfig";
import { DataGrid } from "@mui/x-data-grid";
import { useClasses } from "../../hooks/useClasses";
import { Box } from "@mui/material";

const GeneralScheduleTable = ({ group, semester }) => {
  console.log(semester);
  const { loading, error, classes } = useClasses(group, semester);
  
  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        className="text-xs"
        columns={useGeneralTableColumns()}
        rows={classes}
        pageSize={10}
      />
    </Box>
  );
};

export default GeneralScheduleTable;
