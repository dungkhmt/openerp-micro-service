import React, {Fragment, useEffect, useState} from "react";
import {useParams} from "react-router";
import {Link} from "react-router-dom";
import {request} from "../../../api";
import TeacherViewQuizDetail from "./TeacherViewQuizDetail";
import {Box, Paper, Typography} from "@material-ui/core";
import {Skeleton} from "@material-ui/lab";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles(() => ({
  titleContainer: {
    color: "white",
    backgroundColor: "#03787C",
    padding: 50,
  },
  title: { fontSize: "2.375rem" },
  body: {
    padding: "20px 30px",
  },
  quizzList: {
    padding: "0px 20px",
  },
  skeletonAnswer: {
    marginLeft: 20,
    marginTop: 20,
  },
}));

export default function TeacherCourseTopicDetail() {
  const params = useParams();
  const quizCourseTopicId = params.quizCourseTopicId;
  const courseId = params.courseId;
  const classes = useStyles();
  const [quizList, setQuizList] = useState([]);
  const [loading, setLoading] = useState(true);

  function getQuizListOfTopic() {
    request("get", `get-quiz-of-course-topic/${quizCourseTopicId}`, (res) => {
      console.log("getQuizListOfTopic, res.data = ", res.data);
      setQuizList(res.data);
      setLoading(false);
    });
  }
  useEffect(() => {
    getQuizListOfTopic();
  }, []);
  return (
    <div>
      <Paper elevation={8}>
        <div>
          <Link to={"/edu/course/detail/" + courseId}>
            QUAY VỀ CHI TIẾT MÔN
          </Link>
        </div>

        <div>
          <Box
            display="flex"
            alignItems="center"
            flexDirection="row"
            className={classes.titleContainer}
          >
            <Typography component="span" className={classes.title}>
              Câu hỏi trắc nghiệm
            </Typography>
          </Box>
        </div>
        <div className={classes.body}>
          <div className={classes.quizzList}>
            {loading ? (
              <Fragment>
                <Skeleton
                  variant="rect"
                  width={800}
                  height={24}
                  animation="wave"
                />
                <Skeleton
                  className={classes.skeletonAnswer}
                  variant="rect"
                  width={200}
                  height={24}
                  animation="wave"
                />
                <Skeleton
                  className={classes.skeletonAnswer}
                  variant="rect"
                  width={600}
                  height={24}
                  animation="wave"
                />
                <Skeleton
                  className={classes.skeletonAnswer}
                  variant="rect"
                  width={400}
                  height={24}
                  animation="wave"
                />
                <Skeleton
                  className={classes.skeletonAnswer}
                  variant="rect"
                  width={700}
                  height={24}
                  animation="wave"
                />
              </Fragment>
            ) : (
              quizList.map((quizz, index) => (
                <TeacherViewQuizDetail
                  quiz={quizz}
                  index={index}
                  courseId={courseId}
                />
              ))
            )}
          </div>
        </div>
      </Paper>
    </div>
  );
}
