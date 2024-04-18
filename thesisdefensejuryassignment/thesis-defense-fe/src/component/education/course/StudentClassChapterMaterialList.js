import { Card, CardContent } from "@material-ui/core/";
import MaterialTable from "material-table";
import { useEffect, useState } from "react";
import { useParams } from "react-router";
import { Link } from "react-router-dom";
import { request } from "../../../api";

function StudentClassChapterMaterialList(props) {
  const params = useParams();
  const chapterId = props.chapterId;
  const classId = props.classId;

  const [chapterMaterials, setChapterMaterials] = useState([]);

  const columns = [
    {
      title: "Tên",
      field: "materialId",
      render: (rowData) => (
        <Link
          to={
            "/edu/student/course/chapter/material/detail/" +
            rowData["materialId"]
          }
        >
          {rowData["eduCourseChapterMaterial"]["eduCourseMaterialName"]}
        </Link>
      ),
    },
    {
      title: "Thể loại",
      render: (rowData) =>
        rowData["eduCourseChapterMaterial"]["eduCourseMaterialType"],
    },
  ];

  async function getChapterMaterialList() {
    /*
    let lst = await authGet(
      dispatch,
      token,
      "/edu/class/get-chapter-materials-of-course/" + chapterId
    );
    setChapterMaterials(lst);
      */

    request(
      "get",
      `edu/class/get-chapter-materials-of-class/${classId}/${chapterId}`,
      (res) => {
        const data = res.data;
        setChapterMaterials(data.filter((item) => item.status === true));
      }
    );
  }

  useEffect(() => {
    getChapterMaterialList();
  }, []);

  return (
    <Card>
      <CardContent>
        <MaterialTable
          title={"Material"}
          columns={columns}
          data={chapterMaterials}
        />
      </CardContent>
    </Card>
  );
}

export default StudentClassChapterMaterialList;
