"use strict";

function _typeof(obj) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (obj) { return typeof obj; } : function (obj) { return obj && "function" == typeof Symbol && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }, _typeof(obj); }
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.HustCodeEditor = void 0;
var _react = _interopRequireWildcard(require("react"));
var _reactAce = _interopRequireDefault(require("react-ace"));
var _material = require("@mui/material");
require("ace-builds/src-noconflict/mode-java");
require("ace-builds/src-noconflict/mode-c_cpp");
require("ace-builds/src-noconflict/mode-python");
require("ace-builds/src-noconflict/theme-monokai");
var _LanguagePicker = require("../LanguagePicker");
var _propTypes = _interopRequireDefault(require("prop-types"));
var _excluded = ["classRoot", "title", "placeholder", "language", "onChangeLanguage", "sourceCode", "onChangeSourceCode", "height"];
function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }
function _getRequireWildcardCache(nodeInterop) { if (typeof WeakMap !== "function") return null; var cacheBabelInterop = new WeakMap(); var cacheNodeInterop = new WeakMap(); return (_getRequireWildcardCache = function _getRequireWildcardCache(nodeInterop) { return nodeInterop ? cacheNodeInterop : cacheBabelInterop; })(nodeInterop); }
function _interopRequireWildcard(obj, nodeInterop) { if (!nodeInterop && obj && obj.__esModule) { return obj; } if (obj === null || _typeof(obj) !== "object" && typeof obj !== "function") { return { "default": obj }; } var cache = _getRequireWildcardCache(nodeInterop); if (cache && cache.has(obj)) { return cache.get(obj); } var newObj = {}; var hasPropertyDescriptor = Object.defineProperty && Object.getOwnPropertyDescriptor; for (var key in obj) { if (key !== "default" && Object.prototype.hasOwnProperty.call(obj, key)) { var desc = hasPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : null; if (desc && (desc.get || desc.set)) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } newObj["default"] = obj; if (cache) { cache.set(obj, newObj); } return newObj; }
function _extends() { _extends = Object.assign ? Object.assign.bind() : function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }
function _objectWithoutProperties(source, excluded) { if (source == null) return {}; var target = _objectWithoutPropertiesLoose(source, excluded); var key, i; if (Object.getOwnPropertySymbols) { var sourceSymbolKeys = Object.getOwnPropertySymbols(source); for (i = 0; i < sourceSymbolKeys.length; i++) { key = sourceSymbolKeys[i]; if (excluded.indexOf(key) >= 0) continue; if (!Object.prototype.propertyIsEnumerable.call(source, key)) continue; target[key] = source[key]; } } return target; }
function _objectWithoutPropertiesLoose(source, excluded) { if (source == null) return {}; var target = {}; var sourceKeys = Object.keys(source); var key, i; for (i = 0; i < sourceKeys.length; i++) { key = sourceKeys[i]; if (excluded.indexOf(key) >= 0) continue; target[key] = source[key]; } return target; }
var HustCodeEditor = function HustCodeEditor(props) {
  var classRoot = props.classRoot,
    title = props.title,
    _props$placeholder = props.placeholder,
    placeholder = _props$placeholder === void 0 ? "Write your Source code here" : _props$placeholder,
    language = props.language,
    onChangeLanguage = props.onChangeLanguage,
    sourceCode = props.sourceCode,
    onChangeSourceCode = props.onChangeSourceCode,
    _props$height = props.height,
    height = _props$height === void 0 ? "420px" : _props$height,
    remainProps = _objectWithoutProperties(props, _excluded);
  var convertLanguageToEditorMode = function convertLanguageToEditorMode(language) {
    switch (language) {
      case "CPP":
        return "c_cpp";
      case "JAVA":
        return "java";
      case "PYTHON3":
        return "python";
      default:
        return "c_cpp";
    }
  };
  return /*#__PURE__*/_react["default"].createElement(_material.Box, _extends({}, remainProps, {
    className: "".concat(classRoot),
    sx: {
      marginTop: "24px"
    }
  }), /*#__PURE__*/_react["default"].createElement(_material.Box, {
    sx: {
      display: "flex",
      flexDirection: "row",
      justifyContent: "space-between",
      marginBottom: "8px"
    }
  }, /*#__PURE__*/_react["default"].createElement(_material.Typography, {
    variant: "h5"
  }, title), /*#__PURE__*/_react["default"].createElement(_LanguagePicker.HustCodeLanguagePicker, {
    language: language,
    onChangeLanguage: onChangeLanguage
  })), /*#__PURE__*/_react["default"].createElement(_reactAce["default"], {
    width: "100%",
    height: height,
    style: {
      paddingTop: "6px"
    },
    placeholder: placeholder,
    mode: convertLanguageToEditorMode(language),
    theme: "monokai",
    onChange: onChangeSourceCode,
    fontSize: 16,
    value: sourceCode
  }));
};
exports.HustCodeEditor = HustCodeEditor;
HustCodeEditor.propTypes = {
  title: _propTypes["default"].string,
  language: _propTypes["default"].oneOf(['CPP', 'JAVA', 'PYTHON3']),
  onChangeLanguage: _propTypes["default"].func,
  sourceCode: _propTypes["default"].string,
  onChangeSourceCode: _propTypes["default"].func,
  placeholder: _propTypes["default"].string
};
HustCodeEditor.defaultProps = {
  title: 'Source code',
  language: 'CPP',
  onChangeLanguage: undefined,
  sourceCode: '',
  onChangeSourceCode: undefined,
  placeholder: 'Enter code here'
};