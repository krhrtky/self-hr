import { Meta, StoryObj } from "@storybook/react";
import { Record as Component } from "./Record.tsx";

type ComponentType = typeof Component;

const meta: Meta<ComponentType> = {
  component: Component,
};
export default meta;

type Story = StoryObj<typeof Component>;

export const Basic: Story = {
  args: {
    defaultDate: new Date(2024, 10, 1, 13, 25),
  },
};
export const Loading: Story = {
  args: {
    defaultDate: new Date(2024, 10, 1, 13, 25),
    inSubmitting: true,
  },
};
