import {Card, CardContent} from "@material-ui/core/";
import MaterialTable from "material-table";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import {Link} from "react-router-dom";

function StudentCourseChapterList(props) {
  const params = useParams();
  const courseId = props.courseId;
  const classId = props.classId;
  const chapterList = props.chapters;
  const [chapters, setChapters] = useState(() => {
    console.log("StudentCourseChapterList, chapters = ", props.chapters);
    return props.chapters;
  });

  //const [chapters, setChapters] = useState([]);

  const columns = [
    {
      title: "ChapterId",
      field: "chapterId",
      render: (rowData) => (
        <Link to={"/edu/student/course/chapter/detail/" + rowData["chapterId"]}>
          {rowData["chapterId"]}
        </Link>
      ),
    },
    { title: "Chapter Name", field: "chapterName" },
  ];

  // async function getChapterList() {
  //   let lst = await authGet(
  //     dispatch,
  //     token,
  //     "/edu/class/get-chapters-of-course"
  //   );
  //   setChapters(lst);
  // }

  useEffect(() => {
    //setChapters(chapterList);
    //getChapterList();
    //console.log('StudentCourseChapterList, courseId = ' + courseId);
  }, []);

  return (
    <Card>
      <CardContent>
        <MaterialTable
          title={"Chương"}
          columns={columns}
          data={props.chapters.map((chap) => (
            <chap.chapterId />
          ))}
        />
      </CardContent>
    </Card>
  );
}

export default StudentCourseChapterList;
