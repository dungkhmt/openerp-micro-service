import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import {
  Avatar,
  Box,
  IconButton,
  ListItemAvatar,
  ListItemText,
  Stack,
} from "@mui/material";
import { request } from "api";
import StandardTable from "component/table/StandardTable";
import { isEmpty, trim } from "lodash";
import { useEffect, useState } from "react";
import { toFormattedDateTime } from "utils/dateutils";
import { errorNoti, successNoti } from "utils/notification";
import AddMember2Contest, { stringAvatar } from "./AddMember2Contest";
import UpdatePermissionMemberOfContestDialog from "./UpdatePermissionMemberOfContestDialog";

export default function ContestManagerListMember(props) {
  const contestId = props.contestId;

  const [members, setMembers] = useState([]);
  const [isProcessing, setIsProcessing] = useState(true);
  const [openUpdateMemberDialog, setOpenUpdateMemberDialog] = useState(false);
  const [permissionIds, setPermissionIds] = useState([]);
  const [selectedUserRegisId, setSelectedUserRegisId] = useState(null);

  const columns = [
    // { title: "No.", field: "index" },
    {
      title: "Member",
      field: "userId",
      minWidth: 300,
      render: (rowData) => (
        <Stack direction={"row"} alignItems={"center"}>
          {/* TODO: extract this component from AddMember2Contest */}
          <ListItemAvatar>
            <Avatar
              alt="account avatar"
              {...stringAvatar(rowData.userId, rowData.fullName)}
            />
          </ListItemAvatar>
          <ListItemText
            primary={rowData.fullName}
            secondary={`${rowData.userId}`}
          />
        </Stack>
      ),
    },
    // { title: "Name", field: "fullName" },
    { title: "Role", field: "roleId" },
    // {title: "Updated At", field: "lastUpdatedDate"},
    { title: "Permission", field: "permissionId" },
    {
      // title: "Update Permission",
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
      // title: "Remove",
      render: (row) =>
        row.userId != "admin" && (
          <IconButton
            variant="contained"
            color="error"
            onClick={() => handleRemove(row.id)}
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
        getContestMembers();
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

  function getContestMembers() {
    request("get", `/contests/${contestId}/members`, (res) => {
      const data = res.data.map((e, i) => {
        const member = {
          // index: i + 1,
          id: e.id,
          userId: e.userId,
          fullName: `${e.firstName || ""} ${e.lastName || ""}`,
          roleId: e.roleId,
          permissionId: e.permissionId,
          lastUpdatedDate: toFormattedDateTime(e.lastUpdatedDate),
          updatedByUserId: e.updatedByUserId,
        };

        if (isEmpty(trim(member.fullName))) {
          member.fullName = "Anonymous";
        }

        return member;
      });

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
        getContestMembers();
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

  useEffect(() => {
    getContestMembers();
    getPermissions();
  }, []);

  return (
    <>
      <AddMember2Contest
        contestId={contestId}
        onAddedSuccessfully={getContestMembers}
      />
      {/* <HustContainerCard>
        {isProcessing && <LinearProgress />} */}
      <Box mb={2}>
        <StandardTable
          // title={"Members"}
          columns={columns}
          data={members}
          hideCommandBar
          options={{
            selection: false,
            pageSize: 5,
            search: true,
            sorting: true,
          }}
        />
      </Box>
      <UpdatePermissionMemberOfContestDialog
        open={openUpdateMemberDialog}
        onClose={handleModelClose}
        onUpdateInfo={onUpdateInfo}
        selectedUserRegisId={selectedUserRegisId}
        permissionIds={permissionIds}
      />
      {/* </HustContainerCard> */}
    </>
  );
}
