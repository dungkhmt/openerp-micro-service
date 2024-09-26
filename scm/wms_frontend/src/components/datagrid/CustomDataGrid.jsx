import { Box, LinearProgress, Typography, gridClasses } from "@mui/material";
import { styled } from "@mui/material/styles";
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
 * @property {boolean} hideFooterPagination
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
    hideFooterPagination = false,
    sx,
  } = props;

  const [rowCountState, setRowCountState] = React.useState(totalItem);
  React.useEffect(() => {
    setRowCountState((prevRowCountState) =>
      totalItem !== undefined ? totalItem : prevRowCountState
    );
  }, [totalItem, setRowCountState]);

  const sizeDefault = size ? size : "small";

  const StyledGridOverlay = styled("div")(({ theme }) => ({
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    width: "100%",
    height: "100%",
    "& .ant-empty-img-1": {
      fill: theme.palette.mode === "light" ? "#aeb8c2" : "#262626",
    },
    "& .ant-empty-img-2": {
      fill: theme.palette.mode === "light" ? "#f5f5f7" : "#595959",
    },
    "& .ant-empty-img-3": {
      fill: theme.palette.mode === "light" ? "#dce0e6" : "#434343",
    },
    "& .ant-empty-img-4": {
      fill: theme.palette.mode === "light" ? "#fff" : "#1c1c1c",
    },
    "& .ant-empty-img-5": {
      fillOpacity: theme.palette.mode === "light" ? "0.8" : "0.08",
      fill: theme.palette.mode === "light" ? "#f5f5f5" : "#fff",
    },
  }));
  function CustomNoRowsOverlay() {
    return (
      <StyledGridOverlay>
        <svg
          width="300"
          height="250"
          viewBox="0 0 184 152"
          aria-hidden
          focusable="false"
        >
          <g fill="none" fillRule="evenodd">
            <g transform="translate(24 31.67)">
              <ellipse
                className="ant-empty-img-5"
                cx="67.797"
                cy="106.89"
                rx="67.797"
                ry="12.668"
              />
              <path
                className="ant-empty-img-1"
                d="M122.034 69.674L98.109 40.229c-1.148-1.386-2.826-2.225-4.593-2.225h-51.44c-1.766 0-3.444.839-4.592 2.225L13.56 69.674v15.383h108.475V69.674z"
              />
              <path
                className="ant-empty-img-2"
                d="M33.83 0h67.933a4 4 0 0 1 4 4v93.344a4 4 0 0 1-4 4H33.83a4 4 0 0 1-4-4V4a4 4 0 0 1 4-4z"
              />
              <path
                className="ant-empty-img-3"
                d="M42.678 9.953h50.237a2 2 0 0 1 2 2V36.91a2 2 0 0 1-2 2H42.678a2 2 0 0 1-2-2V11.953a2 2 0 0 1 2-2zM42.94 49.767h49.713a2.262 2.262 0 1 1 0 4.524H42.94a2.262 2.262 0 0 1 0-4.524zM42.94 61.53h49.713a2.262 2.262 0 1 1 0 4.525H42.94a2.262 2.262 0 0 1 0-4.525zM121.813 105.032c-.775 3.071-3.497 5.36-6.735 5.36H20.515c-3.238 0-5.96-2.29-6.734-5.36a7.309 7.309 0 0 1-.222-1.79V69.675h26.318c2.907 0 5.25 2.448 5.25 5.42v.04c0 2.971 2.37 5.37 5.277 5.37h34.785c2.907 0 5.277-2.421 5.277-5.393V75.1c0-2.972 2.343-5.426 5.25-5.426h26.318v33.569c0 .617-.077 1.216-.221 1.789z"
              />
            </g>
            <path
              className="ant-empty-img-3"
              d="M149.121 33.292l-6.83 2.65a1 1 0 0 1-1.317-1.23l1.937-6.207c-2.589-2.944-4.109-6.534-4.109-10.408C138.802 8.102 148.92 0 161.402 0 173.881 0 184 8.102 184 18.097c0 9.995-10.118 18.097-22.599 18.097-4.528 0-8.744-1.066-12.28-2.902z"
            />
            <g className="ant-empty-img-4" transform="translate(149.65 15.383)">
              <ellipse cx="20.654" cy="3.167" rx="2.849" ry="2.815" />
              <path d="M5.698 5.63H0L2.898.704zM9.259.704h4.985V5.63H9.259z" />
            </g>
          </g>
        </svg>
        <Box sx={{ margin: 0 }}>Không có dữ liệu</Box>
      </StyledGridOverlay>
    );
  }
  return (
    <DataGrid
      initialState={{
        ...initialState,
      }}
      columns={getColumnsShow(columns, isSerial, params)}
      rows={rows}
      // isRowSelectable={() => {
      //   return isSelectable;          Got bug if turn this thing on: Uncaught Error: No row with id #... found
      // }}
      hideFooterPagination={hideFooterPagination}
      pageSizeOptions={[5, 10, 25]}
      rowCount={rowCountState}
      paginationModel={{
        page: params.page - 1,
        pageSize: params.pageSize,
      }}
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
      slots={{
        noRowsOverlay: CustomNoRowsOverlay,
        // toolbar: GridToolbar,
      }}
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
