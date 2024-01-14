import { Meta, StoryObj } from "@storybook/react";
import { Layout } from "./Layout";

const meta: Meta<typeof Layout> = {
  component: Layout,
};

export default meta;

type Story = StoryObj<typeof Layout>;

const Child = () => (
  <div className="min-h-screen overflow-y-auto">main content</div>
);
export const Basic: Story = {
  args: {
    children: <Child />,
  },
};
