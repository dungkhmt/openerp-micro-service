import { Helmet } from "react-helmet";
import { useSelector } from "react-redux";
import { ProjectViewRight } from "../../../views/project/ProjectViewRight";
import { CircularProgressLoading }from "../../../components/common/loading/CircularProgressLoading"


const Project = () => {
  const { fetchLoading, project } = useSelector((state) => state.project);

  if (fetchLoading) {
    <CircularProgressLoading />;
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
