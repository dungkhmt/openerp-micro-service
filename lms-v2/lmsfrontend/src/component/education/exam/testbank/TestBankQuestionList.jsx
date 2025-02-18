import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {SortableContainer} from "react-sortable-hoc";
import TestBankQuestionItem from "./TestBankQuestionItem";
import {MenuItem} from "@mui/material";


function TestBankQuestionList(props) {

  return (
    <div>
      {
        props?.items.map((value, index) => {
          return (
            <TestBankQuestionItem  key={`item-${index}`} index={index} id={index} value={value} setQuestionDelete={props?.setQuestionDelete}/>
          )
        })
      }
    </div>
  );
}

const screenName = "MENU_EXAM_TEST_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default SortableContainer(TestBankQuestionList);
