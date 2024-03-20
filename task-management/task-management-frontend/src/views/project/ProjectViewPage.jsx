import { Grid } from "@mui/material";
import ProjectViewLeft from "./ProjectViewLeft";
import { ProjectViewRight } from "./ProjectViewRight";

const ProjectViewPage = () => {
  return (
    <Grid container spacing={6}>
      <Grid item xs={12} md={5} lg={3}>
        <ProjectViewLeft />
      </Grid>
      <Grid item xs={12} md={7} lg={9}>
        <ProjectViewRight />
      </Grid>
    </Grid>
  );
};

export { ProjectViewPage };
