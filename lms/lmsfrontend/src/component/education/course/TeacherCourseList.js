import {Card, CardContent} from "@material-ui/core/";
import MaterialTable from "material-table";
import {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {request} from "../../../api";
import TeacherViewCourseList from "./teacher/TeacherViewCourseList";

function TeacherCourseList() {
  const [courses, setCourses] = useState([]);

  const columns = [
    {
      title: "CourseID",
      field: "id",
      render: (rowData) => (
        <Link to={"/edu/course/detail/" + rowData["id"]}>{rowData["id"]}</Link>
      ),
    },
    { title: "Tên môn", field: "name" },
    { title: "Số tín chỉ", field: "credit" },
  ];

  async function getCourseList() {
    request("get", "/edu/class/get-all-courses", (res) => {
      setCourses(res.data);
    });
  }

  useEffect(() => {
    getCourseList();
  }, []);

  return (
    <div>
      <TeacherViewCourseList />
      <br />

      <Card>
        <CardContent>
          <MaterialTable
            title={"Danh sách môn học"}
            columns={columns}
            data={courses}
          />
        </CardContent>
      </Card>
    </div>
  );
}

const screenName = "SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER";
//export default withScreenSecurity(TeacherCourseList, screenName, true);
export default TeacherCourseList;
