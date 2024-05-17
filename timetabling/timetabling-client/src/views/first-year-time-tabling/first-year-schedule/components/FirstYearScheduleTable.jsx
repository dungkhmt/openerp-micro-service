import React, { useState } from "react";
import { useFirstYearTableColumns } from "./useScheduleTableConfig";
import { DataGrid, GridToolbar  } from "@mui/x-data-grid";
import {
  Box,
} from "@mui/material";
import ClassDetailDialog from "./ClassDetailDialog";

const FirstYearScheduleTable = ({
  saveRequests,
  setSaveRequests,
  isLoading,
  setSelectedRows,
  isDataLoading,
  setClasses,
  setLoading,
  classes,
  semester,
}) => {
  const [open, setOpen] = useState(false);
  const [row, setRow] = useState(null);

  const handleOpenDialog = (params) => {
    console.log(params.row);
    setRow(params.row);
    setOpen(true);
  };

  return (
    <Box style={{ height: 550, width: "100%" }}>
      <ClassDetailDialog open={open} setOpen={setOpen} row={row}/>
      <DataGrid
        onRowSelectionModelChange={(rows) => setSelectedRows(rows)}
        disableRowSelectionOnClick
        checkboxSelection
        onRowDoubleClick={handleOpenDialog}
        className="text-xs"
        loading={isDataLoading || isLoading}
        columns={useFirstYearTableColumns(setClasses, setLoading, semester, saveRequests, setSaveRequests)}
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

export default FirstYearScheduleTable;
