const prod = {
  url: {
    KEYCLOAK_BASE_URL: "https://erp3.soict.ai/iam",
    API_URL: "/api",
  },
};

const dev = {
  url: {
    KEYCLOAK_BASE_URL: "https://erp3.soict.ai/iam",
    API_URL: "http://103.146.166.130:30004/api",
  },
};

export const config = process.env.NODE_ENV === "development" ? dev : prod;
