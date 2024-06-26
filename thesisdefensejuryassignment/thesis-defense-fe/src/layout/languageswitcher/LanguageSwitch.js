import React from "react";
import { useTranslation } from "react-i18next";
import "simplebar-react/dist/simplebar.min.css";
import UKflag from "../../assets/img/flags/UK-flag.png";
import VIflag from "../../assets/img/flags/VN-flag.png";
import { Box, Select } from "@mui/material";
import MenuItem from "@mui/material/MenuItem";

const LanguageSwitch = () => {
  const { i18n } = useTranslation();

  const changeLanguage = (lng) => {
    i18n.changeLanguage(lng);
  };

  const getDefaultLanguage = () => {
    let language = localStorage.getItem("i18nextLng") || "en";
    if (language.includes("vi")) language = "vi-VN";
    if (language.includes("en")) language = "en";
    return language;
  };

  return (
    <Box sx={{ marginRight: "18px" }}>
      <Select
        defaultValue={getDefaultLanguage()}
        onChange={(event) => changeLanguage(event.target.value)}
        sx={{
          boxShadow: "none",
          ".MuiOutlinedInput-notchedOutline": { border: 0 },
        }}
      >
        <MenuItem value={"en"}>
          <img
            src={UKflag}
            alt=""
            style={{ width: "18px", height: "14px", marginRight: "8px" }}
          />{" "}
          ENG
        </MenuItem>
        <MenuItem value={"vi-VN"}>
          <img
            src={VIflag}
            alt=""
            style={{ width: "18px", height: "14px", marginRight: "8px" }}
          />{" "}
          VIE
        </MenuItem>
      </Select>
    </Box>
  );
};

export default LanguageSwitch;
