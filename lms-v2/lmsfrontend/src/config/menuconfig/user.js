import {config} from "config/config";
import {KC_REALM} from "config/keycloak";

export const user = {
  id: "MENU_USER",
  path: "",
  isPublic: false,
  icon: "UsersIcon",
  text: "Users",
  child: [
    {
      id: "MENU_USER.MENU_USER_LIST",
      onClick: () => {
        window.location.href = `${config.url.KEYCLOAK_BASE_URL}/admin/${KC_REALM}/console/#/${KC_REALM}/users`;
      },
      isPublic: false,
      text: "Manage Users (Keycloak)",
      child: [],
    },
    {
      id: "MENU_USER.MENU_USER_ADD",
      path: "/user/upload",
      isPublic: false,
      text: "Upload list",
      child: [],
    },
    {
      id: "MENU_USER.MENU_EXAM_USER_MANAGEMENT",
      path: "/exam-class/list",
      isPublic: false,
      text: "Exam Class Users",
      child: [],
    },
    {
      id: "MENU_USER.MENU_USER_ADD",
      path: "/generated-user/upload",
      isPublic: false,
      text: "Upload generated list",
      child: [],
    },
    {
      id: "MENU_USER.MENU_USER_SEND_MAIL_TO_USERS",
      path: "/user-group/user/send-mail",
      isPublic: false,
      text: "Gửi email",
      child: [],
    },
  ],
};
