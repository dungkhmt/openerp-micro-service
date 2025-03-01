import { useState } from "react";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import { useUploadTableConfig } from "./useUploadTableConfig";
import { useLoadingContext } from "../contexts/LoadingContext";
import ViewClassDetailDialog from "./ViewClassDetailDialog";

const GeneralUploadTable = ({ classes, dataLoading, setClasses, onSelectionChange, selectedIds }) => {
  const { loading: uploadLoading } = useLoadingContext();
  const [openDetailDialog, setOpenDetailDialog] = useState(false);
  const [selectedClass, setSelectedClass] = useState(null);

  const handleOnCellChange = (e, params) => {
    setClasses((prevClasses) => {
      return prevClasses?.map((prevClass) =>
        prevClass?.id === params?.id
          ? {
              ...prevClass,
              [params.field]: e.target.value !== "" ? e.target.value : null,
            }
          : prevClass
      );
    });
  };

  const handleOnCellSelect = (e, params, option) => {
    setClasses((prevClasses) => {
      return prevClasses?.map((prevClass) =>
        prevClass?.id === params?.id
          ? { ...prevClass, [params.field]: option }
          : prevClass
      );
    });
  };

  const handleRowDoubleClick = (params) => {
    setSelectedClass(params.row);
    setOpenDetailDialog(true);
  };

  return (
    <Box style={{ height: 600, width: "100%" }}>
      <DataGrid
        loading={uploadLoading || dataLoading}
        className="text-xs"
        columns={useUploadTableConfig(handleOnCellChange, handleOnCellSelect)}
        rows={classes}
        pageSize={10}
        initialState={{
          sorting: {
            sortModel: [{ field: 'classCode', sort: 'asc' }],
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
        checkboxSelection
        getRowId={(row) => row.id}
        rowSelectionModel={selectedIds}
        onRowSelectionModelChange={onSelectionChange}
        onRowDoubleClick={handleRowDoubleClick}
      />
      <ViewClassDetailDialog
        isOpen={openDetailDialog}
        classData={selectedClass}
        closeDialog={() => setOpenDetailDialog(false)}
      />
    </Box>
  );
};

export default GeneralUploadTable;
