import React from "react";
import { DataGrid } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import { useClasses } from "views/general-time-tabling/hooks/useClasses";
import { useUploadTableConfig } from "./useUploadTableConfig";

const GeneralUploadTable = ({ group, semester }) => {
  console.log(semester);
  const { loading, error, classes, setClasses } = useClasses(group, semester);
  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        className="text-xs"
        columns={useUploadTableConfig()}
        rows={classes}
        pageSize={10}
      />
    </Box>
  );
};

export default GeneralUploadTable;
