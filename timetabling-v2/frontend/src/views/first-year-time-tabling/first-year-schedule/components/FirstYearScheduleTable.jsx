import { useState } from "react";
import { useFirstYearTableColumns } from "./useScheduleTableConfig";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import ClassDetailDialog from "./ClassDetailDialog";

const FirstYearScheduleTable = ({
  isLoading,
  setSelectedRows,
  isDataLoading,
  classes,
  semester,
  onSaveTimeSlot
}) => {
  const [open, setOpen] = useState(false);
  const [row, setRow] = useState(null);

  const handleOpenDialog = (params) => {
    setRow(params.row);
    setOpen(true);
  };

  return (
    <Box style={{ height: 550, width: "100%" }}>
      <ClassDetailDialog open={open} setOpen={setOpen} row={row}/>
      <DataGrid
        onRowSelectionModelChange={setSelectedRows}
        disableRowSelectionOnClick
        checkboxSelection
        onRowDoubleClick={handleOpenDialog}
        className="text-xs"
        loading={isDataLoading || isLoading}
        columns={useFirstYearTableColumns(classes, onSaveTimeSlot, semester)}
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
