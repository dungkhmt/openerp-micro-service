import { config } from "config/constant";
import { KC_REALM } from "config/keycloak";

export const user = {
  id: "MENU_USER_CONTAINER",
  icon: "ManageAccountsIcon",
  text: "User",
  child: [
    {
      id: "MENU_USER.ALL_USER", // TODO: change this
      onClick: () => {
        window.location.href = `${config.url.KEYCLOAK_BASE_URL}/admin/${KC_REALM}/console/#/${KC_REALM}/users`;
      },
      isPublic: false,
      text: "All users",
      child: [],
    },
    {
      id: "MENU_USER.CONTAINER.CUSTOMER",
      path: "/user/customer",
      isPublic: false,
      text: "Customer",
      child: [],
    },
  ],
};
