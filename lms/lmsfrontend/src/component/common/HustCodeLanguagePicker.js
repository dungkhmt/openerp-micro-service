import React from "react";
import {MenuItem, TextField} from "@mui/material";

const HustCodeLanguagePicker = (props) => {
  const {
    language,
    onChangeLanguage,
    classRoot,
    ...remainProps
  } = props;

  const computerLanguageList = ["CPP", "JAVA", "PYTHON3"];

  return (
    <TextField
      {...remainProps}
      className={`${classRoot}`}
      variant={"outlined"}
      size={"small"}
      autoFocus
      value={language}
      select
      id="computerLanguage"
      onChange={onChangeLanguage}
    >
      {computerLanguageList.map((item) => (
        <MenuItem key={item} value={item}>
          {item}
        </MenuItem>
      ))}
    </TextField>
  );
};

export default React.memo(HustCodeLanguagePicker);
