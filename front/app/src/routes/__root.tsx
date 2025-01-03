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
    if (currentPath === "/") {
      return;
    }
    const isSignedIn = await isSignIn();

    if (isSignedIn) {
      if (signInPathPattern.some((path) => path === currentPath)) {
        throw redirect({
          to: "/",
        });
      }
      return;
    }

    if (signInPathPattern.some((path) => path === currentPath)) {
      return;
    }

    throw redirect({
      to: "/sign-in",
      search: {
        path: currentPath,
      },
    });
  },
});

const isSignIn: () => Promise<boolean> = async () => {
  try {
    await getCurrentUser();
    return true;
  } catch (e) {
    return false;
  }
};

const signInPathPattern = ["/sign-in", "/sign-in/"];
