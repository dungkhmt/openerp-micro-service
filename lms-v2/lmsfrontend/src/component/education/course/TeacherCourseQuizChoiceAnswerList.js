import {Typography} from "@material-ui/core/";
import AddIcon from "@material-ui/icons/Add";
import parse from "html-react-parser";
import MaterialTable from "material-table";
import {useEffect, useState} from "react";
import {Link, useHistory} from "react-router-dom";
import {request} from "../../../api";

// const useStyles = makeStyles((theme) => ({
//   card: {
//     marginTop: theme.spacing(2),
//     borderRadius: "6px",
//   },
//   registrationBtn: {},
// }));

function TeacherCourseQuizChoiceAnswerList(props) {
  // const classes = useStyles();
  const questionId = props.questionId;
  const courseId = props.courseId;

  const history = useHistory();
  const [choiceAnswers, setChoiceAnswers] = useState([]);

  const columns = [
    {
      title: "choiceAnswerId",
      field: "choiceAnswerId",
      render: (rowData) => (
        <Link
          to={
            "/edu/teacher/course/quiz/choiceanswer/detail/" +
            rowData["choiceAnswerId"]
          }
        >
          {rowData["choiceAnswerId"]}
        </Link>
      ),
    },
    {
      title: "Content",
      field: "choiceAnswerContent",
      render: (rowData) => (
        <Typography>{parse(rowData.choiceAnswerContent)}</Typography>
      ),
    },
    { title: "isCorrectAnswer", field: "isCorrectAnswer" },
  ];

  async function getChoiceAnswerList() {
    request("get", "/get-quiz-choice-answer-of-a-quiz/" + questionId, (res) => {
      const lst = res.data;
      setChoiceAnswers(lst);
      console.log("getCHoiceAnsweList, questionId = " + questionId);
    });
  }

  useEffect(() => {
    getChoiceAnswerList();
  }, []);

  return (
    <MaterialTable
      title={"Choice Answer"}
      columns={columns}
      data={choiceAnswers}
      actions={[
        {
          icon: () => {
            return <AddIcon color="primary" fontSize="large" />;
          },
          tooltip: "Thêm mới",
          isFreeAction: true,
          onClick: () => {
            history.push(
              "/edu/course/detail/quiz/choiceanswer/create/" +
                questionId +
                "/" +
                courseId
            );
          },
        },
      ]}
    />
  );
}

export default TeacherCourseQuizChoiceAnswerList;
