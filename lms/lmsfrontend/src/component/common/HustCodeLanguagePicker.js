import React from "react";
import {MenuItem, TextField} from "@mui/material";
import {COMPUTER_LANGUAGES, mapLanguageToDisplayName} from "../education/programmingcontestFE/Constant";

const HustCodeLanguagePicker = (props) => {
  const {
    language,
    onChangeLanguage,
    classRoot,
    ...remainProps
  } = props;

  const getLanguage = (language) => {
    if (!language) return COMPUTER_LANGUAGES.CPP17;
    if (language === 'CPP') return COMPUTER_LANGUAGES.CPP17;
    return language;
  }

  return (
    <TextField
      {...remainProps}
      className={`${classRoot}`}
      variant={"outlined"}
      size={"small"}
      autoFocus
      value={getLanguage(language)}
      select
      id="computerLanguage"
      onChange={onChangeLanguage}
    >
      {Object.values(COMPUTER_LANGUAGES).map((item) => (
        <MenuItem key={item} value={item}>
          {mapLanguageToDisplayName(item)}
        </MenuItem>
      ))}
    </TextField>
  );
};

export default React.memo(HustCodeLanguagePicker);
