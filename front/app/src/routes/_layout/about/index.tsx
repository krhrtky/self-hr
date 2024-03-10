import {createFileRoute, Link} from "@tanstack/react-router";

export const Route = createFileRoute("/_layout/about/")({
  component: () => {
    return <Link to="/">home</Link>;
  }
})
