const {
    VITE_BACKEND_BASE_URL,
}
= import.meta.env;

export const env =  {
    BACKEND_BASE_URL: VITE_BACKEND_BASE_URL ?? `${document.location.origin}/api` as string,
};

