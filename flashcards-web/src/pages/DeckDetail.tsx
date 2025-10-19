import { useParams } from "react-router-dom";
import { useState, FormEvent } from "react";
import { useCards, useCreateCard, useDeleteCard } from "../hooks/useCards";
import { useDebounce } from "../hooks/useDebounce";
import { toast } from "sonner";

export default function DeckDetail() {
    const { deckId = "" } = useParams();

    const [q, setQ] = useState("");
    const [tag, setTag] = useState("");

    const qDeb = useDebounce(q, 300);
    const tagDeb = useDebounce(tag, 300);

    const { data, isLoading, isFetching, isError } = useCards(deckId, 0, 50, qDeb || undefined, tagDeb || undefined);
    const createCard = useCreateCard(deckId);
    const delCard = useDeleteCard(deckId);

    const [front, setFront] = useState("");
    const [back, setBack] = useState("");
    const [tags, setTags] = useState("");

    const onCreate = async (e: FormEvent) => {
        e.preventDefault();
        if (!front.trim() || !back.trim()) return toast.error("Front y Back son obligatorios");
        try {
            await createCard.mutateAsync({ front, back, tags: tags || undefined });
            setFront(""); setBack(""); setTags("");
            toast.success("Card creada");
        } catch {
            toast.error("No se pudo crear la card");
        }
    };

    if (isError) return <div>Error cargando cards</div>;

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between">
                <h1 className="text-lg font-semibold">Deck Detail</h1>
                {isFetching && <span className="text-xs text-gray-500">Buscando…</span>}
            </div>

            {/* Buscador */}
            <div className="flex gap-2">
                <input
                    className="border rounded px-3 py-2"
                    placeholder="Buscar texto"
                    value={q}
                    onChange={(e) => setQ(e.target.value)}
                />
                <input
                    className="border rounded px-3 py-2"
                    placeholder="Filtrar por tag"
                    value={tag}
                    onChange={(e) => setTag(e.target.value)}
                />
            </div>

            {/* Crear card */}
            <form onSubmit={onCreate} className="grid md:grid-cols-3 gap-2">
                <input className="border rounded px-3 py-2" placeholder="Front" value={front} onChange={(e) => setFront(e.target.value)} />
                <input className="border rounded px-3 py-2" placeholder="Back" value={back} onChange={(e) => setBack(e.target.value)} />
                <div className="flex gap-2">
                    <input className="border rounded px-3 py-2 flex-1" placeholder="tags (opcional)" value={tags} onChange={(e) => setTags(e.target.value)} />
                    <button className="bg-black text-white px-4 rounded">Crear</button>
                </div>
            </form>

            {/* Lista de cards */}
            <div className="bg-white rounded-xl border">
                {isLoading && !data ? (
                    <div className="p-3 text-sm text-gray-500">Cargando…</div>
                ) : (
                    <ul className="divide-y">
                        {data?.content?.map((c) => (
                            <li key={c.id} className="p-3 flex items-center justify-between">
                                <div className="min-w-0">
                                    <div className="font-medium truncate">{c.front}</div>
                                    <div className="text-sm text-gray-600 truncate">{c.back}</div>
                                    {c.tags && <div className="text-xs text-gray-500">#{c.tags}</div>}
                                </div>
                                <button
                                    onClick={async () => {
                                        try { await delCard.mutateAsync(c.id); toast.success("Card eliminada"); }
                                        catch { toast.error("No se pudo eliminar"); }
                                    }}
                                    className="text-sm text-red-600"
                                >
                                    Eliminar
                                </button>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}