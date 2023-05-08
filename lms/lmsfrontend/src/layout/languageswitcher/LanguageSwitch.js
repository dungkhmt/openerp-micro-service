import React from "react";
import {useTranslation} from "react-i18next";
import "simplebar/dist/simplebar.min.css";
import UKflag from "../../assets/img/flags/UK-flag.png";
import VIflag from "../../assets/img/flags/VN-flag.png";
import {Box, IconButton} from "@mui/material";

const LanguageSwitch = () => {
  const { i18n } = useTranslation();

  const changeLanguage = (lng) => {
    i18n.changeLanguage(lng);
  };

  return (
    <Box sx={{marginRight: "18px"}}>
      <IconButton onClick={() => changeLanguage("en")} sx={{marginRight: "32px", padding: "0"}}>
        <img src={UKflag} alt="" style={{ width: "30px", height: "20px" }} />
      </IconButton>
      <IconButton onClick={() => changeLanguage("vi")} sx={{margin: "0", padding: "0"}}>
        <img src={VIflag} alt="" style={{ width: "30px", height: "20px" }} />
      </IconButton>
    </Box>
  );
};

export default LanguageSwitch;