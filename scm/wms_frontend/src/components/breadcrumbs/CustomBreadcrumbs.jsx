import { NavigateNext } from "@mui/icons-material";
import { Breadcrumbs, Typography } from "@mui/material";
import { useLocation } from "react-router-dom";
import { AppColors } from "../../shared/AppColors";
import "./styles.css";
const CustomBreadcrumbs = () => {
  const location = useLocation();
  let currentLink = "";
  const crumbs = location.pathname
    .split("/")
    .filter((crumb) => crumb !== "")
    .map((crumb) => {
      currentLink += `/${crumb}`;
      return (
        <Typography
          id="modal-modal-title"
          className="crumb"
          variant="h6"
          textTransform="uppercase"
          letterSpacing={1}
          fontSize={16}
          style={{}}
          sx={{
            color: AppColors.primary,
            fontWeight: "bold",
          }}
        >
          {`${crumb}`}
        </Typography>
      );
    });
  return (
    <Breadcrumbs
      separator={<NavigateNext fontSize="small" />}
      aria-label="breadcrumb"
      sx={{
        // boxShadow: 3,
        // paddingX: 2,
        // paddingY: 1,
        display: "flex",
        listStyleType: "none",
        overflow: "hidden",
        // padding: "0",
      }}
    >
      {crumbs.map((crumb) => {
        return crumb;
      })}
    </Breadcrumbs>
  );
};
export default CustomBreadcrumbs;
