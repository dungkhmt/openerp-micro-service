import { CardContent } from "@mui/material";
import { TourProvider } from "@reactour/tour";
import { useCallback, useMemo, useState } from "react";
import { Responsive, WidthProvider } from "react-grid-layout";
import { DashboardCard } from "../../../components/card/DashboardCard";
import { ApexChartWrapper } from "../../../components/chart/apex/ApexChartWrapper";
import { ProjectViewDocument } from "./document/ProjectViewDocument";
import { TaskCompletedStatistic } from "./statistics/TaskCompletedStatistic";
import { TaskCreatedStatistic } from "./statistics/TaskCreatedStatistic";
import { TaskInprogressStatistic } from "./statistics/TaskInprogressStatistic";
import { WorkloadByStatus } from "./statistics/WorkloadByStatus";

const WIDTH_TASK_MD = 2;
const WIDTH_TASK_LG = 3;
const HEIGHT_DOC_MD = 5;
const HEIGHT_DOC_LG = 6;

const ResponsiveGridLayout = WidthProvider(Responsive);

const ProjectViewOverview = () => {
  const [breakpoint, setBreakpoint] = useState("lg");

  const getByBreakpoint = useCallback(
    (object) => {
      return object[breakpoint];
    },
    [breakpoint]
  );

  const position = useMemo(
    () => ({
      doc: {
        x: 0,
        y: 0,
        w: 12,
        h: breakpoint === "lg" ? HEIGHT_DOC_LG : HEIGHT_DOC_MD,
        minH: 3,
        minW: breakpoint === "xs" ? 4 : 6,
        maxW: 12,
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
        w: getByBreakpoint({ lg: 3, md: 4, sm: 3, xs: 4 }),
        h: getByBreakpoint({ lg: 6, md: 6, sm: 4, xs: 4 }),
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
        h: 4,
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
          <ProjectViewDocument
            key="doc"
            data-grid={{
              ...position.doc,
              isDraggable: false,
            }}
            className="document-step"
          />
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
