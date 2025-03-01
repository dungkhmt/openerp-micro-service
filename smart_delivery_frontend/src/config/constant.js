const prod = {
  url: {
    KEYCLOAK_BASE_URL: "http://localhost:8180/",
    API_URL: "/api",
  },
};

const dev = {
  url: {
    KEYCLOAK_BASE_URL: "http://localhost:8180",
    API_URL: "http://localhost:8080/api",
  },
};

export const config = process.env.NODE_ENV === "development" ? dev : prod;
