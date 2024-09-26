const prod = {
  url: {
    KEYCLOAK_BASE_URL: "https://programming.daotao.ai/iam",
    API_URL: "/api",
  },
};

const dev = {
  url: {
    KEYCLOAK_BASE_URL: "https://erp3.soict.ai/iam",
    //KEYCLOAK_BASE_URL: "https://programming.daotao.ai/iam",
    API_URL: "http://localhost:8080/api",
  },
};

export const config = process.env.NODE_ENV === "development" ? dev : prod;

export const PLATFORM_NAME = "HUSTack";
