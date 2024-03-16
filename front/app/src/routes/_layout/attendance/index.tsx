import { createFileRoute, Link } from "@tanstack/react-router";
import { Button } from "@/components/ui/button.tsx";

export const Route = createFileRoute("/_layout/attendance/")({
  component: () => (
    <div className="flex justify-center min-h-screen w-1/2">
      <div className="min-w-full">
        <h1 className="font-bold mb-5">Recording menu</h1>
        <div className="grid gap-y-3 justify-items-stretch">
          <Link to="/attendance/list/">
            <Button type="button" size="lg" className="min-w-full">
              attendance list
            </Button>
          </Link>
          <Link to="/attendance/record/">
            <Button type="button" size="lg" className="min-w-full">
              record attendance
            </Button>
          </Link>
        </div>
      </div>
    </div>
  ),
});
