import React from "react";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import { useUploadTableConfig } from "./useUploadTableConfig";
import { useLoadingContext } from "../contexts/LoadingContext";

const GeneralUploadTable = ({ classes, dataLoading }) => {
  const { loading: uploadLoading, setLoading } = useLoadingContext();
  
  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        loading={uploadLoading || dataLoading}
        className="text-xs"
        columns={useUploadTableConfig()}
        rows={classes}
        pageSize={10}
        initialState={{
          filter: {
            filterModel: {
              items: [],
              quickFilterValues: [''],
            },
          },
        }}
        slots={{ toolbar: GridToolbar }}
        slotProps={{
          toolbar: { 
            printOptions: { disableToolbarButton: true },
            csvOptions: { disableToolbarButton: true },
            showQuickFilter: true,
          },
        }}
        disableColumnSelector
        disableDensitySelector
      />
    </Box>
  );
};

export default GeneralUploadTable;
