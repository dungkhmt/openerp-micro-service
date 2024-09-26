import EditIcon from "@mui/icons-material/Edit";
import IconButton from "@mui/material/IconButton";
import {request} from "api";
import StandardTable from "component/table/StandardTable";
import {useEffect, useState} from "react";
import SuggestedTeacherListForSelectedClassDialog from "../SuggestedTeacherListForSelectedClassDialog";

function NotAssignedClassInSolutionList({ planId }) {
  //
  const [classes, setClasses] = useState([]);
  const [open, setOpen] = useState(false);

  //
  const [selectedClass, setSelectedClass] = useState({});
  const [suggestionData, setSuggestionData] = useState();

  // Table
  const columns = [
    { title: "Mã lớp", field: "classCode" },
    { title: "Mã học phần", field: "courseId" },
    { title: "Tên học phần", field: "courseName" },
    { title: "Loại lớp", field: "classType" },
    { title: "Thời khoá biểu", field: "timetable", sorting: false },
    {
      title: "",
      sorting: false,
      render: (rowData) => (
        <IconButton
          color="primary"
          aria-label="edit"
          onClick={() => {
            onSuggestTeacher({
              classId: rowData["classCode"],
            });
          }}
        >
          <EditIcon />
        </IconButton>
      ),
    },
  ];

  // Funcs
  const openModal = () => {
    setOpen(true);
  };

  const closeModal = () => {
    setOpen(false);
    setSuggestionData(undefined);
  };

  const onAssign = (teacherId) => {
    const { classId } = selectedClass;

    let data = {
      planId: planId,
      classId: classId,
      teacherId: teacherId,
      pinned: true,
    };

    request(
      "PUT",
      `edu/teaching-assignment/plan/${planId}/solution`,
      (res) => {
        const restClasses = classes.filter((c) => c.classCode !== classId);
        setClasses(restClasses);
      },
      { 401: () => {} },
      data
    );

    closeModal();
  };

  // const customUploadHandle = (selectedTeacherId) => {
  //   //console.log(filename);
  //   //setSearchString(sString);
  //   //alert("upload " + filename);
  //   //uploadExcel(selectedFile, choice);
  //   //perform API to assign teacher here
  //   //alert("call API to assign teacher " + selectedTeacherId);
  //   let data = {
  //     classId: selectedClassId,
  //     teacherId: selectedTeacherId,
  //     planId: planId,
  //   };
  //   request(
  //     "post",
  //     "manual-assign-teacher-to-class",
  //     (res) => {
  //       console.log(res);
  //       alert(
  //         "phân giảng viên " +
  //           selectedTeacherId +
  //           " cho lớp " +
  //           selectedClassId +
  //           "  OK"
  //       );
  //     },
  //     { 401: () => {} },
  //     data
  //   );

  //   handleModalClose();
  // };

  const onSuggestTeacher = (selectedAssignment) => {
    setSelectedClass(selectedAssignment);
    openModal();

    request(
      "GET",
      `edu/teaching-assignment/plan/${planId}/class/${selectedAssignment.classId}/suggested-teacher-and-actions`,
      (res) => {
        setSuggestionData(res.data);
        console.log("SUGGEST TEACHERS ", res.data);
      },
      { 401: () => {} }
    );
  };

  function getNotAssignedClassInSolutionList() {
    request(
      "GET",
      `edu/teaching-assignment/plan/${planId}/class/not-assigned-class`,
      (res) => {
        setClasses(res.data);
      }
    );
  }

  // const handleModalClose = () => {
  //   setOpen(false);
  // };

  useEffect(() => {
    getNotAssignedClassInSolutionList();
  }, []);

  return (
    <>
      <StandardTable
        title={"Danh sách lớp chưa được phân công"}
        columns={columns}
        data={classes}
        hideCommandBar
        options={{ selection: false, pageSize: 10 }}
      />

      <SuggestedTeacherListForSelectedClassDialog
        open={open}
        handleClose={closeModal}
        onReassign={onAssign}
        classId={selectedClass.classId}
        suggestions={suggestionData}
      />
    </>
  );
}

export default NotAssignedClassInSolutionList;
