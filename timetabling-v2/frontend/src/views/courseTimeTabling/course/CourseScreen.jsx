import { useState } from "react";
import { useCourseData } from "services/useCourseData";
import CourseDataGrid from "./components/CourseDataGrid";
import CreateNewCourse from "./CreateNewCourseScreen";
import DeleteConfirmDialog from "./components/DeleteConfirmDialog";

export default function CourseScreen() {
  const { courses, isLoading, deleteCourse, isDeleting } = useCourseData();
  const [selectedCourse, setSelectedCourse] = useState(null);
  const [isDialogOpen, setDialogOpen] = useState(false);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [deleteCourseId, setDeleteCourseId] = useState(null);

  const handleCreate = () => {
    setSelectedCourse(null);
    setDialogOpen(true);
  };

  const handleUpdate = (course) => {
    setSelectedCourse(course);
    setDialogOpen(true);
  };

  const handleDelete = (courseId) => {
    setDeleteCourseId(courseId);
    setConfirmDeleteOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (deleteCourseId) {
      await deleteCourse(deleteCourseId);
    }
    setConfirmDeleteOpen(false);
    setDeleteCourseId(null);
  };

  return (
    <div style={{ height: "500px", width: "100%" }}>
      <CourseDataGrid
        courses={courses}
        isLoading={isLoading}
        onEdit={handleUpdate}
        onDelete={handleDelete}
        onCreate={handleCreate}
      />

      <CreateNewCourse
        open={isDialogOpen}
        handleClose={() => setDialogOpen(false)}
        selectedCourse={selectedCourse}
      />

      <DeleteConfirmDialog
        open={confirmDeleteOpen}
        onClose={() => setConfirmDeleteOpen(false)}
        onConfirm={handleConfirmDelete}
        isDeleting={isDeleting}
      />
    </div>
  );
}
