import Box from "@mui/material/Box";
import CircularProgress from "@mui/material/CircularProgress";
import {green, teal} from "@mui/material/colors";
import {menuClasses} from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import {popoverClasses} from "@mui/material/Popover";
import Skeleton from "@mui/material/Skeleton";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import {request} from "api";
import PrimaryButton from "component/button/PrimaryButton";
import {a11yProps, AntTab, AntTabs, TabPanel} from "component/tab";
import React, {useEffect, useState} from "react";
import {useLocation, useParams} from "react-router";
import {Link} from "react-router-dom";
import {errorNoti, successNoti} from "utils/notification";
import TeacherCourseList from "../TeacherCourseList";
import TeacherList from "../TeacherList";
import AssignmentStatistic from "./AssignmentStatistic";
import ClassInPlan from "./ClassInPlan";
import ClassTeacherAssignmentSolutionList from "./ClassTeacherAssignmentSolutionList";
import ConflictClassesAssignedToTeacherInSolution from "./ConflictClassesAssignedToTeacherInSolution";
import NotAssignedClassInSolutionList from "./NotAssignedClassInSolutionList";
import PairConflictTimetableClass from "./PairConflictTimetableClass";
import TeacherBasedTimeTableAssignmentInSolution from "./TeacherBasedTimeTableAssignmentInSolution";
import TeacherCourseInPlan from "./TeacherCourseInPlan";
import TeacherInPlan from "./TeacherInPlan";

const tabsLabel = [
  "Lớp",
  "Giảng viên",
  "Giảng viên trong kế hoạch",
  "Giảng viên-Môn",
  "Giảng viên-Môn trong kế hoạch",
  "Kết quả phân công",
  "Lớp chưa được phân công",
];

const objectives = [
  {
    value: "LOAD_BALANCING_DURATION_CONSIDERATION",
    label: "Cân bằng tải tính đến thời lượng",
  },
  // {
  //   value: "SCORES",
  //   label: "Tối ưu thói quen",
  // },
  // {
  //   value: "PRIORITY",
  //   label: "Tối ưu độ ưu tiên",
  // },
  // {
  //   value: "WORKDAYS",
  //   label: "Tối ưu ngày dạy",
  // },
];

const models = [
  {
    value: "MIP",
    label: "MIP",
  },
  {
    value: "CP",
    label: "CP",
  },
];

const solvers = [
  {
    value: "ORTOOLS",
    label: "Or-tools",
  },
  {
    value: "CPLEX",
    label: "Cplex",
  },
];

const label = ["Solver", "Mô hình", "Mục tiêu phân công"];

// A custom hook that builds on useLocation to parse
// the query string for you.
function useQuery() {
  const { search } = useLocation();
  return React.useMemo(() => new URLSearchParams(search), [search]);
}

export default function PlanDetail() {
  let planId = useParams().planId;
  let query = useQuery();

  //
  const [plan, setPlan] = useState();
  const [solver, setSolver] = useState("ORTOOLS");
  const [model, setModel] = useState("MIP");
  const [objective, setObjective] = useState(
    "LOAD_BALANCING_DURATION_CONSIDERATION"
  );

  //
  // const configParams = [solver, model, objective];
  const setter = [setSolver, setModel, setObjective];

  //
  const [isProcessing, setIsProcessing] = useState(false);
  const [selectedTab, setSelectedTab] = useState(
    query.get("tab") ? parseInt(query.get("tab")) : 0
  );

  //
  const handleChange = (event, index) => {
    setter[index](event.target.value);
  };

  const handleChangeTab = (event, mode) => {
    setSelectedTab(mode);
  };

  const assignTeacher2Class = () => {
    setIsProcessing(true);

    let data = {
      planId: planId,
      config: { solver: "ORTOOLS", model: "MIP", objective: objective },
    };

    request(
      "post",
      "auto-assign-teacher-2-class",
      (res) => {
        successNoti("Đã hoàn thành phân công tự động.");
        setIsProcessing(false);
      },
      {
        onError: () => {
          setIsProcessing(false);
          errorNoti("Đã có lỗi xảy ra.");
        },
        401: () => {},
      },
      data
    );
  };

  useEffect(() => {
    function getClassTeacherAssignmentPlanDetail() {
      request(
        "get",
        "get-class-teacher-assignment-plan/detail/" + planId,
        (res) => {
          setPlan(res.data);
        },
        { 401: () => {} }
      );
    }

    getClassTeacherAssignmentPlanDetail();
  }, []);

  return plan ? (
    <>
      <Typography
        variant="h5"
        sx={{ display: "inline" }}
      >{`${plan.planName}`}</Typography>
      <Box
        sx={{
          display: "flex",
          justifyContent: "flex-end",
          alignItems: "center",
          mt: 2,
          mb: 2,
        }}
      >
        {[
          // solvers, models,
          objectives,
        ].map((config, index) => (
          <TextField
            key={"Mục tiêu phân công"}
            label={"Mục tiêu phân công"}
            value={objective}
            onChange={(event) => setObjective(event.target.value)}
            size="small"
            select
            sx={{ minWidth: 300, mr: 2 }}
            SelectProps={{
              MenuProps: {
                sx: {
                  [`& .${menuClasses.list}`]: {
                    paddingLeft: 1,
                    paddingRight: 1,
                    "& .Mui-selected, .Mui-selected:hover": {
                      color: "#ffffff",
                      backgroundColor: "#1976d2", // updated backgroundColor
                      "&.Mui-focusVisible": { background: "#1976d2" },
                    },
                  },
                  [`& .${popoverClasses.paper}`]: {
                    minWidth: 300,
                    borderRadius: 2,
                    boxShadow:
                      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
                  },
                },
              },
            }}
          >
            {config.map((option) => (
              <MenuItem
                key={option.value}
                value={option.value}
                sx={{ px: 1, borderRadius: 1.5 }}
              >
                {option.label}
              </MenuItem>
            ))}
          </TextField>
        ))}
        <div style={{ position: "relative" }}>
          <PrimaryButton
            disabled={isProcessing}
            onClick={() => assignTeacher2Class()}
          >
            Phân công
          </PrimaryButton>
          {isProcessing && (
            <CircularProgress
              size={24}
              sx={{
                color: green[500],
                position: "absolute",
                top: "50%",
                left: "50%",
                mt: -1.5,
                ml: -1.5,
              }}
            />
          )}
        </div>
      </Box>

      {/* {isProcessing ? <CircularProgress /> : ""} */}
      <AntTabs
        sx={{
          position: "sticky",
          top: 64,
          zIndex: 11,
          backgroundColor: "#fafafa",
        }}
        value={selectedTab}
        onChange={handleChangeTab}
        aria-label="ant example"
        scrollButtons="auto"
        variant="scrollable"
      >
        {tabsLabel.map((label, index) => (
          <AntTab
            key={label}
            label={label}
            component={Link}
            to={`/edu/teaching-assignment/plan/${planId}/?tab=${index}`}
            {...a11yProps(index)}
            sx={{ maxWidth: 160 }}
          />
        ))}
      </AntTabs>

      <TabPanel value={selectedTab} index={0} dir={"ltr"}>
        <ClassInPlan planId={planId} />
        <PairConflictTimetableClass planId={planId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={1} dir={"ltr"}>
        <TeacherList planId={planId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={2} dir={"ltr"}>
        <TeacherInPlan planId={planId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={3} dir={"ltr"}>
        <TeacherCourseList planId={planId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={4} dir={"ltr"}>
        <TeacherCourseInPlan planId={planId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={5} dir={"ltr"}>
        <ClassTeacherAssignmentSolutionList
          planId={planId}
          planName={plan.planName}
        />
        <TeacherBasedTimeTableAssignmentInSolution planId={planId} />
        <ConflictClassesAssignedToTeacherInSolution planId={planId} />
        <AssignmentStatistic planId={planId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={6} dir={"ltr"}>
        <NotAssignedClassInSolutionList planId={planId} />
      </TabPanel>
    </>
  ) : (
    // Loading screen
    <>
      <Typography
        variant="h5"
        sx={{ fontWeight: (theme) => theme.typography.fontWeightMedium }}
      >
        <Skeleton width={400} variant="rect" animation="wave" sx={{ mb: 1 }} />
      </Typography>
      <Typography variant="subtitle1">
        <Skeleton width={200} variant="rect" animation="wave" />
      </Typography>

      <Box display="flex" alignItems="center" pt={2}>
        <Skeleton width={24} height={24} variant="circular" animation="wave" />
        <Typography
          component="span"
          sx={{
            pl: 1,
            color: teal[800],
            fontWeight: (theme) => theme.typography.fontWeightMedium,
            fontSize: "1rem",
          }}
        >
          <Skeleton width={80} variant="rect" animation="wave" />
        </Typography>
      </Box>
    </>
  );
}
