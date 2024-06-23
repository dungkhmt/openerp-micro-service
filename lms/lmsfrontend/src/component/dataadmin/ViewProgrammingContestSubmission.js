import { Tooltip } from "@mui/material";
import { request } from "api";
import { RejudgeButton } from "component/education/programmingcontestFE/RejudgeButton";
import { getStatusColor } from "component/education/programmingcontestFE/lib";
import StandardTable from "component/table/StandardTable";
import withScreenSecurity from "component/withScreenSecurity";
import { Link } from "react-router-dom";
import { toFormattedDateTime } from "utils/dateutils";

function ViewProgrammingContestSubmission() {
  const columns = [
    {
      title: "ID",
      field: "contestSubmissionId",
      filtering: false,
      cellStyle: { minWidth: "80px" },
      render: (rowData) => (
        <Link
          to={
            "/programming-contest/manager-view-contest-problem-submission-detail/" +
            rowData.contestSubmissionId
          }
        >
          {rowData.contestSubmissionId.substring(0, 6)}
        </Link>
      ),
    },
    {
      title: "User ID",
      field: "userId",
      cellStyle: {
        minWidth: "112px",
      },
      render: (rowData) => (
        <Tooltip title={rowData.fullname} placement="bottom-start" arrow>
          {rowData.userId}
        </Tooltip>
      ),
    },
    {
      title: "Contest ID",
      field: "contestId",
      cellStyle: {
        minWidth: "128px",
      },
    },
    {
      title: "Problem ID",
      field: "problemId",
      cellStyle: {
        minWidth: "128px",
      },
      render: (rowData) => (
        <Tooltip title={rowData.problemName} placement="bottom-start" arrow>
          {rowData.problemId}
        </Tooltip>
      ),
    },
    {
      title: "Status",
      field: "status",
      render: (rowData) => (
        <span style={{ color: getStatusColor(`${rowData.status}`) }}>
          {`${rowData.status}`}
        </span>
      ),
    },
    { title: "Pass", field: "testCasePass", filtering: false },
    { title: "Lang", field: "sourceCodeLanguage" },

    {
      title: "Submitted At",
      field: "createAt",
      cellStyle: {
        minWidth: "140px",
        // textAlign: "center"
      },
    },
    {
      title: "Rejudge",
      sortable: "false",
      headerStyle: { textAlign: "center" },
      cellStyle: { textAlign: "center" },
      render: (rowData) => (
        <RejudgeButton submissionId={rowData.contestSubmissionId} />
      ),
    },
  ];

  return (
    <div>
      <StandardTable
        hideCommandBar
        hideToolBar
        columns={columns}
        data={(query) =>
          new Promise((resolve, reject) => {
            let url = `/admin/data/view-contest-submission?page=${query.page}&size=${query.pageSize}`;

            query.filters.forEach((element) => {
              url += `&${element.column.field}=${element.value}`;
            });

            request(
              "get",
              url,
              (res) => {
                const data = res.data;
                const content = data.content.map((submission) => ({
                  ...submission,
                  createAt: toFormattedDateTime(submission.submissionDate),
                }));

                resolve({
                  data: content, // your data array
                  page: data.number, // current page number
                  totalCount: data.totalElements, // total row number
                });
              },
              {
                onError: (e) => {
                  reject({
                    message:
                      "Đã có lỗi xảy ra trong quá trình tải dữ liệu. Thử lại ",
                    errorCause: "query",
                  });
                },
              }
            );
          })
        }
        options={{
          filtering: true,
          debounceInterval: 500,
          pageSize: 5,
          selection: false,
          search: false,
        }}
      />
    </div>
  );
}

const screenName = "SCR_ADMIN_CONTEST_SUBMISSION";
export default withScreenSecurity(
  ViewProgrammingContestSubmission,
  screenName,
  true
);
