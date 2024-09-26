"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _react = _interopRequireDefault(require("react"));
var _lab = require("@mui/lab");
var _material = require("@mui/material");
var _excluded = ["children", "textOk", "textClose", "onClose", "onOk", "isLoading", "isNotShowCloseButton", "isNotShowActionButton", "disabledOk", "okButtonType", "renderActions"];
function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }
function _extends() { _extends = Object.assign ? Object.assign.bind() : function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }
function _objectWithoutProperties(source, excluded) { if (source == null) return {}; var target = _objectWithoutPropertiesLoose(source, excluded); var key, i; if (Object.getOwnPropertySymbols) { var sourceSymbolKeys = Object.getOwnPropertySymbols(source); for (i = 0; i < sourceSymbolKeys.length; i++) { key = sourceSymbolKeys[i]; if (excluded.indexOf(key) >= 0) continue; if (!Object.prototype.propertyIsEnumerable.call(source, key)) continue; target[key] = source[key]; } } return target; }
function _objectWithoutPropertiesLoose(source, excluded) { if (source == null) return {}; var target = {}; var sourceKeys = Object.keys(source); var key, i; for (i = 0; i < sourceKeys.length; i++) { key = sourceKeys[i]; if (excluded.indexOf(key) >= 0) continue; target[key] = source[key]; } return target; }
var ModalAction = function ModalAction(props) {
  var children = props.children,
    textOk = props.textOk,
    textClose = props.textClose,
    onClose = props.onClose,
    onOk = props.onOk,
    isLoading = props.isLoading,
    isNotShowCloseButton = props.isNotShowCloseButton,
    isNotShowActionButton = props.isNotShowActionButton,
    disabledOk = props.disabledOk,
    okButtonType = props.okButtonType,
    Component = props.renderActions,
    remainProps = _objectWithoutProperties(props, _excluded);
  if (!onOk && !onClose && !Component) {
    return /*#__PURE__*/_react["default"].createElement(_react["default"].Fragment, null, " ");
  }
  return /*#__PURE__*/_react["default"].createElement(_material.DialogActions, _extends({}, remainProps, {
    style: {
      paddingTop: "28px",
      paddingRight: "4px"
    }
  }), Component ? /*#__PURE__*/_react["default"].createElement(Component, null) : /*#__PURE__*/_react["default"].createElement(_material.Box, {
    display: "flex"
  }, onClose && !isNotShowCloseButton ? /*#__PURE__*/_react["default"].createElement(_material.Button, {
    variant: "outlined",
    color: "primary",
    size: "small",
    onClick: function onClick() {
      return onClose();
    }
  }, textClose ? textClose : "Cancel") : null, onOk && !isNotShowActionButton ? /*#__PURE__*/_react["default"].createElement(_lab.LoadingButton, {
    variant: "contained",
    color: okButtonType ? okButtonType : "primary",
    size: "small",
    style: {
      marginLeft: "16px"
    },
    onClick: function onClick() {
      return onOk();
    },
    loading: isLoading,
    disabled: disabledOk
  }, textOk ? textOk : "OK") : null));
};
var _default = ModalAction;
exports["default"] = _default;