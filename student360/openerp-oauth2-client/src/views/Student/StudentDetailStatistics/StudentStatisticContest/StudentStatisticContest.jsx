import React, { useState, useEffect } from "react";
import { Grid, Stack } from "@mui/material";
import CodeBracket from "@heroicons/react/24/solid/CodeBracketIcon";
import CommandLine from "@heroicons/react/24/solid/CommandLineIcon";
import AcademicCap from "@heroicons/react/24/solid/AcademicCapIcon";
import Users from "@heroicons/react/24/solid/UsersIcon";
import DoubleBarChart from "../../../../components/chart/DoubleBarChart";
import PieCharts from "../../../../components/chart/PieChart";
import LineChartCoponent from "../../../../components/chart/LineChart";
import DoubleLineChart from "../../../../components/chart/DoubleLineChart";
import TableRanking from "../../../../components/table/RakingTable";
import InfoCard from "../../../../components/card/InfoCard";
import { request } from "../../../../api";
import { errorNoti } from "../../../../utils/notification";

function StudentStatisticsContest(props) {
  const TABLE_HEADERS_1 = ["Problem ID", "Count"];
  const [studentDetail, setStudentDetail] = useState({});
  const studentLoginId = props.studentLoginId;
  useEffect(getStudentDetail, [studentLoginId]);

  function getStudentDetail() {
    let successHandler = (res) => {
      let detail = res.data;
      setStudentDetail(detail);
    };
    let errorHandlers = {
      onError: (error) =>
        errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000),
    };
    request(
      "GET",
      `/student-statistics/student-contest-statistic/${studentLoginId}`,
      successHandler,
      errorHandlers
    );
  }

  const dataTotalSubmission = {
    Accepted: studentDetail.totalProblemSubmittedAccept,
    Partial: studentDetail.totalProblemSubmittedPartial,
    "Compile Error": studentDetail.totalProblemSubmittedCompileError,
    Other:
      studentDetail.totalProblemSubmitted -
      studentDetail.totalProblemSubmittedAccept -
      studentDetail.totalProblemSubmittedPartial -
      studentDetail.totalProblemSubmittedCompileError,
  };

  return (
    <Grid container gap={2}>
      <Grid container justifyContent="space-between">
        <Grid item xs={2.75}>
          <InfoCard
            icon={CommandLine}
            iconColor="#b5ba0d"
            mainTitle={studentDetail.totalSubmitted}
            subTitle="Code Submissions"
          />
        </Grid>
        <Grid item xs={2.75}>
          <InfoCard
            icon={CodeBracket}
            iconColor="#0d2d80"
            mainTitle={studentDetail.totalProblemSubmitted}
            subTitle="Total Problems"
          />
        </Grid>
        <Grid item xs={2.75}>
          <InfoCard
            icon={AcademicCap}
            iconColor="#1976d2"
            mainTitle={studentDetail.totalContestSubmitted}
            subTitle="Total Contests"
          />
        </Grid>
        <Grid item xs={2.75}>
          <InfoCard
            icon={Users}
            iconColor="#139529"
            mainTitle={studentDetail.totalProblemSubmittedAccept}
            subTitle="Total Problems Accepted"
          />
        </Grid>
      </Grid>

      <Stack
        width="100%"
        direction="row"
        justifyContent="space-between"
        gap={2}
      >
        <DoubleBarChart
          data={studentDetail.contestDetails}
          fieldX="totalProblems"
          fieldY="totalProblemsSubmitted"
        />
        <PieCharts
          data={dataTotalSubmission}
          title="Problem Submission Distribution"
          mainTitle="Total Problems"
          totalValue={studentDetail.totalProblemSubmitted}
        />
      </Stack>

      <LineChartCoponent
        data={studentDetail.dailySubmissionCounts}
        title="Submission Count per Contest"
        xAxisName="date"
        yAxisName="Total Submissions"
        subtitle={`Average Submissions Per Day: ${
          studentDetail && studentDetail.averageSubmissionPerDay
            ? studentDetail.averageSubmissionPerDay.toFixed(2)
            : "0"
        }`}
      />

      <Stack
        width="100%"
        direction="row"
        gap={2}
        justifyContent="space-between"
      >
        <LineChartCoponent
          data={studentDetail?.studentSubmissionBySemsters}
          title={studentDetail?.studentSubmissionBySemsters?.[0].semester}
          xAxisName="submissionMonth"
          yAxisName="Total submissions"
        />
        <PieCharts
          title="Programming Language Distribution"
          mainTitle="Total Languages"
          data={studentDetail.programmingLanguageSubmitCounts}
          totalValue={studentDetail.numberProgramLanguage}
        />
      </Stack>

      <DoubleLineChart
        data={studentDetail?.submissionHourlySummary}
        title="Submission Count per Hour"
        xAxisName="hour"
        yAxis1Name="Total Submissions"
        yAxis2Name="Total Passed Submissions"
      />

      <Stack
        width="100%"
        direction="row"
        justifyContent="space-between"
        gap={2}
      >
        <TableRanking
          title="Minimum Submission To Accept"
          subtitle={`${
            studentDetail && studentDetail.averageMinimumSubmissionToAccept > 0
              ? `AVG: ${studentDetail.averageMinimumSubmissionToAccept.toFixed(
                  2
                )}`
              : "Chưa có bài tập nào Accepted"
          }`}
          headTable={TABLE_HEADERS_1}
          data={studentDetail.firstTimeAcceptList}
          typeTable="seller"
        />
        <TableRanking
          title="Minimum Submission To Accept"
          subtitle={`${
            studentDetail && studentDetail.averageMinimumSubmissionToAccept > 0
              ? `AVG: ${studentDetail.averageMinimumSubmissionToAccept.toFixed(
                  2
                )}`
              : "Chưa có bài tập nào Accepted"
          }`}
          headTable={TABLE_HEADERS_1}
          data={studentDetail.firstTimeAcceptList}
          typeTable="seller"
        />
      </Stack>
    </Grid>
  );
}

export default StudentStatisticsContest;
