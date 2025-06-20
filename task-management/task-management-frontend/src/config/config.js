/*
const prod = {
  url: {
    API_URL: "/api",
  },
  keycloak: {
    url: "https://programming.daotao.ai/iam",
    realm: "LMS",
    clientId: "task-management",
  },
};

const dev = {
  url: {
    API_URL: "http://localhost:8080/api",
  },
  keycloak: {
    url: "https://erp3.soict.ai/iam",
    // realm: "LMS",
    // clientId: "task-management",
    realm: "OpenERP-Dev",
    clientId: "openerp-ui-dev",
  },
};

export const config = process.env.NODE_ENV === "development" ? dev : prod;
*/

const apiUrl = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const keycloakUrl = import.meta.env.VITE_KEYCLOAK_URL || "https://erp3.soict.ai/iam";
const keycloakRealm = import.meta.env.VITE_KEYCLOAK_REALM || "OpenERP-Dev";
const keycloakClientId = import.meta.env.VITE_KEYCLOAK_CLIENTID || "openerp-ui-dev";
console.log(process.env.NODE_ENV)
export const config = {
  url: {
    API_URL: process.env.NODE_ENV === 'prod' ? "https://backend-service-48138382301.asia-southeast1.run.app/api" : apiUrl,
  },
  keycloak: {
    url: keycloakUrl,
    realm: keycloakRealm,
    clientId: keycloakClientId,
  },
};
