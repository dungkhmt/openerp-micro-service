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
  updateMultipleApplicationStatus:
    "/application/update-multiple-application-status",
  updateApplication: "/application/update-application",
  updateAssignStatus: "/application/update-assign-status",
  deleteApplication: "/application/delete-application",
  deleteMultipleApplication: "/application/delete-multiple-application",
  autoAssignClass: "/application/auto-assign-class",
  oldAutoAssignClass: "/application/old-auto-assign-class",
  getTaBySemester: "/application/get-ta-by-semester",
  getUniqueTaName: "/application/get-unique-ta",
  getTaInfo: "application/get-ta-info",
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

export const dashboardUrl = {
  getApplicatorData: "/application/get-applicator-data",
  getApplicationData: "/application/get-application-data",
  getTaData: "/application/get-ta-data",
  getCourseData: "/application/get-course-data",
  getClassNumbData: "/class-call/get-class-numb-data",
};
