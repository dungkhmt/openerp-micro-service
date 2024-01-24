import { request } from "api";
import MaterialTable from "material-table";
import { toFormattedDateTime } from "utils/dateutils";
import withScreenSecurity from "../withScreenSecurity";

function ViewCourseVideo() {
  const columns = [
    { title: "UserName", field: "userLoginId" },
    { title: "FullName", field: "fullname" },
    { title: "ClassId", field: "classId" },
    { title: "CourseId", field: "courseId" },
    { title: "CourseName", field: "courseName" },
    { title: "Chapter", field: "chapterName" },
    { title: "Material", field: "materialName" },
    { title: "Date", field: "date" },
  ];
  return (
    <div>
      <h1>View Course Video</h1>
      <MaterialTable
        columns={columns}
        data={(query) =>
          new Promise((resolve, reject) => {
            let url =
              "/admin/data/view-course-video?page=" +
              `${query.page}` +
              "&size=" +
              `${query.pageSize}`;

            request(
              "get",
              url,
              (res) => {
                console.log("get log view video, res = ", res);
                const data = res.data;
                const content = data.content.map((c) => ({
                  ...c,
                  date: toFormattedDateTime(c.date),
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

const screenName = "SCR_ADMIN_VIEW_COURSE_VIDEO";
export default withScreenSecurity(ViewCourseVideo, screenName, true);
