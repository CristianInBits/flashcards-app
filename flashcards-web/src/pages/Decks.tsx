import { FormEvent, useState } from "react";
import { useCreateDeck, useDecks, useDeleteDeck } from "../hooks/useDecks";
import { toast } from "sonner";
import { Link } from "react-router-dom";

export default function Decks() {
    const { data, isLoading, isError } = useDecks(0, 50);
    const createDeck = useCreateDeck();
    const deleteDeck = useDeleteDeck();

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");

    const onCreate = async (e: FormEvent) => {
        e.preventDefault();
        if (!name.trim()) return toast.error("El nombre es obligatorio");
        try {
            await createDeck.mutateAsync({ name, description: description || undefined });
            setName(""); setDescription("");
            toast.success("Deck creado");
        } catch {
            toast.error("No se pudo crear el deck");
        }
    };

    if (isLoading) return <div>Cargando decks…</div>;
    if (isError) return <div>Error cargando decks</div>;

    return (
        <div className="space-y-4">
            <h1 className="text-lg font-semibold">Decks</h1>

            <form onSubmit={onCreate} className="flex gap-2">
                <input
                    className="border rounded px-3 py-2 flex-1"
                    placeholder="Nombre del deck"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
                <input
                    className="border rounded px-3 py-2 flex-1"
                    placeholder="Descripción (opcional)"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
                <button className="bg-black text-white px-4 rounded">
                    Crear
                </button>
            </form>

            <ul className="divide-y bg-white rounded-xl border">
                {data?.content?.map((d) => (
                    <li key={d.id} className="p-3 flex items-center justify-between">
                        <div>
                            <Link to={`/decks/${d.id}`} className="font-medium hover:underline">
                                {d.name}
                            </Link>
                            {d.description && (
                                <div className="text-sm text-gray-500">{d.description}</div>
                            )}
                        </div>
                        <button
                            onClick={async () => {
                                try { await deleteDeck.mutateAsync(d.id); toast.success("Deck eliminado"); }
                                catch { toast.error("No se pudo eliminar"); }
                            }}
                            className="text-sm text-red-600"
                        >
                            Eliminar
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );
}
