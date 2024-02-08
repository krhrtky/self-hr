import { Outlet, redirect, RootRoute } from "@tanstack/react-router";
import { TanStackRouterDevtools } from "@tanstack/router-devtools";
import { getCurrentUser } from "aws-amplify/auth";

export const Route = new RootRoute({
  component: () => (
    <>
      <Outlet />
      <TanStackRouterDevtools initialIsOpen={false} />
    </>
  ),
  beforeLoad: async ({ location }) => {
    const currentPath = location.pathname;
    if (pathsWithoutAuthentication.some((path) => path === currentPath)) {
      return;
    }
    try {
      const user = await getCurrentUser();
      if (user.signInDetails?.loginId !== undefined) {
        return;
      }
    } catch (e) {}
    throw redirect({
      to: "/sign-in",
      search: {
        path: location.pathname,
      },
    });
  },
});

const pathsWithoutAuthentication = ["/", "/sign-in"];
