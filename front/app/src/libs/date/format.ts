import { differenceInMinutes, format, minutesToHours } from "date-fns";

/**
 * カレンダー表示向けの日付フォーマット.
 * @example
 * formatCalenderDate(new Date(2024, 10, 1)) // Nov 01, 2024
 */
export const formatCalenderDate: (date: Date) => string = (date) =>
  format(date, "LLL dd, y");

export const formatTime: (date: Date) => string = (date) =>
  format(date, "HH:mm");

export const formatTimeDifferent: ({
  from,
  to,
}: { from: Date; to?: Date }) => string = ({ from, to }) => {
  const diffTotalMinutes = to ? differenceInMinutes(to, from) : null;
  const diffHour = diffTotalMinutes ? minutesToHours(diffTotalMinutes) : 0;
  const diffMinutes = (diffTotalMinutes ? Math.ceil(diffHour / 15) : 0) * 0.25;
  const diff = diffHour + diffMinutes;

  return to === undefined ? "--" : diff.toString();
};

export const formatyyyMMdd: (date: Date) => string = (date) =>
  format(date, "yyyy-MM-dd");
