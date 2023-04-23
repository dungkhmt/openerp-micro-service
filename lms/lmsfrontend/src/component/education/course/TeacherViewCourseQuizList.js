import {Box, Paper, Typography} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import {Skeleton} from "@material-ui/lab";
import {Fragment, useEffect, useState} from "react";
import {request} from "../../../api";
import TeacherViewQuizDetail from "./TeacherViewQuizDetail";

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

function TeacherViewCourseQuizList({ courseId }) {
  const classes = useStyles();

  const [quizzList, setQuizList] = useState([]);
  const [loading, setLoading] = useState(true);

  const getQuizListOfClass = () => {
    request(
      // token,
      // history,
      "get",
      //`/get-unpublished-quiz-of-course/${courseId}`,
      `/get-quiz-of-course/${courseId}`,
      (res) => {
        console.log("getQuizListOfClass, res.data = ", res.data);
        setQuizList(res.data);
        setLoading(false);
      },
      {
        onError: () => {
          setLoading(false);
        },
      }
    );
  };

  useEffect(() => {
    getQuizListOfClass();
  }, []);

  return (
    <Paper elevation={8}>
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
            quizzList.map((quizz, index) => (
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
  );
}

export default TeacherViewCourseQuizList;
