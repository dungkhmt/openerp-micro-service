"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.StandardTable = exports.Offset = void 0;
exports.TablePaginationActions = TablePaginationActions;
var _material = require("@mui/material");
var _materialTable = _interopRequireWildcard(require("material-table"));
var _propTypes = _interopRequireDefault(require("prop-types"));
var _react = _interopRequireWildcard(require("react"));
var _MaterialTableUtils = require("./MaterialTableUtils");
var _FirstPage = _interopRequireDefault(require("@mui/icons-material/FirstPage"));
var _KeyboardArrowLeft = _interopRequireDefault(require("@mui/icons-material/KeyboardArrowLeft"));
var _KeyboardArrowRight = _interopRequireDefault(require("@mui/icons-material/KeyboardArrowRight"));
var _LastPage = _interopRequireDefault(require("@mui/icons-material/LastPage"));
var _styled = _interopRequireDefault(require("@emotion/styled"));
var _styles = require("@mui/styles");
function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }
function _getRequireWildcardCache(nodeInterop) { if (typeof WeakMap !== "function") return null; var cacheBabelInterop = new WeakMap(); var cacheNodeInterop = new WeakMap(); return (_getRequireWildcardCache = function _getRequireWildcardCache(nodeInterop) { return nodeInterop ? cacheNodeInterop : cacheBabelInterop; })(nodeInterop); }
function _interopRequireWildcard(obj, nodeInterop) { if (!nodeInterop && obj && obj.__esModule) { return obj; } if (obj === null || _typeof(obj) !== "object" && typeof obj !== "function") { return { "default": obj }; } var cache = _getRequireWildcardCache(nodeInterop); if (cache && cache.has(obj)) { return cache.get(obj); } var newObj = {}; var hasPropertyDescriptor = Object.defineProperty && Object.getOwnPropertyDescriptor; for (var key in obj) { if (key !== "default" && Object.prototype.hasOwnProperty.call(obj, key)) { var desc = hasPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : null; if (desc && (desc.get || desc.set)) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } newObj["default"] = obj; if (cache) { cache.set(obj, newObj); } return newObj; }
function _typeof(obj) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (obj) { return typeof obj; } : function (obj) { return obj && "function" == typeof Symbol && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }, _typeof(obj); }
function _extends() { _extends = Object.assign ? Object.assign.bind() : function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }
function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); enumerableOnly && (symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; })), keys.push.apply(keys, symbols); } return keys; }
function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = null != arguments[i] ? arguments[i] : {}; i % 2 ? ownKeys(Object(source), !0).forEach(function (key) { _defineProperty(target, key, source[key]); }) : Object.getOwnPropertyDescriptors ? Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)) : ownKeys(Object(source)).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } return target; }
function _defineProperty(obj, key, value) { key = _toPropertyKey(key); if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }
function _toPropertyKey(arg) { var key = _toPrimitive(arg, "string"); return _typeof(key) === "symbol" ? key : String(key); }
function _toPrimitive(input, hint) { if (_typeof(input) !== "object" || input === null) return input; var prim = input[Symbol.toPrimitive]; if (prim !== undefined) { var res = prim.call(input, hint || "default"); if (_typeof(res) !== "object") return res; throw new TypeError("@@toPrimitive must return a primitive value."); } return (hint === "string" ? String : Number)(input); }
var Offset = (0, _styled["default"])("div")(function (_ref) {
  var theme = _ref.theme;
  return _objectSpread(_objectSpread({
    display: "flex",
    alignItems: "center",
    padding: theme.spacing(0, 1)
  }, theme.mixins.toolbar), {}, {
    justifyContent: "flex-end"
  });
});
exports.Offset = Offset;
function TablePaginationActions(props) {
  var count = props.count,
    page = props.page,
    rowsPerPage = props.rowsPerPage,
    onPageChange = props.onPageChange;
  var handleFirstPageButtonClick = function handleFirstPageButtonClick(event) {
    onPageChange(event, 0);
  };
  var handleBackButtonClick = function handleBackButtonClick(event) {
    onPageChange(event, page - 1);
  };
  var handleNextButtonClick = function handleNextButtonClick(event) {
    onPageChange(event, page + 1);
  };
  var handleLastPageButtonClick = function handleLastPageButtonClick(event) {
    onPageChange(event, Math.max(0, Math.ceil(count / rowsPerPage) - 1));
  };
  return /*#__PURE__*/_react["default"].createElement(_material.Box, {
    sx: {
      flexShrink: 0,
      ml: 2.5
    }
  }, /*#__PURE__*/_react["default"].createElement(_material.IconButton, {
    onClick: handleFirstPageButtonClick,
    disabled: page === 0,
    "aria-label": "first page"
  }, /*#__PURE__*/_react["default"].createElement(_FirstPage["default"], null)), /*#__PURE__*/_react["default"].createElement(_material.IconButton, {
    onClick: handleBackButtonClick,
    disabled: page === 0,
    "aria-label": "previous page"
  }, /*#__PURE__*/_react["default"].createElement(_KeyboardArrowLeft["default"], null)), /*#__PURE__*/_react["default"].createElement(_material.IconButton, {
    onClick: handleNextButtonClick,
    disabled: page >= Math.ceil(count / rowsPerPage) - 1,
    "aria-label": "next page"
  }, /*#__PURE__*/_react["default"].createElement(_KeyboardArrowRight["default"], null)), /*#__PURE__*/_react["default"].createElement(_material.IconButton, {
    onClick: handleLastPageButtonClick,
    disabled: page >= Math.ceil(count / rowsPerPage) - 1,
    "aria-label": "last page"
  }, /*#__PURE__*/_react["default"].createElement(_LastPage["default"], null)));
}
var useStyles = (0, _styles.makeStyles)(function () {
  return {
    tableToolbarHighlight: {
      backgroundColor: "transparent"
    }
  };
});
var StandardTable = function StandardTable(props) {
  var _props$sx;
  var classes = useStyles();
  var rowStyle = (0, _react.useCallback)(function (rowData) {
    return {
      backgroundColor: rowData.tableData.checked ? "#e0e0e0" : "#ffffff"
    };
  }, []);
  return /*#__PURE__*/_react["default"].createElement(_react["default"].Fragment, null, !props.hideCommandBar && /*#__PURE__*/_react["default"].createElement(_react["default"].Fragment, null, /*#__PURE__*/_react["default"].createElement(_material.Box, {
    sx: _objectSpread({
      width: "100%",
      height: 40,
      display: "flex",
      justifyContent: "flex-start",
      alignItems: "center",
      borderBottom: "1px solid rgb(224, 224, 224)",
      pl: 2,
      backgroundColor: "#f5f5f5"
    }, (_props$sx = props.sx) === null || _props$sx === void 0 ? void 0 : _props$sx.commandBar)
    // className={props.classNames?.commandBar}
  }, props.commandBarComponents)), /*#__PURE__*/_react["default"].createElement(_material.ThemeProvider, {
    theme: _MaterialTableUtils.themeTable
  }, /*#__PURE__*/_react["default"].createElement(_materialTable["default"], _extends({}, props, {
    title: props.title ? /*#__PURE__*/_react["default"].createElement(_material.Typography, {
      variant: "h5"
    }, props.title) : /*#__PURE__*/_react["default"].createElement(_react["default"].Fragment, null),
    localization: _objectSpread(_objectSpread({}, _MaterialTableUtils.localization), {}, {
      toolbar: {
        searchPlaceholder: "Search"
      }
    }, props.localization),
    icons: _MaterialTableUtils.tableIcons,
    options: _objectSpread({
      selection: true,
      pageSize: 10,
      headerStyle: {
        backgroundColor: "#f4f4f4",
        color: "#404040",
        fontWeight: 600
      },
      rowStyle: rowStyle
    }, props.options),
    onSelectionChange: function onSelectionChange(rows) {
      props.onSelectionChange(rows);
    },
    onRowClick: props.onRowClick,
    components: _objectSpread(_objectSpread({}, _MaterialTableUtils.components), {}, {
      Container: function Container(props) {
        return /*#__PURE__*/_react["default"].createElement(_material.Paper, _extends({}, props, {
          elevation: 3
        }));
      },
      Toolbar: function Toolbar(props) {
        return /*#__PURE__*/_react["default"].createElement(_materialTable.MTableToolbar, _extends({}, props, {
          classes: {
            highlight: classes.tableToolbarHighlight
          },
          searchFieldStyle: {
            height: 40
          }
        }));
      },
      Cell: function Cell(props) {
        return /*#__PURE__*/_react["default"].createElement(_materialTable.MTableCell, _extends({}, props, {
          style: {
            padding: "16px 16px"
          }
        }));
      }
    }, props.components),
    actions: props.actions,
    editable: props.editable
  }))));
};
exports.StandardTable = StandardTable;
StandardTable.propTypes = {
  hideCommandBar: _propTypes["default"].bool,
  classNames: _propTypes["default"].object,
  localization: _propTypes["default"].object,
  options: _propTypes["default"].object,
  onSelectionChange: _propTypes["default"].func,
  onRowClick: _propTypes["default"].func,
  components: _propTypes["default"].object,
  title: _propTypes["default"].string,
  columns: _propTypes["default"].array.isRequired,
  actions: _propTypes["default"].array,
  data: _propTypes["default"].array,
  commandBarComponents: _propTypes["default"].element,
  editable: _propTypes["default"].object
};