import { useState } from "react";

const useAssignTeacherThesis = () => {
  const [assignedTeacher, setAssignedTeacher] = useState([]);
  const [assignedThesis, setAssignedThesis] = useState([]);
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
  const handleSelectTeacher = (teacher) => {
    const currentIndex = assignedTeacher?.find(
      (item) => item?.id === teacher?.id
    );
    if (!currentIndex) {
      return setAssignedTeacher((prevAssignedTeacher) => [
        ...prevAssignedTeacher,
        teacher,
      ]);
    } else {
      return setAssignedTeacher((prevAssignedTeacher) =>
        prevAssignedTeacher?.filter((item) => item?.id !== teacher?.id)
      );
    }
  };
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
    clearAssignedTeacher,
    clearAssignedThesis,
  };
};

export default useAssignTeacherThesis;
