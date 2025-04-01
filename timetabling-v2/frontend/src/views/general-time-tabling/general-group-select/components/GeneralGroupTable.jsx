import { Box } from "@mui/material";
import { useGroupTableConfig } from "../hooks/useGeneralGroupTableConfig";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";

const GeneralGroupTable = ({
  classes,
  dataLoading,
  handleSelectionModelChange,
}) => {
  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        onRowSelectionModelChange={handleSelectionModelChange}
        checkboxSelection
        loading={dataLoading}
        className="text-xs"
        columns={useGroupTableConfig()}
        rows={classes}
        pageSize={10}
        initialState={{
          sorting: {
            sortModel: [{ field: 'classCode', sort: 'desc' }],
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
      />
    </Box>
  );
};

export default GeneralGroupTable;
