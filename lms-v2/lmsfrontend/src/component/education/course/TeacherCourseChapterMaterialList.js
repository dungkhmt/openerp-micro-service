import {Card, CardContent} from "@material-ui/core/";
import AddIcon from "@material-ui/icons/Add";
import MaterialTable from "material-table";
import {useEffect, useState} from "react";
import {Link, useHistory} from "react-router-dom";
import {request} from "../../../api";

function TeacherCourseChapterMaterialList(props) {
  const chapterId = props.chapterId;

  const history = useHistory();
  const [chapterMaterials, setChapterMaterials] = useState([]);

  const columns = [
    {
      title: "ChapterMaterialId",
      field: "eduCourseMaterialId",
      render: (rowData) => (
        <Link
          to={
            "/edu/teacher/course/chapter/material/detail/" +
            rowData["eduCourseMaterialId"]
          }
        >
          {rowData["eduCourseMaterialId"]}
        </Link>
      ),
    },
    { title: "Name", field: "eduCourseMaterialName" },
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
          actions={[
            {
              icon: () => {
                return <AddIcon color="primary" fontSize="large" />;
              },
              tooltip: "Thêm mới",
              isFreeAction: true,
              onClick: () => {
                history.push(
                  "/edu/course/detail/chapter/material/create/" + chapterId
                );
              },
            },
          ]}
        />
      </CardContent>
    </Card>
  );
}

export default TeacherCourseChapterMaterialList;
