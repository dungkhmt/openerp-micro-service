import { LinearProgress, Typography, gridClasses } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import React from "react";
import { AppColors } from "../../shared/AppColors";

const getColumnsShow = (columns, isSerial, params) => {
  let columnsRaw = columns || [];

  if (isSerial) {
    columnsRaw = [
      {
        field: "idx",
        headerName: "STT",
        renderCell: (index) => (
          <Typography sx={{ fontWeight: "500" }}>
            {index.row.id + 1 + (params.page - 1) * params.pageSize}
          </Typography>
        ),
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
 * @property {import("@mui/x-data-grid").GridColDef[]} columns
 * @property {number} totalItem
 * @property {import("@mui/x-data-grid/models/gridStateCommunity").GridInitialStateCommunity} initialState
 * @property {{page: number, pageSize: number}} params
 * @property {boolean} isLoading
 * @property {Function} setParams
 * @property {Function} onSelectionChange
 * @property {any} selectionModel
 * @property {"small" | "other"} size
 * @property {import("@mui/x-data-grid").GridFeatureMode} paginationMode
 * @property {import("@mui/x-data-grid").GridColumnGroupingModel} columnGroupingModel
 * @property {import("@mui/material").SxProps} sx
 * @param {Prop} props
 */
const CustomDataGrid = (props) => {
  const {
    isSerial,
    isSelectable = false,
    isEditable,
    rows,
    columns,
    totalItem,
    initialState,
    params,
    isLoading,
    setParams,
    onSelectionChange,
    selectionModel,
    paginationMode = "server",
    size,
    columnGroupingModel,
    handlePaginationModelChange,
    sx,
  } = props;

  const [rowCountState, setRowCountState] = React.useState(totalItem);
  React.useEffect(() => {
    setRowCountState((prevRowCountState) =>
      totalItem !== undefined ? totalItem : prevRowCountState
    );
  }, [totalItem, setRowCountState]);

  const sizeDefault = size ? size : "small";
  return (
    <DataGrid
      initialState={{
        ...initialState,
      }}
      columns={getColumnsShow(columns, isSerial, params)}
      rows={rows}
      isRowSelectable={() => {
        return isSelectable;
      }}
      rowCount={rowCountState}
      paginationModel={{
        page: params.page - 1,
        pageSize: params.pageSize,
      }}
      // pageSizeOptions={[params.pageSize]}
      paginationMode={paginationMode}
      onPaginationModelChange={handlePaginationModelChange}
      loading={isLoading}
      isCellEditable={isEditable}
      checkboxSelection={isSelectable}
      sortingMode="server"
      disableRowSelectionOnClick
      disableColumnMenu
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
      // onPageSizeChange={(pageSize) =>
      //   setParams((prev) => ({ ...prev, pageSize, page: 1 }))
      // }
      // onPageChange={(page) =>
      //   setParams((prev) => ({ ...prev, page: page + 1 }))
      // }
      onSortModelChange={(sort) =>
        setParams((prev) => ({
          ...prev,
          page: 1,
          sort: sort[0] ? `${sort[0].field},${sort[0].sort}` : "",
        }))
      }
      // styling
      slots={
        {
          // toolbar: GridToolbar,
        }
      }
      autoHeight
      rowHeight={sizeDefault === "small" ? 50 : 60}
      headerHeight={sizeDefault === "small" ? 50 : 60}
      sx={{
        borderLeft: 0,
        borderRight: 0,
        ".MuiDataGrid-columnHeader": {
          background: "#Dbeef4",
        },
        ".MuiDataGrid-iconSeparator": {
          visibility: "hidden",
        },
        ".MuiDataGrid-columnHeaderTitle": {
          fontWeight: "600",
          fontFamily: "inherit",
          color: AppColors.primary,
          fontSize: 14,
          textTransform: "uppercase",
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
