import { Record } from "@/components/pages/attendance";
import { useSession } from "@/features/authentication";
import { useRecord } from "@/libs/api";
import { useMemo } from "react";
import { useRouter } from "@tanstack/react-router";

export function component() {
  const { token } = useSession();
  const { navigate } = useRouter();
  const result = useRecord({
    axios: {
      headers: {
        Authorization: token,
      },
    },
  });
  const handleRecord = useMemo(
    () =>
      async ({ date, time }: { date: string; time: string }) => {
        const dateTime = new Date(`${date} ${time}`);
        result.mutate(
          {
            data: {
              recordTime: dateTime.toISOString(),
            },
          },
          {
            onSuccess: () =>
              navigate({
                to: "/attendance/",
              }),
          },
        );
      },
    [result],
  );
  return (
    <div className="flex justify-center min-h-screen w-1/2">
      <Record handleRecord={handleRecord} />
    </div>
  );
}
