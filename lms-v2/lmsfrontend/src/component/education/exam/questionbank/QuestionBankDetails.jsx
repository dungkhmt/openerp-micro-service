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
import {MenuItem} from "@mui/material";
import {AttachFileOutlined} from "@material-ui/icons";
import QuestionFilePreview from "./QuestionFilePreview";

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
        <DialogTitle>Chi tiết câu hỏi</DialogTitle>
        <DialogContent>
          <div>
            <div>
              <h3>Mã câu hỏi</h3>
              <p>{question?.code}</p>
            </div>
            <div>
              <h3>Loại câu hỏi</h3>
              <p>{question?.type === 0 ? 'Trắc nghiệm' : 'Tự luận'}</p>
            </div>
            <div>
              <h3>Nội dung câu hỏi</h3>
              <p>{parse(question?.content)}</p>
              {
                (question?.filePath) && (
                  question?.filePath.split(';').map(item => {
                    return (
                      <div style={{display: 'flex', alignItems : 'center'}}>
                        <AttachFileOutlined></AttachFileOutlined>
                        <p style={{fontWeight : 'bold', cursor : 'pointer'}} onClick={() => handleOpenFilePreviewDialog(item)}>{getFilenameFromString(item)}</p>
                      </div>
                    )
                  })
                )
              }
            </div>
            {
              (question?.type === 0) && (
                <>
                  <div>
                    <h3>Số đáp án</h3>
                    <p>{question?.numberAnswer}</p>
                  </div>
                  <div>
                    <h3>Nội dung phương án 1</h3>
                    <p>{parse(question?.contentAnswer1)}</p>
                  </div>
                  {
                    (question?.numberAnswer >= 2) && (
                      <div>
                        <h3>Nội dung phương án 2</h3>
                        <p>{parse(question?.contentAnswer2)}</p>
                      </div>
                    )
                  }
                  {
                    (question?.numberAnswer >= 3) && (
                      <div>
                        <h3>Nội dung phương án 3</h3>
                        <p>{parse(question?.contentAnswer3)}</p>
                      </div>
                    )
                  }
                  {
                    (question?.numberAnswer >= 4) && (
                      <div>
                        <h3>Nội dung phương án 4</h3>
                        <p>{parse(question?.contentAnswer4)}</p>
                      </div>
                    )
                  }
                  {
                    (question?.numberAnswer >= 5) && (
                      <div>
                        <h3>Nội dung phương án 5</h3>
                        <p>{parse(question?.contentAnswer5)}</p>
                      </div>
                    )
                  }
                  <div>
                    <h3>Nhiều lựa chọn</h3>
                    <p>{question?.multichoice ? 'Có' : 'không'}</p>
                  </div>
                </>
              )
            }
            <div>
              <h3>Nội dung đáp án</h3>
              <p>{parse(question?.answer)}</p>
            </div>
            <div>
              <h3>Nội dung giải thích</h3>
              <p>{parse(question?.explain)}</p>
            </div>
          </div>
          <div>
            <Button
              variant="contained"
              onClick={closeDialog}
            >
              Hủy
            </Button>
          </div>
          <QuestionFilePreview
            open={openFilePreviewDialog}
            setOpen={setOpenFilePreviewDialog}
            file={filePreview}>
          </QuestionFilePreview>
        </DialogContent>
      </Dialog>
    </div>
  );
}

const screenName = "MENU_EXAM_QUESTION_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default QuestionBankDetails;
