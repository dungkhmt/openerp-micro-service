import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { useUploadTableConfig } from "./useUploadTableConfig";
import { useLoadingContext } from "../contexts/LoadingContext";
import { Box } from "@mui/material";

const GeneralUploadTable = ({ classes, dataLoading, setClasses, onSelectionChange }) => {
  const { loading: uploadLoading } = useLoadingContext();
  
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

  const handleSelectionChange = (newSelection) => {
    console.log('New Selection:', newSelection);
    console.log('Classes:', classes);
    
    // Lấy selected rows data dựa trên selection
    const selectedRowsData = classes.filter(row => {
      const rowId = row.generalClassId || row.id;
      return newSelection.includes(rowId);
    });
    
    // Lấy mảng các ID
    const selectedIds = selectedRowsData.map(row => Number(row.id));
    
    console.log('Selected Rows Data:', selectedRowsData);
    console.log('Selected IDs:', selectedIds);
    
    onSelectionChange?.(selectedIds);
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
        getRowId={(row) => {
          return row.id;
        }}
        onRowSelectionModelChange={(newSelectionModel) => {
          onSelectionChange?.(newSelectionModel);
        }}
      />
    </Box>
  );
};

export default GeneralUploadTable;
