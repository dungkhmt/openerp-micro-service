import {Box, Button, Checkbox, Typography} from "@material-ui/core";
import {green} from "@material-ui/core/colors";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormGroup from "@material-ui/core/FormGroup";
import {makeStyles, withStyles} from "@material-ui/core/styles";
import parse from "html-react-parser";
import {useEffect, useRef, useState} from "react";
import {request} from "../../../../api";
import TestButton from "./TestButton";

import CreateQuizDoingExplanationDialog from "../../quiztest/quizdoingexplanation/CreateQuizDoingExplanationDialog";
import QuizDoingExplanationDetail from "../../quiztest/quizdoingexplanation/QuizDoingExplanationDetail";
import CommentsOnQuiz from "./CommentsOnQuiz";

const useStyles = makeStyles(() => ({
  testBtn: {},
  wrapper: {
    padding: "24px 0px",
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
  imageContainer: {
    marginTop: "12px",
  },
  imageWrapper: {
    position: "relative",
  },
  imageQuiz: {
    maxWidth: "100%",
  },
  actionContainer: {
    display: "flex",
    marginTop: "12px",
    columnGap: "10px",
  },
  solutionsContainer: {
    marginTop: "16px",
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
export default function Quizz({ quizz, index, classId }) {
  const classes = useStyles();

  const [createExplanationDlgOpen, setCreateExplanationDlgOpen] =
    useState(false);
  const [quizDoingExplanationOpen, setQuizDoingExplanationOpen] =
    useState(false);
  const [result, setResult] = useState({ submited: false, isCorrect: false });
  const [openCommentBox, setOpenCommentBox] = useState(false);
  const [numberComments, setNumberComments] = useState(0);
  const [chkState, setChkState] = useState(() => {
    const isChecked = {};

    quizz.quizChoiceAnswerList.forEach((ans) => {
      isChecked[ans.choiceAnswerId] = false;
    });

    return isChecked;
  });

  const explanationDetailRef = useRef(null);

  function refreshExplanation() {
    if (!explanationDetailRef.current) return;
    explanationDetailRef.current.reload();
  }

  function getNumberComments() {
    request(
      "get",
      "/get-number-comments-on-quiz/" + quizz.questionId,
      (res) => {
        console.log("getNumberComment, res = ", res);
        setNumberComments(res.data);
      }
    );
  }
  useEffect(() => {
    getNumberComments();
  }, []);
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
  function handleClickHideCommentBtn() {
    setOpenCommentBox(false);
  }

  return (
    <div className={classes.wrapper}>
      <Box className={classes.quizzStatement}>
        <Typography component="span">{`Câu ${index + 1}.`}&nbsp;</Typography>
        {parse(quizz.statement)}
      </Box>
      {quizz.attachment &&
        quizz.attachment.length !== 0 &&
        quizz.attachment.map((url, index) => (
          <div key={index} className={classes.imageContainer}>
            <div className={classes.imageWrapper}>
              <img
                src={`data:image/jpeg;base64,${url}`}
                alt="quiz test"
                className={classes.imageQuiz}
              />
            </div>
          </div>
        ))}
      <FormGroup row className={classes.answerWrapper}>
        {quizz.quizChoiceAnswerList.map((answer) => (
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
        ))}
      </FormGroup>

      <div className={classes.actionContainer}>
        <div className={classes.testBtn}>
          <TestButton
            result={result}
            label="Kiểm tra"
            onClick={onClickTestBtn}
          />
        </div>

        {!openCommentBox && (
          <div className={classes.testBtn}>
            <Button variant="outlined" onClick={handleClickCommentBtn}>
              ({numberComments}) Bình luận
            </Button>
          </div>
        )}

        {openCommentBox && (
          <div className={classes.testBtn}>
            <Button variant="outlined" onClick={handleClickHideCommentBtn}>
              Ẩn bình luận
            </Button>
          </div>
        )}

        {!quizDoingExplanationOpen && (
          <div className={classes.testBtn}>
            <Button
              variant="outlined"
              onClick={() => setQuizDoingExplanationOpen(true)}
            >
              Xem cách làm
            </Button>
          </div>
        )}

        {quizDoingExplanationOpen && (
          <div className={classes.testBtn}>
            <Button
              variant="outlined"
              onClick={() => setQuizDoingExplanationOpen(false)}
            >
              Ẩn cách làm
            </Button>
          </div>
        )}

        <div className={classes.testBtn}>
          <Button
            variant="outlined"
            onClick={() => setCreateExplanationDlgOpen(true)}
          >
            Thêm cách làm
          </Button>
        </div>
      </div>

      {quizDoingExplanationOpen && (
        <div className={classes.solutionsContainer}>
          <Typography variant="h6">
            Các cách làm đã tạo cho câu hỏi này
          </Typography>
          <QuizDoingExplanationDetail
            questionId={quizz.questionId}
            ref={explanationDetailRef}
          />
        </div>
      )}

      {openCommentBox && (
        <div>
          <CommentsOnQuiz
            questionId={quizz.questionId}
            open={openCommentBox}
            setOpen={setOpenCommentBox}
          />
        </div>
      )}

      <CreateQuizDoingExplanationDialog
        questionId={quizz.questionId}
        open={createExplanationDlgOpen}
        onClose={() => setCreateExplanationDlgOpen(false)}
        onCreateSuccess={refreshExplanation}
      />
    </div>
  );
}
