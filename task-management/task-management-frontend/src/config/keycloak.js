import Keycloak from "keycloak-js";
import { config } from "./config";

//
export const initOptions = { pkceMethod: "S256" };
export const KC_REALM = config.keycloak.realm;

// Pass initialization options as required or leave blank to load from 'keycloak.json'
const keycloak = new Keycloak(config.keycloak);

export default keycloak;
