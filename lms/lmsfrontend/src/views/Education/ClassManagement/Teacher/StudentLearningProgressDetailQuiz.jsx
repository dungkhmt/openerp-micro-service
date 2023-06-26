import React from "react";
import MaterialTable from "material-table";
import {request} from "../../../../api";
import {toFormattedDateTime} from "../../../../utils/dateutils";

export default function StudentLearningProgressDetailQuiz(props) {
  const studentId = props.studentId;
  const columns = [
    { title: "UserName", field: "userLoginId" },
    { title: "FullName", field: "fullName" },
    { title: "Affiliations", field: "affiliations" },
    { title: "ClassCode", field: "classCode" },
    { title: "CourseId", field: "courseId" },
    { title: "CourseName", field: "courseName" },
    { title: "Chapter", field: "topicName" },
    { title: "Grade", field: "grade" },
    { title: "DateTime", field: "date" },
  ];
  return (
    <div>
      <h1>View Course Video</h1>
      <MaterialTable
        columns={columns}
        data={(query) =>
          new Promise((resolve, reject) => {
            let url =
              "/admin/data/view-users-do-pratice-quiz/" +
              studentId +
              "?page=" +
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
                  date: toFormattedDateTime(c.createdStamp),
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
      ></MaterialTable>
    </div>
  );
}
