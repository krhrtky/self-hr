import { email, minLength, object, Output, regex, string } from "valibot";
import {
  fetchAuthSession,
  signIn as signInImpl,
  signOut as singOutImpl,
} from "aws-amplify/auth";
import { useNavigate } from "@tanstack/react-router";
import { useAuthenticator } from "@aws-amplify/ui-react";
import { useEffect, useState } from "react";

export const AuthenticationSchema = object({
  email: string([email("Input email format.")]),
  password: string([
    minLength(1, "Input any text."),
    regex(/[a-z]/, "Your password must contain a lowercase letter."),
    regex(/[A-Z]/, "Your password must contain a uppercase letter."),
    regex(/[0-9]/, "Your password must contain a number."),
  ]),
});

export type AuthenticationProperty = Output<typeof AuthenticationSchema>;

export type SignInFunction = (
  authenticationProperty: AuthenticationProperty,
  redirectTo?: string,
) => Promise<void>;

export const useAuthentication = () => {
  const navigate = useNavigate();
  const signIn: SignInFunction = async (
    authenticationProperty: AuthenticationProperty,
    redirectTo?: string,
  ) => {
    try {
      await signInImpl({
        username: authenticationProperty.email,
        password: authenticationProperty.password,
        options: {
          authFlowType: "USER_PASSWORD_AUTH",
        },
      });
    } catch (e) {
      alert(e);
      return;
    }
    await navigate({ to: redirectTo ?? "/" });
  };

  const signOut = () => singOutImpl();

  return {
    signIn,
    signOut,
  };
};

export const useSession = () => {
  const { user } = useAuthenticator();
  const [token, setToken] = useState("");

  // biome-ignore lint/correctness/useExhaustiveDependencies: <explanation>
  useEffect(() => {
    fetchAuthSession()
      .then((session) => session?.tokens?.accessToken?.toString ?? "")
      .then((tokenValue) => setToken(tokenValue));
  }, [user?.signInDetails?.loginId]);

  return {
    token,
  };
};
