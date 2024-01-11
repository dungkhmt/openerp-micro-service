const prod = {
  url: {
    KEYCLOAK_BASE_URL: "https://erp3.soict.ai
    API_URL: "/api",
  },
};

const dev = {
  url: {
    KEYCLOAK_BASE_URL: "https://erp3.soict.ai
    API_URL: "http://localhost/api",
  },
};

export const config = process.env.NODE_ENV === "development" ? dev : prod;
