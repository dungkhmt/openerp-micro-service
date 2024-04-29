import { Box, Paper, Typography } from "@material-ui/core";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import { Skeleton } from "@material-ui/lab";
import { Fragment, useEffect, useState } from "react";
import { request } from "../../../api";
//import TeacherViewQuizDetail from "../course/TeacherViewQuizDetail";
import TeacherViewQuizDetailForAssignment from "./TeacherViewQuizDetailForAssignment";
import {
  Button,
  Chip,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
} from "@mui/material";
import { errorNoti } from "utils/notification";

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
  selectBox: {
    padding: 20,
    minWidth: 150,
    marginRight: 40,
    height: 60,
  },
}));

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

function getStyles(tag, tags, theme) {
  return {
    fontWeight:
      tags.indexOf(tag) === -1
        ? theme.typography.fontWeightRegular
        : theme.typography.fontWeightMedium,
  };
}

function QuizListForAssignment({ testId, courseInfo }) {
  const classes = useStyles();

  const [quizList, setQuizList] = useState([]);
  const [quizGroups, setQuizGroups] = useState();
  const [loading, setLoading] = useState(false);
  const [tags, setTags] = useState([]);
  const [chooseTags, setChooseTags] = useState([]);
  const theme = useTheme();

  const courseId = courseInfo.id;

  const handleChange = (event) => {
    const {
      target: { value },
    } = event;
    setChooseTags(
      // On autofill we get a stringified value.
      typeof value === "string" ? value.split(",") : value
    );
  };
  //
  const getQuizListOfClass = () => {
    setLoading(true);
    request(
      // token,
      // history,
      "get",
      `/get-list-quiz-for-assignment-of-test/${testId}`,
      (res) => {
        // console.log("getQuizListOfClass, res.data = ", res.data);
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

  const getListTagOfCourse = () => {
    request("GET", `/get-tags-of-course/${courseId}`, (res) =>
      setTags(res.data.map((item) => item.tagName))
    );
  };

  const handleFilterQuiz = () => {
    setLoading(true);
    let successHandler = (res) => {
      setQuizList(res.data);
      setLoading(false);
    };
    let errorHandlers = {
      onError: () => {
        errorNoti("Đã xảy ra lỗi khi tải dữ liệu", true);
        setLoading(false);
      },
    };
    request(
      "GET",
      `/get-questions-of-course-by-tags?courseId=${courseId}&tags=${chooseTags.join(
        ","
      )}`,
      successHandler,
      errorHandlers
    );
  };

  const getQuizGroup = () => {
    request(
      // token,
      // history,
      "get",
      `/get-test-groups-info?testId=${testId}`,
      (res) => {
        setQuizGroups(res.data);
      }
    );
  };

  useEffect(() => {
    // getQuizListOfClass();
    getQuizGroup();
    getListTagOfCourse();
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
          <InputLabel id="demo-multiple-name-label">
            Lọc câu hỏi theo Tags
          </InputLabel>
          <Select
            labelId="demo-multiple-chip-label"
            id="demo-multiple-chip"
            multiple
            required
            value={chooseTags}
            onChange={handleChange}
            className={classes.selectBox}
            input={<OutlinedInput id="select-multiple-chip" label="Chip" />}
            placeholder="Tags"
            renderValue={(selected) => (
              <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
                {selected.map((value) => (
                  <Chip key={value} label={value} />
                ))}
              </Box>
            )}
            MenuProps={MenuProps}
          >
            {tags.map((tag) => (
              <MenuItem
                key={tag}
                value={tag}
                style={getStyles(tag, tags, theme)}
              >
                {tag}
              </MenuItem>
            ))}
          </Select>
          <Button
            variant="contained"
            color="secondary"
            onClick={handleFilterQuiz}
          >
            Lọc
          </Button>
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
            quizList.map((quiz, index) => (
              <TeacherViewQuizDetailForAssignment
                key={quiz.questionId}
                quiz={quiz}
                index={index}
                testId={testId}
                quizGroups={quizGroups}
              />
            ))
          )}
        </div>
      </div>
    </Paper>
  );
}

export default QuizListForAssignment;
