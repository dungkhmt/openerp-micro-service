import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { useUploadTableConfig } from "./useUploadTableConfig";
import { useLoadingContext } from "../contexts/LoadingContext";
import { Box } from "@mui/material";

const FirstYearUploadTable = ({ classes, dataLoading }) => {
  const { loading: uploadLoading } = useLoadingContext();
  
  // Ensure classes is always an array
  const validClasses = Array.isArray(classes) ? classes : [];

  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        loading={uploadLoading || dataLoading}
        className="text-xs"
        columns={useUploadTableConfig()}
        rows={validClasses}
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

export default FirstYearUploadTable;
