import { Paper } from "@material-ui/core";
import { green } from "@material-ui/core/colors";
import { createTheme } from "@material-ui/core/styles";
import AddBox from "@material-ui/icons/AddBox";
import ArrowDownward from "@material-ui/icons/ArrowDownward";
import Check from "@material-ui/icons/Check";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import ChevronLeft from "@material-ui/icons/ChevronLeft";
import ChevronRight from "@material-ui/icons/ChevronRight";
import Clear from "@material-ui/icons/Clear";
import DeleteOutline from "@material-ui/icons/DeleteOutline";
import Edit from "@material-ui/icons/Edit";
import FirstPage from "@material-ui/icons/FirstPage";
import LastPage from "@material-ui/icons/LastPage";
import RadioButtonUncheckedIcon from "@material-ui/icons/RadioButtonUnchecked";
import Remove from "@material-ui/icons/Remove";
import RemoveCircleIcon from "@material-ui/icons/RemoveCircle";
import SaveAlt from "@material-ui/icons/SaveAlt";
import Search from "@material-ui/icons/Search";
import ViewColumn from "@material-ui/icons/ViewColumn";
import { MTableBodyRow, MTableHeader } from "material-table";
import React, { forwardRef } from "react";
import { FcFilledFilter } from "react-icons/fc";

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
  Filter: forwardRef((props, ref) => <FcFilledFilter {...props} />),
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

export default function changePageSize(totalCount, tableRef) {
  if (totalCount < 6) {
    tableRef.current.dataManager.changePageSize(5);
  } else if (totalCount < 11) {
    tableRef.current.dataManager.changePageSize(10);
  } else {
    tableRef.current.dataManager.changePageSize(20);
  }
}
