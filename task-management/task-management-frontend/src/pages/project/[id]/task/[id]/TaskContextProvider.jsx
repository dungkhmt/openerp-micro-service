/* eslint-disable react-hooks/exhaustive-deps */
import { useCallback, useMemo } from "react";
import { useParams } from "react-router";
import { Outlet } from "react-router-dom";
import TaskContext from "../../../../../context/TaskContext";
import { TaskService } from "../../../../../services/api/task.service";
import { useState } from "react";
import { useEffect } from "react";

export const TaskContextProvider = () => {
  const { tid } = useParams();
  const [task, setTask] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [logs, setLogs] = useState([]);
  const [taskSkills, setTaskSkills] = useState([]);
  const [isUpdate, setIsUpdate] = useState(false);

  const getTask = useCallback(async () => {
    try {
      setIsLoading(true);
      const task = await TaskService.getTask(tid);
      setTask(task);
    } catch (e) {
      setError(e);
    } finally {
      setIsLoading(false);
    }
  }, [tid, isUpdate]);

  const getLogs = useCallback(async () => {
    try {
      const logs = await TaskService.getLogs(tid);
      setLogs(logs);
    } catch (e) {
      setError(e);
    }
  }, [tid, isUpdate]);

  const getTaskSkills = useCallback(async () => {
    try {
      const skills = await TaskService.getTaskSkills(tid);
      setTaskSkills(skills);
    } catch (e) {
      setError(e);
    }
  }, [tid, isUpdate]);
  
  useEffect(() => {
    getTask();
    getLogs();
    getTaskSkills();
  }, [getTask, getLogs, getTaskSkills]);

  const value = useMemo(
    () => ({
      task,
      isLoading,
      error,
      logs,
      taskSkills,
      isUpdate,
      setIsUpdate,
    }),
    [task, isLoading, error, logs, taskSkills, isUpdate]
  );

  return (
    <TaskContext.Provider value={value}>
      <Outlet />
    </TaskContext.Provider>
  );
};
