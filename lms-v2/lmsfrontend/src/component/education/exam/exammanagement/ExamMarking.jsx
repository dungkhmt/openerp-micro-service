import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {
  Box,
  Button, CircularProgress,
  Dialog,
  DialogContent,
  DialogTitle,
  Input
} from "@material-ui/core";
import {DialogActions, Radio, RadioGroup} from "@mui/material";
import {formatDateTime} from "../ultils/DateUltils";
import {request} from "../../../../api";
import {toast} from "react-toastify";
import TestBankDetails from "../testbank/TestBankDetails";
import {DataGrid} from "@material-ui/data-grid";
import {Scoreboard} from "@mui/icons-material";
import {AccessTime, AttachFileOutlined, Cancel, Timer} from "@material-ui/icons";
import {getFilenameFromString, getFilePathFromString} from "../ultils/FileUltils";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import RichTextEditor from "../../../common/editor/RichTextEditor";
import {DropzoneArea} from "material-ui-dropzone";
import QuestionFilePreview from "../questionbank/QuestionFilePreview";
import CheckIcon from '@mui/icons-material/Check';
import FormGroup from "@material-ui/core/FormGroup";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import TextField from "@material-ui/core/TextField";
import indexEsm from "@heroicons/react";
import {parseHTMLToString} from "../ultils/DataUltils";

function ExamMarking(props) {

  const { open, setOpen, data, setDataDetails} = props;

  const [isLoading, setIsLoading] = useState(false);
  const [dataAnswers, setDataAnswers] = useState([]);
  const [totalScore, setTotalScore] = useState(0);
  const [comment, setComment] = useState(data?.comment ? data?.comment : '');
  const [openFilePreviewDialog, setOpenFilePreviewDialog] = useState(false);
  const [filePreview, setFilePreview] = useState(null);

  useEffect(() => {
    let tmpDataAnswers = []
    let totalScore = 0
    for(let item of data?.questionList){
      let score = 0;
      if(item?.questionType === 0){
        if(checkAnswerRadioAndCheckbox(item?.questionType, item?.questionAnswer, item?.answer)){
          score = 1
        }
      }
      if(item?.score){
        score = item?.score
      }
      tmpDataAnswers.push({
        questionOrder: item?.questionOrder,
        id: item?.examResultDetailsId,
        examResultId: data?.examResultId,
        examQuestionId: item?.questionId,
        answer: item?.answer,
        filePath: item?.filePathAnswer,
        score: score
      })
      totalScore += score
    }

    setDataAnswers(tmpDataAnswers)
    setTotalScore(totalScore)
  }, []);

  const handleMarking = () => {
    const body = {
      examResultId: data?.examResultId,
      totalScore: totalScore,
      comment: comment,
      examResultDetails: dataAnswers
    }

    setIsLoading(true)
    request(
      "post",
      '/exam/marking-exam',
      (res) => {
        if(res.status === 200){
          if(res.data.resultCode === 200){
            toast.success(res.data.resultMsg)
            setIsLoading(false)
            handleExamDetails()
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
      body
    );
  }

  const handleExamDetails = () => {
    const body = {
      id: data?.examId
    }
    request(
      "post",
      `/exam/details`,
      (res) => {
        if(res.data.resultCode === 200){
          setDataDetails(res.data.data)
          closeDialog()
        }else{
          toast.error(res.data.resultMsg)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  };

  const handleOpenFilePreviewDialog = (data) => {
    setOpenFilePreviewDialog(true)
    setFilePreview(getFilePathFromString(data))
  };

  const handleMarkingScore = (event, questionOrder) => {
    let tmpScore = 0;
    if(event.target.value === ''){
      dataAnswers[questionOrder-1].score = 0
    }else{
      dataAnswers[questionOrder-1].score = parseInt(event.target.value)
    }

    let totalScore = 0
    for(let item of dataAnswers){
      totalScore += item?.score
    }

    setDataAnswers(dataAnswers)
    setTotalScore(totalScore)
  };

  const checkAnswerRadioAndCheckbox = (questionType, answerQuestion, answerStudent) => {
    if(questionType === 0){
      const answerQuestions = answerQuestion.split(',').sort();
      const answerStudents = answerStudent.split(',').sort();

      return answerStudents.every(elem => answerQuestions.includes(elem));
    }
  }

  const closeDialog = () => {
    setOpen(false)
  }

  const handleKeyPress = (event) => {
    const regex = /^[0-9]+$/
    if (!regex.test(event.key)) {
      event.preventDefault();
    }
  }

  return (
    <div>
      <Dialog open={open} fullWidth maxWidth="lg">
        <DialogTitle>Bài thi của <strong>{data?.examStudentName}({data?.examStudentCode})</strong></DialogTitle>
        <DialogContent>
          <div style={{display: "flex", justifyContent: "space-between",  width: '100%'}}>
            <div>
              <div style={{display: "flex", alignItems: "center", marginBottom: '10px'}}>
                <Scoreboard/>
                <p style={{padding: 0, margin: 0}}><strong>Tổng điểm: </strong> {totalScore}</p>
              </div>
              <div style={{display: "flex", alignItems: "center", marginBottom: '10px'}}>
                <Timer/>
                <p style={{padding: 0, margin: 0}}><strong>Tổng thời gian làm: </strong> {data?.totalTime} (phút)</p>
              </div>
              <div style={{display: "flex", alignItems: "center", marginBottom: '10px'}}>
                <AccessTime/>
                <p style={{padding: 0, margin: 0}}><strong>Thời gian nộp: </strong> {formatDateTime(data?.submitedAt)}
                </p>
              </div>
            </div>
            <div style={{display: "flex", flexDirection: "column"}}>
              <p style={{margin: 0, padding: 0, display: "flex"}}>
              <span style={{
                fontWeight: "bold",
                marginRight: '5px'
              }}>Email:</span>{data?.examStudentEmail}</p>
              <p style={{margin: 0, padding: 0, display: "flex"}}>
                <span style={{fontWeight: "bold", marginRight: '5px'}}>Phone:</span>{data?.examStudentPhone}
              </p>
            </div>
          </div>

          {
            data?.questionList?.map(value => {
              const questionOrder = value?.questionOrder;
              return (
                <div
                  key={questionOrder}
                  style={{
                    border: '2px solid #f5f5f5',
                    borderColor:
                      value?.questionType === 0 ?
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
                          label="Nhập điểm"
                          onKeyPress={handleKeyPress}
                          style={{width: "80px", marginLeft: "16px"}}
                          value={dataAnswers[questionOrder - 1]?.score}
                          onChange={(event) => {
                            handleMarkingScore(event, questionOrder);
                          }}
                          InputLabelProps={{
                            shrink: true,
                          }}
                        />
                      </div>
                    </div>

                    <p style={{display: "flex", alignItems: "center"}}>
                      <strong style={{marginRight: '10px'}}>Câu hỏi: </strong>{parseHTMLToString(value?.questionContent)}
                    </p>
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
                                  {value?.questionAnswer?.includes('1') && (
                                    <CheckIcon style={{marginLeft: 8, color: 'green'}}/>)}
                                </Box>
                              </FormGroup>
                            }
                            control={
                              <Checkbox color="primary"
                                        checked={value?.answer?.includes('1')}
                                        disabled/>
                            }
                          />
                          {
                            value?.questionNumberAnswer >= 2 && (
                              <FormControlLabel
                                label={
                                  <FormGroup row>
                                    <Box display="flex" alignItems="center">
                                      <span>{parseHTMLToString(value?.questionContentAnswer2)}</span>
                                      {value?.questionAnswer?.includes('2') && (
                                        <CheckIcon style={{marginLeft: 8, color: 'green'}}/>)}
                                    </Box>
                                  </FormGroup>
                                }
                                control={
                                  <Checkbox color="primary"
                                            checked={value?.answer?.includes('2')}
                                            disabled/>
                                }
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
                                      {value?.questionAnswer?.includes('3') && (
                                        <CheckIcon style={{marginLeft: 8, color: 'green'}}/>)}
                                    </Box>
                                  </FormGroup>
                                }
                                control={
                                  <Checkbox color="primary"
                                            checked={value?.answer?.includes('3')}
                                            disabled/>
                                }
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
                                      {value?.questionAnswer?.includes('4') && (
                                        <CheckIcon style={{marginLeft: 8, color: 'green'}}/>)}
                                    </Box>
                                  </FormGroup>
                                }
                                control={
                                  <Checkbox color="primary"
                                            checked={value?.answer?.includes('4')}
                                            disabled/>
                                }
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
                                      {value?.questionAnswer?.includes('5') && (
                                        <CheckIcon style={{marginLeft: 8, color: 'green'}}/>)}
                                    </Box>
                                  </FormGroup>
                                }
                                control={
                                  <Checkbox color="primary"
                                            checked={value?.answer?.includes('5')}
                                            disabled/>
                                }
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
                          >
                            <FormControlLabel
                              value="1"
                              control={
                                <Radio
                                  checked={value?.answer?.includes('1')}
                                  disabled
                                />
                              }
                              label={
                                <FormGroup row>
                                  <Box display="flex" alignItems="center">
                                    <span>{parseHTMLToString(value?.questionContentAnswer1)}</span>
                                    {value?.questionAnswer?.includes('1') && (
                                      <CheckIcon style={{marginLeft: 8, color: 'green'}}/>)}
                                  </Box>
                                </FormGroup>
                              }
                            />
                            {
                              value?.questionNumberAnswer >= 2 && (
                                <FormControlLabel
                                  value="2"
                                  control={
                                    <Radio
                                      checked={value?.answer?.includes('2')}
                                      disabled
                                    />
                                  }
                                  label={
                                    <FormGroup row>
                                      <Box display="flex" alignItems="center">
                                        <span>{parseHTMLToString(value?.questionContentAnswer2)}</span>
                                        {value?.questionAnswer?.includes('2') && (
                                          <CheckIcon style={{marginLeft: 8, color: 'green'}}/>)}
                                      </Box>
                                    </FormGroup>
                                  }
                                />
                              )
                            }
                            {
                              value?.questionNumberAnswer >= 3 && (
                                <FormControlLabel
                                  value="3"
                                  control={
                                    <Radio
                                      checked={value?.answer?.includes('3')}
                                      disabled
                                    />
                                  }
                                  label={
                                    <FormGroup row>
                                      <Box display="flex" alignItems="center">
                                        <span>{parseHTMLToString(value?.questionContentAnswer3)}</span>
                                        {value?.questionAnswer?.includes('3') && (
                                          <CheckIcon style={{marginLeft: 8, color: 'green'}}/>)}
                                      </Box>
                                    </FormGroup>
                                  }
                                />
                              )
                            }
                            {
                              value?.questionNumberAnswer >= 4 && (
                                <FormControlLabel
                                  value="4"
                                  control={
                                    <Radio
                                      checked={value?.answer?.includes('4')}
                                      disabled
                                    />
                                  }
                                  label={
                                    <FormGroup row>
                                      <Box display="flex" alignItems="center">
                                        <span>{parseHTMLToString(value?.questionContentAnswer4)}</span>
                                        {value?.questionAnswer?.includes('4') && (
                                          <CheckIcon style={{marginLeft: 8, color: 'green'}}/>)}
                                      </Box>
                                    </FormGroup>
                                  }
                                />
                              )
                            }
                            {
                              value?.questionNumberAnswer >= 5 && (
                                <FormControlLabel
                                  value="5"
                                  control={
                                    <Radio
                                      checked={value?.answer?.includes('5')}
                                      disabled
                                    />
                                  }
                                  label={
                                    <FormGroup row>
                                      <Box display="flex" alignItems="center">
                                        <span>{parseHTMLToString(value?.questionContentAnswer5)}</span>
                                        {value?.questionAnswer?.includes('5') && (
                                          <CheckIcon style={{marginLeft: 8, color: 'green'}}/>)}
                                      </Box>
                                    </FormGroup>
                                  }
                                />
                              )
                            }
                          </RadioGroup>
                        </Box>
                      )
                    }
                    {
                      value?.questionType === 1 && (
                        <div style={{display: "flex", alignItems: "center"}}>
                          <strong style={{marginRight: '10px'}}>Trả lời:</strong>{parseHTMLToString(value?.answer)}
                        </div>
                      )
                    }
                    {
                      value?.questionType === 1 && value?.filePathAnswer != null && value?.filePathAnswer !== '' && (
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
                      value?.questionType === 1 && (
                        <div style={{display: "flex", alignItems: "center"}}>
                          <strong style={{marginRight: '10px'}}>Đáp án:</strong>{parseHTMLToString(value?.questionAnswer)}
                        </div>
                      )
                    }
                    <div style={{display: "flex", alignItems: "center"}}>
                      <strong style={{marginRight: '10px'}}>Giải thích:</strong>{parseHTMLToString(value?.questionExplain)}
                    </div>
                  </Box>
                </div>
              )
            })
          }

          {/*<div>*/}
          {/*  <h4 style={{marginBottom: 0, fontSize: '18px'}}>File đính kèm:</h4>*/}
          {/*  {*/}
          {/*    (data?.answerFiles == null || data?.answerFiles == '') ?*/}
          {/*      (*/}
          {/*        <div>N/A</div>*/}
          {/*      ) :*/}
          {/*      (*/}
          {/*        data?.answerFiles.split(';').map(item => {*/}
          {/*          return (*/}
          {/*            <div style={{display: 'flex', alignItems: 'center'}}>*/}
          {/*              <AttachFileOutlined></AttachFileOutlined>*/}
          {/*              <p style={{fontWeight: 'bold', cursor: 'pointer'}}*/}
          {/*                 onClick={() => handleOpenFilePreviewDialog(item)}>{getFilenameFromString(item)}</p>*/}
          {/*            </div>*/}
          {/*          )*/}
          {/*        })*/}
          {/*      )*/}
          {/*  }*/}
          {/*</div>*/}

          <div>
            <h4 style={{marginBottom: 0, fontSize: '18px'}}>Nhận xét:</h4>
            <RichTextEditor
              content={comment}
              onContentChange={(value) =>
                setComment(value)
              }
            />
          </div>
        </DialogContent>
        <DialogActions>
          <Button
            variant="contained"
            onClick={closeDialog}
          >
            Hủy
          </Button>
          <Button
            disabled={isLoading}
            variant="contained"
            color="primary"
            style={{marginLeft: "15px"}}
            onClick={handleMarking}
            type="submit"
          >
            {isLoading ? <CircularProgress/> : "Chấm bài"}
          </Button>
        </DialogActions>
        <QuestionFilePreview
          open={openFilePreviewDialog}
          setOpen={setOpenFilePreviewDialog}
          file={filePreview}>
        </QuestionFilePreview>
      </Dialog>
    </div>
  );
}

const screenName = "MENU_EXAM_MANAGEMENT";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default ExamMarking;
