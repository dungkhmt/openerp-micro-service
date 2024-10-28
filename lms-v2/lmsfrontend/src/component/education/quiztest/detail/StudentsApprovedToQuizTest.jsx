import React, {useEffect, useState} from "react";
import {request} from "../../../../api";
import {errorNoti, successNoti} from "../../../../utils/notification";
import AssignQuizGroupForStudentDialog from "./AssignQuizGroupForStudentDialog";
import StandardTable from "../../../table/StandardTable";
import {Button, Card, CardContent, Chip} from "@mui/material";
import PublishIcon from '@mui/icons-material/Publish';
import SendIcon from '@mui/icons-material/Send';
import {LoadingButton} from "@mui/lab";

export default function StudentsApprovedToQuizTest(props) {
  const testId = props.testId;
  const [importedExcelFile, setImportedExcelFile] = useState(null);
  const [isProcessing, setIsProcessing] = useState(false);

  const [studentsApprovedToQuizTest, setStudentsApprovedToQuizTest] = useState([]);
  useEffect(getStudentsApprovedToQuizTest, []);

  function getStudentsApprovedToQuizTest() {
    const successHandler = res => {
      let approvedStudents = res.data.filter(student => student.statusId === 'STATUS_APPROVED');
      setStudentsApprovedToQuizTest(approvedStudents);
    }
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000)
    }
    request("GET", `/get-all-student-in-test?testId=${testId}`, successHandler, errorHandlers);
  }


  // Logic assign quiz group for student
  const [assignQuizGroupDlgOpen, setAssignQuizGroupDlgOpen] = useState(false);
  const [selectedStudentToAssignQuizGroup, setSelectedStudentToAssignQuizGroup] = useState();
  const [quizGroups, setQuizGroups] = useState([]);

  useEffect(getQuizGroups, []);

  function getQuizGroups() {
    request("GET", `/get-test-groups-info?testId=${testId}`, (res) => {
      setQuizGroups(res.data);
    });
  }

  function openAssignQuizGroupDialog(assignedStudent) {
    setSelectedStudentToAssignQuizGroup(assignedStudent);
    setAssignQuizGroupDlgOpen(true);
  }

  function refreshWhenAssignQuizGroupSuccess(selectedGroup) {
    selectedStudentToAssignQuizGroup.testGroupCode = selectedGroup.groupCode;
    setStudentsApprovedToQuizTest([...studentsApprovedToQuizTest]);
  }

  // Logic reject student sfrom quiz test
  const [selectedStudentIdsToReject, setSelectedStudentIdsToReject] = useState([]);

  function updateSelectedStudentIdsToReject(newSelectedStudents) {
    setSelectedStudentIdsToReject(newSelectedStudents.map(student => student.userLoginId));
  }

  function rejectStudentsFromQuizTest(rejectedStudentIds) {
    if (rejectedStudentIds.length === 0) return;
    if (!window.confirm("Bạn có chắc muốn loại những thí sinh này khỏi kỳ thi ?")) return;

    let formData = new FormData();
    formData.append("testId", testId);
    formData.append("studentList", rejectedStudentIds.join(";"));

    let successHandler = (res) => {
      let result = res.data || -1;
      if (result < 0) return;
      let remainStudents = studentsApprovedToQuizTest.filter(student =>
        !rejectedStudentIds.includes(student.userLoginId)
      )
      setStudentsApprovedToQuizTest(remainStudents);
    }
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi loại SV khỏi quiz test!", 3000)
    }

    request("POST", "/reject-students-in-test", successHandler, errorHandlers, formData);
  }

  function importStudentsFromExcel(event) {
    event.preventDefault();

    setIsProcessing(true);
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify({ testId }));
    formData.append("file", importedExcelFile);

    let successHandler = (res) => {
      setIsProcessing(false);
      setImportedExcelFile(undefined);
      successNoti("Import sinh viên thành công", true);
    };
    let errorHandlers = {
      onError: (error) => {
        setIsProcessing(false);
        errorNoti("Đã xảy ra lỗi khi import sinh viên", true)
      }
    }
    request("POST", "/upload-excel-student-list", successHandler, errorHandlers, formData);
  };

  // Action buttons
  const AssignQuizGroupButton = ({ student }) => (
    <Button color="primary"
            variant="outlined"
            onClick={() => openAssignQuizGroupDialog(student)} >
      Chọn đề
    </Button>
  )

  const RejectStudentsFromQuizTestButton = ({ rejectedStudentIds, variant}) => (
    <Button color="error"
            variant={variant}
            onClick={() => rejectStudentsFromQuizTest(rejectedStudentIds)} >
      Loại
    </Button>
  )

  const columns = [
    { field: "userLoginId", title: "Login ID" },
    { field: "fullName", title: "Họ và tên" },
    { field: "email", title: "Email" },
    { field: "testGroup", title: "Mã đề",
      render: student => (student.testGroupCode === "-" ? "" : student.testGroupCode)
    },
    { field: "", title: "",
      render: student => (
        <div style={{ display: 'flex', columnGap: '10px' }}>
          <AssignQuizGroupButton student={student}/>
          <RejectStudentsFromQuizTestButton rejectedStudentIds={[student.userLoginId]}
                                            variant="outlined"/>
        </div>
      )
    }
  ];

  const actions = [{
    icon: () => (
      <RejectStudentsFromQuizTestButton
        rejectedStudentIds={selectedStudentIdsToReject}
        variant="contained"
      />
    )
  }]

  return (
    <>
      <div style={{ display: 'flex', alignItems: "center", columnGap: '10px', marginBottom: '10px' }}>
        <Button color="primary" variant="contained" component="label">
          <PublishIcon/> Select excel file to import
          <input type="file" hidden
                 onChange={event => setImportedExcelFile(event.target.files[0])} />
        </Button>
        { importedExcelFile && (
          <Chip color="success" variant="outlined"
                label={importedExcelFile.name}
                onDelete={() => setImportedExcelFile(undefined)}/>
        )}
        <LoadingButton loading={isProcessing}
                       endIcon={<SendIcon/>}
                       disabled={!importedExcelFile}
                       color="primary" variant="contained"
                       onClick={importStudentsFromExcel}>
          Upload
        </LoadingButton>
      </div>

      <Card>
        <CardContent>
          <StandardTable
            title="Danh sách sinh viên"
            columns={columns}
            data={studentsApprovedToQuizTest}
            hideCommandBar
            options={{
              selection: true,
              search: true,
              sorting: true,
              showTitle: true
            }}
            actions={actions}
            onSelectionChange={updateSelectedStudentIdsToReject}
          />
        </CardContent>
      </Card>

      <AssignQuizGroupForStudentDialog
        assignedStudent={selectedStudentToAssignQuizGroup}
        quizGroups={quizGroups}
        open={assignQuizGroupDlgOpen}
        onClose={() => setAssignQuizGroupDlgOpen(false)}
        onAssignSuccess={refreshWhenAssignQuizGroupSuccess}/>
    </>
  );
}
