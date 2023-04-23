import DateFnsUtils from "@date-io/date-fns";
import {Button, Card, Fab, Grid, MenuItem, Select, TextField, Tooltip,} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import {KeyboardArrowDown} from "@material-ui/icons";
import DeleteIcon from "@material-ui/icons/Delete";
import KeyboardArrowUpIcon from "@material-ui/icons/KeyboardArrowUp";
import {KeyboardDatePicker, KeyboardTimePicker, MuiPickersUtilsProvider,} from "@material-ui/pickers";
import {useEffect, useState} from "react";
import ReactHtmlParser from "react-html-parser";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";
import CreateNewQuestionPopup from "../quiztest/CreateNewQuestionPopup";

const styles = {
  label: {
    fontSize: "20px",
    fontWeight: "lighter",
  },

  descStyle: {
    fontSize: "20px",
    fontWeight: "lighter",
  },

  ansStyle: {
    fontSize: "18px",
    fontWeight: "lighter",
    paddingTop: "15px",
    paddingBottom: "30px",
    paddingLeft: "30px",
  },

  subAnsStyle: {
    fontSize: "18px",
    fontWeight: "lighter",
    paddingBottom: "10px",
  },
};

const nextLine = <pre></pre>;

const lineBreak = <pre style={{ userSelect: "none" }}> </pre>;

const useStyles = makeStyles((theme) => ({
  input: {
    "&::placeholder": {
      color: "rgb(0,0,0,1)",
      textAlign: "left",
      fontStyle: "italic",
    },
  },
}));

const questionList = [
  {
    desc: "Con gì đi bằng 4 chân ????",
    ans: [
      ["ans 1", true],
      ["ans 2", false],
      ["ans 3", false],
      ["ans 4", true],
    ],
    questionID: "",
  },
  {
    desc: "asdkasjdlaksjdlaksjd ajldjka lsdpiaops dakjs ldajsdj aqipour pe rqoer qald klanvlaksjd[iqpori[wer ",
    ans: [
      ["ans 1", true],
      ["ans 2", false],
      ["ans 3", false],
      ["ans 4", true],
    ],
    questionID: "",
  },
  {
    desc: "Ui zoi ooi ????",
    ans: [
      ["ans 1", true],
      ["ans 2", false],
      ["ans 3", false],
      ["ans 4", true],
    ],
    questionID: "",
  },
  {
    desc: "934267331140027631584353097282653335534382337833447427495835627332705111928881836050159\
98809153133068051750737022122660506160462551451922361393320990081676590883686042637397276251780805820352\
940173179689386638153898833678754624972661773135558718878205518933234942239373008835054962942826377385599\
582202213330335123693752677691816825911416795078138349847751061762702325982312721627137173091072243398556\
893646984644262820699562506263484587799703974003088626069230789248987258709258382751900810958582206669983\
6392121025323560461045131230146549986250690590997903622826027916102320413561411609088782519054802515923278\
23822019605072128121489849100082346688054458553821314213872330483269068030790969669226848027716812687163843\
400577267876298509580112361464246343754700037371994528212776284197984661855353405675850634261689917447152951\
2912155382351233769839700793371646146594970219217923906938012326346474898036564031960495301003651 is <strong>a prime number</strong>????",
    ans: [
      ["ans 1", true],
      ["ans 2", false],
      ["ans 3", false],
      ["ans 4", true],
    ],
    questionID: "",
  },
];

function useForceUpdate() {
  const [value, setValue] = useState(0); // integer state
  return () => setValue((value) => value + 1); // update the state to force render
}

let isInPage1 = false;
function goToBottom() {
  //isInPage1 = false;
  window.setTimeout(() => {
    isInPage1 = false;
    window.scrollTo({
      top: document.body.scrollHeight /* , behavior: 'smooth' */,
    });
    return " ";
  }, 1);
}

function CreateQuizTest() {
  const [listCourse, setListCourse] = useState([]);

  const [classMap, setClassMap] = useState({});

  const [listClassBelongCourse, setListClass] = useState([]);

  const [duration, setDuration] = useState(90);

  const [testId, setTestId] = useState("");

  const [quizName, setQuizName] = useState("");
  const [note, setNote] = useState("");
  const [selectedCourse, setSelectedCourse] = useState(" ");
  const [selectedDate, setSelectedDate] = useState(new Date());

  const [selectedClass, setSelectedClass] = useState(" ");

  const [clicked, setClicked] = useState(false);
  const [clicked2, setClicked2] = useState(false);

  const [page, setPage] = useState(0);

  const history = useHistory();

  const forceUpdate = useForceUpdate();

  async function getListCourse() {
    request("get", "/edu/class/get-all-courses", (res) => {
      res = res.data;

      let temp = [];
      res.map((elm) => {
        temp.push({
          id: elm.id,
          value: elm.id + " - " + elm.name + " (" + elm.credit + " tín chỉ)",
        });
      });
      setListCourse(temp);
      //console.log(temp);
    });
  }

  async function getClassList() {
    request(
      // token, history,
      "get",
      "/edu/class/list/teacher",
      (res) => {
        //console.log(res.data)
        res.data.map((elm, index) => {
          if (elm["courseId"] in classMap) {
            classMap[elm["courseId"]].push(elm);
          } else {
            classMap[elm["courseId"]] = [elm];
          }
        });

        //console.log(classMap)
      }
    );
  }

  useEffect(() => {
    if (page == 0) getListCourse();
    getClassList();
    return () => {
      //console.log("CreateQuizTest unmounted !!!");
    };
  }, []);

  const handleChangePage = (e) => {
    if (e === 0) {
      isInPage1 = true;
      setPage(page + 1);
    } else {
      isInPage1 = true;
      setPage(page - 1);
    }
  };

  const handleChangeTestId = (event) => {
    setTestId(event.target.value);
  };

  const handleChangeQuizName = (event) => {
    setQuizName(event.target.value);
  };

  const handleChangeNote = (event) => {
    setNote(event.target.value);
  };

  const handleChangeDuration = (e) => {
    setDuration(e.target.value);
  };

  const handleChange = (event) => {
    setSelectedCourse(event.target.value);
    if (event.target.value !== " ") {
      if (event.target.value in classMap) {
        setListClass(classMap[event.target.value]);
        setClicked2(false);
        setSelectedClass(" ");
      } else {
        setListClass([]);
        setClicked2(false);
        setSelectedClass(" ");
        //alert("Học phần bạn chọn không có lớp nào !!!!");
      }
    }
  };

  const handleChangeClass = (event) => {
    setSelectedClass(event.target.value);
  };

  const handleDateChange = (event) => {
    setSelectedDate(event);
  };

  const handleDelete = (index) => {
    questionList.splice(index, 1);
    forceUpdate();
  };

  const handleSwapQuestion = (e, index, code) => {
    if (code === "up") {
      let newIndex = Math.max(index - e.detail, 0);
      let temp = questionList[index];
      questionList[index] = questionList[newIndex];
      questionList[newIndex] = temp;
      //console.log(newIndex)
    } else if (code === "down") {
      let newIndex = Math.min(index + e.detail, questionList.length - 1);
      let temp = questionList[index];
      questionList[index] = questionList[newIndex];
      questionList[newIndex] = temp;
      //console.log(newIndex)
    }

    forceUpdate();
  };

  const classes = useStyles();

  const addQuestion = (input) => {
    questionList.push(input);
    forceUpdate();
  };

  async function handleSubmit() {
    //console.log(quizName)
    //console.log(listCourse)
    //console.log(selectedCourse)
    //console.log(selectedDate)

    if (!testId || testId.length == 0) {
      alert("Mã kỳ thi không được bỏ trống");
      return;
    }

    if (!quizName || quizName.length == 0) {
      alert("Tên kỳ thi không được bỏ trống");
      return;
    }

    if (
      !selectedCourse ||
      selectedCourse == " " ||
      selectedCourse.length == 0
    ) {
      alert("Hãy chọn học phần");
      return;
    }

    if (!selectedClass || selectedClass == " " || selectedClass.length == 0) {
      alert("Hãy chọn mã lớp");
      return;
    }

    if (!duration) {
      alert("Thời gian làm bài không được để trống");
      return;
    }

    let body = {
      testId: testId,
      testName: quizName,
      scheduleDatetime: selectedDate,
      duration: duration,
      courseId: selectedCourse,
      classId: selectedClass,
    };

    let formData = new FormData();
    formData.append("QuizTestCreateInputModel", JSON.stringify(body));

    /* for (var pair of formData.entries()) {
            console.log(pair[0]+ ', ' + pair[1]); 
        } */

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/create-quiz-test",
      (res) => {
        history.push("/edu/class/quiztest/list");
      },
      {},
      formData,
      config
    );

    //await authPost(dispatch, token, "/create-quiz-test", formData);
  }

  return (
    <Grid container spacing={1} justify="center">
      <Card
        style={{
          padding: "3% 10% 7% 10%",
          minWidth: "1024px",
        }}
      >
        <h1 style={{ fontWeight: "normal", padding: "2% 2% 2% 0" }}>
          Tạo mới kỳ thi trắc nghiệm
        </h1>
        {/* <ColoredLine color="blue" /> */}
        <div
          style={{
            borderTop: "3px double rgb(0, 110, 227)",
            marginLeft: "-11%",
            marginRight: "-11%",
          }}
        ></div>
        {lineBreak}
        {page == 0 && (
          <form noValidate autoComplete="off">
            <div style={styles.label}>Mã kỳ thi </div>
            {nextLine}
            <TextField
              label=""
              placeholder="Mã kỳ thi *"
              fullWidth={true}
              InputProps={{ classes: { input: classes.input } }}
              onChange={handleChangeTestId}
            />
            {lineBreak}

            <div style={styles.label}>Tên kỳ thi trắc nghiệm </div>
            {nextLine}
            <TextField
              label=""
              placeholder="Tên kỳ thi trắc nhiệm *"
              fullWidth={true}
              InputProps={{ classes: { input: classes.input } }}
              onChange={handleChangeQuizName}
            />
            {lineBreak}

            <div style={styles.label}>Học phần </div>
            {nextLine}
            <Select
              value={selectedCourse}
              onChange={handleChange}
              fullWidth={true}
              onOpen={(e) => {
                setClicked(true);
              }}
              onClose={(e) => {
                if (selectedCourse == " ") setClicked(false);
              }}
            >
              {!clicked && (
                <MenuItem value=" " key=" ">
                  <em style={{ color: "rgb(0,0,0,0.5)" }}>Chọn học phần</em>
                </MenuItem>
              )}
              {listCourse.map((option) => (
                <MenuItem key={option.id} value={option.id}>
                  {option.value}
                </MenuItem>
              ))}
            </Select>
            {lineBreak}
            <div style={styles.label}>Mã lớp </div>
            {nextLine}
            <Select
              value={selectedClass}
              onChange={handleChangeClass}
              fullWidth={true}
              onOpen={(e) => {
                setClicked2(true);
              }}
              onClose={(e) => {
                if (selectedClass == " ") setClicked2(false);
              }}
            >
              {!clicked2 && (
                <MenuItem value=" " key=" ">
                  <em style={{ color: "rgb(0,0,0,0.5)" }}>
                    Bạn phải chọn học phần trước
                  </em>
                </MenuItem>
              )}
              {listClassBelongCourse.map((option) => {
                //console.log(option)
                let displayStr =
                  option.classCode + "     (" + option.classType + ")";
                return (
                  <MenuItem key={option.id} value={option.id}>
                    {displayStr}
                  </MenuItem>
                );
              })}
            </Select>
            {lineBreak}
            {nextLine}

            <MuiPickersUtilsProvider utils={DateFnsUtils}>
              <Grid container spacing={10}>
                <Grid item xs={4}>
                  <div style={styles.label}>Ngày thi </div>
                </Grid>
                <Grid item xs={4}>
                  <div style={styles.label}>Giờ thi </div>
                </Grid>
                <Grid item xs={4}>
                  <div style={styles.label}>Thời gian làm bài </div>
                </Grid>
              </Grid>
              <div style={{ marginTop: "-80px" }}>
                <Grid container spacing={10}>
                  <Grid item xs={4}>
                    <KeyboardDatePicker
                      format="dd/MM/yyyy"
                      margin="normal"
                      label="Chọn ngày thi"
                      value={selectedDate}
                      onChange={handleDateChange}
                      KeyboardButtonProps={{
                        "aria-label": "change date",
                      }}
                    />
                  </Grid>
                  <Grid item xs={4}>
                    <KeyboardTimePicker
                      margin="normal"
                      label="Chọn giờ thi"
                      value={selectedDate}
                      onChange={handleDateChange}
                      KeyboardButtonProps={{
                        "aria-label": "change date",
                      }}
                    />
                  </Grid>
                  <Grid item xs={4}>
                    <TextField
                      label="Tính bằng phút"
                      placeholder="90"
                      style={{ marginTop: "15px" }}
                      fullWidth
                      onChange={handleChangeDuration}
                      value={duration}
                      type="number"
                    />
                  </Grid>
                </Grid>
              </div>

              {/* <div style={styles.label}>Ngày thi </div> */}
            </MuiPickersUtilsProvider>

            {lineBreak}

            <div style={styles.label}>Chú thích </div>
            {nextLine}
            <TextField
              label=""
              placeholder="Chú thích"
              fullWidth={true}
              InputProps={{ classes: { input: classes.input } }}
              onChange={handleChangeNote}
            />
          </form>
        )}

        {page == 1 && (
          <div>
            <Tooltip
              title="Di chuyển xuống cuối trang"
              aria-label="Di chuyển xuống cuối trang"
            >
              <Fab
                color="primary"
                aria-label="down down"
                style={{
                  margin: 0,
                  top: 80,
                  right: 0,
                  bottom: "auto",
                  left: "auto",
                  position: "fixed",
                }}
                onClick={(e) => {
                  window.scrollTo({
                    top: document.body.scrollHeight,
                    behavior: "smooth",
                  });
                }}
              >
                <KeyboardArrowDown />
              </Fab>
            </Tooltip>
            {questionList.map((elm, elmIndex) => {
              return (
                <div key={elmIndex}>
                  {elmIndex != 0 && (
                    <div
                      style={{
                        borderTop: "1px solid rgb(0, 0, 0, 0.5)",
                        paddingBottom: "40px",
                      }}
                    ></div>
                  )}
                  <Grid container spacing={1} justify="center">
                    <Grid item xs={11}>
                      <div style={styles.descStyle}>
                        <b>Câu hỏi {elmIndex + 1}: </b>
                        <p
                          style={{
                            maxWidth: "1024px",
                            overflowWrap: "break-word",
                          }}
                        >
                          {ReactHtmlParser(elm.desc)}
                        </p>
                      </div>

                      <div style={styles.ansStyle}>
                        {elm.ans.map((a, index) => {
                          return (
                            <div
                              key={index + elmIndex}
                              style={styles.subAnsStyle}
                            >
                              {a[1] == false ? (
                                <div style={{ color: "red" }}>
                                  {index + 1}. &#8287;{a[0]}
                                </div>
                              ) : (
                                <div style={{ color: "green" }}>
                                  {index + 1}. &#8287;{a[0]}
                                </div>
                              )}
                            </div>
                          );
                        })}
                      </div>
                    </Grid>

                    <Grid item xs={1}>
                      <div style={{}}>
                        <Tooltip
                          title="Di chuyển lên"
                          aria-label="Di chuyển lên"
                        >
                          <Button
                            variant="outlined"
                            color="primary"
                            aria-label="Di chuyển lên"
                            size="small"
                            onClick={(e) => {
                              handleSwapQuestion(e, elmIndex, "up");
                            }}
                          >
                            <KeyboardArrowUpIcon />
                          </Button>
                        </Tooltip>
                        {nextLine}
                        <Tooltip
                          title="Di chuyển xuống"
                          aria-label="Di chuyển xuống"
                        >
                          <Button
                            variant="outlined"
                            color="primary"
                            aria-label="Di chuyển xuống"
                            size="small"
                            onClick={(e) => {
                              handleSwapQuestion(e, elmIndex, "down");
                            }}
                          >
                            <KeyboardArrowDown />
                          </Button>
                        </Tooltip>
                        {nextLine}
                        <Tooltip title="Xóa" aria-label="Xóa">
                          <Button
                            variant="outlined"
                            color="secondary"
                            aria-label="Xóa"
                            size="small"
                            onClick={(e) => {
                              handleDelete(elmIndex);
                            }}
                          >
                            <DeleteIcon />
                          </Button>
                        </Tooltip>
                      </div>
                    </Grid>
                  </Grid>
                </div>
              );
            })}
            <div
              style={{
                borderTop: "1px solid rgb(0, 0, 0, 0.5)",
                paddingBottom: "35px",
              }}
            ></div>

            <div
              style={{
                fontSize: "20px",
                fontWeight: "lighter",
              }}
            >
              Lưu ý:{" "}
            </div>
            {nextLine}
            <div style={{ paddingLeft: "5%" }}>
              <div
                style={{
                  fontSize: "20px",
                  fontWeight: "lighter",
                  color: "green",
                }}
              >
                Đáp án đúng sẽ được in màu xanh{" "}
              </div>
              {nextLine}
              <div
                style={{
                  fontSize: "20px",
                  fontWeight: "lighter",
                  color: "red",
                }}
              >
                Đáp án sai sẽ được in màu đỏ{" "}
              </div>
            </div>
            {lineBreak}
            <div
              style={{
                borderTop: "1px solid rgb(0, 0, 0, 0.5)",
                paddingBottom: "35px",
              }}
            ></div>
            {lineBreak}
            <Grid container spacing={1} justify="center">
              <Grid item xs={12}>
                <Button
                  variant="outlined"
                  style={{ height: "100px", fontSize: "20px" }}
                  fullWidth
                  onClick={(e) => {
                    handleChangePage(0);
                  }}
                >
                  Sinh ngẫu nhiên các câu hỏi
                </Button>
              </Grid>

              <Grid item xs={6}>
                <CreateNewQuestionPopup addQuestion={addQuestion} />
              </Grid>

              <Grid item xs={6}>
                <Button
                  variant="outlined"
                  style={{ height: "100px", fontSize: "20px" }}
                  fullWidth
                  onClick={(e) => {
                    handleChangePage(0);
                  }}
                >
                  Thêm câu hỏi có sẵn
                </Button>
              </Grid>
            </Grid>
            {isInPage1 && goToBottom()}
          </div>
        )}

        {lineBreak}
        {lineBreak}
        <Grid container spacing={1} justify="flex-end">
          {/* {page == 0 && 
                    <Grid item xs={6} sm={3}>
                        <Button variant="contained" color="primary" fullWidth disabled >
                            &#60; &#8287; Quay lại
                        </Button>
                    </Grid>
                }
                
                {page != 0 && 
                    <Grid item xs={6} sm={3}>
                        <Button variant="contained" color="primary" fullWidth 
                            onClick={(e)=> { 
                                handleChangePage(1);
                            }}>
                            &#60; &#8287; Quay lại
                        </Button>
                    </Grid>
                } */}
          <Grid item xs={6} sm={3}>
            <Button
              variant="contained"
              color="inherit"
              fullWidth
              onClick={(e) => {
                if (
                  window.confirm(
                    "Tất cả mọi thứ bạn làm sẽ không được lưu lại !!!"
                  )
                )
                  history.push("/edu/class/quiztest/list");
              }}
            >
              Hủy
            </Button>
          </Grid>

          <Grid item xs={6} sm={3}>
            <Button
              variant="contained"
              color="primary"
              fullWidth
              onClick={(e) => {
                handleSubmit();
              }}
            >
              Tạo kỳ thi
            </Button>
          </Grid>
        </Grid>
      </Card>
    </Grid>
  );
}

export default CreateQuizTest;

export { CreateQuizTest, styles };
