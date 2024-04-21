export const createJobPost = {
    id: "MENU_CREATE_jOBPOST",
    icon: "DashboardIcon",
    text: "Job Post",
    child: [
      {
        id: "MENU_CREATE_jOBPOST.CREATE_JOB_POST",
        path: "/create-job-post",
        isPublic: true,
        text: "Create Job Post",
        child: [],
      },
      {
        id: "MENU_CREATE_jOBPOST.View_YOUR_JOB_POST",
        path: "/view-job-post/user/dungpq",
        isPublic: true,
        text: "View all employee job post",
        child: [],
      },
      {
        id: "MENU_CREATE_jOBPOST.View_JOB_POST",
        path: "/view-job-post",
        isPublic: true,
        text: "View all job post",
        child: [],
      }
    ],
  };
  