import AddBox from "@mui/icons-material/AddBox";
import ArrowDownward from "@mui/icons-material/ArrowDownward";
import Check from "@mui/icons-material/Check";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import ChevronLeft from "@mui/icons-material/ChevronLeft";
import ChevronRight from "@mui/icons-material/ChevronRight";
import Clear from "@mui/icons-material/Clear";
import DeleteOutline from "@mui/icons-material/DeleteOutline";
import Edit from "@mui/icons-material/Edit";
import FirstPage from "@mui/icons-material/FirstPage";
import LastPage from "@mui/icons-material/LastPage";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import Remove from "@mui/icons-material/Remove";
import RemoveCircleIcon from "@mui/icons-material/RemoveCircle";
import SaveAlt from "@mui/icons-material/SaveAlt";
import Search from "@mui/icons-material/Search";
import ViewColumn from "@mui/icons-material/ViewColumn";
import { MTableBodyRow, MTableHeader } from "material-table";
import React, { forwardRef } from "react";
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import {createTheme, Paper} from "@mui/material";
import {green} from "@mui/material/colors";

export const components = {
  Container: (props) => (
    <Paper
      {...props}
      elevation={0}
      style={{ backgroundColor: "transparent" }}
    />
  ),

  Row: (props) => (
    <MTableBodyRow
      {...props}
      options={{
        ...props.options,
        selectionProps: {
          ...props.options.selectionProps,
          icon: <RadioButtonUncheckedIcon fontSize="small" />,
          checkedIcon: <CheckCircleIcon fontSize="small" />,
        },
      }}
    />
  ),

  Header: (props) => (
    <MTableHeader
      {...props}
      options={{
        ...props.options,
        headerSelectionProps: {
          ...props.options.headerSelectionProps,
          indeterminateIcon: <RemoveCircleIcon fontSize="small" />,
          icon: <RadioButtonUncheckedIcon fontSize="small" />,
          checkedIcon: <CheckCircleIcon fontSize="small" />,
        },
      }}
    />
  ),
};

export const themeTable = createTheme({
  palette: {
    primary: {
      main: "#3f51b5",
    },
    secondary: {
      main: green[900],
    },
  },
  overrides: {
    MuiTableRow: {
      root: {
        "&:hover": {
          backgroundColor: "#ebebeb !important",
        },
      },
      head: {
        "&:hover": {
          backgroundColor: "transparent !important",
        },
      },
    },
  },
});

export const localization = {
  body: {
    emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
    addTooltip: "Thêm",
    deleteTooltip: "Xoá",
    editTooltip: "Chỉnh sửa",
    filterRow: { filterPlaceHolder: "", filterTooltip: "Lọc" },
    editRow: {
      deleteText: "Bạn có chắc chắn muốn xoá bản ghi này không?",
      cancelTooltip: "Huỷ",
      saveTooltip: "Lưu",
    },
  },
  grouping: {
    placeholder: "Kéo các tiêu đề",
    groupedBy: "Được nhóm bởi",
  },
  header: {
    actions: "",
  },
  pagination: {
    labelDisplayedRows: "{from}-{to} của {count}",
    labelRowsSelect: "hàng",
    labelRowsPerPage: "Bản ghi/trang",
    firstAriaLabel: "Trang đầu",
    firstTooltip: "Trang đầu",
    previousAriaLabel: "Trang trước",
    previousTooltip: "Trang trước",
    nextAriaLabel: "Trang tiếp theo",
    nextTooltip: "Trang tiếp theo",
    lastAriaLabel: "Trang cuối",
    lastTooltip: "Trang cuối",
    hover: "pointer",
  },
  toolbar: {
    addRemoveColumns: "Thêm hoặc xoá các cột",
    nRowsSelected: "{0} hàng được chọn",
    showColumnsTitle: "Hiển thị các cột",
    showColumnsAriaLabel: "Hiển thị các cột",
    exportTitle: "", // incompleted
    searchTooltip: "Tìm kiếm",
    searchPlaceholder: "Tìm kiếm",
  },
};

export const tableIcons = {
  Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
  Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
  Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
  Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
  DetailPanel: forwardRef((props, ref) => (
    <ChevronRight {...props} ref={ref} />
  )),
  Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
  Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref} />),
  Filter: forwardRef((props, ref) => <FilterAltIcon {...props} ref={ref}/>),
  FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
  LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
  NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
  PreviousPage: forwardRef((props, ref) => (
    <ChevronLeft {...props} ref={ref} />
  )),
  ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
  Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
  SortArrow: forwardRef((props, ref) => <ArrowDownward {...props} ref={ref} />),
  ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
  ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />),
};

