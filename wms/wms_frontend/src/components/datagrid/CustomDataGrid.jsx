import { LinearProgress, gridClasses } from "@mui/material";
import { grey } from "@mui/material/colors";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";

/**
 * @typedef Prop
 * @property {any[]} rows
 * @property {any[]} columns
 * @property {number} totalItem
 * @property {{page: number, pageSize: number}} params
 * @property {import("@mui/x-data-grid/models/gridStateCommunity").GridInitialStateCommunity} initialState
 * @property {boolean} isLoading
 * @property {Function} setParams - (data) => void
 * @property {Function} onSelectionChange - (ids) => void
 * @property {any} selectionModel
 * @property {import("@mui/x-data-grid").GridColumnGroupingModel} columnGroupingModel
 * @property {"small" | "other"} size
 * @property {import("@mui/material").SxProps} sx
 * @param {Prop} props
 */
const CustomDataGrid = (props) => {
  const {
    rows,
    columns,
    totalItem,
    initialState,
    params,
    isLoading,
    setParams,
    onSelectionChange,
    selectionModel,
    size,
    columnGroupingModel,
    sx,
  } = props;
  const sizeDefault = size ? size : "small";
  return (
    <DataGrid
      initialState={initialState}
      columns={columns}
      rows={rows}
      rowCount={totalItem}
      rowsPerPageOptions={[10, 20, 50]}
      pageSize={params.pageSize}
      page={params.page - 1}
      loading={isLoading}
      sortingMode="server"
      paginationMode="server"
      checkboxSelection={!!onSelectionChange}
      disableRowSelectionOnClick
      disableColumnMenu
      pagination
      keepNonExistentRowsSelected
      selectionModel={selectionModel}
      columnGroupingModel={columnGroupingModel}
      experimentalFeatures={{
        columnGrouping: columnGroupingModel ? true : false,
      }}
      components={{
        LoadingOverlay: LinearProgress,
      }}
      onSelectionModelChange={(selection) =>
        onSelectionChange ? onSelectionChange(selection) : undefined
      }
      onPageSizeChange={(page_size) =>
        setParams((prev) => ({ ...prev, page_size, page: 1 }))
      }
      onPageChange={(page) =>
        setParams((prev) => ({ ...prev, page: page + 1 }))
      }
      onSortModelChange={(sort) =>
        setParams((prev) => ({
          ...prev,
          page: 1,
          sort: sort[0] ? `${sort[0].field},${sort[0].sort}` : "",
        }))
      }
      // styling
      slots={{
        toolbar: GridToolbar,
      }}
      getRowHeight={() => "auto"}
      rowHeight={sizeDefault === "small" ? 40 : 50}
      headerHeight={sizeDefault === "small" ? 40 : 50}
      sx={{
        height: "70vh",
        ".MuiDataGrid-iconSeparator": {
          visibility: "hidden",
        },
        ".MuiDataGrid-columnHeaderTitle": {
          fontWeight: 600,
          color: grey[900],
        },
        ".MuiDataGrid-actionsCell": {
          gridGap: 0,
        },
        [`& .${gridClasses.cell}`]: {
          py: "4px",
        },
        ".MuiDataGrid-columnHeaders": {
          backgroundColor: grey[100],
        },
        ...sx,
      }}
    />
  );
};
export default CustomDataGrid;
