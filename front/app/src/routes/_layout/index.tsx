import { Suspense } from "react";
import { useDetail } from "@/libs/api";
import { Button } from "@/components/ui/button.tsx";
import {createFileRoute, Link} from "@tanstack/react-router";
import { useAuthentication, useSession } from "@/features/authentication";

export const Route = createFileRoute("/_layout/")({
  component: () => {
    const { signOut } = useAuthentication();
    return (
        <div>
          main content
          <div>
            <Link to="/about/">about</Link>
            <Link to="/attendance/record/">record</Link>
            <Button onClick={() => signOut()}>Logout</Button>
          </div>
          <Tmp />
        </div>
    );
  }
})

const Tmp = () => {
  const { token } = useSession();

  const query = useDetail({
    query: {
      enabled: !!token,
    },
    axios: {
      headers: {
        Authorization: token,
      },
    },
  });

  return (
    <Suspense fallback={<div>suspended</div>}>
      <div>{JSON.stringify(query.data?.data)}</div>
    </Suspense>
  );
};