import React, {useState} from 'react';
import SimpleBar from "simplebar-react";
import {Button, Checkbox, TextField, Tooltip, Typography} from "@material-ui/core/";
import {request} from "../../../../api";
import {makeStyles} from "@material-ui/core/styles";
import {errorNoti, successNoti} from "../../../../utils/notification";
import TertiaryButton from "../../../button/TertiaryButton";
import PrimaryButton from "../../../button/PrimaryButton";
import CustomizedDialogs from "../../../dialog/CustomizedDialogs";

const useStyles = makeStyles((theme) => ({
  dialogContent: {
    paddingBottom: theme.spacing(1),
    width: 362
  },
  customScrollbar: {
    height: "100%",
    maxHeight: 400,
    width: 330,
    overflowX: "hidden",
    overscrollBehaviorY: "none"
  }
}))

export default function GenerateQuizTestGroupDialog(props) {
  const testId = props.testId;
  const classes = useStyles();
  const [generatedGroupsQuantity, setGeneratedGroupsQuantity] = useState(1);

  const handleChangeNumberGroups = (event) => {
    setGeneratedGroupsQuantity(event.target.value);
  };

  function generateQuizGroups() {
    let data = {
      quizTestId: testId,
      numberOfQuizTestGroups: generatedGroupsQuantity
    };
    const successHandler = (res) => {
      if (props.onGenerateSuccess) {
        props.onGenerateSuccess(res)
      }
      successNoti("Thêm đề thành công, xem kết quả trên giao diện!", 3000);
      props.onClose();
    }
    const errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi thêm đề!", 3000)
    }
    request("POST", "generate-quiz-test-group", successHandler, errorHandlers, data);
  }
  
  return (
    <CustomizedDialogs
      open={props.open}
      handleClose={props.onClose}
      title="Sinh đề mới"
      content={
        <>
          <Typography color="textSecondary" gutterBottom>
            Nhập số lượng đề cần sinh thêm
          </Typography>
          <SimpleBar className={classes.customScrollbar}/>
          <TextField
            required
            id="standard-required"
            label="Required"
            defaultValue="1"
            onChange={handleChangeNumberGroups}
          />
        </>
      }
      actions={
        <>
          <TertiaryButton onClick={props.onClose}>Huỷ</TertiaryButton>
          <PrimaryButton onClick={generateQuizGroups}>Sinh đề</PrimaryButton>
        </>
      }
      style={{ content: classes.dialogContent }}
    />
  );
}