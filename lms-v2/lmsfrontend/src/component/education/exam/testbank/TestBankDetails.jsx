import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {
  Box,
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  Input
} from "@material-ui/core";
import {request} from "../../../../api";
import {toast} from "react-toastify";
import {DialogActions} from "@mui/material";
import parser from "html-react-parser";
import TestBankQuestionItem from "./TestBankQuestionItem";
import QuestionBankDetails from "../questionbank/QuestionBankDetails";

function TestBankDetails(props) {

  const { open, setOpen, data} = props;

  const [questions, setQuestions] = useState(null)

  useEffect(() => {
    let tmpData = []
    for(let question of data?.examTestQuestionDetails){
      tmpData.push({
        id: question.questionId,
        code: question.questionCode,
        type: question.questionType,
        content: question.questionContent,
        filePath: question.questionFile,
        numberAnswer: question.questionNumberAnswer,
        contentAnswer1: question.questionContentAnswer1,
        contentAnswer2: question.questionContentAnswer2,
        contentAnswer3: question.questionContentAnswer3,
        contentAnswer4: question.questionContentAnswer4,
        contentAnswer5: question.questionContentAnswer5,
        multichoice: question.questionMultichoice,
        answer: question.questionAnswer,
        explain: question.questionExplain,
        order: question.questionOrder
      })
    }
    console.log('tmpData')
    setQuestions(tmpData)
  }, []);

  const [openDetailsDialog, setOpenDetailsDialog] = useState(false);

  const handleDetailsQuestion = () => {
    setOpenDetailsDialog(true)
  };

  const closeDialog = () => {
    setOpen(false)
  }

  return (
    <div>
      <Dialog open={open} fullWidth maxWidth="md">
        <DialogTitle>{data?.name}</DialogTitle>
        <DialogContent>
          <h4 style={{margin: '0'}}>Mã đề: {data?.code}</h4>
          <p>{parser(data?.description)}</p>

          <div>
            {
              questions?.map((value, index) => {
                return (
                  <div style={{
                    border: '2px solid #f5f5f5',
                    display: 'flex',
                    justifyContent: 'space-between',
                    borderRadius: '10px',
                    padding: '10px',
                    marginBottom: '10px'
                  }}>
                    <Box display="flex"
                         flexDirection='column'
                         width="calc(100% - 70px)">
                      <div style={{display: 'flex'}}>
                        <span style={{display: "block", fontWeight: 'bold', marginRight: '5px'}}>Câu {index + 1}.</span>
                        <span style={{fontStyle: 'italic'}}>({value.type === 0 ? 'Trắc nghiệm' : 'Tự luận'})</span>
                      </div>
                      <p>{parser(value.content)}</p>
                      {
                        value.type === 0 &&
                        (<Box display="flex" flexDirection='column'>
                          <div style={{display: "flex", alignItems: "center"}}>
                            <span style={{marginRight: "5px"}}>1.</span>
                            <span>{parser(value.contentAnswer1)}</span>
                          </div>
                          {
                            value.numberAnswer >= 2 && (
                              <div style={{display: "flex", alignItems: "center"}}>
                                <span style={{marginRight: "5px"}}>2.</span>
                                <span>{parser(value.contentAnswer2)}</span>
                              </div>
                            )
                          }
                          {
                            value.numberAnswer >= 3 && (
                              <div style={{display: "flex", alignItems: "center"}}>
                                <span style={{marginRight: "5px"}}>3.</span>
                                <span>{parser(value.contentAnswer3)}</span>
                              </div>
                            )
                          }
                          {
                            value.numberAnswer >= 4 && (
                              <div style={{display: "flex", alignItems: "center"}}>
                                <span style={{marginRight: "5px"}}>4.</span>
                                <span>{parser(value.contentAnswer4)}</span>
                              </div>
                            )
                          }
                          {
                            value.numberAnswer >= 5 && (
                              <div style={{display: "flex", alignItems: "center"}}>
                                <span style={{marginRight: "5px"}}>5.</span>
                                <span>{parser(value.contentAnswer5)}</span>
                              </div>
                            )
                          }
                        </Box>)
                      }
                    </Box>
                    <Box display="flex" justifyContent='space-between' width="70px">
                      <button style={{
                        height: 'max-content',
                        padding: '8px',
                        border: 'none',
                        borderRadius: '8px',
                        cursor: 'pointer', fontWeight: 'bold'
                      }} onClick={handleDetailsQuestion}>
                        Chi tiết
                      </button>
                    </Box>

                    {
                      openDetailsDialog && (
                        <QuestionBankDetails
                          open={openDetailsDialog}
                          setOpen={setOpenDetailsDialog}
                          question={value}
                        />
                      )
                    }
                  </div>
                )
              })
            }
          </div>
        </DialogContent>
        <DialogActions>
          <Button
            variant="contained"
            onClick={closeDialog}
          >
            Hủy
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

const screenName = "MENU_EXAM_TEST_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default TestBankDetails;
