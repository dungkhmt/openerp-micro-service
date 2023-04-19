import {Box, Grid, Paper} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import React, {useEffect, useState} from "react";
import {Bar, Doughnut, HorizontalBar, Line} from "react-chartjs-2";
import {useDispatch, useSelector} from "react-redux";
import {authPost, request} from "../../api";
import withScreenSecurity from "../withScreenSecurity";

const useStyles = makeStyles((theme) => ({
  root: {
    width: "100%",
    backgroundColor: theme.palette.background.paper,
    position: "relative",
    overflow: "auto",
    maxHeight: 1030,
  },
  listSection: {
    backgroundColor: "inherit",
  },
  ul: {
    backgroundColor: "inherit",
    padding: 0,
  },
  doughnutStyle: {
    maxHeight: 500,
    minHeight: 400,
  },
  sectionHeaderStyle: {
    color: "#666",
  },
  ganttChartStyle: {
    height: "600px",
  },
  avatar: {
    width: 36,
    height: 36,
  },
}));
const taskCounterOpt = {
  responsive: true,
  maintainAspectRatio: false,
  title: {
    display: true,
    text: "Backlog",
    fontSize: 20,
    lineHeight: 1.5,
  },
  legend: {
    position: "bottom",
    align: "center",
  },
  layout: {
    padding: {
      left: 0,
      right: 0,
      top: 10,
      bottom: 10,
    },
  },
  plugins: {
    datalabels: {
      display: function (context) {
        return context.dataset.data[context.dataIndex] !== 0;
      },
    },
  },
};

function MainDashBoard(props) {
  const classes = useStyles();
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const taskCounterOption = taskCounterOpt;

  const [dataAllProject, setDataAllProject] = useState({});
  const [vehicle, setVehicle] = useState([]);
  const [distance, setDistance] = useState([]);

  const [dateRevenue, setDateRevenue] = useState([]);
  const [revenue, setRevenue] = useState([]);

  const [dateStudentParticipation, setDateStudentParticipation] = useState([]);
  const [totalParticipation, setTotalParticipation] = useState([]);
  const [accTotalParticipation, setAccTotalParticipation] = useState([]);

  const [dateQuizParticipation, setDateQuizParticipation] = useState([]);
  const [totalQuizParticipation, setTotalQuizParticipation] = useState([]);
  const [accTotalQuizParticipation, setAccTotalQuizParticipation] = useState(
    []
  );

  const dataStudentParticipation = {
    labels: dateStudentParticipation,
    datasets: [
      {
        label: "Part.",
        data: totalParticipation,

        backgroundColor: "rgba(54, 162, 235, 0.2)",
        borderColor: "rgba(54, 162, 235, 1)",
        borderWidth: 1,
        hoverBackgroundColor: "rgba(54, 162, 235, 0.4)",
        hoverBorderColor: "rgba(54, 162, 235, 1)",
      },
    ],
  };

  const dataStudentAccParticipation = {
    labels: dateStudentParticipation,
    datasets: [
      {
        label: "Acc.Part.",
        data: accTotalParticipation,
        backgroundColor: "rgba(54, 162, 235, 0.2)",
        borderColor: "rgba(54, 162, 235, 1)",
        borderWidth: 1,
        hoverBackgroundColor: "rgba(54, 162, 235, 0.4)",
        hoverBorderColor: "rgba(54, 162, 235, 1)",
      },
    ],
  };

  const dataQuizParticipation = {
    labels: dateQuizParticipation,
    datasets: [
      {
        label: "Qz.",
        data: totalQuizParticipation,
        backgroundColor: "rgba(54, 162, 235, 0.2)",
        borderColor: "rgba(54, 162, 235, 1)",
        borderWidth: 1,
        hoverBackgroundColor: "rgba(54, 162, 235, 0.4)",
        hoverBorderColor: "rgba(54, 162, 235, 1)",
      },
    ],
  };
  const dataQuizAccParticipation = {
    labels: dateQuizParticipation,
    datasets: [
      {
        label: "Acc.Qz.",
        height: 500,
        fill: false,
        data: accTotalQuizParticipation,
        backgroundColor: "rgba(54, 162, 235, 0.2)",
        borderColor: "rgba(54, 162, 235, 1)",
        borderWidth: 1,
        hoverBackgroundColor: "rgba(54, 162, 235, 0.4)",
        hoverBorderColor: "rgba(54, 162, 235, 1)",
      },
    ],
  };

  function getStudentParticipation() {
    request(
      "post",
      "/get-class-participation-statistic",
      (res) => {
        let lst = res.data;
        let dates = [];
        let participations = [];
        let accParticipation = [];

        lst.forEach((r) => {
          dates.push(r.date);
          participations.push(r.count);
          accParticipation.push(r.accCount);
        });

        setDateStudentParticipation(dates);
        setTotalParticipation(participations);
        setAccTotalParticipation(accParticipation);
      },
      { 401: () => {} },
      {
        fromDate: "",
        thruDate: "",
      }
    );
  }

  async function getQuizParticipation() {
    request(
      "post",
      "/get-quiz-participation-statistic",
      (res) => {
        let lst = res.data;
        let dates = [];
        let participations = [];
        let accParticipations = [];

        // console.log("getQuizParticipation, lst = ", lst);

        lst.forEach((r) => {
          dates.push(r.date);
          participations.push(r.count);
          accParticipations.push(r.accCount);
        });

        setDateQuizParticipation(dates);
        setTotalQuizParticipation(participations);
        setAccTotalQuizParticipation(accParticipations);
      },
      { 401: () => {} },
      { fromDate: "", thruDate: "" }
    );
  }

  function getRevenueDateRecent() {
    // console.log("getRevenueDateRecent");
    authPost(dispatch, token, "/report-date-based-revenue-recent", {
      nbDays: 15,
    })
      .then((response) => {
        let listRev = response.revenueElements;
        let arrDates = [];
        let arrRevenues = [];
        listRev.forEach((r) => {
          arrDates.push(r.date);
          arrRevenues.push(r.revenue);
        });
        setDateRevenue(arrDates);
        setRevenue(arrRevenues);
      })
      .catch(console.log);
  }

  function getVehicleDistance() {
    // console.log("getVehicleDistance");
    authPost(dispatch, token, "/statistic-vehicle-distance", {
      fromDate: "",
      thruDate: "",
    })
      .then((response) => {
        let vehicle = [];
        let distance = [];
        response.forEach((vh) => {
          vehicle.push(vh.vehicleId);
          distance.push(vh.distance);
        });
        setVehicle(vehicle);
        setDistance(distance);
      })
      .catch(console.log);
  }

  function getChartBackLog() {
    request(
      "get",
      "/backlog/get-all-dash-board",
      (res) => {
        //setTaskList(res);

        let [taskOpen, taskInprogress, taskResolved, taskclose] = [0, 0, 0, 0];
        Object.keys(res.data).forEach((key) => {
          // task counter data
          let listTask = res.data[key];

          listTask.forEach((task) => {
            switch (task.statusId) {
              case "TASK_OPEN":
                taskOpen++;
                break;
              case "TASK_INPROGRESS":
                taskInprogress++;
                break;
              case "TASK_RESOLVED":
                taskResolved++;
                break;
              case "TASK_CLOSED":
                taskclose++;
                break;
              default:
            }
          });
        });

        let data = {
          datasets: [
            {
              data: [taskOpen, taskInprogress, taskResolved, taskclose],
              backgroundColor: ["#e91e63", "#2196f3", "#4caf50", "#000000"],
              hoverBackgroundColor: [
                "#f50057",
                "#2979ff",
                "#00e676",
                "#56525c",
              ],
            },
          ],
          labels: ["Tạo mới", "Đang thực hiện", "Đã hoàn thành", "Đã đóng"],
        };
        setDataAllProject(data);
      },
      { 401: () => {} }
    );
  }
  useEffect(() => {
    getChartBackLog();
    getVehicleDistance();
    getRevenueDateRecent();
    //getStudentParticipation();
    //getQuizParticipation();
    getStudentParticipation();
    getQuizParticipation();
  }, []);

  return (
    <div>
      <Grid container spacing={3}>
        <Grid item xs={3}>
          <Paper>
            <h2>Cour.</h2>
            <HorizontalBar data={dataStudentParticipation} />
          </Paper>
        </Grid>
        <Grid item xs={3}>
          <Paper>
            <h2>Quiz.</h2>
            <HorizontalBar data={dataQuizParticipation} />
          </Paper>
        </Grid>
        <Grid item xs={3}>
          <Paper>
            <h2>Acc.Cour.</h2>
            <Bar data={dataStudentAccParticipation} />
          </Paper>
        </Grid>
        <Grid item xs={3}>
          <Paper>
            <h2>Quiz.</h2>
            <Line data={dataQuizAccParticipation} />
          </Paper>
        </Grid>
        <Grid item xs={3}>
          <Paper>
            <h2>Quiz.</h2>
            <Line data={dataQuizParticipation} />
          </Paper>
        </Grid>
        <Grid item xs={3}>
          <Paper>
            <h2>Acc.Cour.</h2>
            <Line data={dataStudentAccParticipation} />
          </Paper>
        </Grid>
        <Grid item xs={3}>
          <Paper>
            <h2>Cour.</h2>
            <HorizontalBar data={dataStudentParticipation} />
          </Paper>
        </Grid>
        <Grid item xs={3}>
          <Paper>
            <h2>Quiz.</h2>
            <Line data={dataQuizParticipation} />
          </Paper>
        </Grid>

        <Grid item xs={12}>
          <Paper elevation={0}>
            <Box className={classes.doughnutStyle}>
              <Doughnut data={dataAllProject} options={taskCounterOption} />
            </Box>
          </Paper>
        </Grid>

      </Grid>
    </div>
  );
}

const screenName = "SCREEN_DATA_ADMIN_VIEW_MAIN_DASHBOARD";
export default withScreenSecurity(MainDashBoard, screenName, true);
