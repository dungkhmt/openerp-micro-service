import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router";
import {useHistory} from "react-router-dom";
import StudentCourseChapterMaterialList from "./StudentCourseChapterMaterialList";

function StudentCourseChapterDetail() {
  const params = useParams();
  const chapterId = params.chapterId;
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const history = useHistory();
  const [courseChapter, setCourseChapter] = useState(null);

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
