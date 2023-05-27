import {config} from "config/config";
import {KC_REALM} from "config/keycloak";

export const user = {
  id: "MENU_USER",
  path: "",
  isPublic: false,
  icon: "ManageAccountsIcon",
  text: "Người dùng",
  child: [
    // {
    //   id: "MENU_USER_CREATE",
    //   path: "/userlogin/create",
    //   isPublic: false,
    //   icon: "StarBorder",
    //   text: "Tạo mới",
    //   child: [],
    // },
    // {
    //   id: "MENU_USER_LIST",
    //   path: "/userlogin/list",
    //   isPublic: false,
    //   icon: "StarBorder",
    //   text: "Danh sách",
    //   child: [],
    // },
    // {
      //   id: "MENU_USER_APPROVE_REGISTRATION",
      //   path: "/user-group/user/approve-register",
      //   isPublic: false,
      //   icon: "StarBorder",
      //   text: "Phê duyệt",
      //   child: [],
      // },
    {
      id: "MENU_USER.MENU_USER_LIST",
      onClick: () => {
        window.location.href = `${config.url.KEYCLOAK_BASE_URL}/admin/${KC_REALM}/console/#/${KC_REALM}/users`;
      },
      isPublic: false,
      text: "Manage users (Keycloak)",
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
      id: "MENU_USER.MENU_USER_SEND_MAIL_TO_USERS",
      path: "/user-group/user/send-mail",
      isPublic: false,
      text: "Gửi email",
      child: [],
    },
  ],
};
