import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { useState } from "react";
import { usePlanTableConfig } from "../hooks/usePlanTableConfig";
import ViewClassPlanDialog from "./ViewClassPlanDialog";

const ClassOpenPlanTable = ({
  isOpenDialog,
  semester,
  classes,
  setOpenDialog,
  setClasses
}) => {


  const [selectedRow, setSelectedRow] = useState(null);

  const handleRowDoubleClick = (rowModel) => {
    if (rowModel.row) {
      console.log(rowModel.row);
      setSelectedRow(rowModel.row);
      setOpenDialog(true);
      console.log(isOpenDialog);
    }
  };


  return (
    <div className="">
      <ViewClassPlanDialog
        row={selectedRow}
        planClassId={selectedRow?.id}
        closeDialog={() => setOpenDialog(false)}
        isOpen={isOpenDialog}
        semester={semester}
      />
      <DataGrid
        initialState={{
          sorting: {
            sortModel: [{ field: 'id', sort: 'asc' }],
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
        onRowDoubleClick = {(row) => handleRowDoubleClick(row)}
        rowSelection={true}
        columns={usePlanTableConfig(setClasses)}
        rows={classes}
        sx={{ height: 550 }}
      />
    </div>
  );
};

export default ClassOpenPlanTable;
