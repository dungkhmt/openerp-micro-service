const prefix = "/training_course";
export const TrainingFrogCourse = {
  id: "MENU_TRAINING_COURSE",
  icon: "DashboardIcon",
  text: "Quản lý chương trình học",
  child: [
    
    {
      id: "MENU_TRAINING_COURSE_LIST",
      path: `${prefix}/teacher/course_list`,
      isPublic: true,
      text: "Danh mục học tập",
      child: [],
    },
    {
      id: "MENU_TRAINING_COURSE",
      path: `${prefix}/teacher/course`,
      isPublic: true,
      text: "Danh mục học phần",
      child: [],
    },
    {
      id: "MENU_TRAINING_PROGRAM",
      path: `${prefix}/teacher/program`,
      isPublic: true,
      text: "Danh mục quản lý chương trình",
      child: [],
    },
  ],
};