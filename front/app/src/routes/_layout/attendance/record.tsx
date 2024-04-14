import { Record } from "@/components/pages/attendance";
import { useSession } from "@/features/authentication";
import { useRecord } from "@/libs/api";
import { useMemo, useState } from "react";
import { createFileRoute, useRouter } from "@tanstack/react-router";

export const Route = createFileRoute("/_layout/attendance/record")({
  component: () => {
    const { token } = useSession();
    const { navigate } = useRouter();
    const result = useRecord({
      axios: {
        headers: {
          Authorization: token,
        },
      },
    });
    const [isLoading, setIsLoading] = useState(false);
    const handleRecord = useMemo(
      () =>
        async ({ date, time }: { date: string; time: string }) => {
          setIsLoading(true);
          const dateTime = new Date(`${date} ${time}`);
          result.mutate(
            {
              data: {
                recordTime: dateTime.toISOString(),
              },
            },
            {
              onSuccess: () => {
                navigate({
                  to: "/attendance/",
                });
              },
              onError: (error) => {
                setIsLoading(false);
                alert(error.message);
              },
            },
          );
        },
      [result, navigate],
    );
    return (
      <div className="flex justify-center min-h-screen w-1/2">
        <Record handleRecord={handleRecord} inSubmitting={isLoading} />
      </div>
    );
  },
});
