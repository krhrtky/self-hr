import { createFileRoute, Link } from "@tanstack/react-router";

export const Route = createFileRoute("/_layout/")({
  component: () => {
    return (
      <div className="flex justify-center min-h-screen w-1/2">
        <Link preload={false} to="/attendance/record/">
          record
        </Link>
      </div>
    );
  },
});
