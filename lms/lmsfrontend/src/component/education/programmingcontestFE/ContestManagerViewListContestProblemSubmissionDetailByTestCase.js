import MaterialTable from "material-table";
import React from "react";
import {request} from "../../../api";
import {toFormattedDateTime} from "../../../utils/dateutils";
//import withScreenSecurity from "../withScreenSecurity";

export default function ContestManagerViewListContestProblemSubmissionDetailByTestCase() {
  const columns = [
    {title: "Contest", field: "contestId"},
    {title: "Problem", field: "problemId"},
    {title: "User", field: "userLoginId"},
    {title: "Status", field: "status"},
    {title: "Point", field: "point"},
    {title: "DateTime", field: "createdAt"},
  ];

  return (
    <div>
      <h1>View Submission Detail By Testcase</h1>
      <MaterialTable
        columns={columns}
        data={(query) =>
          new Promise((resolve, reject) => {
            let url =
              "/get-contest-problem-submission-detail-by-testcase?page=" +
              `${query.page}` +
              "&size=" +
              `${query.pageSize}`;

            request(
              "get",
              url,
              (res) => {
                console.log("get log user do practice, res = ", res);
                const data = res.data;
                const content = data.content.map((c) => ({
                  ...c,
                  createdAt: toFormattedDateTime(c.createdAt),
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
      />
    </div>
  );
}
