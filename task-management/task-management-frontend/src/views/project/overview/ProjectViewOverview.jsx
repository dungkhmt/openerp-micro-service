import { CardContent } from "@mui/material";
import { TourProvider } from "@reactour/tour";
import dayjs from "dayjs";
import { useCallback, useEffect, useMemo, useState } from "react";
import { Responsive, WidthProvider } from "react-grid-layout";
import { useDispatch, useSelector } from "react-redux";
import { DashboardCard } from "../../../components/card/DashboardCard";
import { ApexChartWrapper } from "../../../components/chart/apex/ApexChartWrapper";
import { fetchStatisticData } from "../../../store/project/statistic";
import { ProjectViewDocument } from "./document/ProjectViewDocument";
import { ProjectInfo } from "./info/ProjectInfo";
import { Period } from "./statistics/Period";
import { TaskCompletedStatistic } from "./statistics/TaskCompletedStatistic";
import { TaskCreatedStatistic } from "./statistics/TaskCreatedStatistic";
import { TaskInprogressStatistic } from "./statistics/TaskInprogressStatistic";
import { WorkloadByStatus } from "./statistics/WorkloadByStatus";

const WIDTH_TASK_MD = 2;
const WIDTH_TASK_LG = 2;
const HEIGHT_DOC_MD = 5;
const HEIGHT_DOC_LG = 6;
const HEIGHT_INFO = 3;

const ResponsiveGridLayout = WidthProvider(Responsive);

const ProjectViewOverview = () => {
  const [breakpoint, setBreakpoint] = useState("lg");

  const getByBreakpoint = useCallback(
    (object) => {
      return object[breakpoint];
    },
    [breakpoint]
  );

  const [isMounted, setIsMounted] = useState(false);
  const { period } = useSelector((state) => state.statistic);
  const { project } = useSelector((state) => state.project);
  const dispatch = useDispatch();

  const position = useMemo(
    () => ({
      info: {
        x: 0,
        y: 0,
        w: getByBreakpoint({ lg: 3, md: 3, sm: 6, xs: 4 }),
        h: getByBreakpoint({
          lg: HEIGHT_DOC_LG,
          md: HEIGHT_DOC_MD,
          sm: HEIGHT_INFO,
          xs: HEIGHT_INFO,
        }),
      },
      doc: {
        x: getByBreakpoint({ lg: 3, md: 3, sm: 0, xs: 0 }),
        y: getByBreakpoint({
          lg: 0,
          md: 0,
          sm: HEIGHT_INFO,
          xs: HEIGHT_INFO,
        }),
        w: getByBreakpoint({ lg: 9, md: 7, sm: 6, xs: 4 }),
        h: breakpoint === "lg" ? HEIGHT_DOC_LG : HEIGHT_DOC_MD,
        minH: 3,
        minW: breakpoint === "xs" ? 4 : 6,
        maxW: 12,
        static: true,
      },
      period: {
        x: 0,
        y: getByBreakpoint({
          lg: HEIGHT_DOC_LG,
          md: HEIGHT_DOC_MD,
          sm: HEIGHT_DOC_MD + HEIGHT_INFO,
          xs: HEIGHT_DOC_MD + HEIGHT_INFO,
        }),
        w: getByBreakpoint({
          lg: 12,
          md: 10,
          sm: 6,
          xs: 4,
        }),
        h: 0.5,
        static: true,
      },
      taskCreated: {
        x: 0,
        y: breakpoint === "lg" ? HEIGHT_DOC_LG : HEIGHT_DOC_MD,
        w: breakpoint === "lg" ? WIDTH_TASK_LG : WIDTH_TASK_MD,
        h: 2,
        minW: 2,
      },
      taskInprogress: {
        x: breakpoint === "lg" ? WIDTH_TASK_LG : WIDTH_TASK_MD,
        y: breakpoint === "lg" ? HEIGHT_DOC_LG : HEIGHT_DOC_MD,
        w: breakpoint === "lg" ? WIDTH_TASK_LG : WIDTH_TASK_MD,
        h: 2,
        minW: 2,
      },
      taskCompleted: {
        x: getByBreakpoint({
          lg: WIDTH_TASK_LG * 2,
          md: WIDTH_TASK_MD * 2,
          sm: WIDTH_TASK_MD * 2,
          xs: 0,
        }),
        y: getByBreakpoint({
          lg: HEIGHT_DOC_LG,
          md: HEIGHT_DOC_MD,
          sm: HEIGHT_DOC_MD,
          xs: HEIGHT_DOC_MD + 2,
        }),
        w: getByBreakpoint({
          lg: WIDTH_TASK_LG,
          md: WIDTH_TASK_MD,
          sm: WIDTH_TASK_MD,
          xs: 4,
        }),
        h: 2,
        minW: 2,
      },
      activity: {
        x: getByBreakpoint({
          lg: WIDTH_TASK_LG * 3,
          md: WIDTH_TASK_MD * 3,
          sm: 3,
          xs: 4,
        }),
        y: getByBreakpoint({
          lg: HEIGHT_DOC_LG,
          md: HEIGHT_DOC_MD,
          sm: HEIGHT_DOC_MD + 2,
          xs: HEIGHT_DOC_MD + 2,
        }),
        w: getByBreakpoint({ lg: 6, md: 4, sm: 3, xs: 4 }),
        h: getByBreakpoint({ lg: 7, md: 7, sm: 4, xs: 4 }),
      },
      chart: {
        x: 0,
        y: getByBreakpoint({
          lg: HEIGHT_DOC_LG + 2,
          md: HEIGHT_DOC_MD + 2,
          sm: HEIGHT_DOC_MD + 2,
          xs: HEIGHT_DOC_MD + 2,
        }),
        w: getByBreakpoint({
          lg: WIDTH_TASK_LG * 3,
          md: WIDTH_TASK_MD * 3,
          sm: 3,
          xs: 4,
        }),
        h: 5,
      },
    }),
    [breakpoint, getByBreakpoint]
  );

  const steps = [
    {
      selector: ".document-step",
      content: "Xem và viết tài liệu dự án tại đây",
    },
  ];

  useEffect(() => {
    if (!isMounted) {
      setIsMounted(true);
    } else {
      dispatch(
        fetchStatisticData({
          projectId: project.id,
          startDate: dayjs(period.startDate).unix(),
          endDate: dayjs(period.endDate).unix(),
        })
      );
    }
  }, [period, dispatch]);

  return (
    <ApexChartWrapper>
      <TourProvider
        steps={steps}
        showBadge={false}
        showNavigation={false}
        showPrevNextButtons={false}
      >
        <ResponsiveGridLayout
          useCSSTransforms={true}
          className="layout"
          rowHeight={60}
          margin={[16, 16]}
          breakpoints={{ lg: 1200, md: 900, sm: 600, xs: 0 }}
          cols={{ lg: 12, md: 10, sm: 6, xs: 4 }}
          onBreakpointChange={(newBreakpoint) => {
            setBreakpoint(newBreakpoint);
          }}
        >
          <ProjectInfo
            key="info"
            data-grid={{
              ...position.info,
              static: true,
            }}
          />
          <ProjectViewDocument
            key="doc"
            data-grid={{
              ...position.doc,
              isDraggable: false,
            }}
            className="document-step"
          />
          <div key="period" data-grid={position.period}>
            <Period />
          </div>
          <TaskCreatedStatistic
            key="task-created"
            data-grid={position.taskCreated}
          />
          <TaskInprogressStatistic
            key="task-inprogress"
            data-grid={position.taskInprogress}
          />
          <TaskCompletedStatistic
            key="task-completed"
            data-grid={position.taskCompleted}
          />
          <DashboardCard
            key="activity"
            data-grid={position.activity}
            title={"Hoạt động"}
          >
            <CardContent>Không có hoạt động gần đây</CardContent>
          </DashboardCard>
          <WorkloadByStatus key="chart" data-grid={position.chart} />
        </ResponsiveGridLayout>
      </TourProvider>
    </ApexChartWrapper>
  );
};

export { ProjectViewOverview };
