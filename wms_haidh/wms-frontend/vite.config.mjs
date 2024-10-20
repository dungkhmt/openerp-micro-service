import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react-swc";
import svgr from "vite-plugin-svgr";

// https://vitejs.dev/config/
export default ({ mode }) => {
  process.env = { ...process.env, ...loadEnv(mode, process.cwd()) };

  return defineConfig({
    optimizeDeps: {
      include: ["@mui/material/Tooltip"],
    },
    plugins: [
      react(),
      svgr({
        svgrOptions: {
          icon: true,
        },
      }),
    ],
    server: {
      port: 3000,
    },
    envPrefix: "REACT_APP_",
  });
};
