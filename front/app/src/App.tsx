import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Suspense } from "react";
import { useList } from "./libs/api";
import { Layout } from "@/components/layout";
import "./global.css";

const client = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={client}>
      <Layout>
        <div>
          main content
          <Component />
        </div>
      </Layout>
    </QueryClientProvider>
  );
}

const Component = () => {
  const query = useList();

  return (
    <Suspense fallback={<div>suspended</div>}>
      <div>{JSON.stringify(query.data?.data)}</div>
    </Suspense>
  );
};

export default App;
