import { Box, Grid, Paper } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import { request } from "api";
import { useEffect, useState } from "react";
import { Bar, Doughnut, HorizontalBar, Line } from "react-chartjs-2";
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

const columns = [
  { field: "id", headerName: "ID", width: 90 },
  {
    field: "firstName",
    headerName: "First name",
    width: 150,
    editable: true,
  },
  {
    field: "lastName",
    headerName: "Last name",
    width: 150,
    editable: true,
  },
  {
    field: "age",
    headerName: "Age",
    type: "number",
    width: 110,
    editable: true,
  },
  {
    field: "fullName",
    headerName: "Full name",
    description: "This column has a value getter and is not sortable.",
    sortable: false,
    width: 160,
    valueGetter: (params) =>
      `${params.getValue(params.id, "firstName") || ""} ${
        params.getValue(params.id, "lastName") || ""
      }`,
  },
];

const rows = [
  { id: 1, lastName: "Snow", firstName: "Jon", age: 35 },
  { id: 2, lastName: "Lannister", firstName: "Cersei", age: 42 },
  { id: 3, lastName: "Lannister", firstName: "Jaime", age: 45 },
  { id: 4, lastName: "Stark", firstName: "Arya", age: 16 },
  { id: 5, lastName: "Targaryen", firstName: "Daenerys", age: null },
  { id: 6, lastName: "Melisandre", firstName: null, age: 150 },
  { id: 7, lastName: "Clifford", firstName: "Ferrara", age: 44 },
  { id: 8, lastName: "Frances", firstName: "Rossini", age: 36 },
  { id: 9, lastName: "Roxie", firstName: "Harvey", age: 65 },
];

function MainDashBoard(props) {
  const classes = useStyles();

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

  // const data = {
  //   labels: ["January", "February", "March", "April", "May", "June", "July"],
  //   datasets: [
  //     {
  //       label: "My First dataset",
  //       backgroundColor: "rgba(255,99,132,0.2)",
  //       borderColor: "rgba(255,99,132,1)",
  //       borderWidth: 1,
  //       hoverBackgroundColor: "rgba(255,99,132,0.4)",
  //       hoverBorderColor: "rgba(255,99,132,1)",
  //       data: [65, 59, 80, 81, 56, 55, 40],
  //     },
  //   ],
  // };

  const dataRevenue = {
    labels: dateRevenue,
    datasets: [
      {
        label: "Rev.",
        backgroundColor: "rgba(255,99,132,0.2)",
        borderColor: "rgba(255,99,132,1)",
        borderWidth: 1,
        hoverBackgroundColor: "rgba(255,99,132,0.4)",
        hoverBorderColor: "rgba(255,99,132,1)",
        data: revenue,
      },
    ],
  };

  const dataVehicleDistance = {
    labels: vehicle,
    datasets: [
      {
        label: "Dis.",
        backgroundColor: "rgba(255,99,132,0.2)",
        borderColor: "rgba(255,99,132,1)",
        borderWidth: 1,
        hoverBackgroundColor: "rgba(255,99,132,0.4)",
        hoverBorderColor: "rgba(255,99,132,1)",
        data: distance,
      },
    ],
  };

  const dataStudentParticipation = {
    labels: dateStudentParticipation,
    datasets: [
      {
        label: "Part.",
        //backgroundColor: "rgba(255,99,132,0.2)",
        //borderColor: "rgba(255,99,132,1)",
        //borderWidth: 1,
        //height: 500,
        //hoverBackgroundColor: "rgba(255,99,132,0.4)",
        //hoverBorderColor: "rgba(255,99,132,1)",
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
        //backgroundColor: "rgba(255,99,132,0.2)",
        //borderColor: "rgba(255,99,132,1)",
        //borderWidth: 1,
        //height: 500,
        //hoverBackgroundColor: "rgba(255,99,132,0.4)",
        //hoverBorderColor: "rgba(255,99,132,1)",
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
        //backgroundColor: "rgba(255,99,132,0.2)",
        //borderColor: "rgba(255,99,132,1)",
        //borderWidth: 1,
        //hoverBackgroundColor: "rgba(255,99,132,0.4)",
        //hoverBorderColor: "rgba(255,99,132,1)",
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
        //backgroundColor: "rgba(255,99,132,0.2)",
        //borderColor: "rgba(255,99,132,1)",
        //borderWidth: 1,
        height: 500,
        fill: false,
        //hoverBackgroundColor: "rgba(255,99,132,0.4)",
        //hoverBorderColor: "rgba(255,99,132,1)",
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
        // console.log("getStudentParticipation, lst = ", lst);

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
    // // send multipart form
    // var formData = new FormData();
    // formData.append("file", null);

    // request(
    //   "post",
    //   "/get-quiz-participation-statistic",
    //   (res) => {
    //     let lst = res.data;
    //     let dates = [];
    //     let participations = [];

    //     console.log("getQuizParticipation, lst = ", lst);

    //     lst.forEach((r) => {
    //       dates.push(r.date);
    //       participations.push(r.count);
    //     });

    //     setDateQuizParticipation(dates);
    //     setTotalQuizParticipation(participations);
    //   },
    //   {},
    //   formData,
    //   {
    //     headers: {
    //       "Content-Type": "multipart/form-data",
    //     },
    //   }
    // );
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

  // function getRevenueDateRecent() {
  //   // console.log("getRevenueDateRecent");
  //   authPost(dispatch, token, "/report-date-based-revenue-recent", {
  //     nbDays: 15,
  //   })
  //     .then((response) => {
  //       let listRev = response.revenueElements;
  //       //setVehicleDistance(response);
  //       let arrDates = [];
  //       let arrRevenues = [];
  //       listRev.forEach((r) => {
  //         arrDates.push(r.date);
  //         arrRevenues.push(r.revenue);
  //       });
  //       setDateRevenue(arrDates);
  //       setRevenue(arrRevenues);
  //       //dataVehicleDistance.labels = vehicle;
  //       //dataVehicleDistance.datasets[0].data = distance;
  //       //console.log('dataVehicleDistance.vehicle = ', dataVehicleDistance.labels);
  //       //console.log('dataVehicleDistance.distance = ', dataVehicleDistance.datasets[0].data);
  //     })
  //     .catch(console.log);
  // }

  // function getVehicleDistance() {
  //   // console.log("getVehicleDistance");
  //   authPost(dispatch, token, "/statistic-vehicle-distance", {
  //     fromDate: "",
  //     thruDate: "",
  //   })
  //     .then((response) => {
  //       //setVehicleDistance(response);
  //       let vehicle = [];
  //       let distance = [];
  //       response.forEach((vh) => {
  //         vehicle.push(vh.vehicleId);
  //         distance.push(vh.distance);
  //       });
  //       setVehicle(vehicle);
  //       setDistance(distance);
  //       //dataVehicleDistance.labels = vehicle;
  //       //dataVehicleDistance.datasets[0].data = distance;
  //     })
  //     .catch(console.log);
  // }

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
    // getVehicleDistance();
    // getRevenueDateRecent();
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

        {/* <Grid item xs={12}>
          <Paper>
            <DataGrid
              rows={rows}
              columns={columns}
              pageSize={5}
              checkboxSelection
              disableSelectionOnClick
            />
          </Paper>
        </Grid> */}

        <Grid item xs={12}>
          <Paper elevation={0}>
            <Box className={classes.doughnutStyle}>
              <Doughnut data={dataAllProject} options={taskCounterOption} />
            </Box>
          </Paper>
        </Grid>

        {/* <Grid item xs={6}>
          <h2>Rev.</h2>
          <HorizontalBar data={dataRevenue} />
        </Grid>
        <Grid item xs={6}>
          <h2>Dis.</h2>
          <HorizontalBar data={dataVehicleDistance} />
        </Grid> */}
      </Grid>
    </div>
  );
}

const screenName = "SCR_ADMIN_MAIN_DASHBOARD";
export default withScreenSecurity(MainDashBoard, screenName, true);
