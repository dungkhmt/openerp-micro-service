import ReactDOM from "react-dom/client";
import App from "./App.jsx";
import "./index.css";
import * as serviceWorker from "./serviceWorker";

ReactDOM.createRoot(document.getElementById("root")).render(
  <>
    <App />
  </>
);

serviceWorker.unregister();
