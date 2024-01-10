import {MenuItem, TextField} from "@mui/material";
import React from "react";
import {COMPUTER_LANGUAGES, mapLanguageToDisplayName,} from "../education/programmingcontestFE/Constant";

const HustCodeLanguagePicker = (props) => {
  const {listLanguagesAllowed, language, onChangeLanguage, classRoot, ...remainProps} = props;

  const getLanguage = (language) => {
    if (language == null) return COMPUTER_LANGUAGES.CPP17;
    if (language === "CPP") return COMPUTER_LANGUAGES.CPP17;
    return language;
  };

  const isLanguageAvailable = (chosenLanguage) => {
    if (listLanguagesAllowed == null || listLanguagesAllowed.length === 0) return true;
    return listLanguagesAllowed.includes(chosenLanguage);
  }

  return (
    <TextField
      sx={{minWidth: 128}}
      {...remainProps}
      className={`${classRoot}`}
      variant={"outlined"}
      size={"small"}
      value={getLanguage(language)}
      select
      id="computerLanguages"
      onChange={onChangeLanguage}
    >
      {Object.values(COMPUTER_LANGUAGES).map((item) => (
        <MenuItem key={item} value={item} disabled={!isLanguageAvailable(item)}>
          {mapLanguageToDisplayName(item)}
        </MenuItem>
      ))}
    </TextField>
  )
};

export default React.memo(HustCodeLanguagePicker);
