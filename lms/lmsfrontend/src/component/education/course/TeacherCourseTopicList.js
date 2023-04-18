import {Card, CardContent} from "@material-ui/core/";
import AddIcon from "@material-ui/icons/Add";
import MaterialTable from "material-table";
import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router";
import {Link, useHistory} from "react-router-dom";
import {authGet} from "../../../api";

function TeacherCourseTopicList(props) {
  const params = useParams();
  const courseId = props.courseId;
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const history = useHistory();
  const [topics, setTopics] = useState([]);

  const columns = [
    {
      title: "Topic Id",
      field: "quizCourseTopicId",
      render: (rowData) => (
        <Link
          to={
            "/edu/teacher/course/topic/detail/" +
            rowData["quizCourseTopicId"] +
            "/" +
            courseId
          }
        >
          {rowData["quizCourseTopicId"]}
        </Link>
      ),
    },
    { title: "Topic Name", field: "quizCourseTopicName" },
    { title: "Topic Name", field: "quizCourseTopicName" },
    { title: "#Published", field: "numberOfPublishedQuizs" },
    { title: "#Private", field: "numberOfPrivateQuizs" },
  ];

  async function getTopicList() {
    let lst = await authGet(
      dispatch,
      token,
      "/get-quiz-course-topics-of-course/" + courseId
    );
    setTopics(lst);
  }

  useEffect(() => {
    getTopicList();
  }, []);

  return (
    <Card>
      <CardContent>
        <MaterialTable
          title={"Topics"}
          columns={columns}
          data={topics}
          actions={[
            {
              icon: () => {
                return <AddIcon color="primary" fontSize="large" />;
              },
              tooltip: "Thêm mới",
              isFreeAction: true,
              onClick: () => {
                history.push("topic/create/" + courseId);
              },
            },
          ]}
        />
      </CardContent>
    </Card>
  );
}

export default TeacherCourseTopicList;
