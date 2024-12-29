import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {SortableElement} from "react-sortable-hoc";


function TestBankQuestionItem(props) {

  return (
    <div>
      {props.value.questionCode}
    </div>
  );
}

const screenName = "MENU_EXAM_TEST_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default SortableElement(TestBankQuestionItem);
