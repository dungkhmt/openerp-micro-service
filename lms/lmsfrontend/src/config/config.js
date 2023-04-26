const prod = {
  url: {
    KEYCLOAK_BASE_URL: "https://openerp3.dailyopt.ai/iam",
    API_URL: "/api",
  },
};

const dev = {
  url: {
    KEYCLOAK_BASE_URL: "https://openerp3.dailyopt.ai/iam",
    API_URL: "http://localhost/api",
  },
};

export const config = process.env.NODE_ENV === "development" ? dev : prod;
