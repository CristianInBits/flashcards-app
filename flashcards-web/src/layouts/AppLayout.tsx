import { Outlet, Link, useNavigate } from "react-router-dom";

export default function AppLayout() {
    const navigate = useNavigate();
    const onLogout = () => {
        localStorage.removeItem("token");
        navigate("/login", { replace: true });
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="border-b bg-white">
                <div className="mx-auto max-w-5xl px-4 h-14 flex items-center justify-between">
                    <div className="flex items-center gap-4">
                        <Link to="/" className="font-semibold">Flashcards</Link>
                        <Link to="/decks" className="text-sm text-gray-700">Decks</Link>
                        <Link to="/study" className="text-sm text-gray-700">Study</Link>
                    </div>
                    <button onClick={onLogout} className="text-sm text-red-600">Logout</button>
                </div>
            </nav>
            <main className="mx-auto max-w-5xl p-4">
                <Outlet />
            </main>
        </div>
    );
}
