import NavigateNextIcon from "@mui/icons-material/NavigateNext";
import { Box, Breadcrumbs, Typography } from "@mui/material";
import { useLocation } from "react-router-dom";
import { AppColors } from "../../shared/AppColors";
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
          variant="h6"
          textTransform="uppercase"
          letterSpacing={1}
          fontSize={18}
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
      separator={<NavigateNextIcon fontSize="small" />}
      aria-label="breadcrumb"
      sx={{
        boxShadow: 3,
        paddingX: 2,
        paddingY: 1,
      }}
    >
      {crumbs.map((crumb) => {
        return <Box>{crumb}</Box>;
      })}
    </Breadcrumbs>
  );
};
export default CustomBreadcrumbs;
