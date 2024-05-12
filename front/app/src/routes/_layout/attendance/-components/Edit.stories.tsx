import { Meta, StoryObj } from "@storybook/react";
import { Edit as Component } from "./Edit";

type ComponentType = typeof Component;

const meta: Meta<ComponentType> = {
  component: Component,
};

export default meta;

type Story = StoryObj<ComponentType>;

export const Basic: Story = {
  args: {
    time: "13:35",
    id: "123",
    handleEdit: async (props) => console.log(props),
    inSubmitting: false,
  },
};

export const DialogOpened: Story = {
  args: {
    open: true,
    time: "13:35",
    id: "123",
    handleEdit:async (props) => console.log(props),
    inSubmitting: false,
  },
};
