import { Box, CircularProgress, Typography } from "@mui/material";
import { Helmet } from "react-helmet";
import { useSelector } from "react-redux";
import { ProjectViewRight } from "../../../views/project/ProjectViewRight";

const Loading = () => (
  <Box
    sx={{
      mt: 14,
      display: "flex",
      alignItems: "center",
      flexDirection: "column",
    }}
  >
    <CircularProgress sx={{ mb: 4 }} />
    <Typography>Đang tải...</Typography>
  </Box>
);

const Project = () => {
  const { fetchLoading, project } = useSelector((state) => state.project);

  if (fetchLoading) {
    <Loading />;
  }

  if (!project) return null;

  return (
    <>
      <Helmet>
        <title>{project.name} | Task management</title>
      </Helmet>
      <ProjectViewRight />
    </>
  );
};

export default Project;
