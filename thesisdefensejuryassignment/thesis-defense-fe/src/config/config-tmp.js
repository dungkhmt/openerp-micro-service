// export const config.url.API_URL =
//   process.env.NODE_ENV === "production" ? "/api" : "http://localhost:8080/api";

const prod = {
    url: {
      KEYCLOAK_BASE_URL: "https://erp3.soict.ai/iam",
      config.url.API_URL: "/api",
    },
  };
  
  const dev = {
    url: {
      KEYCLOAK_BASE_URL: "https://erp3.soict.ai/iam",
      config.url.API_URL: "http://localhost:8080/api",
    },
  };
  
  export const config = process.env.NODE_ENV === "development" ? dev : prod;
  export const config.url.API_URL = process.env.NODE_ENV === "development" ? dev.url.config.url.API_URL : prod.url.config.url.API_URL;
  