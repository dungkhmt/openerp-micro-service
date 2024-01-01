import { Button } from "@mui/material";
import StandardTable from "component/table/StandardTable";
import { useEffect, useState } from "react";
import { errorNoti, successNoti } from "utils/notification";
import { request } from "../../../api";

export default function ContestManagerListRegisteredParticipant(props) {
  const contestId = props.contestId;
  const [registeredMembers, setRegisteredMembers] = useState([]);
  const [isProcessing, setIsProcessing] = useState(false);
  const columns = [
    { title: "Index", field: "index" },
    { title: "userID", field: "userId" },
    { title: "FullName", field: "fullName" },
    { title: "Role", field: "roleId" },
    { title: "Date", field: "date" },
    {
      title: "Action",
      render: (row) => (
        <Button onClick={() => handleApprove(row.id)}>Approve</Button>
      ),
    },
  ];
  function handleApprove(id) {
    setIsProcessing(true);
    let body = {
      id: id,
    };
    request(
      "post",
      "/contests/registers/approval",
      (res) => {
        successNoti("Successful", 3000);
        setIsProcessing(false);
      },
      {
        onError: () => {
          setIsProcessing(false);
          errorNoti("An error happened", 5000);
        },
        401: () => {},
      },
      body
    );
  }

  function getRegisteredMembers() {
    setIsProcessing(true);
    request("get", "/contests/" + contestId + "/pending-users-v2", (res) => {
      const data = res.data.map((e, i) => ({
        index: i + 1,
        id: e.id,
        userId: e.userId,
        fullName: e.fullName,
        roleId: e.roleId,
        date: e.date,
      }));
      setRegisteredMembers(data);
      setIsProcessing(false);
    });
  }
  useEffect(() => {
    getRegisteredMembers();
  }, []);
  return (
    <div>
      <StandardTable
        title={"Registered Members"}
        columns={columns}
        data={registeredMembers}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
    </div>
  );
}
