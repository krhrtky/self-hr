import { Header } from "@/components/layout/Header.tsx";
import React from "react";

export const Layout = ({ children }: { children: React.ReactNode }) => (
  <div className="flex flex-col min-h-screen max-h-screen max-w-screen min-w-full overflow-y-hidden bg-neutral-50 dark:bg-gray-600">
    <Header className="flex items-center justify-between p-6" />
    <main className="flex justify-center min-h-full min-w-full p-3">
      {children}
    </main>
  </div>
);
