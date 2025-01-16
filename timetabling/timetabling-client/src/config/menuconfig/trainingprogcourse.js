const prefix = "/training_course";
export const TrainingFrogCourse = {
  id: "MENU_TRAINING_COURSE",
  icon: "DashboardIcon",
  text: "Quản lý chương trình",
  child: [

    {
      id: "MENU_STUDENT_COURSE",
      path: `${prefix}/student/course`,
      isPublic: true,
      text: "Danh sách học phần",
      child: [],
    },
    {
      id: "MENU_STUDENT_PROGRAM",
      path: `${prefix}/student/program`,
      isPublic: true,
      text: "Danh sách học phần",
      child: [],
    },
    {
      id: "MENU_TRAINING_COURSE",
      path: `${prefix}/teacher/course`,
      isPublic: true,
      text: "Quản lý học phần",
      child: [],
    },
    {
      id: "MENU_TRAINING_PROGRAM",
      path: `${prefix}/teacher/program`,
      isPublic: true,
      text: "Quản lý chương trình học",
      child: [],
    },
    
   
  ],
};