import React, {useEffect, useState} from 'react';
import RichTextEditor from "../../../common/editor/RichTextEditor";
import FileUploader from "../../../common/uploader/FileUploader";
import CustomizedDialogs from "../../../dialog/CustomizedDialogs";
import TertiaryButton from "../../../button/TertiaryButton";
import PrimaryButton from "../../../button/PrimaryButton";
import {errorNoti, successNoti} from "../../../../utils/notification";
import {request} from "../../../../api";
import FilePreview from "../../../common/uploader/FilePreview";

export default function UpdateQuizDoingExplanationDialog(props) {
  let solution = props.solution;
  const [solutionExplanation, setSolutionExplanation] = useState('');
  const [attachment, setAttachment] = useState();

  useEffect(refreshStateWhenUpdatedSolutionChange, [props.solution])

  function refreshStateWhenUpdatedSolutionChange() {
    setSolutionExplanation(solution.solutionExplanation)
    setAttachment(null);
  }

  function updateQuizDoingExplanation() {
    let formData = new FormData();
    formData.append("solutionExplanation", solutionExplanation);
    formData.append("attachment", attachment);

    let successHandler = (res) => {
      successNoti("Cập nhật cách làm thành công, xem kết quả trên giao diện");
      props.onClose();
      props.onUpdateSuccess(res);
    }
    let errorHandlers = {
      onError: () => {
        errorNoti("Đã xảy ra lỗi khi cập nhật cách làm!")
        props.onClose()
      }

    }
    const config = {
      headers: {
        'Content-Type': "multipart/form-data"
      }
    }
    let explanationId = solution.id;
    request("PUT", `/quiz-doing-explanations/${explanationId}`, successHandler, errorHandlers, formData, config)
  }

  return (
    <CustomizedDialogs
      title="Cập nhật cách làm"
      open={props.open}
      handleClose={props.onClose}
      centerTitle
      contentTopDivider
      content={
        <>
          <RichTextEditor content={solutionExplanation}
                          onContentChange={htmlContent => setSolutionExplanation(htmlContent)}/>
          { (!attachment && solution.attachment) && (
            <FilePreview file={solution.attachment} width="568" height="400" style={{ marginTop: '10px'}}/>
          )}
          <FileUploader onChange={files => setAttachment(files[0])}/>
        </>
      }
      actions={
        <>
          <TertiaryButton onClick={props.onClose}>Huỷ</TertiaryButton>
          <PrimaryButton onClick={updateQuizDoingExplanation}>Cập nhật</PrimaryButton>
        </>
      }
    />
  );
}