import React, { useEffect, useState } from "react";
import { useWeekAcademicTableConfig } from "../hooks/useWeekAcademicTableConfig";
import { useAcademicWeeks } from "../hooks/useAcademicWeeks";
import GeneralSemesterAutoComplete from "views/general-time-tabling/common-components/GeneralSemesterAutoComplete";
import { DataGrid } from "@mui/x-data-grid";
import { Box } from "@mui/material";

const WeekAcademicTable = ({ weeks, isLoading }) => {
  
  return (
    <Box sx={{ width: "100%", height: 500 }}>
      <DataGrid
        rows={weeks}
        loading={isLoading}
        columns={useWeekAcademicTableConfig()}
      />
    </Box>
  );
};

export default WeekAcademicTable;
