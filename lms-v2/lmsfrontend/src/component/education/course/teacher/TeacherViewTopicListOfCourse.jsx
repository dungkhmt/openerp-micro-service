import { Card, CardContent } from "@material-ui/core/";
import AddIcon from "@material-ui/icons/Add";
import React, { useEffect, useState } from "react";
import { Link, useHistory } from "react-router-dom";
import { errorNoti } from "../../../../utils/notification";
import { request } from "../../../../api";
import { Button } from "@mui/material";
import StandardTable from "../../../table/StandardTable";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
  tableWrapper: {
    "& table thead tr": {
      "& th:nth-child(3)": {
        maxWidth: "80px !important",
        padding: "5px",
      },
      "& th:nth-child(4)": {
        maxWidth: "80px !important",
        padding: "5px",
      },
    },
  },
}));

export default function TeacherViewTopicListOfCourse(props) {
  const classes = useStyles();
  const courseId = props.courseId;
  const history = useHistory();
  const [topicsOfCourse, setTopicsOfCourse] = useState([]);

  useEffect(getTopicList, []);

  async function getTopicList() {
    const successHandler = (res) => setTopicsOfCourse(res.data);
    const errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu", true),
    };
    request(
      "GET",
      `/get-quiz-course-topics-of-course/${courseId}`,
      successHandler,
      errorHandlers
    );
  }

  const CreateTopicButton = (
    <Button
      color="primary"
      variant="contained"
      onClick={() => history.push(`topic/create/${courseId}`)}
    >
      <AddIcon /> Thêm mới topic
    </Button>
  );

  const CreateTagButton = (
    <Button
      color="primary"
      variant="contained"
      onClick={() => history.push(`tag/create/${courseId}`)}
    >
      <AddIcon /> Thêm mới tag
    </Button>
  );

  const columns = [
    {
      title: "Topic ID",
      field: "quizCourseTopicId",
      render: (topic) => (
        <Link
          to={`/edu/teacher/course/topic/detail/${topic.quizCourseTopicId}/${courseId}`}
        >
          {topic.quizCourseTopicId}
        </Link>
      ),
    },
    { title: "Topic name", field: "quizCourseTopicName" },
    {
      title: "Public quizs",
      field: "numberOfPublishedQuizs",
      cellStyle: { maxWidth: "80px", padding: "5px", textAlign: "center" },
    },
    {
      title: "Private quizs",
      field: "numberOfPrivateQuizs",
      cellStyle: { maxWidth: "80px", padding: "5px", textAlign: "center" },
    },
  ];

  const actions = [
    { icon: () => CreateTopicButton, isFreeAction: true },
    { icon: () => CreateTagButton, isFreeAction: true },
  ];

  return (
    <Card>
      <CardContent className={classes.tableWrapper}>
        <StandardTable
          title="Danh sách chủ đề"
          columns={columns}
          data={topicsOfCourse}
          hideCommandBar
          options={{
            selection: false,
            search: true,
            sorting: true,
          }}
          actions={actions}
        />
      </CardContent>
    </Card>
  );
}

TeacherViewTopicListOfCourse.propTypes = {
  courseId: PropTypes.string.isRequired,
};
