import {MenuItem} from "@mui/material";
import React from "react";
import {COMPUTER_LANGUAGES, mapLanguageToDisplayName,} from "../education/programmingcontestFE/Constant";
import StyledSelect from "../select/StyledSelect";

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
    <StyledSelect
      {...remainProps}
      className={`${classRoot}`}
      fullWidth
      id="computerLanguages"
      value={getLanguage(language)}
      sx={{width: 128, mr: 'unset'}}
      onChange={onChangeLanguage}
    >
      {Object.values(COMPUTER_LANGUAGES).map((language) => (
        <MenuItem key={language} value={language} disabled={!isLanguageAvailable(language)}>
          {mapLanguageToDisplayName(language)}
        </MenuItem>
      ))}
    </StyledSelect>
  )
};

export default React.memo(HustCodeLanguagePicker);
