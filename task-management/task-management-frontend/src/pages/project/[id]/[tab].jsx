import {
  Grid,
  Card,
  CardContent,
  Box,
  Skeleton,
  Divider,
  CircularProgress,
  Typography,
} from "@mui/material";
import { useSelector } from "react-redux";
import ProjectViewLeft from "../../../views/project/ProjectViewLeft";
import { ProjectViewRight } from "../../../views/project/ProjectViewRight";

const LeftLoading = () => (
  <Card>
    <CardContent
      sx={{
        pt: 15,
        display: "flex",
        alignItems: "center",
        flexDirection: "column",
        gap: 4,
        height: "80vh",
      }}
    >
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: 4,
        }}
      >
        <Skeleton variant="rectangular" width={120} height={120} />
        <Skeleton variant="text" width={80} />
      </Box>
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          gap: 6,
        }}
      >
        <Skeleton variant="rounded" width={80} height={44} />
        <Skeleton variant="rounded" width={80} height={44} />
      </Box>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          width: "100%",
          px: 6,
          gap: 2,
        }}
      >
        <Divider sx={{ my: `1rem !important` }} />
        <Skeleton variant="text" width={150} />
        <Skeleton variant="text" width={80} />
        <Skeleton variant="text" width={120} />
        <Skeleton variant="text" width={60} />
      </Box>
    </CardContent>
  </Card>
);

const RightLoading = () => (
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

  return (
    <Grid container spacing={6}>
      <Grid item xs={12} md={4} lg={3} xl={2.5}>
        {fetchLoading ? <LeftLoading /> : <ProjectViewLeft />}
      </Grid>
      <Grid item xs={12} md={8} lg={9} xl={9.5}>
        {fetchLoading ? <RightLoading /> : <ProjectViewRight />}
      </Grid>
    </Grid>
  );
};

export default Project;
