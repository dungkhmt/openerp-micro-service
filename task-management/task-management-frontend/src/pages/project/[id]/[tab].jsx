import { Box, CircularProgress, Typography } from "@mui/material";
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
  const { fetchLoading, errors, project } = useSelector(
    (state) => state.project
  );

  if (fetchLoading) {
    <Loading />;
  }

  if (!fetchLoading && errors?.length > 0) {
    const is404 = errors[0].message.includes("404");

    return (
      <Box
        sx={{
          mt: 6,
          display: "flex",
          alignItems: "center",
          flexDirection: "column",
        }}
      >
        <Typography variant="h4" color="error">
          {is404 ? "Không tìm thấy dự án" : "Lỗi"}
        </Typography>
        <Typography variant="body1" color="error">
          {errors[0].message}
        </Typography>
      </Box>
    );
  }

  if (!project) return null;

  return <ProjectViewRight />;
};

export default Project;
