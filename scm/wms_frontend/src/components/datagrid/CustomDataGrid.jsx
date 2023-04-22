import {
  LinearProgress,
  Pagination,
  PaginationItem,
  gridClasses,
} from "@mui/material";
import { grey } from "@mui/material/colors";
import {
  DataGrid,
  gridPageCountSelector,
  gridPageSelector,
  useGridApiContext,
  useGridSelector,
} from "@mui/x-data-grid";

/**
 * @typedef Prop
 * @property {any[]} rows
 * @property {import("@mui/x-data-grid").GridColDef[]} columns
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
function CustomPagination() {
  const apiRef = useGridApiContext();
  const page = useGridSelector(apiRef, gridPageSelector);
  const pageCount = useGridSelector(apiRef, gridPageCountSelector);

  return (
    <Pagination
      color="secondary"
      variant="text"
      shape="circular"
      page={page + 1}
      count={pageCount}
      showFirstButton
      showLastButton
      siblingCount={4}
      // @ts-expect-error
      renderItem={(props2) => <PaginationItem {...props2} disableRipple />}
      onChange={(event, value) => apiRef.current.setPage(value - 1)}
    />
  );
}

const getColumnsShow = (columns, isSerial, params) => {
  let columnsRaw = columns || [];

  if (isSerial) {
    columnsRaw = [
      {
        field: "idx",
        headerName: "STT",
        renderCell: (index) =>
          index.row.id + 1 + (params.page - 1) * params.pageSize,
        sortable: false,
        width: 50,
      },
      ...columnsRaw,
    ];
  }

  return columnsRaw;
};
/**
 * @typedef Prop
 * @property {boolean} isSerial
 * @property {boolean} isSelectable
 * @property {import("@mui/x-data-grid").GridRowProps[]} rows
 * @property {any[]} columns
 * @property {number} totalItem
 * @property {import("@mui/x-data-grid/models/gridStateCommunity").GridInitialStateCommunity} initialState
 * @property {{page: number, pageSize: number}} params
 * @property {boolean} isLoading
 * @property {Function} setParams
 * @property {Function} onSelectionChange
 * @property {any} selectionModel
 * @property {"small" | "other"} size
 * @property {import("@mui/x-data-grid").GridColumnGroupingModel} columnGroupingModel
 * @property {import("@mui/material").SxProps} sx
 * @param {Prop} props
 */
const CustomDataGrid = (props) => {
  const {
    isSerial,
    isSelectable = false,
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
      columns={getColumnsShow(columns, isSerial, params)}
      rows={rows}
      rowCount={totalItem}
      // rowsPerPageOptions={[10, 20, 50, 70]}
      // pageSize={params.pageSize}
      // page={params.page - 1}
      loading={isLoading}
      isRowSelectable={() => {
        return isSelectable;
      }}
      checkboxSelection={isSelectable}
      sortingMode="server"
      // paginationMode="server"
      // checkboxSelection={!!onSelectionChange}
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
      onRowSelectionModelChange={onSelectionChange}
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
        // toolbar: GridToolbar,
        pagination: CustomPagination,
      }}
      autoHeight
      getRowHeight={() => "auto"}
      rowHeight={sizeDefault === "small" ? 40 : 50}
      headerHeight={sizeDefault === "small" ? 40 : 50}
      sx={{
        borderLeft: 0,
        borderRight: 0,
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
        ...sx,
      }}
    />
  );
};
export default CustomDataGrid;
