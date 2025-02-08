const prod = {
  url: {
    KEYCLOAK_BASE_URL: "https://programming.daotao.ai/iam",
    API_URL: "/api",
  },
  REALM: "LMS",
  CLIENT_ID: "timetable"
}; 

const dev = {
  url: {
    KEYCLOAK_BASE_URL: "https://erp3.soict.ai/iam",
    API_URL: "http://localhost:8080/api",
  },
  //REALM: "OpenERP-Dev",
  //CLIENT_ID: "openerp-ui-dev"
  REALM: "LMS",
  CLIENT_ID: "timetable"
};

export const config = process.env.NODE_ENV === "development" ? dev : prod;
