
  
  export const userUrl = {
    getUserInfo: "/user/get-user-info",
  };
  
  export const semesterUrl = {
    getAllSemester: "/ta-semester/get-all-semester",
    getCurrentSemester: "/ta-semester/get-current-semester",
  };
  
 
export const courseUrl = {
    getAllCourse: "/training_prog_course/getall",
    createCourse: "/training_prog_course/create",
    getCourseDetail: "/training_prog_course/detail",
    deleteCourse : "/training_prog_course/delete",
    updateCourse : "/training_prog_course/update",
    importCourse :"/training_prog_course/import-training-prog-course"
  };

  export const programUrl = {
    getAllPrograms: "/training_prog_program/getall",
    createProgram: "/training_prog_program/create",
    getProgramDetail: "/training_prog_program/detail",
    deleteProgram : "/training_prog_program/deletes",
    updateProgram : "/training_prog_program/update",
    addCoursesToProgram: "/training_prog_program/add-courses",
    getAllCourseId: "/training_prog_program/get-all-courseid",
    getAvailableCourse: "/training_prog_program/get-available-course",
    deleteCourses:"/training_prog_program/delete",
    updateSemesterCount  :"/training_prog_program/semester",
    changeCourse  :"/training_prog_program/course-change",
    deletePrograms  :"/training_prog_program/delete-programs",
    origin: "/training_prog_program", 
  };