import {useState} from "@hookstate/core";
//import Button from "@material-ui/core/Button";
import Checkbox from "@material-ui/core/Checkbox";
// import { green } from "@material-ui/core/colors";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import {makeStyles} from "@material-ui/core/styles";
// import CheckCircleOutlineRoundedIcon from "@material-ui/icons/CheckCircleOutlineRounded";
//import { Typography } from "@mui/material";
import ReactHtmlParser from "react-html-parser";

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  paper: {
    padding: theme.spacing(2),
    textAlign: "left",
    //color: theme.palette.text.secondary,
    color: "black",
    borderRadius: "20px",
    //background: "beige",
    background: "#EBEDEF",
  },
  ansWrapper: {
    "& p": {
      margin: 0,
    },
  },
}));

export default function QuizViewForCheckCode({
  question,
  order,
  choseAnswers,
}) {
  const classes = useStyles();
  const {
    questionId,
    statement,
    attachment,
    quizChoiceAnswerList: choices,
  } = question;

  // Keep track of checking state of all choices
  const checkState = useState(choseAnswers);

  //

  return (
    <Grid item xs={12} key={questionId}>
      <Paper className={classes.paper}>
        <div className={classes.root}>
          <h4>Question {order + 1}.</h4> {ReactHtmlParser(statement)}
          {attachment &&
            attachment.length !== 0 &&
            attachment.map((url, index) => (
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
          <>
            {choices.map((ans) => (
              <div
                className={classes.ansWrapper}
                key={ans.choiceAnswerId}
                style={{ display: "flex", alignItems: "center" }}
              >
                <Checkbox
                  color="primary"
                  key={ans.choiceAnswerId}
                  //checked={checkState[ans.choiceAnswerId].get()}
                  //onChange={handleChange}
                  name={ans.choiceAnswerId}
                  inputProps={{
                    "aria-label": "secondary checkbox",
                  }}
                  style={{ maxHeight: 42 }}
                />
                {ReactHtmlParser(ans.choiceAnswerContent)}
              </div>
            ))}
          </>
        </div>
      </Paper>
    </Grid>
  );
}
