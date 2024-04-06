import { createContext } from "react";

const projectContextDefault = {
  project: null,
  isLoading: true,
  error: null,
  tasksMap: {},
  setTasksMap: () => {},
  members: [],
  statuses: [],
  categories: [],
  priorities: [],
  setIsUpdate: () => {},
};

const ProjectContext = createContext(projectContextDefault);

export default ProjectContext;
