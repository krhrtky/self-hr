import { Outlet } from "@tanstack/react-router";
import { Layout } from "@/components/layout";

export const component = () => (
  <Layout>
    <Outlet />
  </Layout>
);
