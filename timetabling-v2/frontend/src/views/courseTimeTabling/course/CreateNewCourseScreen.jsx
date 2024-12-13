import { useState, useEffect } from 'react';
import { useCourseData } from "services/useCourseData";
import CourseDialog from './components/CourseDialog';

export default function CreateNewCourse({ open, handleClose, selectedCourse }) {
  const [id, setId] = useState('');
  const [courseName, setCourseName] = useState('');
  const [credit, setCredit] = useState('');
  const [isUpdate, setIsUpdate] = useState(false);
  const { createCourse, updateCourse } = useCourseData();

  useEffect(() => {
    if (selectedCourse) {
      setId(selectedCourse.id);
      setCourseName(selectedCourse.courseName);
      setCredit(selectedCourse.credit);
      setIsUpdate(true);
    } else {
      setId('');
      setCourseName('');
      setCredit('');
      setIsUpdate(false);
    }
  }, [selectedCourse]);

  const handleSubmit = async () => {
    const courseData = { id, courseName, credit };
    try {
      if (isUpdate) {
        await updateCourse(courseData);
      } else {
        await createCourse(courseData);
      }
      handleClose();
    } catch (error) {
      // Error handling is managed by the mutations
    }
  };

  return (
    <CourseDialog
      open={open}
      handleClose={handleClose}
      id={id}
      courseName={courseName}
      credit={credit}
      setId={setId}
      setCourseName={setCourseName}
      setCredit={setCredit}
      onSubmit={handleSubmit}
      isUpdate={isUpdate}
    />
  );
}
