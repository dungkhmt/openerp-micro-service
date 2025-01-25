import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {SortableElement} from "react-sortable-hoc";
import InfoIcon from "@mui/icons-material/Info";
import DeleteIcon from "@material-ui/icons/Delete";
import {Box} from "@material-ui/core";
import QuestionBankDetails from "../questionbank/QuestionBankDetails";
import {parseHTMLToString} from "../ultils/DataUltils";


function TestBankQuestionItem(props) {

  const {id, value, setQuestionDelete } = props

  const [openDetailsDialog, setOpenDetailsDialog] = useState(false);

  const handleDetailsQuestion = () => {
    setOpenDetailsDialog(true)
  };

  const handleDeleteQuestionSelected = () => {
    setQuestionDelete(value)
  }

  return (
    <div style={{
      border: '2px solid #f5f5f5',
      display: 'flex',
      justifyContent: 'space-between',
      borderRadius: '10px',
      padding: '10px',
      marginBottom: '10px'}}>
      <Box display="flex"
           flexDirection='column'
           width="calc(100% - 110px)"
           style={{
              cursor: 'pointer',
              userSelect: "none",
              WebkitUserSelect: "none",
              MozUserSelect: "none",
              msUserSelect: "none"}}>
        <div style={{display: 'flex'}}>
          <span style={{display: "block", fontWeight: 'bold', marginRight: '5px'}}>Câu {id + 1}.</span>
          <span style={{fontStyle: 'italic'}}>({value.type === 0 ? 'Trắc nghiệm' : 'Tự luận'})</span>
        </div>
        <p>{parseHTMLToString(value.content)}</p>
        {
          value.type === 0 &&
            (<Box display="flex" flexDirection='column'>
              <div style={{display: "flex", alignItems: "center"}}>
                <span style={{marginRight: "5px"}}>1.</span>
                <span>{parseHTMLToString(value.contentAnswer1)}</span>
              </div>
              {
                value.numberAnswer >= 2 && (
                  <div style={{display: "flex", alignItems: "center"}}>
                    <span style={{marginRight: "5px"}}>2.</span>
                    <span>{parseHTMLToString(value.contentAnswer2)}</span>
                  </div>
                )
              }
              {
                value.numberAnswer >= 3 && (
                  <div style={{display: "flex", alignItems: "center"}}>
                    <span style={{marginRight: "5px"}}>3.</span>
                    <span>{parseHTMLToString(value.contentAnswer3)}</span>
                  </div>
                )
              }
              {
                value.numberAnswer >= 4 && (
                  <div style={{display: "flex", alignItems: "center"}}>
                    <span style={{marginRight: "5px"}}>4.</span>
                    <span>{parseHTMLToString(value.contentAnswer4)}</span>
                  </div>
                )
              }
              {
                value.numberAnswer >= 5 && (
                  <div style={{display: "flex", alignItems: "center"}}>
                    <span style={{marginRight: "5px"}}>5.</span>
                    <span>{parseHTMLToString(value.contentAnswer5)}</span>
                  </div>
                )
              }
            </Box>)
        }
      </Box>
      <Box display="flex" justifyContent='space-between' width="110px">
        <button style={{
          height: 'max-content',
          padding: '8px',
          border: 'none',
          borderRadius: '8px',
          cursor: 'pointer', fontWeight: 'bold'}} onClick={(event) => {
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
            question={value}
          />
        )
      }
    </div>
  );
}

const screenName = "MENU_EXAM_TEST_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default SortableElement(TestBankQuestionItem);
