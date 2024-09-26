import { KC_REALM } from "config/keycloak";
import { config } from "../../controllers/constant";

export const user = {
  id: "MENU_USER",
  icon: "ManageAccountsIcon",
  text: "User",
  child: [
    {
      id: "MENU_USER.ALL_USER",
      path: "/all-user",
      isPublic: true,
      text: "All users",
      child: [],
    },
    {
      id: "MENU_USER.SETTINGS", // TODO: change this
      onClick: () => {
        window.location.href = `${config.url.KEYCLOAK_BASE_URL}/admin/${KC_REALM}/console/#/${KC_REALM}/users`;
      },
      isPublic: true,
      text: "Settings",
      child: [],
    },
    {
      id: "MENU_USER_SEND_MAIL_TO_USERS",
      path: "/user-group/user/send-mail",
      isPublic: true,
      text: "Gửi email",
      child: [],
    },
  ],
};
