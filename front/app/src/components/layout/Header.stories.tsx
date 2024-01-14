import { Header } from "./Header";
import { Meta, StoryObj } from "@storybook/react";

const meta: Meta<typeof Header> = {
  component: Header,
};
export default meta;

type Story = StoryObj<typeof Header>;

export const Basic: Story = {};
