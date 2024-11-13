import { Box, Paper, Typography } from "@material-ui/core";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import { Skeleton } from "@material-ui/lab";
import React, { Fragment, useEffect, useState } from "react";
import TeacherViewQuizDetail from "../TeacherViewQuizDetail";
import { request } from "../../../../api";
import { errorNoti } from "../../../../utils/notification";
import {
  Autocomplete,
  Button,
  Chip,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
  TextField,
} from "@mui/material";

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
  wrapper: {
    padding: 32,
  },
  subTitle: {
    fontSize: 20,
    fontWeight: 600,
    marginBottom: 16,
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

export default function TeacherViewQuizLibrary({ courseId }) {
  const theme = useTheme();
  const classes = useStyles();
  const [tags, setTags] = useState([]);
  const [chooseTags, setChooseTags] = useState([]);

  const [quizzList, setQuizzList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [coursePool, setCoursePool] = useState([]);
  const [chooseCourse, setChooseCourse] = useState(null);

  useEffect(() => {
    getAllCourses();
  }, []);

  function getListTagOfCourse(courseId) {
    setLoading(true);
    let successHandler = (res) => {
      setTags(res.data.map((item) => item.tagName));
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
      `/get-tags-of-course/${courseId}`,
      successHandler,
      errorHandlers
    );
  }

  const getAllCourses = () => {
    request(
      "get",
      "/edu/class/get-all-courses",
      (response) => {
        response = response.data;
        setCoursePool(
          response.map((item) => ({
            label: `${item.id} - ${item.name}`,
            id: item.id,
          }))
        );
      },
      {
        onError: (error) => {
          setCoursePool([]);
        },
      }
    );
  };

  const handleFilterQuiz = () => {
    setLoading(true);
    let successHandler = (res) => {
      setQuizzList(res.data);
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
      `/get-questions-of-course-by-tags?courseId=${chooseCourse}&tags=${chooseTags.join(
        ","
      )}`,
      successHandler,
      errorHandlers
    );
  };

  const onCourseChange = (event, value) => {
    setChooseTags([]);
    setTags([]);
    if (value && value.id) {
      setChooseCourse(value.id);
      getListTagOfCourse(value.id);
    }
    console.log(value);
  };

  const handleChange = (event) => {
    const {
      target: { value },
    } = event;
    setChooseTags(
      // On autofill we get a stringified value.
      typeof value === "string" ? value.split(",") : value
    );
  };

  const loadingSkeletonWidths = [800, 200, 600, 400, 700];

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

      <Box className={classes.wrapper}>
        <Typography component="span" className={classes.subTitle}>
          Chọn course
        </Typography>
        <InputLabel id="demo-multiple-name-label">Courses</InputLabel>
        <Autocomplete
          sx={{ width: 300 }}
          options={coursePool}
          renderInput={(params) => <TextField {...params} label="Course" />}
          onChange={onCourseChange}
        />
      </Box>

      <Box className={classes.wrapper}>
        <Typography component="span" className={classes.subTitle}>
          Lọc câu hỏi theo các tags
        </Typography>
        <InputLabel id="demo-multiple-name-label">Tags</InputLabel>
        <Select
          label="tags"
          id="demo-multiple-chip"
          multiple
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
            <MenuItem key={tag} value={tag} style={getStyles(tag, tags, theme)}>
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
      </Box>
      <div className={classes.body}>
        <div className={classes.quizzList}>
          {loading && (
            <Fragment>
              {loadingSkeletonWidths.map((skeletonWidth) => (
                <Skeleton
                  variant="rect"
                  width={skeletonWidth}
                  height={24}
                  animation="wave"
                />
              ))}
            </Fragment>
          )}
          {!loading &&
            quizzList.map((quizz, index) => (
              <TeacherViewQuizDetail
                quiz={quizz}
                index={index}
                courseId={courseId}
              />
            ))}
        </div>
      </div>
    </Paper>
  );
}
