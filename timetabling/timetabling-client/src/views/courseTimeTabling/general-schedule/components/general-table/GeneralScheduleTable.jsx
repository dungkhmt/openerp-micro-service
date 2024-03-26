import React from "react";
import { generalTableColumns } from "./tableConfig";
import { DataGrid } from "@mui/x-data-grid";
import { useClasses } from "../../hooks/useClasses";

const GeneralScheduleTable = ({group, semeter}) => {
  const { loading, error, classes } = useClasses(group, semeter);

  return (
    <div className="w-full h-[500px] overflow-auto">
      <DataGrid columns={generalTableColumns} rows={classes} pageSize={10} />
    </div>
  );
};

export default GeneralScheduleTable;
