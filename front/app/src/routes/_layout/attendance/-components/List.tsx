import { addBusinessDays, differenceInBusinessDays } from "date-fns";
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button.tsx";
import { cn } from "@/libs/shared.ts";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover.tsx";
import { CalendarIcon } from "lucide-react";
import { Calendar } from "@/components/ui/calendar.tsx";
import { useState } from "react";
import {
  formatCalenderDate,
  formatTime,
  formatTimeDifferent,
  formatyyyMMdd,
} from "@/libs/date";

interface ListProps {
  isOpenDatePicker?: boolean;
  onSelectDatePicker: (dateRange: DateRange) => void;
  range: {
    from: Date;
    to: Date;
  };
  attendance: AttendanceList;
}

type DateRange =
  | {
      from: Date | undefined;
      to?: Date | undefined;
    }
  | undefined;

interface AttendanceItem {
  attendanceDate: string;
  startAt: string;
  endAt?: string;
}

type AttendanceList =
  | {
      isLoading: true;
    }
  | {
      isLoading: false;
      date: Array<AttendanceItem>;
    };

const createDateList = ({ from, to }: { from: Date; to: Date }) => {
  const diff = differenceInBusinessDays(to, from);
  return [...Array(diff + 2).keys()].map((i) =>
    formatyyyMMdd(addBusinessDays(from, i)),
  );
};

export function List({
  isOpenDatePicker = false,
  onSelectDatePicker = (_dateRange) => {},
  range,
  attendance,
}: ListProps) {
  const [isOpen, setIsOpen] = useState(isOpenDatePicker);

  const dateList = createDateList(range);

  return (
    <>
      <div className="grid gap-2 mb-5">
        <Popover open={isOpen}>
          <PopoverTrigger asChild>
            <Button
              id="date"
              variant={"outline"}
              className={cn(
                "w-[300px] justify-start text-left font-normal",
                !range && "text-muted-foreground",
              )}
              onClick={() => setIsOpen(true)}
            >
              <CalendarIcon className="mr-2 h-4 w-4" />
              {range?.from ? (
                range.to ? (
                  <>
                    {formatCalenderDate(range.from)} -{" "}
                    {formatCalenderDate(range.to)}
                  </>
                ) : (
                  formatCalenderDate(range.from)
                )
              ) : (
                <span>Pick a date</span>
              )}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="start">
            <Calendar
              initialFocus
              mode="range"
              defaultMonth={range?.from}
              selected={range}
              onSelect={(value) => {
                onSelectDatePicker(value);
                if (value?.to) {
                  setIsOpen(() => false);
                }
              }}
              numberOfMonths={2}
            />
          </PopoverContent>
        </Popover>
      </div>

      <Table>
        <TableCaption>Attendance</TableCaption>
        <TableHeader className="sticky top-0 bg-gray-100">
          <TableRow>
            <TableHead className="text-center">Date</TableHead>
            <TableHead className="text-center">Start At</TableHead>
            <TableHead className="text-center">End At</TableHead>
            <TableHead className="text-center">Working Hours</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {attendance.isLoading ? (
            <TableRow>
              <TableCell className="font-medium">
                <Skeleton className="h-4 min-w-full" />
              </TableCell>
              <TableCell>
                <Skeleton className="h-4 min-w-full" />
              </TableCell>
              <TableCell>
                <Skeleton className="h-4 min-w-full" />
              </TableCell>
              <TableCell>
                <Skeleton className="h-4 min-w-full" />
              </TableCell>
            </TableRow>
          ) : (
            dateList.map((date) => {
              const item = attendance.date.find(
                (item) => item.attendanceDate === date,
              );
              if (item !== undefined) {
                const dateRange = {
                  from: new Date(item.startAt),
                  to: item.endAt ? new Date(item.endAt) : undefined,
                };

                return (
                  <TableRow key={date}>
                    <TableCell className="font-medium text-center">
                      {item.attendanceDate}
                    </TableCell>
                    <TableCell className="text-center">
                      {formatTime(dateRange.from)}
                    </TableCell>
                    <TableCell className="text-center">
                      {dateRange.to ? formatTime(dateRange.to) : "-"}
                    </TableCell>
                    <TableCell className="text-center">
                      {formatTimeDifferent(dateRange)}
                    </TableCell>
                  </TableRow>
                );
              } else {
                return (
                  <TableRow key={date}>
                    <TableCell className="font-medium text-center">
                      {date}
                    </TableCell>
                    <TableCell className="text-center">--</TableCell>
                    <TableCell className="text-center">--</TableCell>
                    <TableCell className="text-center">--</TableCell>
                  </TableRow>
                );
              }
            })
          )}
        </TableBody>
      </Table>
    </>
  );
}
