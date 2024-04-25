import React, { useEffect, useState } from "react";
import { useGeneralTableColumns } from "./useScheduleTableConfig";
import { DataGrid } from "@mui/x-data-grid";
import { useClasses } from "../../hooks/useClasses";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from "@mui/material";
import { toast } from "react-toastify";
import ClassDetailDialog from "./ClassDetailDialog";

const GeneralScheduleTable = ({
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
        columns={useGeneralTableColumns(setClasses, setLoading, semester)}
        rows={classes}
        pageSize={10}
      />
    </Box>
  );
};

export default GeneralScheduleTable;
