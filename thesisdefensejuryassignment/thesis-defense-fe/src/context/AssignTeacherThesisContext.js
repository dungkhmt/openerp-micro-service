import { createContext, useState, useContext, useMemo } from "react";
import { useForm } from "react-hook-form";
import { useFetchData } from "hooks/useFetchData";

const Context = createContext(null);
export const AssignTeacherThesisContext = ({ children }) => {
  const [assignedTeacher, setAssignedTeacher] = useState([]);
  const [assignedThesis, setAssignedThesis] = useState([]);
  const { register, handleSubmit } = useForm();
  const handleSelectTeacher = useMemo(
    () => (teacher) => {
      const currentIndex = assignedTeacher.find(
        (item) => item?.id === teacher?.id
      );
      if (!currentIndex) {
        return setAssignedTeacher((prevAssignedTeacher) => [
          ...prevAssignedTeacher,
          teacher,
        ]);
      } else {
        return setAssignedTeacher((prevAssignedTeacher) =>
          prevAssignedTeacher.filter((item) => item?.id !== teacher?.id)
        );
      }
    },
    [assignedTeacher]
  );
  const handleSelectThesis = useMemo(
    () => (thesis) => {
      const currentIndex = assignedThesis.find((item) => item === thesis?.id);
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
    },
    [assignedThesis]
  );

  return (
    <Context.Provider
      value={{
        assignedTeacher,
        handleSelectTeacher,
        register,
        handleSubmit,
        assignedThesis,
        handleSelectThesis,
      }}
    >
      {children}
    </Context.Provider>
  );
};

export const useAssignTeacherThesis = () => useContext(Context);
