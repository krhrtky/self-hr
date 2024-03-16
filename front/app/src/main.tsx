import React from "react";
import ReactDOM from "react-dom/client";
import Axios from "axios";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { env } from "./libs/env";
import { RouterProvider, Router } from "@tanstack/react-router";
import { routeTree } from "@/routeTree.gen";
import { Amplify } from "aws-amplify";
import { Authenticator } from "@aws-amplify/ui-react";
import "./global.css";

Axios.defaults.baseURL = env.BACKEND_BASE_URL ?? `${location.href}api`;
const root = document.getElementById("root");
const router = new Router({
  routeTree,
  defaultPreload: "intent",
});
Amplify.configure({
  Auth: {
    Cognito: {
      userPoolId: env.COGNITO_USER_POOL_ID,
      userPoolClientId: env.COGNITO_USER_POOL_CLIENT_ID,
      loginWith: {
        email: true,
      },
      userPoolEndpoint: env.AWS_BASE_URL,
      signUpVerificationMethod: "link",
    },
  },
});
const client = new QueryClient();

root &&
  ReactDOM.createRoot(root).render(
    <React.StrictMode>
      {/*<Authenticator hideSignUp={true} >*/}

      <Authenticator.Provider>
        <QueryClientProvider client={client}>
          <RouterProvider router={router} />
        </QueryClientProvider>
      </Authenticator.Provider>
      {/*</Authenticator>*/}
    </React.StrictMode>,
  );
