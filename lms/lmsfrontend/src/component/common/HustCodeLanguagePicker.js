import React from "react";
import {MenuItem, TextField} from "@mui/material";
import {COMPUTER_LANGUAGES} from "../education/programmingcontestFE/Constant";

const HustCodeLanguagePicker = (props) => {
  const {
    language,
    onChangeLanguage,
    classRoot,
    ...remainProps
  } = props;

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
      {Object.values(COMPUTER_LANGUAGES).map((item) => (
        <MenuItem key={item} value={item}>
          {item}
        </MenuItem>
      ))}
    </TextField>
  );
};

export default React.memo(HustCodeLanguagePicker);
