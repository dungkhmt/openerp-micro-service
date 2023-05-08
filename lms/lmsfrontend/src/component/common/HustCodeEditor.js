import React from "react";
import AceEditor from "react-ace";
import {Box, Typography} from "@mui/material";
import HustCodeLanguagePicker from "./HustCodeLanguagePicker";
import "ace-builds/src-noconflict/mode-java";
import "ace-builds/src-noconflict/mode-c_cpp";
import "ace-builds/src-noconflict/mode-python";
import "ace-builds/src-noconflict/theme-monokai";

const HustCodeEditor = (props) => {
  const {
    classRoot,
    title,
    placeholder = "Write your Source code here",
    language,
    onChangeLanguage,
    sourceCode,
    onChangeSourceCode,
    height = "420px",
    ...remainProps
  } = props;

  const convertLanguageToEditorMode = (language) => {
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
  }
  return (
    <Box {...remainProps} className={`${classRoot}`} sx={{marginTop: "24px"}}>
      <Box sx={{display: "flex", flexDirection: "row", justifyContent: "space-between", marginBottom: "8px"}}>
        <Typography variant="h5">{title}</Typography>
        <HustCodeLanguagePicker language={language} onChangeLanguage={onChangeLanguage}/>
      </Box>

      <AceEditor
        width="100%"
        height={height}
        style={{paddingTop: "6px"}}
        placeholder={placeholder}
        mode={convertLanguageToEditorMode(language)}
        theme="monokai"
        onChange={onChangeSourceCode}
        fontSize={16}
        value={sourceCode}/>
    </Box>

  );
};

export default React.memo(HustCodeEditor);
