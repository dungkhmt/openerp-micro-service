import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import StandardTable from "component/table/StandardTable";
import {Button, IconButton, LinearProgress} from "@mui/material";
import {errorNoti, successNoti} from "utils/notification";
import {toFormattedDateTime} from "utils/dateutils";
import UpdatePermissionMemberOfContestDialog from "./UpdatePermissionMemberOfContestDialog";
import DeleteIcon from "@mui/icons-material/Delete";
import HustContainerCard from "../../common/HustContainerCard";
import EditIcon from "@mui/icons-material/Edit";
import UploadUserToContestDialog from "./UploadUserToContestDialog";

export default function ContestManagerListMember(props) {
  const contestId = props.contestId;
  const [members, setMembers] = useState([]);
  const [isProcessing, setIsProcessing] = useState(true);
  const [openUpdateMemberDialog, setOpenUpdateMemberDialog] = useState(false);
  const [permissionIds, setPermissionIds] = useState([]);
  const [selectedUserRegisId, setSelectedUserRegisId] = useState(null);

  const [filename, setFilename] = useState(null);

  const [openUploadDialog, setOpenUploadDialog] = useState(false);

  const columns = [
    {title: "No.", field: "index"},
    {title: "User ID", field: "userId"},
    {title: "Name", field: "fullName"},
    {title: "Role", field: "roleId"},
    // {title: "Updated At", field: "lastUpdatedDate"},
    {title: "Permission", field: "permissionId"},
    {
      title: "Update Permission",
      render: (row) => (
        <IconButton variant="contained" color="info" onClick={() => handleForbidSubmit(row.id)}>
          <EditIcon/>
        </IconButton>
      ),
    },
    {
      title: "Remove",
      render: (row) => (
        <IconButton variant="contained" color="error" onClick={() => handleRemove(row.id)}>
          <DeleteIcon/>
        </IconButton>
      )
    },
  ];

  function handleForbidSubmit(id) {
    setSelectedUserRegisId(id);
    setOpenUpdateMemberDialog(true);
  }

  function handleRemove(id) {
    setIsProcessing(true);
    let body = {
      id: id,
    };
    request(
      "delete",
      "/contests/members",
      (res) => {
        successNoti("Participant removed successfully");
        setIsProcessing(false);
        getMembersOfContest();
      },
      {
        onError: () => {
          setIsProcessing(false);
          errorNoti("An error happened");
        },
        401: () => {
        },
      },
      body
    );
  }

  function getMembersOfContest() {
    request("get", "/contests/" + contestId + "/members", (res) => {
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

      setIsProcessing(false);
    });
  }

  function onUpdateInfo(selectedPermission, selectedUserRegisId) {
    setIsProcessing(true);
    let body = {
      userRegisId: selectedUserRegisId,
      permissionId: selectedPermission,
    };
    request(
      "put",
      "/contests/permissions",
      (res) => {
        successNoti("Updated Successfully");
        setIsProcessing(false);
        setOpenUpdateMemberDialog(false);
        getMembersOfContest();
      },
      {
        onError: () => {
          setIsProcessing(false);
          errorNoti("An error happened");
        },
        401: () => {
        },
      },
      body
    );
  }

  function handleModelClose() {
    setOpenUpdateMemberDialog(false);
  }

  function getPermissions() {
    request("get", "/contests/permissions", (res) => {
      setPermissionIds(res.data);
    });
  }

  const handleUploadExcelStudentList = (event) => {
    event.preventDefault();
    setIsProcessing(true);
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
      "/contests/students/upload-list",
      (res) => {
        setIsProcessing(false);
      },
      {
        onError: (e) => {
          setIsProcessing(false);
          console.error(e);
        },
      },
      formData,
      config
    );
  };

  function onFileChange(event) {
    setFilename(event.target.files[0]);
  }

  const handleCloseUploadUserDialog = () => {
    getMembersOfContest();
    setOpenUploadDialog(false);
  }

  useEffect(() => {
    getMembersOfContest();
    getPermissions();
  }, []);

  return (
    <HustContainerCard>
      {isProcessing && <LinearProgress/>}
      <StandardTable
        title={"Users"}
        columns={columns}
        data={members}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
        actions={[
          {
            icon: () => {
              return <Button variant="contained" sx={{ml: 2, mr: 2}} onClick={() => {
                setOpenUploadDialog(true)
              }}>
                Upload
              </Button>
            },
            tooltip: 'Upload Users with Excel file',
            isFreeAction: true
          }
        ]}
      />
      <UpdatePermissionMemberOfContestDialog
        open={openUpdateMemberDialog}
        onClose={handleModelClose}
        onUpdateInfo={onUpdateInfo}
        selectedUserRegisId={selectedUserRegisId}
        permissionIds={permissionIds}
      />
      <UploadUserToContestDialog
        isOpen={openUploadDialog}
        contestId={contestId}
        onClose={handleCloseUploadUserDialog}
      />
    </HustContainerCard>
  );
}
