import MenuItem from "@mui/material/MenuItem";
import StyledSelect from "component/select/StyledSelect";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import "simplebar/dist/simplebar.min.css";
import UKflag from "../../assets/img/flags/UK-flag.png";
import VIflag from "../../assets/img/flags/VN-flag.png";

const langOptions = [
  { flagImageSrc: UKflag, value: "en", label: "ENG" },
  { flagImageSrc: VIflag, value: "vi-VN", label: "VIE" },
];

const getDefaultLanguage = () => {
  let language = localStorage.getItem("i18nextLng") || "en";

  if (language.includes("vi")) language = "vi-VN";
  if (language.includes("en")) language = "en";

  return language;
};

const LanguageSwitch = () => {
  const { i18n } = useTranslation();

  const [selectedLang, setSelectedLang] = useState(getDefaultLanguage());

  const changeLanguage = (lang) => {
    i18n.changeLanguage(lang);
    setSelectedLang(lang);
  };

  return (
    <StyledSelect
      value={selectedLang}
      key={"LanguageSwitcher"}
      sx={{
        width: "112px",
        boxShadow: "none",
        ".MuiOutlinedInput-notchedOutline": { border: 0 },
      }}
      onChange={(event) => changeLanguage(event.target.value)}
    >
      {langOptions.map(({ flagImageSrc, value, label }) => (
        <MenuItem key={value} value={value} sx={{ px: 1, borderRadius: 1.5 }}>
          <img
            src={flagImageSrc}
            alt={label}
            style={{ width: "18px", height: "14px", marginRight: "8px" }}
          />
          {label}
        </MenuItem>
      ))}
    </StyledSelect>
  );
};

export default LanguageSwitch;
