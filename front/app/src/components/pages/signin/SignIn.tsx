import { useForm } from "react-hook-form";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form.tsx";
import { Input } from "@/components/ui/input.tsx";
import { valibotResolver } from "@hookform/resolvers/valibot";
import {
  AuthenticationProperty,
  AuthenticationSchema,
  SignInFunction,
} from "@/features/authentication";
import { Button } from "@/components/ui/button.tsx";
import { type FC } from "react";
import { useSearch } from "@tanstack/react-router";

interface SignInProps {
  signIn: SignInFunction;
}

export const SignIn: FC<SignInProps> = ({ signIn }) => {
  const { path } = useSearch({
    strict: false,
  });
  const form = useForm<AuthenticationProperty>({
    mode: "onSubmit",
    resolver: valibotResolver(AuthenticationSchema),
    values: {
      email: "",
      password: "",
    },
  });
  return (
    <div className="min-h-5 max-w-screen-sm">
      <Form {...form}>
        <form onSubmit={form.handleSubmit((values) => signIn(values, path))}>
          <FormField
            control={form.control}
            name="email"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Username</FormLabel>
                <FormControl>
                  <Input
                    type="email"
                    placeholder="username@example.com"
                    {...field}
                  />
                </FormControl>
                <FormDescription>input email address.</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="password"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Password</FormLabel>
                <FormControl>
                  <Input type="password" placeholder="*****" {...field} />
                </FormControl>
                <FormDescription>input your password.</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button type="submit">submit</Button>
        </form>
      </Form>
    </div>
  );
};
