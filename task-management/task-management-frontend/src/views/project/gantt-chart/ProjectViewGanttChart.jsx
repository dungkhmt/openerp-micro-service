import { Box, CircularProgress } from "@mui/material";
import { Gantt, ViewMode } from "gantt-task-react";
import "gantt-task-react/dist/index.css";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams, useNavigate } from "react-router";
import { fetchGanttChartTasks } from "../../../store/project/gantt-chart";
import { GanttControl } from "./GanttControl";
import { TaskListHeader } from "./TaskListHeader";
import { TaskListTable } from "./TaskListTable";
import { TaskPreview } from "./TaskPreview";
import { convertTasks, defaultTasks } from "./helper";

const ProjectViewGanttChart = () => {
  const ref = useRef(null);
  const { id: projectId } = useParams();
  const navigate = useNavigate();

  const {
    view,
    tasks: tasksStore,
    fetchLoading,
  } = useSelector((state) => state.gantt);
  const dispatch = useDispatch();

  const [tasks, setTasks] = useState(defaultTasks);
  const [height, setHeight] = useState(300);
  const [isInitiated, setIsInitiated] = useState(false);

  const columnWidth = useMemo(() => {
    if (view === ViewMode.Year) {
      return 350;
    } else if (view === ViewMode.Month) {
      return 300;
    } else if (view === ViewMode.Week) {
      return 250;
    }
    return 65;
  }, [view]);

  const handleTaskChange = (task) => {
    console.log("On date change Id:" + task.id);
    let newTasks = tasks.map((t) => (t.id === task.id ? task : t));
    setTasks(newTasks);
  };

  // const handleTaskDelete = (task: Task) => {
  //   const conf = window.confirm("Are you sure about " + task.name + " ?");
  //   if (conf) {
  //     setTasks(tasks.filter((t) => t.id !== task.id));
  //   }
  //   return conf;
  // };

  const handleProgressChange = async (task) => {
    setTasks(tasks.map((t) => (t.id === task.id ? task : t)));
    console.log("On progress change Id:" + task.id);
  };

  const handleClick = (task) => {
    console.log("On Click event Id:" + task.id);
    navigate(`/project/${projectId}/task/${task.id}`);
  };

  // const handleDblClick = (task: Task) => {
  //   alert("On Double Click event Id:" + task.id);
  // };

  const handleSelect = (task, isSelected) => {
    console.log(task.name + " has " + (isSelected ? "selected" : "unselected"));
  };

  const handleExpanderClick = (task) => {
    setTasks(tasks.map((t) => (t.id === task.id ? task : t)));
  };

  const getTasks = useCallback(async () => {
    if (!isInitiated && tasksStore.length > 0) return;
    // TODO: handle filters and search, fetch from startDate
    await dispatch(fetchGanttChartTasks({ projectId }));
    setIsInitiated(true);
  }, [projectId]);

  useEffect(() => {
    const timer = setTimeout(() => {
      if (!ref.current) {
        return;
      }

      if (ref.current) {
        // Get the menu DOM rect
        const domRect = ref.current.getBoundingClientRect();
        setHeight(window.innerHeight - domRect.top - 80);
      }
    }, 10);

    return () => clearTimeout(timer);
  }, [ref]);

  useEffect(() => {
    getTasks();
  }, [getTasks]);

  useEffect(() => {
    setTasks(convertTasks(tasksStore));
  }, [tasksStore]);

  return (
    <Box>
      <GanttControl />
      <Box ref={ref} sx={{ mt: 2, position: "relative" }}>
        <Gantt
          tasks={tasks}
          viewMode={view}
          onDateChange={handleTaskChange}
          // onDelete={handleTaskDelete}
          onProgressChange={handleProgressChange}
          // onDoubleClick={handleDblClick}
          onClick={handleClick}
          onSelect={handleSelect}
          onExpanderClick={handleExpanderClick}
          ganttHeight={height}
          columnWidth={columnWidth}
          TaskListTable={TaskListTable}
          TaskListHeader={TaskListHeader}
          TooltipContent={TaskPreview}
        />
        {fetchLoading && (
          <Box
            sx={{
              position: "absolute",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              width: "100%",
              height: "100%",
              top: 0,
              left: 0,
              background: (theme) => theme.palette.action.hover,
            }}
          >
            <CircularProgress size={30} />
          </Box>
        )}
      </Box>
    </Box>
  );
};

export { ProjectViewGanttChart };
