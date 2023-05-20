import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import StandardTable from "component/table/StandardTable";
import {Button, CircularProgress} from "@mui/material";
import PublishIcon from "@mui/icons-material/Publish";
import {errorNoti, successNoti} from "utils/notification";
import {toFormattedDateTime} from "utils/dateutils";
import UpdatePermissionMemberOfContestDialog from "./UpdatePermissionMemberOfContestDialog";

export default function ContestManagerListMember(props) {
  const contestId = props.contestId;
  const [members, setMembers] = useState([]);
  const [isProcessing, setIsProcessing] = useState(false);
  const [openUpdateMemberDialog, setOpenUpdateMemberDialog] = useState(false);
  const [permissionIds, setPermissionIds] = useState([]);
  const [selectedUserRegisId, setSelectedUserRegisId] = useState(null);

  const [filename, setFilename] = useState(null);
  const [uploadMessage, setUploadMessage] = useState("");

  const columns = [
    { title: "Index", field: "index" },
    { title: "userID", field: "userId" },
    { title: "FullName", field: "fullName" },
    { title: "Role", field: "roleId" },
    { title: "Permission", field: "permissionId" },
    { title: "updated Date", field: "lastUpdatedDate" },
    { title: "Updated By UserId", field: "updatedByUserId" },
    {
      title: "Remove",
      render: (row) => (
        <Button onClick={() => handleRemove(row.id)}>Remove</Button>
      ),
    },
    {
      title: "Permission Submit",
      render: (row) => (
        <Button onClick={() => handleForbidSubmit(row.id)}>
          Update Permission Submit
        </Button>
      ),
    },
  ];

  function handleForbidSubmit(id) {
    // alert("remove " + id);
    setSelectedUserRegisId(id);
    setOpenUpdateMemberDialog(true);
  }
  function handleRemove(id) {
    // alert("remove " + id);
    setIsProcessing(true);
    let body = {
      id: id,
    };
    request(
      "post",
      "/remove-member-from-contest",
      (res) => {
        successNoti("Đã hoàn thành phân công tự động.");
        setIsProcessing(false);
      },
      {
        onError: () => {
          setIsProcessing(false);
          errorNoti("Đã có lỗi xảy ra.");
        },
        401: () => {},
      },
      body
    );
  }
  function getMembersOfContest() {
    request("get", "/get-members-of-contest/" + contestId, (res) => {
      const data = res.data.map((e, i) => ({
        index: i + 1,
        id: e.id,
        userId: e.userId,
        fullName: e.fullName,
        roleId: e.roleId,
        permissionId: e.permissionId,
        lastUpdatedDate: toFormattedDateTime(e.lastUpdatedDate),
        updatedByUserId: e.updatedByUserId,
      }));
      setMembers(data);
    });
  }
  function onUpdateInfo(selectedPermission, selectedUserRegisId) {
    setIsProcessing(true);
    let body = {
      userRegisId: selectedUserRegisId,
      permissionId: selectedPermission,
    };
    request(
      "post",
      //"/forbid-member-from-submit-to-contest",
      "/update-permission-of-member-to-contest",
      (res) => {
        successNoti("Đã hoàn thành phân công tự động.");
        setIsProcessing(false);
        setOpenUpdateMemberDialog(false);
        getMembersOfContest();
      },
      {
        onError: () => {
          setIsProcessing(false);
          errorNoti("Đã có lỗi xảy ra.");
        },
        401: () => {},
      },
      body
    );
  }
  function handleModelClose() {
    setOpenUpdateMemberDialog(false);
  }
  function getPermissions() {
    request("get", "/get-permissions-of-members-of-contest", (res) => {
      setPermissionIds(res.data);
    });
  }

  const handleUploadExcelStudentList = (event) => {
    event.preventDefault();
    setIsProcessing(true);
    setUploadMessage("");
    //alert("handleUploadExcelStudentList " + testId);
    let body = {
      contestId: contestId,
    };
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", filename);

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/upload-excel-student-list-to-contest",
      (res) => {
        setIsProcessing(false);
        console.log("handleFormSubmit, res = ", res);
        setUploadMessage(res.message);
        //if (res.status == "TIME_OUT") {
        //  alert("Time Out!!!");
        //} else {
        //}
      },
      {
        onError: (e) => {
          setIsProcessing(false);
          console.error(e);
          //alert("Time Out!!!");
        },
      },
      formData,
      config
    );
  };

  function onFileChange(event) {
    setFilename(event.target.files[0]);
  }

  useEffect(() => {
    getMembersOfContest();
    getPermissions();
  }, []);
  return (
    <div>
      <Button color="primary" variant="contained" component="label">
        <PublishIcon /> Select file to upload
        <input hidden type="file" id="selected-upload-file" onChange={onFileChange} />
      </Button>
      <Button onClick={handleUploadExcelStudentList}>Upload</Button>
      {isProcessing ? <CircularProgress /> : ""}

      <StandardTable
        title={"DS Users"}
        columns={columns}
        data={members}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />
      <UpdatePermissionMemberOfContestDialog
        open={openUpdateMemberDialog}
        onClose={handleModelClose}
        onUpdateInfo={onUpdateInfo}
        selectedUserRegisId={selectedUserRegisId}
        permissionIds={permissionIds}
      />
    </div>
  );
}
