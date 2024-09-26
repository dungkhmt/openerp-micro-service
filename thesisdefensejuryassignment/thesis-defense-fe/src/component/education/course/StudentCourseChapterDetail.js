import { useEffect } from "react";
import { useParams } from "react-router";
import StudentCourseChapterMaterialList from "./StudentCourseChapterMaterialList";
import StudentClassChapterMaterialList from "./StudentClassChapterMaterialList";

function StudentCourseChapterDetail() {
  const params = useParams();
  const chapterId = params.chapterId;
  const classId = params.classId;
  useEffect(() => {
    console.log(chapterId);
  }, []);

  return (
    // <Card>
    //     <CardContent>
    classId ? (
      <StudentClassChapterMaterialList
        classId={classId}
        chapterId={chapterId}
      />
    ) : (
      <StudentCourseChapterMaterialList chapterId={chapterId} />
    )

    //     </CardContent>
    // </Card>
  );
}

export default StudentCourseChapterDetail;
