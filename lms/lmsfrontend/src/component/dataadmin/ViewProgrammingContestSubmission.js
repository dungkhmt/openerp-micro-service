import MaterialTable from "material-table";
import React from "react";
import {request} from "../../api";
import {toFormattedDateTime} from "../../utils/dateutils";

export default function ViewProgrammingContestSubmission() {
  const columns = [
    { title: "UserName", field: "userId" },
    { title: "FullName", field: "fullname" },
    { title: "Affiliations", field: "affiliation" },
    { title: "ContestId", field: "contestId" },
    { title: "ProblemId", field: "problemId" },
    { title: "Point", field: "point" },
    { title: "Status", field: "status" },
    { title: "Testcase Passes", field: "testCasePass" },
    { title: "Date", field: "date" },
  ];

  return (
    <div>
      <MaterialTable
        columns={columns}
        data={(query) =>
          new Promise((resolve, reject) => {
            let url =
              "/admin/data/view-contest-submission?page=" +
              `${query.page}` +
              "&size=" +
              `${query.pageSize}`;

            request(
              "get",
              url,
              (res) => {
                const data = res.data;
                const content = data.content.map((c) => ({
                  ...c,
                  date: toFormattedDateTime(c.submissionDate),
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

            /*  
            let token = localStorage.getItem("TOKEN");
            axios
              .get(url, { headers: { "x-auth-token": token } })
              .then((res) => res.data)
              .then((res) => {
                console.log("before resolve, res = ", res);
                resolve({
                  data: res.content,
                  page: query.page,
                  totalCount: res.totalElements,
                });
              })
              .catch((err) => {
                //if (err.response.status === 401) {
                //}
                console.log("exception err = ", err);
              });
              */
          })
        }
      ></MaterialTable>
    </div>
  );
}
