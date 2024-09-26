import React from "react";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import { useUploadTableConfig } from "./useUploadTableConfig";
import { useLoadingContext } from "../contexts/LoadingContext";

const GeneralUploadTable = ({ classes, dataLoading, setClasses }) => {
  const { loading: uploadLoading, setLoading } = useLoadingContext();
  
  const handleOnCellChange = (e, params) => {
    setClasses((prevClasses) => {
      return prevClasses?.map((prevClass) =>
        prevClass?.id === params?.id
          ? {
              ...prevClass,
              [params.field]: e.target.value !== "" ? e.target.value : null,
            }
          : prevClass
      );
    });
  };

  const handleOnCellSelect = (e, params, option) => {
    setClasses((prevClasses) => {
      return prevClasses?.map((prevClass) =>
        prevClass?.id === params?.id
          ? { ...prevClass, [params.field]: option }
          : prevClass
      );
    });
  };

  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        loading={uploadLoading || dataLoading}
        className="text-xs"
        columns={useUploadTableConfig(handleOnCellChange, handleOnCellSelect)}
        rows={classes}
        pageSize={10}
        initialState={{
          sorting: {
            sortModel: [{ field: 'classCode', sort: 'asc' }],
          },
          filter: {
            filterModel: {
              items: [],
              quickFilterValues: [""],
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
