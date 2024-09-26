import {Card, CardContent} from "@material-ui/core/";
import MaterialTable from "material-table";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import {Link} from "react-router-dom";
import {request} from "../../../api";

function StudentCourseChapterMaterialList(props) {
  const params = useParams();
  const chapterId = props.chapterId;

  const [chapterMaterials, setChapterMaterials] = useState([]);

  const columns = [
    {
      title: "Tên",
      field: "eduCourseMaterialId",
      render: (rowData) => (
        <Link
          to={
            "/edu/student/course/chapter/material/detail/" +
            rowData["eduCourseMaterialId"]
          }
        >
          {rowData["eduCourseMaterialName"]}
        </Link>
      ),
    },
    { title: "Thể loại", field: "eduCourseMaterialType" },
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
      "/edu/class/get-chapter-materials-of-course/" + chapterId,
      (res) => {
        console.log("get log user do practice, res = ", res);
        const data = res.data;
        setChapterMaterials(data);
      }
    );
  }

  useEffect(() => {
    getChapterMaterialList();
    console.log("TeacherCourseChapterMaterialList, chapterId = " + chapterId);
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

export default StudentCourseChapterMaterialList;
