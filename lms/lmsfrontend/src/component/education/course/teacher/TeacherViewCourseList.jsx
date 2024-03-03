import { Card, CardContent } from "@material-ui/core/";
import React, { useEffect, useState } from "react";
import { Link, useHistory } from "react-router-dom";
import { Button } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import StandardTable from "../../../table/StandardTable";
import { errorNoti } from "../../../../utils/notification";
import { request } from "../../../../api";

export default function TeacherViewCourseList() {
  const history = useHistory();
  const [courses, setCourses] = useState([]);

  useEffect(getCourseList, []);

  function getCourseList() {
    let successHandler = (res) => setCourses(res.data);
    let errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi tải dữ liệu", true),
    };
    request("GET", "/edu/class/get-all-courses", successHandler, errorHandlers);
  }

  const CreateCourseButton = (
    <Button
      color="primary"
      variant="contained"
      onClick={() => history.push(`create`)}
    >
      <AddIcon /> Thêm mới
    </Button>
  );

  const columns = [
    {
      title: "Course ID",
      field: "id",
      render: (course) => (
        <Link to={`/edu/course/detail/${course.id}`}>{course.id}</Link>
      ),
    },
    { title: "Tên môn học", field: "name" },
    { title: "Số tín chỉ", field: "credit" },
  ];

  const actions = [{ icon: () => CreateCourseButton, isFreeAction: true }];

  return (
    <Card>
      <CardContent>
        <StandardTable
          title="Danh sách môn học"
          columns={columns}
          data={courses}
          hideCommandBar
          options={{
            selection: false,
            search: true,
            sorting: true,
          }}
          actions={actions}
        />
      </CardContent>
    </Card>
  );
}
