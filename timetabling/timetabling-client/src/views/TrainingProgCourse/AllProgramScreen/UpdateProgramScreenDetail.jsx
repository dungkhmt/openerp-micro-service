import React, { useState, useEffect } from "react";
import {
  TextField,
  Paper,
  Typography,
  Button,
  List,
  ListItem,
  ListItemText,
  MenuItem,
  Select,
  FormControl,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { request } from "api";
import { useParams, useHistory } from "react-router-dom";
import { styles } from "./index.style";
import { programUrl } from "../apiURL";
import { successNoti, warningNoti, errorNoti } from "utils/notification";
import AddCourseDialog from "./AddCourseDialog";
import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import CheckIcon from "@mui/icons-material/Check";
import CloseIcon from "@mui/icons-material/Close";
import EditIcon from "@mui/icons-material/Edit";

const UpdateProgramPage = () => {
  const { programId } = useParams();
  const [courses, setCourses] = useState([]);
  const [semesterUpdates, setSemesterUpdates] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [programName, setProgramName] = useState("");
  const [dialogOpen, setDialogOpen] = useState(false); // State to control dialog open/close
  const [semesterCount, setSemesterCount] = useState(0);
  const [tempSemesterCount, setTempSemesterCount] = useState(0);
  const [isEditingSemesterCount, setIsEditingSemesterCount] = useState(false); // Khởi tạo state
  const [selectedRows, setSelectedRows] = useState([]);
  const [actionSemester, setActionSemester] = useState({});

  const history = useHistory();

  useEffect(() => {
    setIsLoading(true);
    request("get", `${programUrl.getProgramDetail}/${programId}`, (res) => {
      if (res && res.data && res.data.schedules) {
        setProgramName(res.data.name);
        setSemesterCount(res.data.semesterCount);
        const sortedCourses = res.data.schedules.sort(
          (a, b) => a.semester - b.semester
        );
        setCourses(sortedCourses);
      }
      setIsLoading(false);
    });
  }, [programId]);

  const checkCoursePrerequisites = (updatedCourses, changedCourseId) => {
    const errors = [];
    const warnings = [];

    const changedCourse = updatedCourses.find(
      (course) => course.id === changedCourseId
    );

    if (changedCourse) {
      changedCourse.prerequisites.forEach((prereqId) => {
        const prereqCourse = updatedCourses.find((c) => c.id === prereqId);
        if (prereqCourse && prereqCourse.semester >= changedCourse.semester) {
          errors.push(
            `Học phần "${changedCourse.courseName}" (Kì ${changedCourse.semester}) phải học sau môn tiên quyết "${prereqCourse.courseName}" (Kì ${prereqCourse.semester}).`
          );
        }
      });

      updatedCourses.forEach((otherCourse) => {
        if (otherCourse.prerequisites.includes(changedCourse.id)) {
          if (changedCourse.semester >= otherCourse.semester) {
            warnings.push(
              `Học phần "${changedCourse.courseName}" (Kì ${changedCourse.semester}) là môn tiên quyết của "${otherCourse.courseName}" (Kì ${otherCourse.semester}) nhưng lại học sau hoặc cùng Kì.`
            );
          }
        }
      });
    }

    return { errors, warnings };
  };

  const handleSemesterChange = (courseId, newSemester) => {
    const updatedSemesterUpdates = {
      ...semesterUpdates,
      [courseId]: newSemester,
    };
    setSemesterUpdates(updatedSemesterUpdates);

    const updatedCourses = courses.map((course) => ({
      ...course,
      semester: updatedSemesterUpdates[course.id] || course.semester,
    }));

    const { errors, warnings } = checkCoursePrerequisites(
      updatedCourses,
      courseId
    );

    if (errors.length > 0) {
      errors.forEach((error) => warningNoti(error, 5000));
    }
    if (warnings.length > 0) {
      warnings.forEach((warning) => warningNoti(warning, 5000));
    }
  };

  const validatePrerequisiteCourses = (updatedCourses) => {
    let errors = [];

    updatedCourses.forEach((course) => {
      course.prerequisites.forEach((prereqId) => {
        const prereqCourse = updatedCourses.find((c) => c.id === prereqId);
        if (prereqCourse && prereqCourse.semester >= course.semester) {
          errors.push(
            `Học phần "${course.courseName}" (Kì ${course.semester}) phải học sau môn tiên quyết "${prereqCourse.courseName}" (Kì ${prereqCourse.semester}).`
          );
        }
      });
    });

    return errors;
  };

  const handleUpdateSemesters = () => {
    const updatedCourses = courses.map((course) => ({
      ...course,
      semester: semesterUpdates[course.id] || course.semester,
    }));

    const errors = validatePrerequisiteCourses(updatedCourses);

    if (errors.length > 0) {
      errors.forEach((error) => errorNoti(error, 5000));
      return;
    }

    const scheduleUpdates = updatedCourses.map((course) => ({
      programId,
      courseId: course.id,
      semesterId: course.semester,
    }));

    request(
      "put",
      `${programUrl.updateProgram}`,
      (res) => {
        if (res.status === 200) {
          successNoti("Cập nhật chương trình thành công!", 5000);
          history.push(`/training_course/teacher/program/${programId}`);
        } else {
          errorNoti("Cập nhật chương trình không thành công!", 5000);
        }
      },
      (error) => {
        console.error("Error updating:", error);
        errorNoti("Cập nhật chương trình không thành công!", 5000);
      },
      scheduleUpdates
    );
  };
  const fetchProgramData = () => {
    setIsLoading(true);
    request("get", `${programUrl.getProgramDetail}/${programId}`, (res) => {
      if (res && res.data && res.data.schedules) {
        setProgramName(res.data.name);
        const sortedCourses = res.data.schedules.sort(
          (a, b) => a.semester - b.semester
        );
        setCourses(sortedCourses);
      }
      setIsLoading(false);
    });
  };

  const handleEditSemesterCount = () => {
    setTempSemesterCount(semesterCount); // Copy giá trị hiện tại
    setIsEditingSemesterCount(true); // Chuyển sang trạng thái chỉnh sửa
  };

  const handleSaveSemesterCount = () => {
    if (tempSemesterCount < 1) {
      warningNoti("Số kì học phải lớn hơn 0.", 5000);
      return;
    }

    request(
      "put",
      `${programUrl.updateSemesterCount}`,
      () => {
        setSemesterCount(tempSemesterCount);
        successNoti("Cập nhật số kì học thành công!", 5000);
        setIsEditingSemesterCount(false);
      },
      (error) => {
        console.error("Error updating semester count:", error);
        errorNoti("Không thể cập nhật số kì học!", 5000);
      },

      { programId: programId, semesterCount: tempSemesterCount }
    );
  };
  const handleCancelEditSemesterCount = () => {
    setTempSemesterCount(semesterCount); // Khôi phục giá trị ban đầu
    setIsEditingSemesterCount(false); // Thoát chế độ chỉnh sửa
  };

  useEffect(() => {
    fetchProgramData();
  }, [programId]);

  const handleAddCourse = (needReload) => {
    if (needReload) {
      fetchProgramData();
    }
  };

  const handleSearch = (e) => {
    setSearchText(e.target.value);
  };

  const handleBack = (e) => {
    history.push(`/training_course/teacher/program/${programId}`);
  };

  const handleUpdateFromAPI = () => {
    setIsLoading(true);

    request(
      "get",
      `http://localhost:8080/api/training_prog_program/schedule/${programId}`,
      (res) => {
        if (res && res.data) {
          const updatedCourses = courses.map((course) => {
            const updatedCourse = res.data.find(
              (courseFromAPI) => String(courseFromAPI.id) === String(course.id)
            );
            if (updatedCourse) {
              return {
                ...course,
                semester: parseInt(updatedCourse.semester), // Convert to number directly
              };
            }
            return course;
          });

          const sortedCourses = updatedCourses.sort(
            (a, b) => a.semester - b.semester
          );
          setCourses(sortedCourses);
          successNoti("Cập nhật học kì thành công từ API!", 5000);
        } else {
          errorNoti("Không thể cập nhật học kì từ API!", 5000);
        }
        setIsLoading(false);
      },
      (error) => {
        console.error("Error fetching data from API:", error);
        errorNoti("Không thể kết nối đến API!", 5000);
        setIsLoading(false);
      }
    );
  };
  const filteredCourses = courses.filter(
    (course) =>
      course.courseName.toLowerCase().includes(searchText.toLowerCase()) ||
      course.id.toLowerCase().includes(searchText.toLowerCase())
  );

  // Sửa lại hàm xử lý selection change
  const handleSelectionChange = (newSelectionModel) => {
    // Lọc ra các id Học phần từ các dòng được chọn
    const selectedCourseIds = filteredCourses
      .filter((course) => newSelectionModel.includes(course.id))
      .map((course) => course.id);

    setSelectedRows(selectedCourseIds);
    console.log("Selected course IDs:", selectedCourseIds); // Debug
  };

  const handleDeleteCourses = () => {
    if (selectedRows.length === 0) {
      warningNoti("Vui lòng chọn ít nhất một học phần để xóa.", 5000);
      return;
    }

    // Kiểm tra môn tiên quyết cho tất cả các môn được chọn
    const allErrors = [];
    selectedRows.forEach((courseId) => {
      const errors = checkIfCourseIsPrerequisite(courseId);
      allErrors.push(...errors);
    });

    if (allErrors.length > 0) {
      allErrors.forEach((error) => warningNoti(error, 5000));
      return;
    }

    const payload = {
      programId: programId,
      courseIds: selectedRows,
    };

    request(
      "delete",
      programUrl.deleteCourses,
      (res) => {
        if (res.status === 200) {
          successNoti("Xóa học phần thành công!", 5000);
          fetchProgramData(); // Tải lại danh sách Học phần
          setSelectedRows([]); // Xóa selection
        } else {
          errorNoti("Xóa học phần không thành công!", 5000);
        }
      },
      (error) => {
        console.error("Error deleting courses:", error);
        errorNoti("Không thể xóa học phần!", 5000);
      },
      payload // Truyền payload
    );
  };

  const checkIfCourseIsPrerequisite = (courseId) => {
    const errors = [];

    // Duyệt qua tất cả các Học phần
    courses.forEach((course) => {
      // Nếu Học phần bị xóa là môn tiên quyết của Học phần khác
      if (course.prerequisites.includes(courseId)) {
        // Kiểm tra xem Học phần có nằm trong danh sách các môn bị xóa không
        if (!selectedRows.includes(course.id)) {
          errors.push(
            `Học phần "${course.courseName}" (Mã: ${course.id}) có môn tiên quyết là môn bạn đang xóa.`
          );
        }
      }
    });

    return errors;
  };
  const handleCourseChange = (courseId, newSemester) => {
    const requestData = {
      courseId: courseId,
      targetSemester: newSemester,
      programId: programId,
    };

    request(
      "post",
      `${programUrl.changeCourse}`,
      (res) => {
        if (res.status === 200 && res.data) {
          // Create a new array with updated semester values
          const updatedCourses = courses.map((course) => {
            const courseUpdate = res.data.find(
              (update) => update.id === course.id
            );
            return courseUpdate
              ? { ...course, semester: courseUpdate.semester }
              : course;
          });

          // Sort the updated courses by semester
          const sortedCourses = updatedCourses.sort(
            (a, b) => a.semester - b.semester
          );

          // Update the courses state
          setCourses(sortedCourses);

          // Create message showing changed courses
          const changedCoursesMessage = res.data.map((update) => {
            const course = courses.find((c) => c.id === update.id);
            return `Học phần với id: ${update.id} đã chuyển sang kì ${update.semester}`;
          });

          // Show single success notification with all changes
          if (changedCoursesMessage.length > 0) {
            successNoti(`${changedCoursesMessage.join("\n")}`, 5000);
          }

          // Reset action semester state
          setActionSemester((prev) => {
            const newState = { ...prev };
            delete newState[courseId];
            return newState;
          });

          // Clear any semester updates
          setSemesterUpdates({});
        } else {
          errorNoti("Cập nhật kì học không thành công!", 3000);
        }
      },
      (error) => {
        console.error("Error changing course semester:", error);
        errorNoti("Không thể tìm được cách chuyển học phần", 3000);
      },
      requestData
    );
  };

  const getCourseNameById = (courseId) => {
    const course = courses.find((c) => c.id === courseId);
    return course ? course.courseName : "Không có"; // Trả về tên học phần nếu tìm thấy, nếu không thì trả về "Không có"
  };

  const dataGridColumns = [
    {
      field: "id",
      headerName: "Mã học phần",
      align: "center",
      headerAlign: "center",
      flex: 0.75,
      renderCell: (params) => (
        <div style={{ fontSize: "0.85em" }}>{params.value}</div>
      ),
    },
    {
      field: "courseName",
      headerName: "Tên học phần",
      flex: 1.5,
      renderCell: (params) => (
        <div style={{ fontSize: "0.85em" }}>{params.value}</div>
      ),
    },
    {
      field: "credit",
      headerName: "Số tín chỉ",
      align: "center",
      headerAlign: "center",
      flex: 0.5,
      renderCell: (params) => (
        <div style={{ fontSize: "0.85em" }}>{params.value}</div>
      ),
    },
    {
      field: "semester",
      headerName: "Kì học",
      align: "center",
      headerAlign: "center",
      flex: 0.5,
      renderCell: (params) => (
        <FormControl fullWidth variant="outlined">
          <Select
            value={semesterUpdates[params.row.id] || params.row.semester || ""}
            onChange={(e) =>
              handleSemesterChange(params.row.id, e.target.value)
            }
            size="small"
            style={{ fontSize: "0.85em" }}
          >
            {[...Array(semesterCount)].map((_, index) => (
              <MenuItem key={index + 1} value={index + 1}>
                Kì {index + 1}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      ),
    },
    {
      field: "prerequisites",
      headerName: "Tiền quyết",
      flex: 1,
      renderCell: (params) => {
        const prerequisites = params.value || [];
        if (Array.isArray(prerequisites) && prerequisites.length > 0) {
          return (
            <div
              style={{
                fontSize: "0.85em",
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
              }}
            >
              {prerequisites.join(", ")}{" "}
              {/* Hiển thị các ID tiền quyết, ngăn cách bằng dấu phẩy */}
            </div>
          );
        }
        return "Không có"; // Nếu không có môn tiên quyết, hiển thị "Không có"
      },
    },
    {
      field: "action",
      headerName: "Hành động",
      align: "center",
      headerAlign: "center",
      flex: 1,
      renderCell: (params) => (
        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <FormControl
            variant="outlined"
            size="small"
            style={{ width: "100px" }}
          >
            <Select
              value={actionSemester[params.row.id] || ""}
              onChange={(e) =>
                setActionSemester({
                  ...actionSemester,
                  [params.row.id]: e.target.value,
                })
              }
              style={{ fontSize: "0.85em" }}
              displayEmpty
            >
              <MenuItem value="" disabled>
                Chọn Kì
              </MenuItem>
              {[...Array(semesterCount)].map((_, index) => (
                <MenuItem key={index + 1} value={index + 1}>
                  Kì {index + 1}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <Button
            variant="contained"
            size="small"
            onClick={() => {
              const newSemester = actionSemester[params.row.id];
              if (newSemester) {
                handleCourseChange(params.row.id, newSemester);
              } else {
                warningNoti("Vui lòng chọn Kì học trước khi cập nhật", 3000);
              }
            }}
            disabled={!actionSemester[params.row.id]}
            style={{ fontSize: "0.85em" }}
          >
            Chuyển Kì
          </Button>
        </div>
      ),
    },
  ];

  return (
    <Paper elevation={3} style={styles.paper}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
          {`Chương trình: ${programName} (Mã: ${programId})`}
        </Typography>
        <div style={styles.semesterCount}>
          {isEditingSemesterCount ? (
            <div style={styles.editContainer}>
              <TextField
                type="number"
                size="small"
                value={tempSemesterCount}
                onChange={(e) => setTempSemesterCount(Number(e.target.value))}
                style={{ width: "60px", marginRight: "8px", height: "40px" }} // Điều chỉnh chiều cao của TextField
                inputProps={{ min: 1, style: { textAlign: "center" } }} // Căn giữa số
              />
              <Tooltip title="Lưu">
                <IconButton
                  color="primary"
                  onClick={handleSaveSemesterCount}
                  style={styles.iconButton}
                >
                  <CheckIcon />
                </IconButton>
              </Tooltip>
              <Tooltip title="Hủy">
                <IconButton
                  color="secondary"
                  onClick={handleCancelEditSemesterCount}
                  style={styles.iconButton}
                >
                  <CloseIcon />
                </IconButton>
              </Tooltip>
            </div>
          ) : (
            <div style={styles.viewContainer}>
              <Typography variant="h6" style={{ marginRight: "8px" }}>
                {`Số học Kì của chương trình : ${semesterCount}`}
              </Typography>
              <Tooltip title="Chỉnh sửa">
                <IconButton
                  size="small"
                  onClick={handleEditSemesterCount}
                  style={styles.iconButton}
                >
                  <EditIcon />
                </IconButton>
              </Tooltip>
            </div>
          )}
        </div>
        <div style={styles.searchArea}>
          <Button
            variant="contained"
            color="primary"
            style={styles.firstButton}
            onClick={() => setDialogOpen(true)} // Open dialog
          >
            Thêm Học phần
          </Button>

          <Button
            variant="contained"
            color="secondary"
            style={styles.firstButton}
            onClick={handleUpdateFromAPI}
          >
            Sắp xếp thông minh
          </Button>
          <Button
            variant="contained"
            color="error"
            onClick={handleDeleteCourses}
            style={styles.actionButton}
          >
            Xóa Học phần
          </Button>
          <Button
            variant="contained"
            color="primary"
            style={styles.firstButton}
            onClick={handleUpdateSemesters}
          >
            Lưu
          </Button>

          <TextField
            label="Tìm kiếm"
            variant="outlined"
            value={searchText}
            onChange={handleSearch}
            style={styles.searchBox}
          />
        </div>
      </div>

      <DataGrid
        rows={filteredCourses}
        columns={dataGridColumns}
        checkboxSelection
        disableRowSelectionOnClick
        loading={isLoading}
        hideFooter
        pagination={false}
        style={styles.table}
        onRowSelectionModelChange={handleSelectionChange} // Updated prop name
        rowSelectionModel={selectedRows} // Updated prop name
        getRowId={(row) => row.id} // Add this to ensure proper row identification
      />
      <AddCourseDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        programId={programId}
        onCourseAdded={handleAddCourse}
        existingCourses={courses}
      />
    </Paper>
  );
};

export default UpdateProgramPage;
