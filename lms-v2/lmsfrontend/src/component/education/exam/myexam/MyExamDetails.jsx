import React, {useEffect, useState} from 'react';
import {Box, Button, Card, CardContent, CircularProgress, Input} from "@material-ui/core";
import {request} from "../../../../api";
import DateFnsUtils from "@date-io/date-fns";
import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";
import CardActions from "@material-ui/core/CardActions";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {toast} from "react-toastify";
import RichTextEditor from "../../../common/editor/RichTextEditor";
import {makeStyles} from "@material-ui/core/styles";
import {useHistory} from "react-router-dom";
import {useLocation} from "react-router";
import {formatDateTime} from "../ultils/DateUltils";
import Checkbox from "@material-ui/core/Checkbox";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import {Radio, RadioGroup} from "@mui/material";
import {DropzoneArea} from "material-ui-dropzone";
import {AccessTime, AttachFileOutlined, Cancel, Timer} from "@material-ui/icons";
import {getFilenameFromString, getFilePathFromString} from "../ultils/FileUltils";
import QuestionFilePreview from "../questionbank/QuestionFilePreview";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import FormGroup from "@material-ui/core/FormGroup";
import CheckIcon from "@mui/icons-material/Check";
import {parseHTMLToString} from "../ultils/DataUltils";

const useStyles = makeStyles((theme) => ({
  root: {
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: 200
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
  },
}));
const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;

function MyExamDetails(props) {

  const classes = useStyles();
  const history = useHistory();
  const location = useLocation();
  const data = location.state?.data

  if(data === undefined){
    window.location.href = '/exam/my-exam';
  }

  const [isLoading, setIsLoading] = useState(false);
  const [dataAnswers, setDataAnswers] = useState([]);
  const [tmpTextAnswer, setTmpTextAnswer] = useState("");
  const [answersFiles, setAnswersFiles] = useState([]);
  const [openFilePreviewDialog, setOpenFilePreviewDialog] = useState(false);
  const [filePreview, setFilePreview] = useState(null);
  const [startLoadTime, setStartLoadTime] = useState(null);

  useEffect(() => {
    let tmpDataAnswers = []
    let tmpFileAnswers = []
    for(let item of data?.questionList){
      tmpDataAnswers.push({
        questionOrder: item?.questionOrder,
        examQuestionId: item?.questionId,
        answer: ""
      })
      tmpFileAnswers.push({
        questionOrder: item?.questionOrder,
        files: null
      })
    }
    setDataAnswers(tmpDataAnswers)
    setAnswersFiles(tmpFileAnswers)
    setStartLoadTime(new Date());
  }, []);

  const handleAnswerCheckboxChange = (questionOrder, answer, isChecked) => {
    if(isChecked){
      if(dataAnswers[questionOrder-1]?.answer === ''){
        dataAnswers[questionOrder-1].answer = answer
      }else{
        dataAnswers[questionOrder-1].answer += ',' + answer
      }
    }else{
      const answersArray = dataAnswers[questionOrder-1].answer.split(',');
      const filteredAnswers = answersArray.filter(item => item !== answer);
      dataAnswers[questionOrder-1].answer = filteredAnswers.join(',');
    }

    setDataAnswers(dataAnswers)
  };

  const handleAnswerRadioChange = (event, questionOrder) => {
    dataAnswers[questionOrder-1].answer = event.target.value

    setDataAnswers(dataAnswers)
  };

  const handleAnswerTextChange = (value, questionOrder) => {
    dataAnswers[questionOrder-1].answer = value

    setDataAnswers(dataAnswers)
  };

  const handleAnswerFileChange = (files, questionOrder) => {
    answersFiles[questionOrder-1].files = files

    setAnswersFiles(answersFiles)
  }

  const handleSubmit = () => {
    const endLoadTime = new Date();
    const totalTime = Math.round((endLoadTime - startLoadTime) / 60000);

    const body = {
      examId: data?.examId,
      examStudentId: data?.examStudentId,
      totalTime: totalTime,
      examResultDetails: dataAnswers
    }

    let tmpAnswersFiles = []
    for(let item of answersFiles){
      if(item?.files != null){
        for(let file of item?.files){
          const fileNameParts = file.name.split('.');
          const newFileName = `${fileNameParts[0]}_${item?.questionOrder}.${fileNameParts[1]}`;

          const updatedFile = new File([file], newFileName, {
            type: file.type,
            lastModified: file.lastModified,
          });

          tmpAnswersFiles.push(updatedFile)
        }
      }
    }

    let formData = new FormData();
    formData.append("body", JSON.stringify(body));
    for (const file of tmpAnswersFiles) {
      formData.append("files", file);
    }

    setIsLoading(true)
    request(
      "post",
      '/exam/doing-my-exam',
      (res) => {
        if(res.status === 200){
          if(res.data.resultCode === 200){
            toast.success(res.data.resultMsg)
            setIsLoading(false)
            history.push("/exam/my-exam")
          }else{
            toast.error(res.data.resultMsg)
            setIsLoading(false)
          }
        }else {
          toast.error(res)
          setIsLoading(false)
        }
      },
      { onError: (e) => toast.error(e) },
      formData
    );
  }

  const handleOpenFilePreviewDialog = (data) => {
    setOpenFilePreviewDialog(true)
    setFilePreview(getFilePathFromString(data))
  };

  const checkAnswerRadioAndCheckbox = (questionType, answerQuestion, answerStudent) => {
    if(questionType === 0){
      const answerQuestions = answerQuestion.split(',').sort();
      const answerStudents = answerStudent.split(',').sort();

      return answerStudents.every(elem => answerQuestions.includes(elem));
    }
  }

  // Checking focus tab
  useEffect(() => {
    // handleCheckingFocusTab()
  }, []);
  const onFocus = () => {
    console.log("Tab is in focus");
  };
  // const onBlur = () => {
  //   console.log("Tab is blurred");
  // };
  const onVisibilitychange = () => {
    console.log('document.visibilityState',document.visibilityState)
    console.log("Ghi lại nội dung tab hiện tại:", document.documentURI, document.title);
  };
  const handleCheckingFocusTab = () => {
    window.addEventListener("focus", onFocus);
    // window.addEventListener("blur", onBlur);
    document.addEventListener("visibilitychange", onVisibilitychange);
    onFocus();
    return () => {
      window.removeEventListener("focus", onFocus);
      // window.removeEventListener("blur", onBlur);
      document.removeEventListener("visibilitychange", onVisibilitychange);
    };
  }
  return (
    <div>
      <MuiPickersUtilsProvider utils={DateFnsUtils}>
        <Card>
          <CardContent>
            <div style={{display: "flex", flexDirection: "column", alignItems: 'center', width: '100%'}}>
              <h1 style={{margin: 0, padding: 0}}>{data?.examName}</h1>
              <p style={{margin: 0, padding: 0}}>{parseHTMLToString(data?.examDescription)}</p>
              <h2 style={{margin: 0, padding: 0}}>{data?.examTestName}</h2>
              <div style={{margin: 0, padding: 0, display: "flex"}}><span style={{fontWeight: "bold", marginRight: '5px'}}>Mã đề:</span>{data?.examTestCode}</div>
              <div style={{display: "flex"}}>
                <p style={{margin: '0 20px 0 0', padding: 0, display: "flex"}}><span style={{fontWeight: "bold", marginRight: '5px'}}>Thời gian bắt đầu:</span>{formatDateTime(data?.startTime)}</p>
                <p style={{margin: 0, padding: 0, display: "flex"}}><span style={{fontWeight: "bold", marginRight: '5px'}}>Thời gian kết thúc:</span>{formatDateTime(data?.endTime)}</p>
              </div>
            </div>

            {
              data?.examResultId != null && (
                <div>
                  <div style={{
                    display: "flex",
                    width: '100%',
                    border: '2px solid #000000b8',
                    borderRadius: '10px',
                    margin: '10px 0'
                  }}>
                    <div style={{display: "flex", flexDirection: "column", width: '200px'}}>
                      <h3 style={{margin: 0, padding: '10px', borderBottom: '2px solid #000000b8'}}>Điểm</h3>
                      <p style={{padding: 0, margin: "auto", height: '150px', lineHeight: '150px', fontWeight: "bold", fontSize: '70px'}}>{data?.totalScore}</p>
                    </div>
                    <div style={{display: "flex", flexDirection: "column", borderLeft: '2px solid #000000b8', width: 'calc(100% - 200px)'}}>
                      <h3 style={{margin: 0, padding: '10px', borderBottom: '2px solid #000000b8'}}>Nhận xét</h3>
                      <p style={{padding: '0 10px', margin: 0, height: '150px'}}>{data?.comment ? parseHTMLToString(data?.comment) : ''}</p>
                    </div>
                  </div>
                  <div style={{display: "flex", alignItems:"center", marginBottom: '10px', justifyContent: "flex-end"}}>
                    <Timer/>
                    <p style={{padding: 0, margin: 0}}><strong>Tổng thời gian làm: </strong> {data?.totalTime} (phút)</p>
                  </div>
                  <div style={{display: "flex", alignItems:"center", marginBottom: '10px', justifyContent: "flex-end"}}>
                    <AccessTime/>
                    <p style={{padding: 0, margin: 0}}><strong>Thời gian nộp: </strong> {formatDateTime(data?.submitedAt)}</p>
                  </div>
                </div>
              )
            }

            {
              data?.questionList?.map(value => {
                const questionOrder = value?.questionOrder;
                return (
                  <div
                    key={value?.questionOrder}
                    style={{
                      border: '2px solid #f5f5f5',
                      borderColor:
                        (value?.questionType === 0 && data?.totalScore) ?
                          (checkAnswerRadioAndCheckbox(value?.questionType, value?.questionAnswer, value?.answer) ? '#61bd6d' : '#f50000c9'):
                          '#f5f5f5',
                      display: 'flex',
                      justifyContent: 'space-between',
                      borderRadius: '10px',
                      padding: '10px',
                      marginBottom: '10px'
                    }}>
                    <Box display="flex"
                         flexDirection='column'
                         width="100%">

                      <div style={{display: "flex", justifyContent: "space-between"}}>
                        <div style={{display: 'flex'}}>
                        <span style={{
                          display: "block",
                          fontWeight: 'bold',
                          marginRight: '5px'
                        }}>Câu {value?.questionOrder}.</span>
                          <span
                            style={{fontStyle: 'italic'}}>({value?.questionType === 0 ? 'Trắc nghiệm' : 'Tự luận'})</span>
                        </div>

                        {
                          data?.totalScore && (
                            <div style={{display: "flex", alignItems: "center"}} key={questionOrder}>
                              {
                                value?.questionType === 0 ?
                                  (checkAnswerRadioAndCheckbox(value?.questionType, value?.questionAnswer, value?.answer) ?
                                    <CheckCircleIcon style={{color: '#61bd6d'}}/> :
                                    <Cancel style={{color: '#f50000c9'}}/>) :
                                  (<></>)
                              }
                              <TextField
                                id={`scoreInput-${questionOrder}`}
                                label="Điểm"
                                style={{width: "60px", marginLeft: "16px"}}
                                value={value?.score}
                                disabled
                                InputLabelProps={{
                                  shrink: true,
                                }}
                              />
                            </div>
                          )
                        }
                      </div>

                      <p><strong style={{marginRight: '10px'}}>Câu
                        hỏi: </strong>{parseHTMLToString(value?.questionContent)}</p>
                      {
                        value?.questionFile && (
                          value?.questionFile.split(';').map(item => {
                            return (
                              <div style={{display: 'flex', alignItems: 'center'}}>
                                <AttachFileOutlined></AttachFileOutlined>
                                <p style={{fontWeight: 'bold', cursor: 'pointer'}}
                                   onClick={() => handleOpenFilePreviewDialog(item)}>{getFilenameFromString(item)}</p>
                              </div>
                            )
                          })
                        )
                      }
                      {
                        value?.questionType === 0 && value?.questionMultichoice && (
                          <Box sx={{display: 'flex', flexDirection: 'column'}}>
                            <p style={{margin: 0, padding: 0, fontWeight: "bold"}}>Chọn các đáp án đúng trong các đáp án
                              sau:</p>
                            <FormControlLabel
                              label={
                                <FormGroup row>
                                  <Box display="flex" alignItems="center">
                                    <span>{parseHTMLToString(value?.questionContentAnswer1)}</span>
                                    {( data?.totalScore && value?.questionAnswer?.includes('1')) && (<CheckIcon style={{ marginLeft: 8, color: 'green' }} />)}
                                  </Box>
                                </FormGroup>
                              }
                              control={<Checkbox color="primary"
                                                 checked={value?.answer?.includes('1')}
                                                 disabled={data?.examResultId != null}
                                                 onChange={(event) => handleAnswerCheckboxChange(value?.questionOrder, '1', event.target.checked)
                                                 }/>}
                            />
                            {
                              value?.questionNumberAnswer >= 2 && (
                                <FormControlLabel
                                  label={
                                    <FormGroup row>
                                      <Box display="flex" alignItems="center">
                                        <span>{parseHTMLToString(value?.questionContentAnswer2)}</span>
                                        {( data?.totalScore && value?.questionAnswer?.includes('2')) && (<CheckIcon style={{ marginLeft: 8, color: 'green' }} />)}
                                      </Box>
                                    </FormGroup>
                                  }
                                  control={<Checkbox color="primary"
                                                     checked={value?.answer?.includes('2')}
                                                     disabled={data?.examResultId != null}
                                                     onChange={(event) => handleAnswerCheckboxChange(value?.questionOrder, '2', event.target.checked)
                                                     }/>}
                                />
                              )
                            }
                            {
                              value?.questionNumberAnswer >= 3 && (
                                <FormControlLabel
                                  label={
                                    <FormGroup row>
                                      <Box display="flex" alignItems="center">
                                        <span>{parseHTMLToString(value?.questionContentAnswer3)}</span>
                                        {( data?.totalScore && value?.questionAnswer?.includes('3')) && (<CheckIcon style={{ marginLeft: 8, color: 'green' }} />)}
                                      </Box>
                                    </FormGroup>
                                  }
                                  control={<Checkbox color="primary"
                                                     checked={value?.answer?.includes('3')}
                                                     disabled={data?.examResultId != null}
                                                     onChange={(event) => handleAnswerCheckboxChange(value?.questionOrder, '3', event.target.checked)
                                                     }/>}
                                />
                              )
                            }
                            {
                              value?.questionNumberAnswer >= 4 && (
                                <FormControlLabel
                                  label={
                                    <FormGroup row>
                                      <Box display="flex" alignItems="center">
                                        <span>{parseHTMLToString(value?.questionContentAnswer4)}</span>
                                        {( data?.totalScore && value?.questionAnswer?.includes('4')) && (<CheckIcon style={{ marginLeft: 8, color: 'green' }} />)}
                                      </Box>
                                    </FormGroup>
                                  }
                                  control={<Checkbox color="primary"
                                                     checked={value?.answer?.includes('4')}
                                                     disabled={data?.examResultId != null}
                                                     onChange={(event) => handleAnswerCheckboxChange(value?.questionOrder, '4', event.target.checked)
                                                     }/>}
                                />
                              )
                            }
                            {
                              value?.questionNumberAnswer >= 5 && (
                                <FormControlLabel
                                  label={
                                    <FormGroup row>
                                      <Box display="flex" alignItems="center">
                                        <span>{parseHTMLToString(value?.questionContentAnswer5)}</span>
                                        {( data?.totalScore && value?.questionAnswer?.includes('5')) && (<CheckIcon style={{ marginLeft: 8, color: 'green' }} />)}
                                      </Box>
                                    </FormGroup>
                                  }
                                  control={<Checkbox color="primary"
                                                     checked={value?.answer?.includes('5')}
                                                     disabled={data?.examResultId != null}
                                                     onChange={(event) => handleAnswerCheckboxChange(value?.questionOrder, '5', event.target.checked)
                                                     }/>}
                                />
                              )
                            }
                          </Box>
                        )
                      }
                      {
                        value?.questionType === 0 && !value?.questionMultichoice && (
                          <Box sx={{display: 'flex', flexDirection: 'column'}}>
                            <p style={{margin: 0, padding: 0, fontWeight: "bold"}}>Chọn đáp án đúng nhất:</p>
                            <RadioGroup
                              aria-labelledby="demo-radio-buttons-group-label"
                              name="radio-buttons-group"
                              value={dataAnswers[value?.questionOrder - 1]?.answer}
                              onChange={(event) => handleAnswerRadioChange(event, value?.questionOrder)}
                            >
                              <FormControlLabel value="1" control={<Radio checked={value?.answer?.includes('1')}
                                                                          disabled={data?.examResultId != null}/>}
                                                label={
                                                  <FormGroup row>
                                                    <Box display="flex" alignItems="center">
                                                      <span>{parseHTMLToString(value?.questionContentAnswer1)}</span>
                                                      {( data?.totalScore && value?.questionAnswer?.includes('1')) && (<CheckIcon style={{ marginLeft: 8, color: 'green' }} />)}
                                                    </Box>
                                                  </FormGroup>
                                                }/>
                              {
                                value?.questionNumberAnswer >= 2 && (
                                  <FormControlLabel value="2" control={<Radio checked={value?.answer?.includes('2')}
                                                                              disabled={data?.examResultId != null}/>}
                                                    label={
                                                      <FormGroup row>
                                                        <Box display="flex" alignItems="center">
                                                          <span>{parseHTMLToString(value?.questionContentAnswer2)}</span>
                                                          {( data?.totalScore && value?.questionAnswer?.includes('2')) && (<CheckIcon style={{ marginLeft: 8, color: 'green' }} />)}
                                                        </Box>
                                                      </FormGroup>
                                                    }/>
                                )
                              }
                              {
                                value?.questionNumberAnswer >= 3 && (
                                  <FormControlLabel value="3" control={<Radio checked={value?.answer?.includes('3')}
                                                                              disabled={data?.examResultId != null}/>}
                                                    label={
                                                      <FormGroup row>
                                                        <Box display="flex" alignItems="center">
                                                          <span>{parseHTMLToString(value?.questionContentAnswer3)}</span>
                                                          {( data?.totalScore && value?.questionAnswer?.includes('3')) && (<CheckIcon style={{ marginLeft: 8, color: 'green' }} />)}
                                                        </Box>
                                                      </FormGroup>
                                                    }/>
                                )
                              }
                              {
                                value?.questionNumberAnswer >= 4 && (
                                  <FormControlLabel value="4" control={<Radio checked={value?.answer?.includes('4')}
                                                                              disabled={data?.examResultId != null}/>}
                                                    label={
                                                      <FormGroup row>
                                                        <Box display="flex" alignItems="center">
                                                          <span>{parseHTMLToString(value?.questionContentAnswer4)}</span>
                                                          {( data?.totalScore && value?.questionAnswer?.includes('4')) && (<CheckIcon style={{ marginLeft: 8, color: 'green' }} />)}
                                                        </Box>
                                                      </FormGroup>
                                                    }/>
                                )
                              }
                              {
                                value?.questionNumberAnswer >= 5 && (
                                  <FormControlLabel value="5" control={<Radio checked={value?.answer?.includes('5')}
                                                                              disabled={data?.examResultId != null}/>}
                                                    label={
                                                      <FormGroup row>
                                                        <Box display="flex" alignItems="center">
                                                          <span>{parseHTMLToString(value?.questionContentAnswer5)}</span>
                                                          {( data?.totalScore && value?.questionAnswer?.includes('5')) && (<CheckIcon style={{ marginLeft: 8, color: 'green' }} />)}
                                                        </Box>
                                                      </FormGroup>
                                                    }/>
                                )
                              }
                            </RadioGroup>
                          </Box>
                        )
                      }
                      {
                        value?.questionType === 1 && (
                          <div key={questionOrder}>
                            {
                              data?.examResultId == null && (
                                <div>
                                  <RichTextEditor
                                    content={tmpTextAnswer}
                                    onContentChange={(value) =>
                                      handleAnswerTextChange(value, questionOrder)
                                    }
                                  />
                                  <DropzoneArea
                                    dropzoneClass={classes.dropZone}
                                    filesLimit={20}
                                    showPreviews={true}
                                    showPreviewsInDropzone={false}
                                    useChipsForPreview
                                    dropzoneText={`Kéo và thả tệp vào đây hoặc nhấn để chọn tệp cho Câu hỏi số ${questionOrder}`}
                                    previewText="Xem trước:"
                                    previewChipProps={{
                                      variant: "outlined",
                                      color: "primary",
                                      size: "medium",
                                    }}
                                    getFileAddedMessage={(fileName) =>
                                      `Tệp ${fileName} tải lên thành công`
                                    }
                                    getFileRemovedMessage={(fileName) => `Tệp ${fileName} đã loại bỏ`}
                                    getFileLimitExceedMessage={(filesLimit) =>
                                      `Vượt quá số lượng tệp tối đa được cho phép. Chỉ được phép tải lên tối đa ${filesLimit} tệp.`
                                    }
                                    alertSnackbarProps={{
                                      anchorOrigin: {vertical: "bottom", horizontal: "right"},
                                      autoHideDuration: 1800,
                                    }}
                                    onChange={(files) => handleAnswerFileChange(files, questionOrder)}
                                  ></DropzoneArea>
                                </div>
                              )
                            }
                            {
                              data?.examResultId != null && (
                                <div style={{display: "flex", alignItems: "center"}}><strong style={{marginRight: '10px'}}>Trả
                                  lời:</strong>{parseHTMLToString(value?.answer)}</div>
                              )
                            }
                            {
                              data?.examResultId != null && value?.filePathAnswer != null && value?.filePathAnswer !== '' && (
                                <div style={{marginTop: '10px'}}>
                                  <strong>File trả lời đính kèm:</strong>
                                  {
                                    value?.filePathAnswer.split(';').map(item => {
                                      return (
                                        <div style={{display: 'flex', alignItems: 'center'}}>
                                          <AttachFileOutlined></AttachFileOutlined>
                                          <p style={{fontWeight: 'bold', cursor: 'pointer'}}
                                             onClick={() => handleOpenFilePreviewDialog(item)}>{getFilenameFromString(item)}</p>
                                        </div>
                                      )
                                    })
                                  }
                                </div>
                              )
                            }
                            {
                              data?.totalScore && (
                                <div style={{display: "flex", alignItems: "center"}}>
                                  <strong style={{marginRight: '10px'}}>Đáp án:</strong>{parseHTMLToString(value?.questionAnswer)}
                                </div>
                              )
                            }
                          </div>
                        )
                      }
                      {
                        data?.totalScore && (
                          <div style={{display: "flex", alignItems: "center"}}>
                            <strong style={{marginRight: '10px'}}>Giải thích:</strong>{parseHTMLToString(value?.questionExplain)}
                          </div>
                        )
                      }
                    </Box>
                  </div>
                )
              })
            }

            {/*{*/}
            {/*  data?.examResultId == null && (*/}
            {/*    <DropzoneArea*/}
            {/*      dropzoneClass={classes.dropZone}*/}
            {/*      filesLimit={20}*/}
            {/*      showPreviews={true}*/}
            {/*      showPreviewsInDropzone={false}*/}
            {/*      useChipsForPreview*/}
            {/*      dropzoneText={`Kéo và thả tệp vào đây hoặc nhấn để chọn tệp cho bài thi`}*/}
            {/*      previewText="Xem trước:"*/}
            {/*      previewChipProps={{*/}
            {/*        variant: "outlined",*/}
            {/*        color: "primary",*/}
            {/*        size: "medium",*/}
            {/*      }}*/}
            {/*      getFileAddedMessage={(fileName) =>*/}
            {/*        `Tệp ${fileName} tải lên thành công`*/}
            {/*      }*/}
            {/*      getFileRemovedMessage={(fileName) => `Tệp ${fileName} đã loại bỏ`}*/}
            {/*      getFileLimitExceedMessage={(filesLimit) =>*/}
            {/*        `Vượt quá số lượng tệp tối đa được cho phép. Chỉ được phép tải lên tối đa ${filesLimit} tệp.`*/}
            {/*      }*/}
            {/*      alertSnackbarProps={{*/}
            {/*        anchorOrigin: {vertical: "bottom", horizontal: "right"},*/}
            {/*        autoHideDuration: 1800,*/}
            {/*      }}*/}
            {/*      onChange={(files) => setAnswersFiles(files)}*/}
            {/*    ></DropzoneArea>*/}
            {/*  )*/}
            {/*}*/}

            {/*{*/}
            {/*  data?.examResultId != null && (*/}
            {/*    <div>*/}
            {/*      <h4 style={{marginBottom: 0, fontSize: '18px'}}>File đính kèm:</h4>*/}
            {/*      {*/}
            {/*        (data?.answerFiles == null || data?.answerFiles == '') ?*/}
            {/*          (*/}
            {/*            <div>N/A</div>*/}
            {/*          ) :*/}
            {/*          (*/}
            {/*            data?.answerFiles.split(';').map(item => {*/}
            {/*              return (*/}
            {/*                <div style={{display: 'flex', alignItems: 'center'}}>*/}
            {/*                  <AttachFileOutlined></AttachFileOutlined>*/}
            {/*                  <p style={{fontWeight: 'bold', cursor: 'pointer'}}*/}
            {/*                     onClick={() => handleOpenFilePreviewDialog(item)}>{getFilenameFromString(item)}</p>*/}
            {/*                </div>*/}
            {/*              )*/}
            {/*            })*/}
            {/*          )*/}
            {/*      }*/}
            {/*    </div>*/}
            {/*  )*/}
            {/*}*/}

          </CardContent>
          <CardActions style={{justifyContent: 'flex-end'}}>
            <Button
              variant="contained"
              onClick={() => history.push("/exam/my-exam")}
            >
              Hủy
            </Button>
            {
              data?.examResultId == null && (
                <Button
                  disabled={isLoading}
                  variant="contained"
                  color="primary"
                  style={{marginLeft: "15px"}}
                  onClick={handleSubmit}
                  type="submit"
                >
                  {isLoading ? <CircularProgress/> : "Nộp bài"}
                </Button>
              )
            }
          </CardActions>
        </Card>
        <QuestionFilePreview
          open={openFilePreviewDialog}
          setOpen={setOpenFilePreviewDialog}
          file={filePreview}>
        </QuestionFilePreview>
      </MuiPickersUtilsProvider>
    </div>
  );
}

const screenName = "MENU_EXAMINEE_PARTICIPANT";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default MyExamDetails;
