import { FormEvent, useState } from "react";
import { useLogin, useRegister } from "../hooks/useAuth";
import { toast } from "sonner";

export default function Login() {
    const [email, setEmail] = useState("demo@local");
    const [password, setPassword] = useState("Demo12345!");
    const login = useLogin();
    const register = useRegister();

    const onSubmit = async (e: FormEvent) => {
        e.preventDefault();
        try {
            const res = await login.mutateAsync({ email, password });
            // Guardamos "Bearer <token>" usando tokenType del backend
            const bearer = `${res.tokenType ?? "Bearer"} ${res.token}`;
            localStorage.setItem("token", bearer);

            window.location.href = "/"; // entra al layout protegido
        } catch (err: any) {
            const msg =
                err?.response?.data?.message ||
                err?.response?.data?.error ||
                err?.message ||
                "No se pudo iniciar sesión";
            console.error("LOGIN ERROR:", err?.response || err);
            toast.error(String(msg));
        }
    };

    const quickRegister = async () => {
        try {
            await register.mutateAsync({ email, password });
            toast.success("Usuario registrado. Inicia sesión.");
        } catch (err: any) {
            const msg =
                err?.response?.data?.message ||
                err?.response?.data?.error ||
                err?.message ||
                "No se pudo registrar";
            console.error("REGISTER ERROR:", err?.response || err);
            toast.error(String(msg));
        }
    };

    return (
        <div className="min-h-screen grid place-items-center bg-zinc-900">
            <form onSubmit={onSubmit} className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-sm space-y-4">
                <h1 className="text-2xl font-bold">Iniciar sesión</h1>

                <input
                    className="w-full border rounded px-3 py-2"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    className="w-full border rounded px-3 py-2"
                    placeholder="Password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <button
                    type="submit"
                    className="w-full bg-black text-white rounded py-2 disabled:opacity-50"
                    disabled={login.isPending}
                >
                    Entrar
                </button>

                <button
                    type="button"
                    onClick={quickRegister}
                    className="w-full border rounded py-2 disabled:opacity-50"
                    disabled={register.isPending}
                >
                    Registrar rápido
                </button>
            </form>
        </div>
    );
}
