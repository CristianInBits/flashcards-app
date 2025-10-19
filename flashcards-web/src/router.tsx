import { createBrowserRouter, Navigate } from "react-router-dom";
import AppLayout from "./layouts/AppLayout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";

export function ProtectedRoute({ children }: { children: JSX.Element }) {
    const token = localStorage.getItem("token");
    return token ? children : <Navigate to="/login" replace />;
}

export const router = createBrowserRouter([
    { path: "/login", element: <Login /> },
    {
        path: "/",
        element: (
            <ProtectedRoute>
                <AppLayout />
            </ProtectedRoute>
        ),
        children: [
            { index: true, element: <Dashboard /> },
            // más rutas protegidas más adelante
        ],
    },
]);
