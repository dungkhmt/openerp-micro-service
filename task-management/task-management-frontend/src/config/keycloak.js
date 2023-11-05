import Keycloak from "keycloak-js";
import { config } from "./config";

//
export const initOptions = { pkceMethod: "S256" };
export const KC_REALM = "LMS";
// export const KC_REALM = "OpenERP-Dev";

// Pass initialization options as required or leave blank to load from 'keycloak.json'
const keycloak = new Keycloak({
  url: `${config.url.KEYCLOAK_BASE_URL}`,
  realm: KC_REALM,
  clientId: "lms-ui-dev",
  // clientId: "openerp-ui-dev",
});

export default keycloak;
