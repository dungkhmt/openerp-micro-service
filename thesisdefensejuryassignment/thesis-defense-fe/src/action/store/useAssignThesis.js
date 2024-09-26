import { create } from "zustand";
const useAssignThesis = create((set) => ({
  assignedThesis: [],
  handleSelectThesis: (thesis) =>
    set((state) => {
      const currentIndex = state?.assignedThesis?.find(
        (item) => item === thesis?.id
      );
      if (!currentIndex) {
        return { assignedThesis: [...state?.assignedThesis, thesis?.id] };
      } else {
        return {
          assignedThesis: state?.assignedThesis?.filter(
            (item) => item !== thesis?.id
          ),
        };
      }
    }),
  clearAssignedThesis: () =>
    set((state) => ({
      assignedThesis: [],
    })),
}));
export default useAssignThesis;
