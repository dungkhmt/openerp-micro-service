import {useEffect} from "react";
import {useParams} from "react-router";
import StudentCourseChapterMaterialList from "./StudentCourseChapterMaterialList";

function StudentCourseChapterDetail() {
  const params = useParams();
  const chapterId = params.chapterId;

  useEffect(() => {
    console.log(chapterId);
  }, []);

  return (
    // <Card>
    //     <CardContent>
    <StudentCourseChapterMaterialList chapterId={chapterId} />
    //     </CardContent>
    // </Card>
  );
}

export default StudentCourseChapterDetail;
