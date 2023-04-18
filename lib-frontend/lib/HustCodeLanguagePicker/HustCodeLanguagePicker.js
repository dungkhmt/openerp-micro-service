"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.HustCodeLanguagePicker = void 0;
var _react = _interopRequireDefault(require("react"));
var _material = require("@mui/material");
var _excluded = ["language", "onChangeLanguage", "classRoot"];
function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }
function _extends() { _extends = Object.assign ? Object.assign.bind() : function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }
function _objectWithoutProperties(source, excluded) { if (source == null) return {}; var target = _objectWithoutPropertiesLoose(source, excluded); var key, i; if (Object.getOwnPropertySymbols) { var sourceSymbolKeys = Object.getOwnPropertySymbols(source); for (i = 0; i < sourceSymbolKeys.length; i++) { key = sourceSymbolKeys[i]; if (excluded.indexOf(key) >= 0) continue; if (!Object.prototype.propertyIsEnumerable.call(source, key)) continue; target[key] = source[key]; } } return target; }
function _objectWithoutPropertiesLoose(source, excluded) { if (source == null) return {}; var target = {}; var sourceKeys = Object.keys(source); var key, i; for (i = 0; i < sourceKeys.length; i++) { key = sourceKeys[i]; if (excluded.indexOf(key) >= 0) continue; target[key] = source[key]; } return target; }
var HustCodeLanguagePicker = function HustCodeLanguagePicker(props) {
  var language = props.language,
    onChangeLanguage = props.onChangeLanguage,
    classRoot = props.classRoot,
    remainProps = _objectWithoutProperties(props, _excluded);
  var computerLanguageList = ["CPP", "JAVA", "PYTHON3"];
  return /*#__PURE__*/_react["default"].createElement(_material.TextField, _extends({}, remainProps, {
    className: "".concat(classRoot),
    variant: "outlined",
    size: "small",
    autoFocus: true,
    value: language,
    select: true,
    id: "computerLanguage",
    onChange: onChangeLanguage,
    label: "Language"
  }), computerLanguageList.map(function (item) {
    return /*#__PURE__*/_react["default"].createElement(_material.MenuItem, {
      key: item,
      value: item
    }, item);
  }));
};
exports.HustCodeLanguagePicker = HustCodeLanguagePicker;