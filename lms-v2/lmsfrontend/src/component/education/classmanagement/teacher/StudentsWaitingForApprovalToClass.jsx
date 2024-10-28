import React, {useEffect, useState} from "react";
import {request} from "../../../../api";
import {errorNoti, successNoti} from "../../../../utils/notification";
import {Button} from "@mui/material";
import StandardTable from "../../../table/StandardTable";


export default function StudentsWaitingForApprovalToClass(props) {
  const classId = props.classId;
  const [registStudents, setRegistStudents] = useState([]);
  const [selectedRegists, setSelectedRegists] = useState([]);

  useEffect(getStudentsWaitingForApprovalToClass, []);

  function getStudentsWaitingForApprovalToClass() {
    request("GET", `/edu/class/${classId}/registered-students`, (res) => {
      setRegistStudents(res.data);
    })
  }

  function updateSelectedStudentsToUpdateStatus(newSelectedStudents) {
    setSelectedRegists(newSelectedStudents.map(selectedStudent => selectedStudent.id));
  }

  function updateRegistrationStatusForStudents(idsOfUpdatedStudents, newStatus) {
    let successNotiMsg = newStatus === "APPROVED" ? 
      "Phê duyệt thành công, xem kết quả ở bảng" :
      "Đã từ chối sinh viên tham gia vào lớp học";
    let successHandler = res => {
      successNoti(successNotiMsg, 3000);
      setRegistStudents(registStudents.filter(student => !idsOfUpdatedStudents.includes(student.id)))
    }
    
    let errorNotiMsg = newStatus === "APPROVED" ?
      "Đã xảy ra lỗi khi phê duyệt" :
      "Đã xảy ra lỗi khi từ chối";
    let errorHandlers = {
      onError: error => errorNoti(errorNotiMsg, 3000)
    }

    let data = { classId, studentIds: idsOfUpdatedStudents, status: newStatus};
    request("PUT", "/edu/class/registration-status", successHandler, errorHandlers, data);
  }
  
  const UpdateStatusButtons = ({ studentIds }) => (
    <div style={{ display: 'flex', columnGap: '10px' }}>
      <Button variant="outlined"
              onClick={() => updateRegistrationStatusForStudents(studentIds, "APPROVED")}>
        Phê duyệt
      </Button>
      <Button variant="outlined"
              onClick={() => updateRegistrationStatusForStudents(studentIds, "REFUSED")}>
        Từ chối
      </Button>
    </div>
  )

  const registCols = [
    { field: "name", title: "Họ và tên" },
    {
      field: "email", title: "Email",
      render: student => <a href={`mailto:${student.email}`}>{student.email}</a>
    },
    {
      field: "", title: "",
      render: student => <UpdateStatusButtons studentIds={[student.id]}/>
    }
  ];

  return (
    <div>
      { selectedRegists.length > 0 &&
        <UpdateStatusButtons studentIds={selectedRegists}/>
      }
      
      <StandardTable
        title="Phê duyệt sinh viên đăng ký"
        columns={registCols}
        data={registStudents}
        hideCommandBar
        options={{
          selection: true,
          search: true,
          sorting: true
        }}
        onSelectionChange={updateSelectedStudentsToUpdateStatus}
      />
    </div>
  );
}
