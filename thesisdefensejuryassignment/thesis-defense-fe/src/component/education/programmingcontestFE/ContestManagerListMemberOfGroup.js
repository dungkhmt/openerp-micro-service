import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import { Button, IconButton, LinearProgress } from "@mui/material";
import StandardTable from "component/table/StandardTable";
import { useEffect, useState } from "react";
import { toFormattedDateTime } from "utils/dateutils";
import { errorNoti, successNoti } from "utils/notification";
import { request } from "../../../api";
import HustContainerCard from "../../common/HustContainerCard";
import UpdatePermissionMemberOfContestDialog from "./UpdatePermissionMemberOfContestDialog";
import UploadUserToContestGroupDialog from "./UploadUserToContestGroupDialog";

export default function ContestManagerListMemberOfGroup(props) {
  const contestId = props.contestId;
  const [members, setMembers] = useState([]);
  const [isProcessing, setIsProcessing] = useState(true);
  const [openUpdateMemberDialog, setOpenUpdateMemberDialog] = useState(false);
  const [permissionIds, setPermissionIds] = useState([]);
  const [selectedUserRegisId, setSelectedUserRegisId] = useState(null);

  const [openUploadDialog, setOpenUploadDialog] = useState(false);

  const columns = [
    { title: "No.", field: "index" },
    { title: "User ID", field: "userId" },
    { title: "Name", field: "fullName" },
    { title: "Role", field: "roleId" },
    // {title: "Updated At", field: "lastUpdatedDate"},
    { title: "Permission", field: "permissionId" },
    {
      title: "Update Permission",
      render: (row) => (
        <IconButton
          variant="contained"
          color="info"
          onClick={() => handleForbidSubmit(row.id)}
        >
          <EditIcon />
        </IconButton>
      ),
    },
    {
      title: "Remove",
      render: (row) =>
        row.userId != "admin" && (
          <IconButton
            variant="contained"
            color="error"
            onClick={() => handleRemove(row.userId)}
          >
            <DeleteIcon />
          </IconButton>
        ),
    },
  ];

  function handleForbidSubmit(id) {
    setSelectedUserRegisId(id);
    setOpenUpdateMemberDialog(true);
  }

  function handleRemove(userId) {
    setIsProcessing(true);
    let body = {
      contestId: contestId,
      participantId: userId,
    };
    request(
      "delete",
      "/contests/group/members",
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
        401: () => {},
      },
      body
    );
  }

  function getMembersOfContest() {
    request("get", "/contests/" + contestId + "/group/members", (res) => {
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
        401: () => {},
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

  const handleCloseUploadUserDialog = () => {
    getMembersOfContest();
    setOpenUploadDialog(false);
  };

  useEffect(() => {
    getMembersOfContest();
    getPermissions();
  }, []);

  return (
    <HustContainerCard>
      {isProcessing && <LinearProgress />}
      <StandardTable
        title={"My Group"}
        columns={columns}
        data={members}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
        actions={[
          {
            icon: () => {
              return (
                <Button
                  variant="contained"
                  sx={{ ml: 2, mr: 2 }}
                  onClick={() => {
                    setOpenUploadDialog(true);
                  }}
                >
                  Upload
                </Button>
              );
            },
            tooltip: "Upload Users with Excel file",
            isFreeAction: true,
          },
        ]}
      />
      <UpdatePermissionMemberOfContestDialog
        open={openUpdateMemberDialog}
        onClose={handleModelClose}
        onUpdateInfo={onUpdateInfo}
        selectedUserRegisId={selectedUserRegisId}
        permissionIds={permissionIds}
      />
      <UploadUserToContestGroupDialog
        isOpen={openUploadDialog}
        contestId={contestId}
        onClose={handleCloseUploadUserDialog}
      />
    </HustContainerCard>
  );
}
