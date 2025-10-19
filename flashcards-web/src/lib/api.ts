import axios from "axios";

export const API_BASE_URL =
    import.meta.env.VITE_API_URL ?? "http://localhost:8080/api";

const api = axios.create({ baseURL: API_BASE_URL });

// añade Authorization si existe
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token"); // guardaremos "Bearer ey..."
    if (token) config.headers.Authorization = token;
    return config;
});

// ante 401 → limpiar token y reenviar a /login
api.interceptors.response.use(
    (res) => res,
    (err) => {
        if (err?.response?.status === 401) {
            localStorage.removeItem("token");
            if (window.location.pathname !== "/login") {
                window.location.href = "/login";
            }
        }
        return Promise.reject(err);
    }
);

export default api;
