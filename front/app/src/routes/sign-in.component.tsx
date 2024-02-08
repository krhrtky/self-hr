import { useAuthentication } from "@/features/authentication";
import { SignIn } from "@/components/pages/signin";

export const component = () => {
  const { signIn } = useAuthentication();
  return <SignIn signIn={signIn} />;
};
