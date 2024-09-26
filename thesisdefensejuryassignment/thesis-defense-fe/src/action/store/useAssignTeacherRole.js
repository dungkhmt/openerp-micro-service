import { create } from "zustand";
const useAssignTeacherRole = create((set) => ({
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
  clearAssignedTeacher: () =>
    set(() => ({
      assignedTeacher: [],
    })),
}));

export default useAssignTeacherRole;
