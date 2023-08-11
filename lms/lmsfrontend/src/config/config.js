const prod = {
  url: {
    // KEYCLOAK_BASE_URL: "https://lms.dailyopt.ai/iam",
    KEYCLOAK_BASE_URL: "https://lms.dailyopt.ai/iam",
    API_URL: "/api",
  },
};

const dev = {
  url: {
    // KEYCLOAK_BASE_URL: "https://lms.dailyopt.ai/iam",
    KEYCLOAK_BASE_URL: "https://lms.dailyopt.ai/iam",
    API_URL: "http://localhost:8080/api",
  },
};

export const config = process.env.NODE_ENV === "development" ? dev : prod;
