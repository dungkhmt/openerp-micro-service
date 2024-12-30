import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {SortableElement} from "react-sortable-hoc";
import parser from "html-react-parser";
import InfoIcon from "@mui/icons-material/Info";
import DeleteIcon from "@material-ui/icons/Delete";
import {Box} from "@material-ui/core";
import QuestionBankDetails from "../questionbank/QuestionBankDetails";


function TestBankQuestionItem(props) {

  const [openDetailsDialog, setOpenDetailsDialog] = useState(false);

  const handleDetailsQuestion = () => {
    setOpenDetailsDialog(true)
  };

  const handleDeleteQuestionSelected = () => {
    // let tmpQuestionSelectedList = questionSelectedList.filter(item => item.id !== data.id);
    // setQuestionSelectedList(tmpQuestionSelectedList)
    console.log('props.value',props.value)
  }

  return (
    <div style={{
      border: '2px solid #f5f5f5',
      display: 'flex',
      justifyContent: 'space-between',
      borderRadius: '10px',
      padding: '10px',
      marginBottom: '10px'}}>
      <Box display="flex" flexDirection='column' width="calc(100% - 110px)" style={{cursor: 'pointer'}}>
        <div style={{display: 'flex'}}>
          <span style={{display: "block", fontWeight: 'bold', marginRight: '5px'}}>Câu {props.id + 1}.</span>
          <span style={{fontStyle: 'italic'}}>({props.value.questionType === 0 ? 'Trắc nghiệm' : 'Tự luận'})</span>
        </div>
        <p>{parser(props.value.content)}</p>
      </Box>
      <Box display="flex" justifyContent='space-between' width="110px">
        <button style={{height: 'max-content', padding: '8px', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold'}} onClick={(event) => {
          handleDetailsQuestion()
          event.preventDefault()
          event.stopPropagation()
        }}>
          Chi tiết
        </button>
        <button style={{height: 'max-content', padding: '8px', border: 'none', borderRadius: '8px', color: 'red', cursor: 'pointer', fontWeight: 'bold'}}  onClick={(event) => {
          handleDeleteQuestionSelected()
          event.preventDefault()
          event.stopPropagation()
        }}>
          Xoá
        </button>
      </Box>

      {
        openDetailsDialog && (
          <QuestionBankDetails
            open={openDetailsDialog}
            setOpen={setOpenDetailsDialog}
            question={props.value}
          />
        )
      }
    </div>
  );
}

const screenName = "MENU_EXAM_TEST_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default SortableElement(TestBankQuestionItem);
