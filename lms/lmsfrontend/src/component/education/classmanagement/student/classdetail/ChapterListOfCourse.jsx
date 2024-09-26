import React, {useEffect, useState} from 'react';
import {useHistory} from "react-router-dom";
import {request} from "../../../../../api";
import StandardTable from "../../../../table/StandardTable";

export default function ChapterListOfCourse(props) {
  const history = useHistory();
  const classId = props.classId;
  const [chapterList, setChapterList] = useState([]);

  useEffect(getChapterListOfClass, []);

  function getChapterListOfClass() {
    request("get", `/edu/class/get-chapters-of-class/${classId}`, (res) => {
      console.log("getChapterListOfClass, res.data = ", res.data);
      setChapterList(res.data);
    });
  }

  function navigateToChapterDetailPage(event, chapter) {
    history.push(`/edu/student/course/chapter/detail/${chapter.chapterId}`)
  }

  const chapterColumns = [
    { title: "Chương", field: "chapterName" },
    { title: "Trạng thái", field: "statusId", lookup: CHAPTER_STATUSES }
  ];

  return (
    <StandardTable  title="Danh sách chương"
                    columns={chapterColumns}
                    data={chapterList}
                    hideCommandBar
                    options={{
                      selection: false,
                      search: true,
                      sorting: true
                    }}
                    onRowClick={navigateToChapterDetailPage}
    />
  );
}

const CHAPTER_STATUSES = {
  STATUS_PUBLIC: 'Công khai',
  STATUS_PRIVATE: 'Không công khai'
}