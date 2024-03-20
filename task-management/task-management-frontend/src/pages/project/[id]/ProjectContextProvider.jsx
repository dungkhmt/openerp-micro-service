import { Outlet, useParams } from "react-router-dom";
import { useState, useMemo, useEffect } from "react";
import ProjectContext from "../../../context/ProjectContext";
import { ProjectService } from "../../../services/api/project.service";
import { StatusService } from "../../../services/api/task-status.service";
import { CategoryService } from "../../../services/api/task-category.service";
import { PriorityService } from "../../../services/api/task-priority.service";

export const ProjectContextProvider = () => {
  const { id } = useParams();
  const [project, setProject] = useState();
  const [members, setMembers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [statuses, setStatuses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [priorities, setPriorities] = useState([]);
  const [isUpdate, setIsUpdate] = useState(false);

  useEffect(() => {
    const fetchProject = async () => {
      const project = await ProjectService.getProject(id);
      setProject(project);
    };
    const fetchMembers = async () => {
      const members = await ProjectService.getMembers(id);
      setMembers(members);
    };

    Promise.resolve(() => {
      setIsLoading(true);
    })
      .then(() => Promise.all([fetchProject(), fetchMembers()]))
      .catch(setError)
      .finally(() => {
        setIsLoading(false);
      });
  }, [id, isUpdate]);

  useEffect(() => {
    const fetchStatuses = async () => {
      const statuses = await StatusService.getStatuses();
      setStatuses(statuses);
    };
    const fetchCategories = async () => {
      const categories = await CategoryService.getCategories();
      setCategories(categories);
    };
    const fetchPriorities = async () => {
      const priorities = await PriorityService.getPriorities();
      setPriorities(priorities);
    };

    Promise.all([fetchCategories(), fetchPriorities(), fetchStatuses()]);
  }, []);

  const value_ = useMemo(
    () => ({
      project,
      members,
      statuses,
      categories,
      priorities,
      isLoading,
      error,
      setIsUpdate,
    }),
    [project, members, isLoading, error, statuses, categories, priorities]
  );

  return (
    <ProjectContext.Provider value={value_}>
      <Outlet />
    </ProjectContext.Provider>
  );
};
