import DeleteRoundedIcon from "@mui/icons-material/DeleteRounded";
// import EditIcon from "@material-ui/icons/Edit";
import {request} from "api";
import StandardTable from "component/table/StandardTable";
import {useEffect, useState} from "react";
// import UpdateTeacherCourseForAssignmentModel from "../UpdateTeacherCourseForAssignmentModel";
import {CommandBarButton, commandBarStyles, NumSelectedRows,} from "./ClassInPlan";

function TeacherCourseInPlan(props) {
  const planId = props.planId;

  // Command delete button
  const [selectedRows, setSelectedRows] = useState([]);
  const [teacherCourses, setTeacherCourses] = useState([]);

  // const [openUpdateTeacherCourse, setOpenUpdateTeacherCourse] = useState(false);
  // const [selectedTeacherCourse, setSelectedTeacherCourse] = useState(null);

  // Table
  const columns = [
    { title: "Giảng viên", field: "teacherId" },
    { title: "Mã học phần", field: "courseId" },
    { title: "Loại lớp", field: "classType" },
    // { title: "Độ ưu tiên", field: "priority" },
    // { title: "Score", field: "score" },
    // {
    //   title: "",
    //   render: (rowData) => (
    //     <IconButton
    //       color="primary"
    //       aria-label="edit"
    //       onClick={() => {
    //         onUpdatePriority(rowData);
    //       }}
    //     >
    //       <EditIcon />
    //     </IconButton>
    //   ),
    // },
  ];

  // function onUpdatePriority(rowData) {
  //   setSelectedTeacherCourse({ ...rowData });

  //   handleModalUpdateTeacherCourseOpen();
  // }

  // const handleModalUpdateTeacherCourseOpen = () => {
  //   setOpenUpdateTeacherCourse(true);
  // };

  // const handleModalUpdateTeacherCourseClose = () => {
  //   setOpenUpdateTeacherCourse(false);
  // };

  // const customUpdateHandle = (priority, score) => {
  //   delete selectedTeacherCourse["table"];
  //   const data = {
  //     ...selectedTeacherCourse,
  //     priority: priority,
  //     // score: score,
  //   };

  //   request(
  //     "PUT",
  //     `edu/teaching-assignment/plan/${planId}/teacher-course`,
  //     (res) => {
  //       const index = teacherCourses.findIndex(
  //         (tc) => tc.teacherCourseId === data.teacherCourseId
  //       );

  //       const updatedTeacherCourses = teacherCourses.map((tc, i) => {
  //         if (i !== index) return tc;
  //         return data;
  //       });

  //       setTeacherCourses(updatedTeacherCourses);
  //     },
  //     { 401: () => {} },
  //     data
  //   );

  //   handleModalUpdateTeacherCourseClose();
  // };

  const getTeacherCourseForAssignmentList = () => {
    request(
      "GET",
      `edu/teaching-assignment/plan/${planId}/teacher-course`,
      (res) => {
        setTeacherCourses(res.data);
      }
    );
  };

  const removeTeacherCourseFromAssignmentPlan = () => {
    if (selectedRows.length > 0) {
      const data = selectedRows.map((tc) => ({
        teacherCourseId: tc.teacherCourseId,
        planId: tc.planId,
        teacherId: tc.teacherId,
        courseId: tc.courseId,
        classType: tc.classType,
      }));

      request(
        "DELETE",
        `edu/teaching-assignment/plan/${planId}/teacher-course`,
        (res) => {
          const toRemove = new Set(
            selectedRows.map((tc) => tc.teacherCourseId)
          );
          const difference = teacherCourses.filter(
            (tc) => !toRemove.has(tc.teacherCourseId)
          );

          setTeacherCourses(difference);
        },
        {},
        data
      );
    }
  };

  useEffect(() => {
    getTeacherCourseForAssignmentList();
  }, []);

  return (
    <>
      <StandardTable
        title={"Danh sách giáo viên"}
        columns={columns}
        data={teacherCourses}
        sx={{
          commandBar: commandBarStyles,
        }}
        onSelectionChange={(selectedRows) => setSelectedRows(selectedRows)}
        commandBarComponents={
          <>
            <CommandBarButton
              startIcon={<DeleteRoundedIcon />}
              onClick={removeTeacherCourseFromAssignmentPlan}
              disabled={selectedRows.length === 0}
            >
              Xoá
            </CommandBarButton>
            {selectedRows.length > 0 && (
              <NumSelectedRows numSelected={selectedRows.length} />
            )}
          </>
        }
      />

      {/* <UpdateTeacherCourseForAssignmentModel
        open={openUpdateTeacherCourse}
        onClose={handleModalUpdateTeacherCourseClose}
        onUpdateInfo={customUpdateHandle}
        selectedTeacherCourse={selectedTeacherCourse}
      /> */}
    </>
  );
}

export default TeacherCourseInPlan;
