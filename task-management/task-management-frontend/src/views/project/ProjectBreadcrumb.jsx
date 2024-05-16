import { Icon } from "@iconify/react";
import { Breadcrumbs, Link, Typography, emphasize } from "@mui/material";
import { useSelector } from "react-redux";
import { Link as RouterLink } from "react-router-dom";

const ProjectBreadcrumb = () => {
  const { project } = useSelector((state) => state.project);

  return (
    <Breadcrumbs aria-label="breadcrumb">
      <Link
        component={RouterLink}
        underline="none"
        sx={{
          display: "flex",
          alignItems: "center",
          gap: 1,
          padding: (theme) => theme.spacing(1),
          "&:hover": {
            backgroundColor: (theme) => theme.palette.grey[300],
            borderRadius: "5px",
          },
        }}
        color="inherit"
        to="/projects"
      >
        <Icon icon="material-symbols-light:folder-outline" fontSize={18} />
        <Typography variant="body2">Dự án</Typography>
      </Link>
      <Link
        component={RouterLink}
        underline="none"
        sx={{
          display: "flex",
          alignItems: "center",
          gap: 1,
          padding: (theme) => theme.spacing(1),
          "&:hover": {
            backgroundColor: (theme) => theme.palette.grey[300],
            borderRadius: "5px",
          },
        }}
        color="inherit"
        to="#"
      >
        <Icon icon="ph:list" fontSize={18} />
        <Typography
          variant="body2"
          sx={{
            fontWeight: 550,
            color: (theme) => emphasize(theme.palette.text.primary, 0.12),
          }}
        >
          {project?.name ?? ""}
        </Typography>
      </Link>
    </Breadcrumbs>
  );
};

export { ProjectBreadcrumb };
