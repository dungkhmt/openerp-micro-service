import React from "react";
import MaterialTable from "material-table";
import {request} from "../../../../api";
import {toFormattedDateTime} from "../../../../utils/dateutils";

export default function StudentLearningProgressDetailViewVideo(props) {
  const studentId = props.studentId;
  const columns = [
    { title: "UserName", field: "userLoginId" },
    { title: "FullName", field: "fullname" },
    { title: "ClassCode", field: "classId" },
    { title: "CourseId", field: "courseId" },
    { title: "CourseName", field: "courseName" },
    { title: "Chapter", field: "chapterName" },
    { title: "Material", field: "materialName" },
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
              "/admin/data/view-course-video-of-a-student/" +
              studentId +
              "?page=" +
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
