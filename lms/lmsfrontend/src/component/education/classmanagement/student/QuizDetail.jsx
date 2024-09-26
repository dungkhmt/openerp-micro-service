import {Box, Button, Checkbox, Typography} from "@material-ui/core";
import {green} from "@material-ui/core/colors";
import FormGroup from "@material-ui/core/FormGroup";
import {makeStyles, withStyles} from "@material-ui/core/styles";
import parse from "html-react-parser";
import {useState} from "react";
import {request} from "../../../../api";
import CommentsOnQuiz from "./CommentsOnQuiz";
import TestButton from "./TestButton";

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
export default function QuizDetail({ quizz, index, classId }) {
  const classes = useStyles();

  const [result, setResult] = useState({ submited: false, isCorrect: false });
  const [openCommentBox, setOpenCommentBox] = useState(false);

  const [chkState, setChkState] = useState(() => {
    const isChecked = {};

    if (quizz != null) {
      if (quizz.quizChoiceAnswerList != null) {
        quizz.quizChoiceAnswerList.forEach((ans) => {
          isChecked[ans.choiceAnswerId] = false;
        });
      }
    }

    return isChecked;
  });

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
          classId: classId,
        }
      );
    }
  };

  function handleClickCommentBtn() {
    setOpenCommentBox(true);
  }
  return (
    <div className={classes.wrapper}>
      <Box className={classes.quizzStatement}>
        <Typography component="span">{`Câu ${index + 1}.`}&nbsp;</Typography>
        {parse(quizz.statement)}
      </Box>
      <FormGroup row className={classes.answerWrapper}>
        <div className={classes.testBtn}>
          <TestButton
            result={result}
            label="Kiểm tra"
            onClick={onClickTestBtn}
          />
        </div>
        <div className={classes.testBtn}>
          <Button variant="outlined" onClick={handleClickCommentBtn}>
            Bình luận
          </Button>
        </div>
      </FormGroup>
      <CommentsOnQuiz
        questionId={quizz.questionId}
        open={openCommentBox}
        setOpen={setOpenCommentBox}
      />
    </div>
  );
}
