import {createFileRoute, Outlet} from "@tanstack/react-router";
import { Layout } from "@/components/layout";

export const Route = createFileRoute("/_layout")({
    component: () => (
        <Layout>
            <Outlet />
        </Layout>
    )
})
