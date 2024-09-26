import { createContext } from "react";

const defaultTaskContext = {
  isLoading: true,
  task: null,
  error: null,
  logs: [],
  isUpdate: false,
  setIsUpdate: () => {},
};

const TaskContext = createContext(defaultTaskContext);

export default TaskContext;
