import { Meta, StoryObj } from "@storybook/react";
import { SignIn as Component } from "./SignIn";

type ComponentType = typeof Component;

const meta: Meta<ComponentType> = {
  component: Component,
};

export default meta;

type Story = StoryObj<ComponentType>;

export const Basic: Story = {
  args: {
    signIn: async (authenticationProperty, _) =>
      alert(JSON.stringify(authenticationProperty)),
  },
};
