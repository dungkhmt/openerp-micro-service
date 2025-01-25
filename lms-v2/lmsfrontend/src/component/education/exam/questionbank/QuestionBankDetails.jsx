import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  Input
} from "@material-ui/core";
import parse from "html-react-parser";
import {getFilenameFromString, getFilePathFromString} from "../ultils/FileUltils";
import {DialogActions, MenuItem} from "@mui/material";
import {AttachFileOutlined} from "@material-ui/icons";
import QuestionFilePreview from "./QuestionFilePreview";
import {parseHTMLToString} from "../ultils/DataUltils";

function QuestionBankDetails(props) {

  const { open, setOpen, question} = props;

  const [openFilePreviewDialog, setOpenFilePreviewDialog] = useState(false);
  const [filePreview, setFilePreview] = useState(null);

  const closeDialog = () => {
    setOpen(false)
  }

  const handleOpenFilePreviewDialog = (data) => {
    setOpenFilePreviewDialog(true)
    setFilePreview(getFilePathFromString(data))
  };

  return (
    <div>
      <Dialog open={open} fullWidth maxWidth="md">
        <DialogTitle>Chi tiết câu hỏi - {question?.code}</DialogTitle>
        <DialogContent>
          <div>
            <div>
              <h4 style={{marginRight: '5px', marginTop: 0}}>Nội dung câu hỏi:</h4>
              <p style={{marginTop: 0, marginBottom: 0}}>{parseHTMLToString(question?.content)}</p>
              {
                (question?.filePath) && (
                  question?.filePath.split(';').map(item => {
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
            </div>
            <div style={{display: 'flex'}}>
              <h4 style={{marginRight: '5px', marginTop: 0}}>Loại câu hỏi:</h4>
              <p style={{marginTop: 0, marginBottom: 0}}>{question?.type === 0 ? 'Trắc nghiệm' : 'Tự luận'}</p>
            </div>
            {
              (question?.type === 0) && (
                <>
                  <div style={{display: 'flex'}}>
                    <h4 style={{marginRight: '5px', marginTop: 0}}>Số đáp án:</h4>
                    <p style={{marginTop: 0, marginBottom: 0}}>{question?.numberAnswer}</p>
                  </div>
                  <div>
                    <h4 style={{marginRight: '5px', marginTop: 0}}>Nội dung phương án 1:</h4>
                    <p style={{marginTop: 0, marginBottom: 0}}>{parseHTMLToString(question?.contentAnswer1)}</p>
                  </div>
                  {
                    (question?.numberAnswer >= 2) && (
                      <div>
                        <h4 style={{marginRight: '5px', marginTop: 0}}>Nội dung phương án 2:</h4>
                        <p style={{marginTop: 0, marginBottom: 0}}>{parseHTMLToString(question?.contentAnswer2)}</p>
                      </div>
                    )
                  }
                  {
                    (question?.numberAnswer >= 3) && (
                      <div>
                        <h4 style={{marginRight: '5px', marginTop: 0}}>Nội dung phương án 3:</h4>
                        <p style={{marginTop: 0, marginBottom: 0}}>{parseHTMLToString(question?.contentAnswer3)}</p>
                      </div>
                    )
                  }
                  {
                    (question?.numberAnswer >= 4) && (
                      <div>
                        <h4 style={{marginRight: '5px', marginTop: 0}}>Nội dung phương án 4:</h4>
                        <p style={{marginTop: 0, marginBottom: 0}}>{parseHTMLToString(question?.contentAnswer4)}</p>
                      </div>
                    )
                  }
                  {
                    (question?.numberAnswer >= 5) && (
                      <div>
                        <h4 style={{marginRight: '5px', marginTop: 0}}>Nội dung phương án 5:</h4>
                        <p style={{marginTop: 0, marginBottom: 0}}>{parseHTMLToString(question?.contentAnswer5)}</p>
                      </div>
                    )
                  }
                  <div style={{display: 'flex'}}>
                    <h4 style={{marginRight: '5px', marginTop: 0}}>Nhiều lựa chọn:</h4>
                    <p style={{marginTop: 0, marginBottom: 0}}>{question?.multichoice ? 'Có' : 'không'}</p>
                  </div>
                </>
              )
            }
            <div>
              <h4 style={{marginRight: '5px', marginTop: 0}}>Nội dung đáp án:</h4>
              <p style={{marginTop: 0, marginBottom: 0}}>{parseHTMLToString(question?.answer)}</p>
            </div>
            <div>
              <h4 style={{marginRight: '5px', marginTop: 0}}>Nội dung giải thích:</h4>
              <p>{parseHTMLToString(question?.explain)}</p>
            </div>
          </div>
          <QuestionFilePreview
            open={openFilePreviewDialog}
            setOpen={setOpenFilePreviewDialog}
            file={filePreview}>
          </QuestionFilePreview>
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

const screenName = "MENU_EXAM_QUESTION_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default QuestionBankDetails;
