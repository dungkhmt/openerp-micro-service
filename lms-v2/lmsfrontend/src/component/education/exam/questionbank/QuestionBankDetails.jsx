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

function QuestionBankDetails(props) {

  const { open, setOpen, question} = props;

  console.log('question',question)

  const closeDialog = () => {
    setOpen(false)
  }

  return (
    <div>
      <Dialog open={open}>
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
        </DialogContent>
      </Dialog>
    </div>
  );
}

const screenName = "MENU_EXAM_QUESTION_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default QuestionBankDetails;
