import { cleanup, render } from "@testing-library/react";
import { afterEach, describe, expect, it } from "vitest";
import { Header } from "./Header.tsx";

describe(Header.name, () => {
  afterEach(cleanup);
  it("first render", () => {
    const { container } = render(<Header />);
    expect(container.innerHTML).toMatchSnapshot();
  });
});
