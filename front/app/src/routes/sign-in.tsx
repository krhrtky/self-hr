import {
  AuthenticationProperty,
  useAuthentication,
} from "@/features/authentication";
import { SignIn } from "@/components/pages/signin";
import { createFileRoute, useSearch } from "@tanstack/react-router";
import { useMemo } from "react";

export const Route = createFileRoute("/sign-in")({
  component: () => {
    const { signIn } = useAuthentication();
    const { path } = useSearch({
      strict: false,
    });
    const handleSignIn = useMemo(
      () => (authenticationProperty: AuthenticationProperty) =>
        signIn(authenticationProperty, path),
      [signIn, path],
    );
    return (
      <div className="flex flex-row justify-around  min-h-screen">
        <div className="grid content-around w-1/2">
          <SignIn signIn={handleSignIn} />
        </div>
      </div>
    );
  },
});
