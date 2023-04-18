import React, {useState} from 'react';
import RichTextEditor from "../../../common/editor/RichTextEditor";
import FileUploader from "../../../common/uploader/FileUploader";
import CustomizedDialogs from "../../../dialog/CustomizedDialogs";
import TertiaryButton from "../../../button/TertiaryButton";
import PrimaryButton from "../../../button/PrimaryButton";
import {errorNoti, successNoti} from "../../../../utils/notification";
import {request} from "../../../../api";

export default function CreateQuizDoingExplanationDialog(props) {
  const [solutionExplanation, setSolutionExplanation] = useState('');
  const [attachment, setAttachment] = useState();

  function createQuizDoingExplanation() {
    let formData = new FormData();
    const { questionId, testId } = props;
    let explanation = { questionId, testId, solutionExplanation }
    formData.append("quizDoingExplanation", new Blob([JSON.stringify(explanation)], { type: "application/json" }));
    formData.append("attachment", attachment);

    let successHandler = (res) => {
      successNoti("Thêm cách làm thành công, xem kết quả trên giao diện");
      props.onClose();
      props.onCreateSuccess(res);
    }
    let errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi thêm cách làm!")
    }
    const config = {
      headers: {
        'Content-Type': "multipart/form-data"
      }
    }
    request("POST", "/quiz-doing-explanations", successHandler, errorHandlers, formData, config)
  }

  return (
    <CustomizedDialogs
      title="Thêm cách làm"
      open={props.open}
      handleClose={props.onClose}
      centerTitle
      contentTopDivider
      content={
        <>
          <RichTextEditor content={solutionExplanation}
                          onContentChange={htmlContent => setSolutionExplanation(htmlContent)}/>
          <FileUploader onChange={files => setAttachment(files[0])}/>
        </>
      }
      actions={
        <>
          <TertiaryButton onClick={props.onClose}>Huỷ</TertiaryButton>
          <PrimaryButton onClick={createQuizDoingExplanation}>Thêm</PrimaryButton>
        </>
      }
    />
  );
}