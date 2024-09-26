import React, {useEffect, useState} from 'react';
import SimpleBar from "simplebar-react";
import {FcDocument} from "react-icons/fc";
import CustomizedDialogs from "../../../dialog/CustomizedDialogs";
import {Typography} from "@mui/material";
import TertiaryButton from "../../../button/TertiaryButton";
import PrimaryButton from "../../../button/PrimaryButton";
import {request} from "../../../../api";
import {errorNoti, successNoti} from "../../../../utils/notification";
import {makeStyles} from "@material-ui/core/styles";
import {blue, grey} from "@material-ui/core/colors";
import {List, ListItem, ListItemIcon, ListItemText} from "@material-ui/core";

const useStyles = makeStyles(theme => ({
  dialogContent: { paddingBottom: theme.spacing(1), width: 362 },
  customScrollBar: {
    height: "100%",
    maxHeight: 400,
    overflowX: "hidden",
    overscrollBehaviorY: "none",
  },
  list: {
    paddingBottom: 0,
  },
  listItem: {
    borderRadius: 6,
    "&:hover": {
      backgroundColor: grey[200],
    },
    "&.Mui-selected": {
      backgroundColor: blue[500],
      color: theme.palette.getContrastText(blue[500]),
      "&:hover": {
        backgroundColor: blue[500],
      },
    },
  },
}))

export default function AssignQuizGroupForStudentDialog(props) {
  const classes = useStyles();
  const assignedStudent = props.assignedStudent;
  const quizGroups = props.quizGroups;

  const [selectedGroup, setSelectedGroup] = useState(null);

  useEffect(initSelectedGroup, [assignedStudent]);

  function initSelectedGroup() {
    if (!assignedStudent) {
      setSelectedGroup(null)
    } else {
      let initialSelectedGroup = quizGroups.find(group => group.groupCode === assignedStudent.testGroupCode)
      setSelectedGroup(initialSelectedGroup)
    }
  }

  function assignQuizGroupForStudent() {
    if (!selectedGroup) return;

    let data = {
      participantUserLoginId: assignedStudent.userLoginId,
      quizTestGroupId: selectedGroup.quizGroupId,
    }
    let successHandler = res => {
      if (props.onAssignSuccess) {
        props.onAssignSuccess(selectedGroup);
      }
      successNoti("Phân đề thành công. Xem kết quả trên giao diện!", true);
      props.onClose();
    }
    let errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi phân đề")
    }
    request("POST", "/add-participant-to-quiz-test-group", successHandler, errorHandlers, data);
  }

  return (
    <CustomizedDialogs
      open={props.open}
      handleClose={props.onClose}
      style={{ content: classes.dialogContent }}
      title="Phân đề cho sinh viên"
      content={
        <>
          <Typography color="textSecondary" gutterBottom>
            Chọn một đề cho <b>{assignedStudent?.fullName}</b> trong danh sách dưới đây.
          </Typography>

          <SimpleBar className={classes.customScrollBar}>
            <List className={classes.list}>
              {
                quizGroups.map((group) => (
                  <ListItem
                    key={group.quizGroupId}
                    className={classes.listItem}
                    selected={ selectedGroup?.quizGroupId === group.quizGroupId}
                    onClick={(_) => setSelectedGroup(group)}
                  >
                    <ListItemIcon>
                      <FcDocument size={24} />
                    </ListItemIcon>
                    <ListItemText primary={group.groupCode} />
                  </ListItem>
                ))
              }
            </List>
          </SimpleBar>
        </>
      }
      actions={
        <>
          <TertiaryButton onClick={props.onClose}>Huỷ</TertiaryButton>
          <PrimaryButton onClick={assignQuizGroupForStudent}>Lưu</PrimaryButton>
        </>
      }
    />
  );
}