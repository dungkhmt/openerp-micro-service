export const ProgrammingContestMenuTeacher = {
  id: "MENU_PROGRAMMING_CONTEST_MANAGER",
  icon: "LocalLibraryIcon",
  text: "Programming Contest Teacher",
  child: [
    // {
    //   id: "MENU_PROGRAMMING_CONTEST_MANAGER.MENU_PROGRAMMING_CONTEST_MANAGER_CREATE_PROBLEM",
    //   path: "/programming-contest/create-problem",
    //   isPublic: false,
    //   icon: null,
    //   text: "Create Problem",
    //   child: [],
    // },
    {
      id: "MENU_PROGRAMMING_CONTEST_MANAGER.MENU_PROGRAMMING_CONTEST_MANAGER_LIST_PROBLEM",
      path: "/programming-contest/list-problems",
      isPublic: false,
      icon: null,
      text: "Manage Problem",
      child: [],
    },
    // {
    //   id: "MENU_PROGRAMMING_CONTEST_MANAGER.MENU_PROGRAMMING_CONTEST_MANAGER_CREATE_CONTEST",
    //   path: "/programming-contest/create-contest",
    //   isPublic: false,
    //   icon: null,
    //   text: "Create Contest",
    //   child: [],
    // },
    {
      id: "MENU_PROGRAMMING_CONTEST_MANAGER.MENU_PROGRAMMING_CONTEST_MANAGER_LIST_CONTEST",
      path: "/programming-contest/teacher-list-contest-manager",
      isPublic: false,
      icon: null,
      text: "Manage Contest",
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
  icon: "LocalLibraryIcon",
  text: "Programming Contest Student",
  child: [
    {
      id: "MENU_PROGRAMMING_CONTEST_PARTICIPANT.MENU_PROGRAMMING_CONTEST_PARTICIPANT_LIST_NOT_REGISTERED_CONTEST",
      path: "/programming-contest/student-list-contest-not-registered",
      isPublic: false,
      icon: null,
      text: "Join contest",
      child: [],
    },
    {
      id: "MENU_PROGRAMMING_CONTEST_PARTICIPANT.MENU_PROGRAMMING_CONTEST_PARTICIPANT_LIST_REGISTERED_CONTEST",
      path: "/programming-contest/student-list-contest-registered",
      isPublic: false,
      icon: null,
      text: "My Contests",
      child: [],
    },
    /*
    {
      id: "MENU_PROGRAMMING_CONTEST_PARTICIPANT.MENU_PROGRAMMING_CONTEST_PARTICIPANT_LIST_PUBLIC_PROBLEM",
      path: "/programming-contest/student-public-problem",
      isPublic: false,
      icon: null,
      text: "Practical Problem",
      child: [],
    },
    */
  ],
};
