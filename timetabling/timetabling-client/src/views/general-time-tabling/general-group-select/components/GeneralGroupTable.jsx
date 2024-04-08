import React from "react";
import { useGroupTableConfig } from "../hooks/useGeneralGroupTableConfig";
import Box from "@mui/material/Box";
import { Button } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";

const GeneralGroupTable = ({
  classes,
  dataLoading,
  handleSelectionModelChange,
}) => {
  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        onRowSelectionModelChange={handleSelectionModelChange}
        checkboxSelection
        sortingOrder={'asc'}
        loading={dataLoading}
        className="text-xs"
        columns={useGroupTableConfig()}
        rows={classes}
        pageSize={10}
      />
    </Box>
  );
};

export default GeneralGroupTable;
