import {Container, Paper, Typography} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import React, {memo, useEffect, useState} from "react";
import {Bar} from "react-chartjs-2";
import {request} from "../../../api";

const useStyles = makeStyles((theme) => ({
  // root: {
  //   padding: theme.spacing(2),
  //   display: 'flex',
  //   justifyContent: 'center',
  //   textAlign: 'center',
  //   maxHeight: 400
  // },
  wrapper: {
    padding: theme.spacing(2),
  },
}));

const options = {
  responsive: true,
  scales: {
    yAxes: [
      {
        scaleLabel: {
          display: true,
          labelString: "Số sinh viên",
          fontColor: "#C46BAE",
          fontSize: 16,
          fontStyle: "bold",
        },
        ticks: {
          fontColor: "#C46BAE",
          fontStyle: "bold",
          precision: 0,
          beginAtZero: true,
        },
      },
    ],
    xAxes: [
      {
        scaleLabel: {
          display: true,
          labelString: "Điểm",
          fontColor: "#1E152A",
          fontSize: 18,
          fontStyle: "bold",
        },
        ticks: {
          fontColor: "#1E152A",
          fontStyle: "bold",
        },
      },
    ],
  },
};

function QuizTestResultChart(props) {
  const classes = useStyles();

  const { testId } = props;
  const [data, setData] = useState({});

  function getResults(){
    request(
      "post",
      "/get-quiz-test-participation-execution-result",
      ({ data }) => {
        const result = hanleData(data);
        setData(result);
      },
      {},
      { testId }
    );
  }
  useEffect(() => {
    try {
      var refreshIntervalId = setInterval(async () => {
        getResults();
      }, 3000);
    } catch (e) {
      console.log("FOUND exception", e);
    }

    return function cleanInterval() {
      clearInterval(refreshIntervalId);
    };

    //getResults();
    
  }, [testId]);

  return (
    <Container maxWidth="lg">
      <Paper className={classes.wrapper} elevation={3}>
        <Typography variant="h5" align="center" color="primary">
          BIỂU ĐỒ PHÂN BỐ ĐIỂM SINH VIÊN
        </Typography>
        <Bar data={data} options={options} />
      </Paper>
    </Container>
  );
}

export default memo(QuizTestResultChart);

/*--------------------------------------*/
function hanleData(rawData) {
  const particitors = {};
  let maxQuestionCount = 0;
  rawData.map((item) => {
    let particitorId = item.participationUserLoginId;
    if (!particitors[particitorId]) {
      let object = {
        id: particitorId,
        name: item.participationFullName,
        totalGrade: 0,
        questionCount: 0,
        correctAnswer: 0,
      };
      particitors[particitorId] = object;
    }
    particitors[particitorId].totalGrade += item.grade;
    if (item.result.toUpperCase() === "Y") {
      particitors[particitorId].correctAnswer++;
    }
    particitors[particitorId].questionCount++;
    if (particitors[particitorId].questionCount > maxQuestionCount)
      maxQuestionCount = particitors[particitorId].questionCount;
  });

  const gradeCount = {};
  const particitorIds = Object.keys(particitors);
  particitorIds.forEach((key) => {
    const grade = particitors[key].totalGrade;
    if (gradeCount[grade] === undefined) {
      gradeCount[grade] = 0;
    }
    gradeCount[grade]++;
  });
  // console.log(particitors)
  // console.log("grade count", gradeCount)

  const gradeHistorygram = [];
  const labels = [];
  for (let i = 0; i <= maxQuestionCount; i++) {
    gradeHistorygram.push(gradeCount[i] || 0);
    labels.push(`${i}`);
  }
  // console.log(gradeHistorygram);

  return {
    labels,
    datasets: [
      {
        label: "Số sinh viên",
        data: gradeHistorygram,
        backgroundColor: "rgba(54, 162, 235, 0.2)",
        borderColor: "rgba(54, 162, 235, 1)",
        borderWidth: 1,
        hoverBackgroundColor: "rgba(54, 162, 235, 0.4)",
        hoverBorderColor: "rgba(54, 162, 235, 1)",
      },
    ],
  };
}
