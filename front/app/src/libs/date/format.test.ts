import { describe, expect, it } from "vitest";
import {
  formatCalenderDate,
  formatTime,
  formatTimeDifferent,
} from "@/libs/date/format.ts";

describe(formatCalenderDate, () => {
  it("should be expected format", () => {
    const result = formatCalenderDate(new Date(2024, 10, 1));
    expect(result).is.eq("Nov 01, 2024");
  });
});

describe(formatTime, () => {
  it("should be expected format.", () => {
    const result = formatTime(new Date(2024, 10, 1, 11, 10));
    expect(result).is.eq("11:10");
  });
  it("should be format 24 notation.", () => {
    const result = formatTime(new Date(2024, 10, 1, 22, 10));
    expect(result).is.eq("22:10");
  });
  it("should be 1 digit, pad it with 0.", () => {
    const result = formatTime(new Date(2024, 10, 1, 2, 1));
    expect(result).is.eq("02:01");
  });
});

describe(formatTimeDifferent, () => {
  it("should be expected format.", () => {
    const from = new Date(2024, 10, 1, 11, 10);
    const to = new Date(2024, 10, 1, 13, 25);

    const result = formatTimeDifferent({ from, to });
    expect(result).is.eq("2.25");
  });
  it("should be `--` when `to` is undefined.", () => {
    const from = new Date(2024, 10, 1, 11, 10);

    const result = formatTimeDifferent({ from, to: undefined });
    expect(result).is.eq("--");
  });
  it("should be 0 when different minutes is less than 15.", () => {
    const from = new Date(2024, 10, 1, 11, 10);
    const to = new Date(2024, 10, 1, 11, 11);

    const result = formatTimeDifferent({ from, to });
    expect(result).is.eq("0");
  });
});
