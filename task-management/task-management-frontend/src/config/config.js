const prod = {
  url: {
    API_URL: "/api",
    NOTIFICATION_SERVER_URL: "http://localhost:8081/api",
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
    NOTIFICATION_SERVER_URL: "http://localhost:8081/api",
  },
  keycloak: {
    url: "https://openerp3.dailyopt.ai/iam",
    realm: "LMS",
    clientId: "task-management",
  },
};

export const config = process.env.NODE_ENV === "development" ? dev : prod;
