import { useDetail1 } from "@/libs/api";
import { useSession } from "@/features/authentication";
import { format, lastDayOfMonth, startOfMonth } from "date-fns";
import { useState } from "react";
import { DateRange } from "react-day-picker";
import { List } from "@/routes/_layout/attendance/-components/List.tsx";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_layout/attendance/list")({
  component: () => {
    const current = new Date();
    const startOfMonthDate = startOfMonth(current);
    const lastDayOfMonthDate = lastDayOfMonth(current);

    const { token } = useSession();
    const [date, setDate] = useState<DateRange | undefined>({
      from: startOfMonthDate,
      to: lastDayOfMonthDate,
    });

    const result = useDetail1(
      {
        from: format(date?.from ?? startOfMonthDate, "yyyy-MM-dd"),
        to: format(date?.to ?? lastDayOfMonthDate, "yyyy-MM-dd"),
      },
      {
        query: {
          enabled: !!token && !!date?.from && !!date?.to,
          queryKey: ["attendanceListKey", date],
        },
        axios: {
          headers: {
            Authorization: token,
          },
        },
      },
    );

    return (
      <div className="min-h-screen w-3/4 flex flex-col">
        <h1 className="font-bold mb-5">Recording list</h1>
        <List
          onSelectDatePicker={setDate}
          range={{
            from: date?.from ?? startOfMonthDate,
            to: date?.to ?? lastDayOfMonthDate,
          }}
          attendance={
            result.isLoading
              ? {
                  isLoading: true,
                }
              : {
                  isLoading: false,
                  date: result.data?.data?.data ?? [],
                }
          }
        />
      </div>
    );
  },
});
