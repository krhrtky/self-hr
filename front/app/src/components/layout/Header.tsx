import { Button } from "@/components/ui/button";
import { clsx } from "clsx";
import { Link } from "@tanstack/react-router";

export function Header({ className }: { className?: string | undefined }) {
  return (
    <header
      className={clsx(
        [
          "flex",
          "items-center",
          "justify-between",
          "px-4",
          "py-2",
          "bg-neutral-100",
          "dark:bg-gray-800",
        ],
        className,
      )}
    >
      <Link to="/">
        <p className="flex items-center gap-2">
          <HomeIcon />
          <span className="text-lg font-semibold text-gray-800 dark:text-gray-200">
            Self-HR
          </span>
        </p>
      </Link>
      <nav className="flex items-center gap-4">
        <p className="text-sm font-medium text-gray-800 hover:underline dark:text-gray-200">
          <Link to="/attendance/">Attendance</Link>
        </p>
        <Button className="bg-blue-500 hover:bg-blue-600 text-white py-2 px-4 rounded-md">
          Get Started
        </Button>
      </nav>
    </header>
  );
}

function HomeIcon() {
  return (
    <svg
      className="w-6 h-6 text-gray-800 dark:text-gray-200"
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <title>home icon</title>
      <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
      <polyline points="9 22 9 12 15 12 15 22" />
    </svg>
  );
}
