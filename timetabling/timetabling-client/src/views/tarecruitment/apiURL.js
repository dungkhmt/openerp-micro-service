export const applicationUrl = {
  createApplication: "/application/create-application",
  getApplicationByClass: "/application/get-application-by-class",
  getApplicationById: "/application/get-application-by-id",
  getMyApplication: "/application/my-applications",
  getApplicationBySemester: "/application/get-application-by-semester",
  getApplicationByStatusAndSemester:
    "/application/get-application-by-status-and-semester",
  getAssignListFile: "/application/get-assign-list-file",
  updateApplicationStatus: "/application/update-application-status",
  updateApplication: "/application/update-application",
  updateAssignStatus: "/application/update-assign-status",
  deleteApplication: "/application/delete-application",
  deleteMultipleApplication: "/application/delete-multiple-application",
  autoAssignClass: "/application/auto-assign-class",
  getTaBySemester: "/application/get-ta-by-semester",
};

export const classCallUrl = {
  createClass: "/class-call/create-class",
  getClassBySemesterL: "/class-call/get-class-by-semester",
  getClassById: "/class-call/get-class",
  getMyRegisterClass: "/class-call/get-my-registered-class",
  updateClass: "/class-call/update-class",
  deleteClass: "/class-call/delete-class",
  deleteMultipleClass: "/class-call/delete-multiple-class",
  importClass: "/class-call/import-class",
};

export const userUrl = {
  getUserInfo: "/user/get-user-info",
};

export const semesterUrl = {
  getAllSemester: "/ta-semester/get-all-semester",
  getCurrentSemester: "/ta-semester/get-current-semester",
};
