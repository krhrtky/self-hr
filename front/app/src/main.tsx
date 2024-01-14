import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import Axios from "axios";
import { env } from "./libs/env";

Axios.defaults.baseURL = env.BACKEND_BASE_URL ?? `${location.href}api`;
const root = document.getElementById("root");

root &&
  ReactDOM.createRoot(root).render(
    <React.StrictMode>
      {" "}
      <App />{" "}
    </React.StrictMode>,
  );
