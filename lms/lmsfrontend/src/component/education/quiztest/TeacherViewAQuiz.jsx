import {useState} from "@hookstate/core";
import Checkbox from "@material-ui/core/Checkbox";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import {makeStyles} from "@material-ui/core/styles";
import React from "react";
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

export default function TeacherViewAQuiz({
  question,
  order,
  choseAnswers,
  onSave,
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
  const handleChange = (event) => {
    checkState[event.target.name].set(event.target.checked);
  };

  const handleSubmit = () => {
    const choseAnswers = [];

    choices.forEach(({ choiceAnswerId }) => {
      if (checkState[choiceAnswerId].get()) {
        choseAnswers.push(choiceAnswerId);
      }
    });

    onSave(order, questionId, choseAnswers);
  };
  /*
  console.log(
    "load, checkState ",
    checkState,
    " order = ",
    order,
    "question = ",
    question
  );
  */
  return (
    <Grid item xs={12} key={questionId}>
      <Paper className={classes.paper}>
        <div className={classes.root}>
          <h4>Câu hỏi {order + 1}.</h4> {ReactHtmlParser(statement)}
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
          {/* Answers */}
          {checkState.get() && (
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
                    checked={checkState[ans.choiceAnswerId].get()}
                    onChange={handleChange}
                    name={ans.choiceAnswerId}
                    inputProps={{
                      "aria-label": "secondary checkbox",
                    }}
                    style={{ maxHeight: 42 }}
                  />
                  {ReactHtmlParser(ans.choiceAnswerContent)}
                </div>
              ))}

              {/*
              <div style={{ textAlign: "right" }}>
                {checkState.submitted.get() ? (
                  <Button
                    variant="contained"
                    color="primary"
                    endIcon={<CheckCircleOutlineRoundedIcon />}
                    onClick={handleSubmit}
                    style={{
                      textTransform: "none",
                      backgroundColor: green[800],
                    }}
                  >
                    Đã lưu
                  </Button>
                ) : (
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSubmit}
                    style={{
                      textTransform: "none",
                    }}
                  >
                    Lưu
                  </Button>
                              )}
                
                          </div>
                          */}
            </>
          )}
        </div>
      </Paper>
    </Grid>
  );
}
