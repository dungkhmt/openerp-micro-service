"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.HustModal = void 0;
var _CloseRounded = _interopRequireDefault(require("@mui/icons-material/CloseRounded"));
var _material = require("@mui/material");
var _react = _interopRequireDefault(require("react"));
var _ModalAction = _interopRequireDefault(require("./ModalAction"));
var _excluded = ["children", "onClose", "onOk", "textOk", "textClose", "title", "isLoading", "showCloseBtnTitle", "maxWidthPaper", "minWidthPaper", "classRoot", "isNotShowCloseButton", "isNotShowActionButton", "isNotShowFooter", "disabledOk"];
function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }
function _extends() { _extends = Object.assign ? Object.assign.bind() : function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }
function _objectWithoutProperties(source, excluded) { if (source == null) return {}; var target = _objectWithoutPropertiesLoose(source, excluded); var key, i; if (Object.getOwnPropertySymbols) { var sourceSymbolKeys = Object.getOwnPropertySymbols(source); for (i = 0; i < sourceSymbolKeys.length; i++) { key = sourceSymbolKeys[i]; if (excluded.indexOf(key) >= 0) continue; if (!Object.prototype.propertyIsEnumerable.call(source, key)) continue; target[key] = source[key]; } } return target; }
function _objectWithoutPropertiesLoose(source, excluded) { if (source == null) return {}; var target = {}; var sourceKeys = Object.keys(source); var key, i; for (i = 0; i < sourceKeys.length; i++) { key = sourceKeys[i]; if (excluded.indexOf(key) >= 0) continue; target[key] = source[key]; } return target; }
var HustModal = function HustModal(props) {
  var children = props.children,
    onClose = props.onClose,
    onOk = props.onOk,
    textOk = props.textOk,
    textClose = props.textClose,
    title = props.title,
    isLoading = props.isLoading,
    _props$showCloseBtnTi = props.showCloseBtnTitle,
    showCloseBtnTitle = _props$showCloseBtnTi === void 0 ? true : _props$showCloseBtnTi,
    _props$maxWidthPaper = props.maxWidthPaper,
    maxWidthPaper = _props$maxWidthPaper === void 0 ? 640 : _props$maxWidthPaper,
    minWidthPaper = props.minWidthPaper,
    classRoot = props.classRoot,
    isNotShowCloseButton = props.isNotShowCloseButton,
    isNotShowActionButton = props.isNotShowActionButton,
    isNotShowFooter = props.isNotShowFooter,
    disabledOk = props.disabledOk,
    remainProps = _objectWithoutProperties(props, _excluded);
  return /*#__PURE__*/_react["default"].createElement(_material.Modal, _extends({}, remainProps, {
    onClose: onClose,
    className: "".concat(classRoot)
  }), /*#__PURE__*/_react["default"].createElement(_material.Box, {
    sx: {
      position: "absolute",
      top: "50%",
      left: "50%",
      transform: "translate(-50%, -50%)",
      bgcolor: "background.paper",
      border: "2px solid gray",
      width: maxWidthPaper,
      maxWidth: maxWidthPaper,
      maxHeight: "500px",
      overflowY: "auto",
      boxShadow: 24,
      p: "16px 28px 20px 28px",
      borderRadius: 3,
      outline: 0
      //   "& > :not(style)": { m: 1 },
    }
  }, /*#__PURE__*/_react["default"].createElement(_material.Box, {
    display: "flex",
    flexDirection: "row",
    justifyContent: "space-between",
    mb: 2
  }, /*#__PURE__*/_react["default"].createElement(_material.Typography, {
    align: "center",
    variant: "h5"
  }, title || ""), showCloseBtnTitle ? /*#__PURE__*/_react["default"].createElement(_material.IconButton, {
    onClick: onClose
  }, /*#__PURE__*/_react["default"].createElement(_CloseRounded["default"], null)) : /*#__PURE__*/_react["default"].createElement(_react["default"].Fragment, null)), children, !isNotShowFooter && /*#__PURE__*/_react["default"].createElement(_ModalAction["default"], {
    onClose: onClose,
    onOk: onOk,
    textOk: textOk,
    textClose: textClose,
    isLoading: isLoading,
    disabledOk: disabledOk,
    isNotShowCloseButton: isNotShowCloseButton,
    isNotShowActionButton: isNotShowActionButton
  })));
};
exports.HustModal = HustModal;