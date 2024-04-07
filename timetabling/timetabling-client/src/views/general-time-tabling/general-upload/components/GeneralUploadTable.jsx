import React from "react";
import { DataGrid } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import { useClasses } from "views/general-time-tabling/hooks/useClasses";
import { useUploadTableConfig } from "./useUploadTableConfig";
import { useLoadingContext } from "../contexts/LoadingContext";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import { Error } from "@mui/icons-material";

const GeneralUploadTable = ({ semester }) => {
  const { loading: uploadLoading, setLoading } = useLoadingContext();
  console.log(semester);
  const { loading, error, classes } = useClasses(null, semester);
  console.log(uploadLoading, loading);
  if (error)
    return (
      <div className="">
        <Error />
        <p>{{ error }}</p>
      </div>
    );

  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        loading={uploadLoading || loading}
        className="text-xs"
        columns={useUploadTableConfig()}
        rows={classes}
        pageSize={10}
      />
    </Box>
  );
};

export default GeneralUploadTable;
