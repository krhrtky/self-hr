import { Meta } from "@storybook/react";
import { Record as Component } from "./Record.tsx";

type ComponentType = typeof Component;

const meta: Meta<ComponentType> = {
  component: Component,
};
export default meta;
