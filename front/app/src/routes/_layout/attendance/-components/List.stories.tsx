import type { Meta, StoryObj } from "@storybook/react";
import { List as Component } from "./List";

type ComponentType = typeof Component;

const meta: Meta<ComponentType> = {
  component: Component,
};

export default meta;

type Story = StoryObj<ComponentType>;

export const Loading: Story = {
  args: {
    attendance: {
      isLoading: true,
    },
    range: {
      from: new Date(Date.parse("2024-02-01T00:00:00+09:00")),
      to: new Date(Date.parse("2024-02-29T00:00:00+09:00")),
    },
    edit: async (value) => console.log(value),
  },
};

export const DatePickerOpen: Story = {
  args: {
    isOpenDatePicker: true,
    attendance: {
      isLoading: true,
    },
    range: {
      from: new Date(Date.parse("2024-02-01T00:00:00+09:00")),
      to: new Date(Date.parse("2024-02-29T00:00:00+09:00")),
    },
    edit: async (value) => console.log(value),
  },
};

export const Result: Story = {
  args: {
    attendance: {
      isLoading: false,
      date: [
        {
          attendanceDate: "2024-02-07",
          startAt: {
            id: "xxx",
            time: "2024-02-07T13:35:10+09:00",
          },
          endAt: {
            id: "xxx",
            time: "2024-02-07T23:31:47+09:00",
          },
        },
        {
          attendanceDate: "2024-02-19",
          startAt: {
            id: "xxx",
            time: "2024-02-19T13:14:49+09:00",
          },
          endAt: {
            id: "xxx",
            time: "2024-02-19T14:52:21+09:00"
          },
        },
      ],
    },
    range: {
      from: new Date(Date.parse("2024-02-01T00:00:00+09:00")),
      to: new Date(Date.parse("2024-02-29T00:00:00+09:00")),
    },
    edit: async (value) => console.log(value),
  },
};
