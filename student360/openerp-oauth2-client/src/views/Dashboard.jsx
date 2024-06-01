import React, { useEffect, useState } from "react";
import { Grid, Stack } from "@mui/material";
import Typography from "@mui/material/Typography";
import CodeBracket from "@heroicons/react/24/solid/CodeBracketIcon";
import CommandLine from "@heroicons/react/24/solid/CommandLineIcon";
import AcademicCap from "@heroicons/react/24/solid/AcademicCapIcon";
import Users from "@heroicons/react/24/solid/UsersIcon";
import InfoCard from "../components/card/InfoCard";
// import DailyProgramming from "../components/dashboard/DailyProgramming";
// import DailyQuiz from "../components/dashboard/DailyQuiz";
// import { Box } from "@material-ui/core";
// import TopicWordCloud from "../components/dashboard/TopicWordCloud";
import { request } from "../api";
import LineChartComponent from "../components/chart/LineChart";
import ScoreTable from "../components/table/ScoreTable";

export default function Home() {
  const TABLE_HEADERS = ["Semester", "A/A+", "B/B+", "C/C+", "D/D+", "F"];
  const [statistics, setStatistics] = useState([]);
  useEffect(() => {
    request("get", "/dash-board", (res) => {
      setStatistics(res?.data);
    }).then();
  }, []);
  return (
    <Stack gap={2}>
      {/* <HustContainerCard> */}
      <Grid container>
        <Grid item xs={12} sx={{ textAlign: "center" }}>
          <Typography
            variant="h6"
            sx={{
              color: "#ae1d2c",
              fontSize: "24px",
              fontWeight: 800,
              textShadow: "2px 1px 2px #e3e3e3",
            }}
          >
            HUSTack
          </Typography>
          <Typography variant="body2">
            Empower your programming journey and solve real-world problems
          </Typography>
        </Grid>
        <Grid
          container
          justifyContent="space-between"
          sx={{ marginTop: "16px" }}
        >
          <Grid item xs={2.75}>
            <InfoCard
              icon={CodeBracket}
              iconColor="#0d2d80"
              mainTitle={statistics?.totalProblem}
              subTitle="Coding Problems"
            />
          </Grid>
          <Grid item xs={2.75}>
            <InfoCard
              icon={AcademicCap}
              iconColor="#1976d2"
              mainTitle={statistics?.totalQuizQuestion}
              subTitle="Quiz Tests"
            />
          </Grid>
          <Grid item xs={2.75}>
            <InfoCard
              icon={Users}
              iconColor="#139529"
              mainTitle={statistics?.totalUserActive}
              subTitle="Active Users"
            />
          </Grid>
          <Grid item xs={2.75}>
            <InfoCard
              icon={CommandLine}
              iconColor="#b5ba0d"
              mainTitle={statistics?.totalSubmissions}
              subTitle="Code Submissions"
            />
          </Grid>
        </Grid>

        {/* <Grid
          container
          justifyContent="space-between"
          sx={{ marginTop: "20px" }}
        > */}
        {/* <Grid item xs={7}>
            <Box>
              <DailyQuiz />
              <Box sx={{ height: "24px" }} />
              <DailyProgramming />
            </Box>
          </Grid> */}
        {/* <Grid item xs={4.75}>
            <TopicWordCloud />
          </Grid> */}
        {/* </Grid> */}
      </Grid>

      <LineChartComponent
        data={statistics?.totalStudentPassBySemester}
        title="Number of Students Passing by Semester"
        xAxisName="Semester"
        yAxisName="Total Student Passed"
      />
      <LineChartComponent
        data={statistics?.totalStudentPlagiarismBySemester}
        title="Number of Students Cheating by Semester"
        xAxisName="Semester"
        yAxisName="Total Student Plagiarism"
      />
      {/* </HustContainerCard> */}
      <ScoreTable
        title="Semester Student Grades"
        headTable={TABLE_HEADERS}
        data={statistics?.semesterScores}
        typeTable="seller"
      />
    </Stack>
  );
}
