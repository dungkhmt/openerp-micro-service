import {Card, CardContent} from "@material-ui/core/";
import React, {useEffect, useState} from "react";
import {Link, useHistory} from "react-router-dom";
import {errorNoti, successNoti} from "../../../../utils/notification";
import {request} from "../../../../api";
import {Button} from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import StandardTable from "../../../table/StandardTable";
import PropTypes from "prop-types";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles(theme => ({
  tableWrapper: {
    '& table thead tr': {
      '& th:nth-child(3)': {
        maxWidth: '140px !important'
      },
      '& th:nth-child(4)': {
        maxWidth: '140px !important',
        padding: '5px'
      }
    }
  }
}))

export default function TeacherViewChapterListOfCourse(props) {
  const classes = useStyles();
  const courseId = props.courseId;
  const history = useHistory();
  const [chaptersOfCourse, setChaptersOfCourse] = useState([]);

  useEffect(getChaptersOfCourse, []);

  async function getChaptersOfCourse() {
    const successHandler = res => setChaptersOfCourse(res.data);
    const errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu", true)
    }
    request("GET", `/edu/class/get-chapters-of-course/${courseId}`, successHandler, errorHandlers);
  }

  function toggleChapterStatus(chapter) {
    const successHandler = res => {
      successNoti("Thay đổi trạng thái chương thành công, xem kết quả trên giao diện!", true);
      chapter.statusId = chapter.statusId === 'STATUS_PUBLIC' ? 'STATUS_PRIVATE' : 'STATUS_PUBLIC';
      setChaptersOfCourse([...chaptersOfCourse]);
    }
    const errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi thay đổi trạng thái", true)
    }
    request("GET", `/edu/class/change-chapter-status/${chapter.chapterId}`, successHandler, errorHandlers);
  }

  const CreateChapterButton = (
    <Button color="primary" variant="contained"
            onClick={() => history.push(`chapter/create/${courseId}`)}>
      <AddIcon/> Thêm mới
    </Button>
  )

  const ToggleChapterStatusButton = ({ chapter }) => (
    <Button color="primary" variant="outlined"
            onClick={() => toggleChapterStatus(chapter)}>
      Update status
    </Button>
  )

  const columns = [
    { title: "Chapter ID", field: "chapterId",
      render: (chapter) => (
        <Link to={`/edu/teacher/course/chapter/detail/${chapter.chapterId}`}>
          {chapter.chapterId}
        </Link>
      )
    },
    { title: "Tên chương", field: "chapterName" },
    { title: "Trạng thái", field: "statusId", lookup: CHAPTER_STATUSES, cellStyle: { maxWidth: '140px' }},
    { field: "", title: "", cellStyle: { maxWidth: '140px', padding: '5px', textAlign: "center" },
      render: (chapter) => <ToggleChapterStatusButton chapter={chapter}/>
    }
  ];

  const actions = [{ icon: () => CreateChapterButton, isFreeAction: true }]

  return (
    <Card>
      <CardContent className={classes.tableWrapper}>
        <StandardTable  title="Danh sách chương"
                        columns={columns}
                        data={chaptersOfCourse}
                        hideCommandBar
                        options={{
                          selection: false,
                          search: true,
                          sorting: true
                        }}
                        actions={actions}/>
      </CardContent>
    </Card>
  );
}

TeacherViewChapterListOfCourse.propTypes = {
  courseId: PropTypes.string.isRequired
}

const CHAPTER_STATUSES = {
  STATUS_PUBLIC: 'Công khai',
  STATUS_PRIVATE: 'Không công khai'
}
