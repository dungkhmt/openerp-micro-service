import React, { useEffect } from "react";
import { useGeneralTableColumns } from "./useScheduleTableConfig";
import { DataGrid } from "@mui/x-data-grid";
import { useClasses } from "../../hooks/useClasses";
import { Box } from "@mui/material";
import { toast } from "react-toastify";

const GeneralScheduleTable = ({ semester, selectedGroup }) => {
  const { loading, error, classes, setClasses, setLoading } = useClasses(
    selectedGroup,
    semester
  );

  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        className="text-xs"
        loading={loading}
        columns={useGeneralTableColumns(setClasses, setLoading)}
        rows={classes}
        pageSize={10}
      />
    </Box>
  );
};

export default GeneralScheduleTable;
