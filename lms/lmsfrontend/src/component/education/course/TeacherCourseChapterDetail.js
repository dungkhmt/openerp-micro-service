import {Card, CardContent} from "@material-ui/core/";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import TeacherCourseChapterMaterialList from "./TeacherCourseChapterMaterialList";

function TeacherCourseChapterDetail() {
  const params = useParams();
  const chapterId = params.chapterId;

  const [courseChapter, setCourseChapter] = useState(null);

  useEffect(() => {
    console.log(chapterId);
  }, []);

  return (
    <Card>
      <CardContent>
        <TeacherCourseChapterMaterialList chapterId={chapterId} />
      </CardContent>
    </Card>
  );
}

export default TeacherCourseChapterDetail;
