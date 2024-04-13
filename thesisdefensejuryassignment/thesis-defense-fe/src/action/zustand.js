import { create } from "zustand";
const useAssignTeacher = create((set) => ({
  assignedTeacher: [],
  handleSelectTeacher: (teacher) =>
    set((state) => {
      const currentIndex = state.assignedTeacher?.find(
        (item) => item?.id === teacher?.id
      );
      if (!currentIndex) {
        return { assignedTeacher: [...state.assignedTeacher, teacher] };
      } else {
        return {
          assignedTeacher: state.assignedTeacher?.filter(
            (item) => item?.id !== teacher?.id
          ),
        };
      }
    }),
  handleAssignRole: (e) =>
    set((state) => {
      const role = e.target.value;
      const teacherId = e.target.name;
      const teacher = state?.assignedTeacher.find(
        (item) => item?.id === teacherId
      );
      if (teacher) {
        teacher.role = role;
        return {
          assignedTeacher: [...state?.assignedTeacher],
        };
      }
    }),
}));

const useAssignThesis = create((set) => ({
  assignedThesis: [],
  handleSelectThesis: (thesis) =>
    set((state) => {
      const currentIndex = state.assignedThesis?.find(
        (item) => item === thesis?.id
      );
      if (!currentIndex) {
        return { assignedThesis: state?.assignedThesis?.push(thesis) };
      } else {
        return {
          assignedThesis: state?.assignedThesis?.filter(
            (item) => item !== thesis?.id
          ),
        };
      }
    }),
}));
export { useAssignTeacher, useAssignThesis };
