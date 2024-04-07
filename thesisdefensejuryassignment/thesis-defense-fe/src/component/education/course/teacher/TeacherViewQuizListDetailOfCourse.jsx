import {Box, Paper, Typography} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import {Skeleton} from "@material-ui/lab";
import React, {Fragment, useEffect, useState} from "react";
import TeacherViewQuizDetail from "../TeacherViewQuizDetail";
import {request} from "../../../../api";
import {errorNoti} from "../../../../utils/notification";

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

export default function TeacherViewQuizListDetailOfCourse({ courseId }) {
  const classes = useStyles();
  const [quizzList, setQuizzList] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(getQuizListOfClass, []);

  function getQuizListOfClass() {
    let successHandler = res => {
      setQuizzList(res.data);
      setLoading(false);
    }
    let errorHandlers = {
      onError: () => {
        errorNoti("Đã xảy ra lỗi khi tải dữ liệu", true);
        setLoading(false);
      }
    }
    request("GET", `/get-quiz-of-course/${courseId}`,successHandler, errorHandlers);
  }

  const loadingSkeletonWidths = [800, 200, 600, 400, 700]

  return (
    <Paper elevation={8}>
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

      <div className={classes.body}>
        <div className={classes.quizzList}>
          { loading && (
            <Fragment>
              { loadingSkeletonWidths.map(skeletonWidth => (
                <Skeleton variant="rect"
                          width={skeletonWidth}
                          height={24}
                          animation="wave"/>
              ))}
            </Fragment>
          )}
          { !loading && (
            quizzList.map((quizz, index) => (
              <TeacherViewQuizDetail quiz={quizz}
                                     index={index}
                                     courseId={courseId}/>
            ))
          )}
        </div>
      </div>
    </Paper>
  );
}
