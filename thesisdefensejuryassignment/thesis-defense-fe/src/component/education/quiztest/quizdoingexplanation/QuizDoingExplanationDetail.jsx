import React, {useEffect, useImperativeHandle, useState} from 'react';
import {errorNoti, successNoti} from "../../../../utils/notification";
import {request} from "../../../../api";
import FilePreview from "../../../common/uploader/FilePreview";
import {makeStyles} from "@material-ui/core/styles";
import {Button} from "@mui/material";
import EditIcon from '@mui/icons-material/Edit';
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import DownloadIcon from '@mui/icons-material/Download';
import UpdateQuizDoingExplanationDialog from "./UpdateQuizDoingExplanationDialog";
import CustomizedDialogs from "../../../dialog/CustomizedDialogs";
import {Typography} from "@material-ui/core";

const useStyles = makeStyles(theme => ({
  solutionContainer: {
    border: '1px solid black',
    borderRadius: '5px',
    padding: '10px',
    marginBottom: '10px'
  },
  solutionExplanation: {
    marginBottom: '10px'
  },
  actions: {
    display: "flex",
    columnGap: '10px',
    marginBottom: '10px'
  }
}))

const QuizDoingExplanationDetail = React.forwardRef(({ questionId }, ref) => {
  useImperativeHandle(ref, () => ({
    reload: getSolutionsForQuestion
  }))

  const classes = useStyles();

  const [ solutions, setSolutions ] = useState([]);
  const [ attachments, setAttachments ] = useState([]);

  const [ updateExplanationDialogOpen, setUpdateExplanationDialogOpen] = useState(false);
  const [ updatedSolution, setUpdatedSolution] = useState({ solutionExplanation: null, attachment: null });

  const [explanationIdToDelete, setExplanationIdToDelete] = useState(null);
  const [confirmDeleteDialogOpen, setConfirmDeleteDialogOpen ] = useState(false);

  useEffect(getSolutionsForQuestion, []);

  function getSolutionsForQuestion() {
    let successHandler = (res) => setSolutions(res.data);
    let errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi tải dữ liệu")
    }
    request("GET", `/quiz-doing-explanations/${questionId}`, successHandler, errorHandlers);
  }

  useEffect(getCorrespondingAttachmentsForSolutions, [solutions]);

  async function getCorrespondingAttachmentsForSolutions() {
    let attachments = await Promise.all(
      solutions.map( solution => getAttachmentByStorageId(solution.attachment) )
    );
    setAttachments(attachments)
  }

  async function getAttachmentByStorageId(storageId) {
    let attachment;
    const convertResponseToBlob = (res) => {
      attachment = res.data;
    }
    let errorHandlers = {
      onError: (error) => console.log("error", error)
    }
    await request("GET", `/content/get/${storageId}`, convertResponseToBlob, errorHandlers,null, { responseType: 'blob'});
    return attachment;
  }

  function openUpdateExplanationDialog(solution, attachment) {
    setUpdatedSolution({ ...solution, attachment});
    setUpdateExplanationDialogOpen(true);
  }

  function openConfirmDeleteDialog(explanationId) {
    setExplanationIdToDelete(explanationId);
    setConfirmDeleteDialogOpen(true);
  }

  return (
    <div>
      {solutions.map((solution, index) => (
        <div key={index} className={classes.solutionContainer}>
          <div className={classes.actions}>
            <Button variant="contained" color="primary"
                    onClick={ () => openUpdateExplanationDialog(solution, attachments[index]) }>
              <EditIcon/>Chỉnh sửa
            </Button>
            <Button variant="contained" color="error"
                    onClick={ () => openConfirmDeleteDialog(solution.id) }>
              <DeleteForeverIcon/>Xóa
            </Button>
          </div>

          <div className={classes.solutionExplanation}
               dangerouslySetInnerHTML={{__html: solution.solutionExplanation}}/>
          { attachments[index] && (
            <div style={{ display: "flex", alignItems: "flex-end", columnGap: '10px'}}>
              <FilePreview file={attachments[index]} width="480" height="280"/>

              <a href={URL.createObjectURL(attachments[index])} download
                 style={{ textDecoration: "none" }}>
                <Button variant="contained" color="primary">
                  <DownloadIcon/>Tải về
                </Button>
              </a>
            </div>
          )}
        </div>
      ))}

      <UpdateQuizDoingExplanationDialog solution={updatedSolution}
                                        open={updateExplanationDialogOpen}
                                        onClose={() => setUpdateExplanationDialogOpen(false)}
                                        onUpdateSuccess={getSolutionsForQuestion}/>

      <ConfirmDeleteExplanationDialog explanationId={explanationIdToDelete}
                                      open={confirmDeleteDialogOpen}
                                      onClose={() => setConfirmDeleteDialogOpen(false)}
                                      onDeleteSuccess={getSolutionsForQuestion}/>
    </div>
  );
})


function ConfirmDeleteExplanationDialog(props) {

  function deleteExplanation(explanationId) {
    const successHandler = res => {
      successNoti("Xóa cách làm thành công, xem kết quả trên giao diện!")
      props.onClose();
      props.onDeleteSuccess(explanationId);
    }
    const errorHandlers = {
      onError: (error) => {
        errorNoti("Đã xảy ra lỗi khi xóa cách làm!")
        props.onClose();
      }
    }
    request("DELETE", `/quiz-doing-explanations/${explanationId}`, successHandler, errorHandlers);
  }


  return (
    <CustomizedDialogs
      open={props.open}
      handleClose={props.onClose}
      title="Xoá cách làm?"
      content={
        <Typography gutterBottom>
          Bạn không thể hủy hành động này sau khi đã thực hiện.<br/>
          Bạn có chắc muốn xóa cách làm này ?
        </Typography>
      }
      actions={
        <>
          <Button color="primary"
                  onClick={props.onClose}>
            Hủy
          </Button>
          <Button variant="contained" color="primary"
                  onClick={() => deleteExplanation(props.explanationId)}>
            Xóa
          </Button>
        </>
      }
    />
  )
}

export default QuizDoingExplanationDetail;