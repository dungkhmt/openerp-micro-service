import AddIcon from "@mui/icons-material/Add";
// import EditIcon from "@mui/icons-material/Edit";
import PublishRoundedIcon from "@mui/icons-material/PublishRounded";
import {request} from "api";
import StandardTable from "component/table/StandardTable";
import {useEffect, useState} from "react";
import {errorNoti, successNoti} from "utils/notification";
// import UpdateTeacherCourseModel from "../UpdateTeacherCourseModel";
import {CommandBarButton, commandBarStyles, Input, NumSelectedRows,} from "./assignmentPlan/ClassInPlan";

function TeacherCourseList(props) {
  const planId = props.planId;

  // Command add button
  const [selectedRows, setSelectedRows] = useState([]);

  //
  const [teacherCourses, setTeacherCourses] = useState([]);
  // const [open, setOpen] = React.useState(false);
  // const [isProcessing, setIsProcessing] = useState(false);

  // const [selectedTeacherCourse, setSelectedTeacherCourse] = useState(null);
  // const [openUpdateTeacherCourse, setOpenUpdateTeacherCourse] = useState(false);

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
    //         onUpdatePriority(rowData["teacherId"], rowData["courseId"]);
    //       }}
    //     >
    //       <EditIcon />
    //     </IconButton>
    //   ),
    // },
  ];

  // Funcs
  const onUpload = (e) => {
    console.log("SUBMIT");
    uploadExcel(e);
    // handleModalClose();
  };

  function uploadExcel(e) {
    const selectedFile = e.target.files[0];
    console.log("upload file " + selectedFile.name);

    const data = new FormData();
    data.append("file", selectedFile);

    request(
      "POST",
      `edu/teaching-assignment/plan/${planId}/teacher-course/upload-excel`,
      (res) => {
        e.target.value = "";

        if (res.data === true) {
          successNoti("Đã tải lên.");
          getTeacherForAssignmentList();
        } else {
          errorNoti(
            "Đã có lỗi xảy ra. Vui lòng kiểm tra định dạng file excel và thử lại."
          );
        }
      },
      {
        onError: (error) => {
          e.target.value = "";
          console.error(error);
          errorNoti("Đã có lỗi xảy ra.");
        },
      },
      data
    );
  }

  // TODO: fix this func
  // function onUpdatePriority(teacherId, courseId) {
  //   //alert("update priority " + teacherId + "-" + courseId);

  //   setSelectedTeacherCourse({
  //     planId: planId,
  //     teacherId: teacherId,
  //     courseId: courseId,
  //   });

  //   handleModalUpdateTeacherCourseOpen();
  // }

  // const handleModalUpdateTeacherCourseOpen = () => {
  //   setOpenUpdateTeacherCourse(true);
  // };

  // const handleModalUpdateTeacherCourseClose = () => {
  //   setOpenUpdateTeacherCourse(false);
  // };

  // TODO: fix this func, error in API not exist
  // const customUpdateHandle = (priority, score) => {
  //   //alert("update  class " + selectedClassId + " with ourload = " + hourLoad);

  //   let data = {
  //     planId: selectedTeacherCourse.planId,
  //     teacherId: selectedTeacherCourse.teacherId,
  //     courseId: selectedTeacherCourse.courseId,
  //     priority: priority,
  //     score: score,
  //   };

  //   request(
  //     "post",
  //     "update-teacher-course",
  //     (res) => {
  //       console.log(res);
  //       alert("Cập nhật " + "  OK");
  //     },
  //     { 401: () => {} },
  //     data
  //   );

  //   handleModalUpdateTeacherCourseClose();
  // };

  const getTeacherForAssignmentList = () => {
    request("GET", "edu/teaching-assignment/teacher-course", (res) => {
      setTeacherCourses(res.data);
    });
  };

  // const handleModalOpen = () => {
  //   setOpen(true);
  // };

  // const handleModalClose = () => {
  //   setOpen(false);
  // };

  const addTeacherCourseToAssignmentPlan = () => {
    if (selectedRows.length > 0) {
      const data = selectedRows.map((tc) => ({
        teacherCourseId: tc.refId,
        priority: tc.priority,
        score: tc.score, // currently not used in API
      }));

      request(
        "POST",
        `edu/teaching-assignment/plan/${planId}/teacher-course`,
        (res) => {
          successNoti("Đã thêm vào kế hoạch.");
          const toRemove = new Set(selectedRows.map((tc) => tc.refId));
          const difference = teacherCourses.filter(
            (tc) => !toRemove.has(tc.refId)
          );

          setTeacherCourses(difference);
        },
        {},
        data
      );
    }
  };

  useEffect(() => {
    getTeacherForAssignmentList();
  }, []);

  return (
    <>
      <StandardTable
        title={"Danh sách giáo viên - môn"}
        columns={columns}
        data={teacherCourses}
        sx={{
          commandBar: commandBarStyles,
        }}
        onSelectionChange={(selectedRows) => setSelectedRows(selectedRows)}
        commandBarComponents={
          <>
            {selectedRows.length === 0 ? (
              <label htmlFor="upload-excel-teacher-course">
                <Input
                  type="file"
                  accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                  id="upload-excel-teacher-course"
                  onChange={onUpload}
                />
                <CommandBarButton
                  component="span"
                  startIcon={<PublishRoundedIcon />}
                >
                  Tải lên Excel
                </CommandBarButton>
              </label>
            ) : (
              <>
                <CommandBarButton
                  startIcon={<AddIcon />}
                  onClick={addTeacherCourseToAssignmentPlan}
                >
                  Thêm vào kế hoạch
                </CommandBarButton>
                <NumSelectedRows numSelected={selectedRows.length} />
              </>
            )}
          </>
        }
      />

      {/* <UploadExcelTeacherCourseModel
        open={open}
        onClose={handleModalClose}
        onUpload={customUploadHandle}
      /> */}

      {/* <UpdateTeacherCourseModel
        open={openUpdateTeacherCourse}
        onClose={handleModalUpdateTeacherCourseClose}
        onUpdateInfo={customUpdateHandle}
        selectedTeacherCourse={selectedTeacherCourse}
      /> */}
    </>
  );
}

export default TeacherCourseList;
