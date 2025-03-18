import Keycloak from "keycloak-js";
import { config } from "./constant";

//
export const initOptions = { pkceMethod: "S256" };
export const KC_REALM = "OPEN ERP";

// Pass initialization options as required or leave blank to load from 'keycloak.json'
const keycloak = new Keycloak({
  url: `${config.url.KEYCLOAK_BASE_URL}`,
  realm: "OPEN ERP",
  clientId: "smart_delivery"
});

export default keycloak;
