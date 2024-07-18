import { useState } from "react";
/**
 * Custom hook để lựa chọn giáo viên và đồ án
 * @Input: ds giáo viên, ds đồ án
 *
 */

const useAssignTeacherThesis = (teacherList = [], thesisList = []) => {
  const [assignedTeacher, setAssignedTeacher] = useState(teacherList);
  const [assignedThesis, setAssignedThesis] = useState(thesisList);
  // lựa chọn đồ án, nếu chọn 2 lần thì loại khỏi ds đã chọn
  const handleSelectThesis = (thesis) => {
    const currentIndex = assignedThesis?.find((item) => item === thesis?.id);
    if (!currentIndex) {
      return setAssignedThesis((prevAssignedThesis) => [
        ...prevAssignedThesis,
        thesis?.id,
      ]);
    } else {
      return setAssignedThesis((prevAssignedThesis) =>
        prevAssignedThesis.filter((item) => item !== thesis?.id)
      );
    }
  };
  // lựa chọn giáo viên, nếu chọn 2 lần thì loại khỏi ds đã chọn
  const handleSelectTeacher = (teacher) => {
    const currentIndex = assignedTeacher?.find(
      (item) => item?.id === teacher?.id
    );
    if (!currentIndex) {
      return setAssignedTeacher((prevAssignedTeacher) => [
        ...prevAssignedTeacher,
        { ...teacher },
      ]);
    }
    return setAssignedTeacher((prevAssignedTeacher) =>
      prevAssignedTeacher?.filter((item) => item?.id !== teacher?.id)
    );
  };
  // function để cập nhật gv và đồ án, thêm ds gvien đã chọn vào ds ban đầu
  const handleSelectTeacherList = (teacherList) => {
    return setAssignedTeacher((prevAssignedTeacher) => [
      ...prevAssignedTeacher,
      ...teacherList,
    ]);
  };
  // Lựa chọn role
  const handleAssignRole = (e) => {
    const role = e.target.value;
    const teacherId = e.target.name;
    const teacher = assignedTeacher.find((item) => item?.id === teacherId);
    if (teacher) {
      teacher.role = role;
      return setAssignedTeacher((prevAssignedTeacher) => [
        ...prevAssignedTeacher,
      ]);
    }
  };
  const clearAssignedThesis = () => setAssignedThesis([]);
  const clearAssignedTeacher = () => setAssignedTeacher([]);

  return {
    assignedTeacher,
    assignedThesis,
    handleAssignRole,
    handleSelectTeacher,
    handleSelectThesis,
    setAssignedTeacher,
    setAssignedThesis,
    clearAssignedTeacher,
    clearAssignedThesis,
    handleSelectTeacherList,
  };
};

export default useAssignTeacherThesis;
