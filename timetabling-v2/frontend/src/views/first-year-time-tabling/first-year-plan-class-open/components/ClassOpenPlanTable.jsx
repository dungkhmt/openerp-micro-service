import { useState } from "react";
import { usePlanTableConfig } from "../hooks/usePlanTableConfig";
import ViewClassPlanDialog from "./ViewClassPlanDialog";
import { DataGrid } from "@mui/x-data-grid";

const ClassOpenPlanTable = ({
  isOpenDialog,
  semester,
  classes,
  setOpenDialog,
}) => {
  const [selectedRow, setSelectedRow] = useState(null);

  const handleRowClick = (rowModel) => {
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
        onRowClick={(row) => handleRowClick(row)}
        rowSelection={true}
        columns={usePlanTableConfig()}
        rows={classes}
        sx={{ height: 550 }}
      />
    </div>
  );
};

export default ClassOpenPlanTable;
