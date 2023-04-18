import React, {useEffect, useState} from "react";
import {Button} from "@mui/material";
import {request} from "../../../api";
import StandardTable from "component/table/StandardTable";
import {errorNoti, successNoti} from "utils/notification";

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
    //alert("approve " + id);
    setIsProcessing(true);
    let body = {
      id: id,
    };
    request(
      "post",
      "/approve-registered-user-2-contest",
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

  function getRegisteredMembers() {
    setIsProcessing(true);
    request(
      "get",
      "/get-pending-registered-users-of-contest/" + contestId,
      (res) => {
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
      }
    );
  }
  useEffect(() => {
    getRegisteredMembers();
  }, []);
  return (
    <div>
      {" "}
      <StandardTable
        title={"Registered Members"}
        columns={columns}
        data={registeredMembers}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />
    </div>
  );
}
