import { describe, it, expect, afterEach } from "vitest";
import { render, cleanup } from "@testing-library/react";
import { component as Component } from "./index.component.tsx";

describe(Component.name, () => {
  afterEach(cleanup);
  it("first render", async () => {
    const { container } = render(<Component />);
    expect(container.innerHTML).toMatchSnapshot();
  });
});
