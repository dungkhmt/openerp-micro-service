import React, { useEffect, useState } from "react";

import StandardTable from "../../../components/table/StandardTable";
import { request } from "api";
import { formatDecimal } from "../../../utils/number";
// import { Tooltip } from "@mui/material";
import { Link } from "react-router-dom";

function StudentListScreen() {
  const [statistics, setStatistics] = useState([]);
  useEffect(() => {
    request("get", "/student-statistics/get-all", (res) => {
      setStatistics(res?.data);
    }).then();
  }, []);

  const columns = [
    {
      title: "Student",
      field: "studentId",
      cellStyle: {
        minWidth: "128px",
      },
      render: (rowData) => (
        <Link to={"/students/statistics-detail/" + rowData.studentId}>
          {rowData.studentId.substring(0, 8)}
        </Link>
      ),
    },
    {
      title: "Total Submissions",
      field: "totalSubmitted",
      cellStyle: {
        minWidth: "128px",
      },
    },
    {
      title: "Total Problems Submitted",
      field: "totalProblemSubmitted",
      cellStyle: {
        minWidth: "128px",
      },
    },
    {
      title: "Total Problems Accepted",
      field: "totalProblemSubmittedAccept",
      cellStyle: {
        minWidth: "128px",
      },
    },
    {
      title: "Number Program Language",
      field: "numberProgramLanguage",
      cellStyle: {
        minWidth: "128px",
      },
    },
    {
      title: "First Submission Score Rate",
      field: "firstSubmissionAccuracyRate",
      cellStyle: {
        minWidth: "128px",
      },
      render: (rowData) =>
        formatDecimal(rowData?.firstSubmissionAccuracyRate, 1) + "%",
    },
    {
      title: "Average Submissions To Success",
      field: "averageMinimumSubmissionToAccept",
      cellStyle: {
        minWidth: "128px",
      },
      render: (rowData) =>
        formatDecimal(rowData?.averageMinimumSubmissionToAccept, 2),
    },
    {
      title: "Submissions Percent Day Average",
      field: "averageSubmissionPerDay",
      cellStyle: {
        minWidth: "128px",
      },
      render: (rowData) => formatDecimal(rowData?.averageSubmissionPerDay, 2),
    },
  ];
  return (
    <div>
      <StandardTable
        hideCommandBar
        columns={columns}
        data={statistics}
        title="Student Statistics"
        options={{
          filtering: false,
          debounceInterval: 500,
          selection: false,
          search: true,
        }}
      />
    </div>
  );
}

export default StudentListScreen;