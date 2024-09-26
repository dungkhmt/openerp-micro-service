import {Button, Card, Grid, TextField, Tooltip} from "@material-ui/core";
import Checkbox from "@material-ui/core/Checkbox";
import Dialog from "@material-ui/core/Dialog";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormGroup from "@material-ui/core/FormGroup";
import {makeStyles} from "@material-ui/core/styles";
import CloseIcon from "@material-ui/icons/Close";
import DeleteIcon from "@material-ui/icons/Delete";
import {convertToRaw, EditorState} from "draft-js";
import DraftToHtml from "draftjs-to-html";
import React, {useEffect, useReducer, useState} from "react";
import {Editor} from "react-draft-wysiwyg";

const useStyles = makeStyles((theme) => ({
  input: {
    "&::placeholder": {
      color: "rgb(0,0,0,1)",
      textAlign: "left",
      fontStyle: "italic",
    },
  },
}));

function getModalStyle() {
  return {
    width: "100%",
    height: "100%",
    backgroundColor: "rgb(200, 200, 200, 0.4)",
  };
}

const editorStyle = {
  toolbar: {
    //background: "#90caf9",
    border: "1px solid gray",
  },
  editor: {
    border: "1px solid gray",
    minHeight: "300px",
    fontSize: "18px",
  },
};

const styles = {
  label: {
    fontSize: "25px",
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

const listAns = [];

function NewAnswer(props) {
  const classes = useStyles();
  const [open, setOpen] = React.useState(false);
  const [modalStyle] = React.useState(getModalStyle);

  //đúng là yes, sai là no
  const [yesNo, setStateYesNo] = React.useState({
    yes: false,
    no: true,
  });

  const [ans, setAns] = React.useState("");

  const [err, setErr] = React.useState(false);

  const [helperText, setHelperText] = React.useState(" ");

  const parent = props.parent;

  const handleClickOpen = () => {
    setStateYesNo({
      yes: false,
      no: true,
    });
    setAns("");
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const contentClick = (e) => {
    e.stopPropagation();
  };

  const handleChangeCheckBox = (event) => {
    if (event.target.name == "Đúng" && event.target.checked)
      setStateYesNo({
        yes: true,
        no: false,
      });
    else {
      setStateYesNo({
        yes: false,
        no: true,
      });
    }
    //setStateYesNo({ ...state, [event.target.name]: event.target.checked });
  };

  const handleSubmit = (e) => {
    //console.log(ans);
    //console.log(yesNo);

    if (ans == "") {
      setErr(true);
      setHelperText("Đáp án không được để trống");
      alert("Đáp án không được để trống");
      return;
    }

    setOpen(false);

    listAns.push({
      ans: ans,
      correct: yesNo.yes,
    });

    parent();
  };

  const { yes, no } = yesNo;

  return (
    <div>
      <Button
        variant="outlined"
        style={{ height: "100px", fontSize: "20px" }}
        fullWidth
        onClick={handleClickOpen}
      >
        Thêm đáp án
      </Button>
      <Dialog
        onClose={handleClose}
        aria-labelledby="simple-dialog-title"
        open={open}
        fullScreen={true}
        PaperProps={{
          style: {
            backgroundColor: "transparent",
            boxShadow: "none",
            //height: 'auto'
          },
        }}
      >
        <div style={modalStyle} className={classes.paper} onClick={handleClose}>
          <Grid
            container
            spacing={0}
            direction="row"
            alignItems="center"
            justify="center"
            style={{ minHeight: "300px", height: "95%" }}
          >
            <Grid item xs="auto">
              <Card
                style={{
                  minWidth: "1000px",
                  minHeight: `${window.screen.height / 3}px`,
                  padding: "10% 10% 7% 10%",
                }}
                onClick={contentClick}
              >
                <div style={styles.label}>Tạo đáp án mới </div>
                {lineBreak}
                <TextField
                  margin="dense"
                  id="name"
                  label="Đáp án"
                  type="text"
                  helperText={helperText}
                  fullWidth
                  value={ans}
                  onChange={(e) => {
                    setAns(e.target.value);
                    if (e.target.value != "") {
                      setErr(false);
                      setHelperText(" ");
                    } else {
                      setErr(true);
                      setHelperText("Đáp án không được để trống");
                    }
                  }}
                  /* onClickCapture={(e) => {
                                if(e.target.value == '') {
                                    setErr(true)
                                    setHelperText('Đáp án không được để trống')
                                }
                            }} */

                  onSelect={(e) => {
                    if (e.target.value == "") {
                      setErr(true);
                      setHelperText("Đáp án không được để trống");
                    }
                  }}
                  error={err}
                />
                {lineBreak}
                <FormGroup row>
                  <FormControlLabel
                    control={
                      <Checkbox
                        checked={yes}
                        onChange={handleChangeCheckBox}
                        name="Đúng"
                      />
                    }
                    label="Đúng"
                    style={{ minWidth: "500px" }}
                  />

                  <FormControlLabel
                    control={
                      <Checkbox
                        checked={no}
                        onChange={handleChangeCheckBox}
                        name="Sai"
                      />
                    }
                    label="Sai"
                  />
                </FormGroup>
                {lineBreak}
                <div style={{ borderTop: "1px solid rgb(0, 0, 0, 0.5)" }}></div>
                {lineBreak}
                <Grid container spacing={5} justify="center">
                  <Grid item xs={3}>
                    <Button
                      variant="contained"
                      color="primary"
                      fullWidth
                      onClick={handleSubmit}
                    >
                      Tạo đáp án
                    </Button>
                  </Grid>
                  <Grid item xs={3}>
                    <Button
                      variant="contained"
                      color="primary"
                      fullWidth
                      onClick={handleClose}
                    >
                      Hủy
                    </Button>
                  </Grid>
                </Grid>
              </Card>
            </Grid>
          </Grid>
        </div>
      </Dialog>
    </div>
  );
}

function CreateNewQuestionPopup(props) {
  const classes = useStyles();
  const [modalStyle] = React.useState(getModalStyle);
  const [open, setOpen] = React.useState(false);

  const [isHovered, setHovered] = useState(false);

  const [editorState, setEditorState] = useState(EditorState.createEmpty());

  const [, forceUpdate] = useReducer((x) => x + 1, 0);

  const onChangeEditorState = (editorState) => {
    setEditorState(editorState);
  };

  const handleOpen = (e) => {
    setOpen(true);
    setHovered(false);
    listAns.splice(0, listAns.length);
    setEditorState(EditorState.createEmpty());
  };

  const handleClose = (e) => {
    setHovered(false);
    if (
      window.confirm(
        "Bạn muốn thoát và không lưu lại tất cả những gì mình đã nhập ???"
      )
    ) {
      setOpen(false);
    }
  };

  let addQuestion = props.addQuestion;

  const handleSubmit = (e) => {
    //console.log('submit')
    //console.log()
    //console.log(listAns);

    //let question = DraftToHtml(convertToRaw(editorState.getCurrentContent()));
    let elm = {};
    elm.desc = DraftToHtml(convertToRaw(editorState.getCurrentContent()));
    elm.ans = [];
    listAns.map((e, index) => {
      elm.ans.push([e.ans, e.correct]);
    });

    elm.questionID = "";

    addQuestion(elm);

    setOpen(false);
    //editorState.
  };

  const contentClick = (e) => {
    e.stopPropagation();
    //alert("hahah");
  };

  function update() {
    forceUpdate();
  }

  useEffect(() => {
    window.addEventListener("resize", update);
    return () => {
      window.removeEventListener("resize", update);
    };
  }, []);

  const calculatedStyle = isHovered
    ? {
        backgroundColor: "red",
        color: "white",
        float: "right",
        marginRight: "-100px",
      }
    : {
        backgroundColor: "rgb(255, 0, 0, 0.6)",
        color: "white",
        float: "right",
        marginRight: "-100px",
      };

  const handleChangeCheckBox = (elm, index, isCorrect) => {
    listAns[index].correct = !listAns[index].correct;
    //console.log(listAns)
    forceUpdate();
  };

  const handleDelete = (elm, index) => {
    listAns.splice(index, 1);
    forceUpdate();
  };

  let body = (
    <div style={modalStyle} className={classes.paper} onClick={handleClose}>
      <Grid
        container
        spacing={0}
        direction="row"
        alignItems="center"
        justify="center"
        style={{ minHeight: "300px", height: "95%" }}
      >
        <Grid item xs="auto">
          <div onClick={contentClick}>
            <Card
              style={{
                minWidth: "1200px",
                minHeight: `${window.screen.height}px`,
                padding: "3% 10% 7% 10%",
              }}
            >
              <CloseIcon
                style={calculatedStyle}
                onClick={handleClose}
                onMouseEnter={() => setHovered(true)}
                onMouseLeave={() => setHovered(false)}
              />
              {nextLine}
              <div style={styles.label}>Tạo câu hỏi trắc nghiệm mới</div>
              {lineBreak}
              <div
                style={{
                  borderTop: "1px solid rgb(0, 0, 0)",
                  marginLeft: "-11%",
                  marginRight: "-11%",
                }}
              ></div>

              {lineBreak}
              <div style={styles.label}>Câu hỏi: </div>
              {nextLine}
              <Editor
                editorState={editorState}
                handlePastedText={() => false}
                onEditorStateChange={onChangeEditorState}
                toolbarStyle={editorStyle.toolbar}
                editorStyle={editorStyle.editor}
                placeholder="Gõ câu hỏi vào đây..."
              />
              {lineBreak}
              <div style={styles.label}>Đáp án: </div>
              {nextLine}

              <Grid container spacing={1} justify="center">
                <Grid item xs={12}>
                  <div style={styles.ansStyle}>
                    {listAns.map((elm, index) => {
                      return (
                        <div key={index} style={styles.subAnsStyle}>
                          <Grid container spacing={1} justify="center">
                            <Grid item xs={9}>
                              {index + 1}. &#8287;
                              {elm.ans}
                            </Grid>
                            <Grid item xs={3}>
                              <div
                                key={index}
                                style={{
                                  display: "inline",
                                  fontSize: "18px",
                                  fontWeight: "lighter",
                                  paddingBottom: "100px",
                                }}
                              >
                                <div style={{ display: "inline" }}>
                                  <FormControlLabel
                                    control={
                                      <Checkbox
                                        checked={elm.correct}
                                        onChange={(e) => {
                                          handleChangeCheckBox(
                                            elm,
                                            index,
                                            e.target.value
                                          );
                                        }}
                                        name="Đúng"
                                      />
                                    }
                                    label="Đúng"
                                  />
                                </div>
                                <div
                                  style={{
                                    display: "inline",
                                    userSelect: "none",
                                  }}
                                >
                                  &#8287;&#8287;&#8287;&#8287;&#8287;&#8287;&#8287;
                                  &#8287;&#8287;&#8287;&#8287;&#8287;&#8287;&#8287;
                                  &#8287;
                                </div>
                                <div style={{ display: "inline" }}>
                                  <Tooltip title="Xóa" aria-label="Xóa">
                                    <Button
                                      variant="outlined"
                                      color="secondary"
                                      aria-label="Xóa"
                                      size="small"
                                      onClick={(e) => {
                                        handleDelete(elm, index);
                                      }}
                                    >
                                      <DeleteIcon />
                                    </Button>
                                  </Tooltip>
                                </div>
                              </div>
                            </Grid>
                          </Grid>
                          <div
                            style={{
                              borderTop: "1px solid rgb(0, 0, 0)",
                              marginRight: "3%",
                            }}
                          ></div>
                        </div>
                      );
                    })}
                  </div>
                </Grid>

                {/* <Grid item xs={2}>
                                <div style={{}}>                 
                                    {listAns.map((elm, index) => {
                                        return(
                                            <div key={index} style={styles.subAnsStyle}>
                                                <FormControlLabel
                                                    control={<Checkbox checked={elm.correct}
                                                        onChange={(e) => { handleChangeCheckBox(elm, index, e.target.value) }} name="Đúng" />}
                                                    label="Đúng"
                                                />
                                            </div>
                                        )
                                    })}
                                </div>
                            </Grid> */}
              </Grid>

              <Grid container spacing={1} justify="center">
                <Grid item xs={12}>
                  <NewAnswer parent={forceUpdate} />
                </Grid>
              </Grid>
              {lineBreak}
              {lineBreak}
              <Grid container spacing={5} justify="center">
                <Grid item xs={3}>
                  <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    onClick={handleSubmit}
                  >
                    Tạo câu hỏi
                  </Button>
                </Grid>
                <Grid item xs={3}>
                  <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    onClick={handleClose}
                  >
                    Hủy
                  </Button>
                </Grid>
              </Grid>
            </Card>
          </div>
        </Grid>
      </Grid>
    </div>
  );

  return (
    <div>
      <div>
        <Dialog
          onClose={handleClose}
          aria-labelledby="simple-dialog-title"
          open={open}
          fullScreen={true}
          PaperProps={{
            style: {
              backgroundColor: "transparent",
              boxShadow: "none",
              //height: 'auto'
            },
          }}
        >
          {body}
        </Dialog>
      </div>
      <Button
        variant="outlined"
        style={{ height: "100px", fontSize: "20px" }}
        fullWidth
        onClick={handleOpen}
      >
        Thêm câu hỏi hoàn toàn mới
      </Button>
    </div>
  );
}

export default CreateNewQuestionPopup;
