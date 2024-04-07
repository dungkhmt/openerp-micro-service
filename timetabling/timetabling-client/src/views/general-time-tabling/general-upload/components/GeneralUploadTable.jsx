import React from "react";
import { DataGrid } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import { useClasses } from "views/general-time-tabling/hooks/useClasses";
import { useUploadTableConfig } from "./useUploadTableConfig";
import { useLoadingContext } from "../contexts/LoadingContext";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import { Error } from "@mui/icons-material";

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
      />
    </Box>
  );
};

export default GeneralUploadTable;
