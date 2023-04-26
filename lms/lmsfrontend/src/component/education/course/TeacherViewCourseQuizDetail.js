import {Box, Button, Checkbox, Typography} from "@material-ui/core";
import {green} from "@material-ui/core/colors";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormGroup from "@material-ui/core/FormGroup";
import {makeStyles, withStyles} from "@material-ui/core/styles";
import parse from "html-react-parser";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import {request} from "../../../api";

import CommentsOnQuiz from "../classmanagement/student/CommentsOnQuiz";
import TestButton from "../classmanagement/student/TestButton";
import QuizTestUsingAQuestion from "../quiztest/QuizTestUsingAQuestion";

const useStyles = makeStyles(() => ({
  testBtn: {
    marginLeft: 40,
    marginTop: 32,
  },
  wrapper: {
    padding: "32px 0px",
  },
  answerWrapper: {
    "& label": {
      "&>:nth-child(2)": {
        display: "inline-block",
        "& p": {
          margin: 0,
          textAlign: "justify",
        },
      },
    },
  },
  answer: {
    width: "100%",
    marginTop: 20,
  },
  quizzStatement: {
    fontSize: "1rem",
    "&>p:first-of-type": {
      display: "inline",
    },
  },
}));

/**
 * Customized checkbox.
 */
const GreenCheckbox = withStyles({
  root: {
    color: green[400],
    "&$checked": {
      color: green[600],
    },
    paddingLeft: 20,
    paddingTop: 0,
    paddingBottom: 0,
  },
  checked: {},
})((props) => <Checkbox color="default" disableRipple {...props} />);

/**
 * Describe a multiple-choice quizz.
 * @returns
 */

export default function TeacherViewCourseQuizDetail() {
  const params = useParams();
  const questionId = params.questionId;
  const courseId = params.courseId;
  const [quizz, setQuizz] = useState(null);

  function getQuizDetail() {
    request("get", "/get-quiz-question-detail/" + questionId, (res) => {
      console.log("get quiz questino detail, res = ", res);
      setQuizz(res.data);

      let isChecked = {};

      quizz.quizChoiceAnswerList.forEach((ans) => {
        isChecked[ans.choiceAnswerId] = false;
      });

      setChkState(isChecked);
    });
  }
  useEffect(() => {
    getQuizDetail();
  }, []);

  const classes = useStyles();

  const [result, setResult] = useState({ submited: false, isCorrect: false });
  const [openCommentBox, setOpenCommentBox] = useState(false);
  const [chkState, setChkState] = useState([]);

  const handleChange = (event) => {
    setChkState({ ...chkState, [event.target.name]: event.target.checked });
  };

  const onClickTestBtn = () => {
    setResult({ ...result, submited: false });
    const chooseAnsIds = [];

    for (var id in chkState) {
      if (chkState[id] === true) {
        chooseAnsIds.push(id);
      }
    }

    if (chooseAnsIds.length > 0) {
      request(
        // token,
        // history,
        "post",
        "/quiz-choose_answer",
        (res) => {
          setResult({ submited: true, isCorrect: res.data });
        },
        {},
        {
          questionId: quizz.questionId,
          chooseAnsIds: chooseAnsIds,
          classId: null,
        }
      );
    }
  };

  function handleClickCommentBtn() {
    setOpenCommentBox(true);
  }
  function handleClickHideCommentBtn() {
    setOpenCommentBox(false);
  }
  return (
    <div className={classes.wrapper}>
      <Box className={classes.quizzStatement}>
        <Typography component="span">{`Câu ${1}.`}&nbsp;</Typography>
        {quizz ? parse(quizz.statement) : ""}
      </Box>
      <FormGroup row className={classes.answerWrapper}>
        {quizz
          ? quizz.quizChoiceAnswerList.map((answer) => (
              <FormControlLabel
                key={answer.choiceAnswerId}
                className={classes.answer}
                control={
                  <GreenCheckbox
                    checked={chkState[answer.choiceAnswerId]}
                    onChange={handleChange}
                    name={answer.choiceAnswerId}
                  />
                }
                label={parse(answer.choiceAnswerContent)}
              />
            ))
          : ""}
        {openCommentBox ? (
          <CommentsOnQuiz
            questionId={quizz.questionId}
            open={openCommentBox}
            setOpen={setOpenCommentBox}
          />
        ) : (
          ""
        )}
        <div className={classes.testBtn}>
          <TestButton
            result={result}
            label="Kiểm tra"
            onClick={onClickTestBtn}
          />
        </div>
        {!openCommentBox ? (
          <div className={classes.testBtn}>
            <Button variant="outlined" onClick={handleClickCommentBtn}>
              Bình luận
            </Button>
          </div>
        ) : (
          ""
        )}
        {openCommentBox ? (
          <div className={classes.testBtn}>
            <Button variant="outlined" onClick={handleClickHideCommentBtn}>
              Ẩn Bình luận
            </Button>
          </div>
        ) : (
          ""
        )}
      </FormGroup>
      <QuizTestUsingAQuestion questionId={questionId} />
    </div>
  );
}
