/// <reference types="vitest" />
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { resolve } from "path";
import {TanStackRouterVite} from "@tanstack/router-vite-plugin";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    TanStackRouterVite(),
  ],
  resolve: {
    alias: [
      {
        find: "@",
        replacement: resolve(__dirname, "./src"),
      },
      {
        find: "./runtimeConfig",
        replacement: "./runtimeConfig.browser", // ensures browser compatible version of AWS JS SDK is used
      },
    ],
  },
  server: {
    hmr: true,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
    },
  },
  test: {
    setupFiles: ["./src/test/setup.ts"],
    globals: true,
    environment: "happy-dom",
  },
});
