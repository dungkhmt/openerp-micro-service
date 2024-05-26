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
        id: "",
        path: "/view-job-posts/employer",
        isPublic: true,
        text: "View your all job post",
        child: [],
      },      
      {
        id: "MENU_CREATE_jOBPOST.View_YOUR_JOB_POST",
        path: "/view-job-post/user",
        isPublic: true,
        text: "View application from hotest job",
        child: [],
      }

    ],
  };
  