import { Icon } from "@iconify/react";
import { Box, CircularProgress, useTheme } from "@mui/material";
import dayjs from "dayjs";
import { Gantt, ViewMode } from "gantt-task-react";
import "gantt-task-react/dist/index.css";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import toast from "react-hot-toast";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router";
import { fetchGanttChartTasks } from "../../../store/project/gantt-chart";
import { buildFilterString } from "../../../utils/task-filter";
import { GanttControl } from "./GanttControl";
import { TaskListHeader } from "./TaskListHeader";
import { TaskListTable } from "./TaskListTable";
import { TaskPreview } from "./TaskPreview";
import { useConvertTasks } from "./helper";

const ProjectViewGanttChart = () => {
  const ref = useRef(null);
  const { id: projectId } = useParams();
  const navigate = useNavigate();

  const {
    view,
    tasks: tasksStore,
    fetchLoading,
    range,
    search: searchStore,
    filters,
  } = useSelector((state) => state.gantt);
  const dispatch = useDispatch();

  const theme = useTheme();

  const [tasks, setTasks] = useConvertTasks(tasksStore, range.startDate);
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
    // TODO: handler change task
    let newTasks = tasks.map((t) => (t.id === task.id ? task : t));
    setTasks(newTasks);
    toast("The feature is not available yet", {
      icon: <Icon icon="ic:round-warning" />,
      style: {
        color: theme.palette.warning.main,
        border: `1px solid ${theme.palette.warning.main}`,
      },
      iconTheme: {
        primary: theme.palette.warning.main,
        secondary: theme.palette.warning.contrastText,
      },
    });
  };

  // const handleTaskDelete = (task: Task) => {
  //   const conf = window.confirm("Are you sure about " + task.name + " ?");
  //   if (conf) {
  //     setTasks(tasks.filter((t) => t.id !== task.id));
  //   }
  //   return conf;
  // };

  const handleProgressChange = async (task) => {
    // TODO: handler change task
    setTasks(tasks.map((t) => (t.id === task.id ? task : t)));
    toast("The feature is not available yet", {
      icon: <Icon icon="ic:round-warning" />,
      style: {
        color: theme.palette.warning.main,
        border: `1px solid ${theme.palette.warning.main}`,
      },
      iconTheme: {
        primary: theme.palette.warning.main,
        secondary: theme.palette.warning.contrastText,
      },
    });
  };

  const handleClick = (task) => {
    console.log("On Click event Id:" + task.id);
  };

  const handleDblClick = (task) => {
    navigate(`/project/${projectId}/task/${task.id}`);
  };

  const handleSelect = (task, isSelected) => {
    console.log(task.name + " has " + (isSelected ? "selected" : "unselected"));
  };

  const handleExpanderClick = (task) => {
    setTasks(tasks.map((t) => (t.id === task.id ? task : t)));
  };

  const buildQueryString = useCallback(() => {
    const builder = [];
    const encodedSearch = encodeURIComponent(searchStore).replace(
      /%20/g,
      "%1F"
    );
    builder.push(
      encodedSearch
        ? `( name:*${encodedSearch}* OR description:*${encodedSearch}* )`
        : ""
    );

    builder.push(buildFilterString(filters));

    return builder.filter((s) => s !== "").join(" AND ");
  }, [searchStore, filters]);

  const getTasks = useCallback(async () => {
    if (!isInitiated && tasksStore.length > 0) setIsInitiated(true);
    // TODO: handle filters and search, fetch from startDate
    await dispatch(
      fetchGanttChartTasks({
        projectId,
        from: dayjs(range.startDate).startOf("day").unix(),
        to: dayjs(range.startDate)
          .add(range.duration, "month")
          .endOf("day")
          .unix(),
        q: buildQueryString(),
      })
    );
  }, [projectId, range, dispatch, buildQueryString]);

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

  return (
    <Box>
      <GanttControl />
      <Box ref={ref} sx={{ mt: 2, position: "relative" }}>
        <Gantt
          tasks={tasks}
          viewMode={view}
          viewDate={range.startDate}
          locale="vi-VN"
          onDateChange={handleTaskChange}
          // onDelete={handleTaskDelete}
          onProgressChange={handleProgressChange}
          onDoubleClick={handleDblClick}
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
