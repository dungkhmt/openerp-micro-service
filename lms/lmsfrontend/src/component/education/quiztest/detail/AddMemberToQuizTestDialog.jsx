import {makeStyles} from "@material-ui/core/styles";

import PrimaryButton from "component/button/PrimaryButton";
import TertiaryButton from "component/button/TertiaryButton";
import CustomizedDialogs from "component/dialog/CustomizedDialogs";
import React, {useEffect, useMemo, useState} from "react";
import {request} from "../../../../api";
import {errorNoti, successNoti} from "../../../../utils/notification";
import AutocompleteItem from "../../../common/form/AutocompleteItem";
import SelectItem from "../../../common/form/SelectItem";
import {Stack} from "@mui/material";

const useStyles = makeStyles((theme) => ({
  dialogContent: {
    minWidth: 480,
    minHeight: 64,
  },
  btn: { margin: "4px 8px" },
}));

export default function AddMemberToQuizTestDialog(props) {
  const classes = useStyles();
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [selectedRole, setSelectedRole] = useState("");
  const [assignableRoles, setAssignableRoles] = useState([]);

  const roleOptions = useMemo(() => {
    return assignableRoles.map((role) => ({ value: role, label: role }));
  }, [assignableRoles]);

  async function getUsers(keyword, currentPage, pageSize) {
    let users;

    const config = {
      params: { keyword, page: currentPage, size: pageSize },
    };
    let successHandler = (res) => {
      users = res.data.contents.content.map((user) => ({
        userName: user.userName,
        fullName: [user.lastName, user.middleName, user.firstName].join(" "),
      }));
    };
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi khi lấy dữ liệu user", true),
    };
    await request(
      "GET",
      "/search-user",
      successHandler,
      errorHandlers,
      null,
      config
    );

    return users;
  }

  async function getUserOptions(search) {
    let users = await getUsers(search, 0, 1000);
    return users.map((user) => ({
      value: user.userName,
      label: `${user.fullName} (${user.userName})`,
    }));
  }

  useEffect(() => {
    setSelectedRole("");
    getRolesNotGrantedToUser(selectedUserId);
  }, [selectedUserId]);

  function getRolesNotGrantedToUser(userId) {
    if (!userId) return;
    let successHandler = (res) => setAssignableRoles(res.data);
    let errorHandlers = {
      onError: (error) =>
        errorNoti("Đã xảy ra lỗi khi lấy dữ liệu vai trò", true),
    };
    request(
      "GET",
      `/get-roles-user-not-granted-in-quiz-test/${props.testId}/${userId}`,
      successHandler,
      errorHandlers
    );
  }

  function addMemberRole() {
    let newMemberRole = {
      testId: props.testId,
      userId: selectedUserId,
      roleId: selectedRole,
    };
    let successHandler = (res) => {
      successNoti("Thêm vai trò thành viên thành công!", true);
      props.onAddSuccess();
      closeAndResetData();
    };
    let errorHandlers = {
      onError: (error) =>
        errorNoti("Đã xảy ra lỗi khi thêm vai trò thành viên", true),
    };
    request(
      "POST",
      "/add-quiz-test-participant-role",
      successHandler,
      errorHandlers,
      newMemberRole
    );
  }

  function closeAndResetData() {
    setSelectedUserId(null);
    setSelectedRole("");
    props.onClose();
  }

  return (
    <CustomizedDialogs
      open={props.open}
      handleClose={closeAndResetData}
      title="Thêm thành viên vào bài thi"
      content={
        <Stack spacing={2} style={{ padding: "4px 12px" }}>
          <AutocompleteItem
            label="Thành viên"
            style={{ width: "100%" }}
            optionsRetriever={getUserOptions}
            placeholder="Nhập login ID để tìm kiếm"
            onChange={(value) => setSelectedUserId(value)}
          />
          <SelectItem
            label="Vai trò"
            style={{ width: "100%" }}
            value={selectedRole}
            options={roleOptions}
            disabled={!selectedUserId}
            onChange={(value) => setSelectedRole(value)}
          />
        </Stack>
      }
      actions={
        <>
          <TertiaryButton className={classes.btn} onClick={closeAndResetData}>
            Huỷ
          </TertiaryButton>
          <PrimaryButton className={classes.btn} onClick={addMemberRole}>
            Cập nhật
          </PrimaryButton>
        </>
      }
      classNames={{ content: classes.dialogContent }}
    />
  );
}
