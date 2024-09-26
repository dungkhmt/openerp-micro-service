"use strict";

function _typeof(obj) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (obj) { return typeof obj; } : function (obj) { return obj && "function" == typeof Symbol && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }, _typeof(obj); }
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.themeTable = exports.tableIcons = exports.localization = exports.components = void 0;
var _AddBox = _interopRequireDefault(require("@mui/icons-material/AddBox"));
var _ArrowDownward = _interopRequireDefault(require("@mui/icons-material/ArrowDownward"));
var _Check = _interopRequireDefault(require("@mui/icons-material/Check"));
var _CheckCircle = _interopRequireDefault(require("@mui/icons-material/CheckCircle"));
var _ChevronLeft = _interopRequireDefault(require("@mui/icons-material/ChevronLeft"));
var _ChevronRight = _interopRequireDefault(require("@mui/icons-material/ChevronRight"));
var _Clear = _interopRequireDefault(require("@mui/icons-material/Clear"));
var _DeleteOutline = _interopRequireDefault(require("@mui/icons-material/DeleteOutline"));
var _Edit = _interopRequireDefault(require("@mui/icons-material/Edit"));
var _FirstPage = _interopRequireDefault(require("@mui/icons-material/FirstPage"));
var _LastPage = _interopRequireDefault(require("@mui/icons-material/LastPage"));
var _RadioButtonUnchecked = _interopRequireDefault(require("@mui/icons-material/RadioButtonUnchecked"));
var _Remove = _interopRequireDefault(require("@mui/icons-material/Remove"));
var _RemoveCircle = _interopRequireDefault(require("@mui/icons-material/RemoveCircle"));
var _SaveAlt = _interopRequireDefault(require("@mui/icons-material/SaveAlt"));
var _Search = _interopRequireDefault(require("@mui/icons-material/Search"));
var _ViewColumn = _interopRequireDefault(require("@mui/icons-material/ViewColumn"));
var _materialTable = require("material-table");
var _react = _interopRequireWildcard(require("react"));
var _FilterAlt = _interopRequireDefault(require("@mui/icons-material/FilterAlt"));
var _material = require("@mui/material");
var _colors = require("@mui/material/colors");
function _getRequireWildcardCache(nodeInterop) { if (typeof WeakMap !== "function") return null; var cacheBabelInterop = new WeakMap(); var cacheNodeInterop = new WeakMap(); return (_getRequireWildcardCache = function _getRequireWildcardCache(nodeInterop) { return nodeInterop ? cacheNodeInterop : cacheBabelInterop; })(nodeInterop); }
function _interopRequireWildcard(obj, nodeInterop) { if (!nodeInterop && obj && obj.__esModule) { return obj; } if (obj === null || _typeof(obj) !== "object" && typeof obj !== "function") { return { "default": obj }; } var cache = _getRequireWildcardCache(nodeInterop); if (cache && cache.has(obj)) { return cache.get(obj); } var newObj = {}; var hasPropertyDescriptor = Object.defineProperty && Object.getOwnPropertyDescriptor; for (var key in obj) { if (key !== "default" && Object.prototype.hasOwnProperty.call(obj, key)) { var desc = hasPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : null; if (desc && (desc.get || desc.set)) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } newObj["default"] = obj; if (cache) { cache.set(obj, newObj); } return newObj; }
function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }
function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); enumerableOnly && (symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; })), keys.push.apply(keys, symbols); } return keys; }
function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = null != arguments[i] ? arguments[i] : {}; i % 2 ? ownKeys(Object(source), !0).forEach(function (key) { _defineProperty(target, key, source[key]); }) : Object.getOwnPropertyDescriptors ? Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)) : ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } return target; }
function _defineProperty(obj, key, value) { key = _toPropertyKey(key); if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }
function _toPropertyKey(arg) { var key = _toPrimitive(arg, "string"); return _typeof(key) === "symbol" ? key : String(key); }
function _toPrimitive(input, hint) { if (_typeof(input) !== "object" || input === null) return input; var prim = input[Symbol.toPrimitive]; if (prim !== undefined) { var res = prim.call(input, hint || "default"); if (_typeof(res) !== "object") return res; throw new TypeError("@@toPrimitive must return a primitive value."); } return (hint === "string" ? String : Number)(input); }
function _extends() { _extends = Object.assign ? Object.assign.bind() : function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }
var components = {
  Container: function Container(props) {
    return /*#__PURE__*/_react["default"].createElement(_material.Paper, _extends({}, props, {
      elevation: 0,
      style: {
        backgroundColor: "transparent"
      }
    }));
  },
  Row: function Row(props) {
    return /*#__PURE__*/_react["default"].createElement(_materialTable.MTableBodyRow, _extends({}, props, {
      options: _objectSpread(_objectSpread({}, props.options), {}, {
        selectionProps: _objectSpread(_objectSpread({}, props.options.selectionProps), {}, {
          icon: /*#__PURE__*/_react["default"].createElement(_RadioButtonUnchecked["default"], {
            fontSize: "small"
          }),
          checkedIcon: /*#__PURE__*/_react["default"].createElement(_CheckCircle["default"], {
            fontSize: "small"
          })
        })
      })
    }));
  },
  Header: function Header(props) {
    return /*#__PURE__*/_react["default"].createElement(_materialTable.MTableHeader, _extends({}, props, {
      options: _objectSpread(_objectSpread({}, props.options), {}, {
        headerSelectionProps: _objectSpread(_objectSpread({}, props.options.headerSelectionProps), {}, {
          indeterminateIcon: /*#__PURE__*/_react["default"].createElement(_RemoveCircle["default"], {
            fontSize: "small"
          }),
          icon: /*#__PURE__*/_react["default"].createElement(_RadioButtonUnchecked["default"], {
            fontSize: "small"
          }),
          checkedIcon: /*#__PURE__*/_react["default"].createElement(_CheckCircle["default"], {
            fontSize: "small"
          })
        })
      })
    }));
  }
};
exports.components = components;
var themeTable = (0, _material.createTheme)({
  palette: {
    primary: {
      main: "#3f51b5"
    },
    secondary: {
      main: _colors.green[900]
    }
  },
  overrides: {
    MuiTableRow: {
      root: {
        "&:hover": {
          backgroundColor: "#ebebeb !important"
        }
      },
      head: {
        "&:hover": {
          backgroundColor: "transparent !important"
        }
      }
    }
  }
});
exports.themeTable = themeTable;
var localization = {
  body: {
    emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
    addTooltip: "Thêm",
    deleteTooltip: "Xoá",
    editTooltip: "Chỉnh sửa",
    filterRow: {
      filterPlaceHolder: "",
      filterTooltip: "Lọc"
    },
    editRow: {
      deleteText: "Bạn có chắc chắn muốn xoá bản ghi này không?",
      cancelTooltip: "Huỷ",
      saveTooltip: "Lưu"
    }
  },
  grouping: {
    placeholder: "Kéo các tiêu đề",
    groupedBy: "Được nhóm bởi"
  },
  header: {
    actions: ""
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
    hover: "pointer"
  },
  toolbar: {
    addRemoveColumns: "Thêm hoặc xoá các cột",
    nRowsSelected: "{0} hàng được chọn",
    showColumnsTitle: "Hiển thị các cột",
    showColumnsAriaLabel: "Hiển thị các cột",
    exportTitle: "",
    // incompleted
    searchTooltip: "Tìm kiếm",
    searchPlaceholder: "Tìm kiếm"
  }
};
exports.localization = localization;
var tableIcons = {
  Add: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_AddBox["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  Check: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_Check["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  Clear: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_Clear["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  Delete: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_DeleteOutline["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  DetailPanel: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_ChevronRight["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  Edit: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_Edit["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  Export: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_SaveAlt["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  Filter: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_FilterAlt["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  FirstPage: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_FirstPage["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  LastPage: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_LastPage["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  NextPage: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_ChevronRight["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  PreviousPage: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_ChevronLeft["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  ResetSearch: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_Clear["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  Search: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_Search["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  SortArrow: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_ArrowDownward["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  ThirdStateCheck: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_Remove["default"], _extends({}, props, {
      ref: ref
    }));
  }),
  ViewColumn: /*#__PURE__*/(0, _react.forwardRef)(function (props, ref) {
    return /*#__PURE__*/_react["default"].createElement(_ViewColumn["default"], _extends({}, props, {
      ref: ref
    }));
  })
};
exports.tableIcons = tableIcons;