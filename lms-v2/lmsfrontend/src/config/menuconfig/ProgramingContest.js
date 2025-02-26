export const ProgrammingContestMenuTeacher = {
  id: "MENU_PROGRAMMING_CONTEST_MANAGER",
  icon: "ProgrammingIcon",
  text: "Programming Teacher",
  child: [
    {
      id: "MENU_PROGRAMMING_CONTEST_MANAGER.MENU_PROGRAMMING_CONTEST_MANAGER_LIST_PROBLEM",
      path: "/programming-contest/list-problems",
      isPublic: false,
      icon: null,
      text: "Problem",
      child: [],
    },
    {
      id: "MENU_PROGRAMMING_CONTEST_MANAGER.MENU_PROGRAMMING_CONTEST_MANAGER_LIST_CONTEST",
      path: "/programming-contest/teacher-list-contest-manager",
      isPublic: false,
      icon: null,
      text: "Contest",
      child: [],
    },
    {
      id: "MENU_PROGRAMMING_CONTEST_MANAGER.MENU_PROGRAMMING_CONTEST_ADMIN_VIEW_ALL_CONTEST",
      path: "/programming-contest/list-all-contests",
      isPublic: false,
      icon: null,
      text: "All contests",
      child: [],
    },
  ],
};

export const ProgrammingContestMenuStudent = {
  id: "MENU_PROGRAMMING_CONTEST_PARTICIPANT",
  icon: "CodeIcon",
  text: "Programming Student",
  child: [
    // {
    //   id: "MENU_PROGRAMMING_CONTEST_PARTICIPANT.MENU_PROGRAMMING_CONTEST_PARTICIPANT_LIST_NOT_REGISTERED_CONTEST",
    //   //path: "/programming-contest/student-list-contest-not-registered",
    //   isPublic: false,
    //   icon: null,
    //   text: "Join contest (N/A)",
    //   child: [],
    // },
    {
      id: "MENU_PROGRAMMING_CONTEST_PARTICIPANT.MENU_PROGRAMMING_CONTEST_PARTICIPANT_LIST_REGISTERED_CONTEST",
      path: "/programming-contest/student-list-contest-registered",
      isPublic: false,
      icon: null,
      text: "My Contests",
      child: [],
    },    {
      id: "MENU_PROGRAMMING_CONTEST_PARTICIPANT.MENU_PROGRAMMING_CONTEST_PARTICIPANT_LIST_REGISTERED_CONTEST",
      path: "/programming-contest/student-list-library",
      isPublic: false,  
      icon: null,
      text: "My Library",
      child: [],
    },
  ],
};
