export const admin = {
  id: "MENU_ADMIN",
  icon: "GiTeacher",
  text: "Admin",
  child: [
    {
      id: "MENU_ADMIN.DEPARTMENT",
      path: "/department",
      isPublic: false,
      text: "Department",
      child: [],
    },
    {
      id: "MENU_ADMIN.JOB_POSITION",
      path: "/job-position",
      isPublic: false,
      text: "Job Position",
      child: [],
    },
  ],
};
