import { useEffect, useMemo, useState } from "react";
import { useNextReview, useAnswerReview, useSkipReview } from "../hooks/useReviews";
import { useDecks } from "../hooks/useDecks";
import { toast } from "sonner";
import Flashcard from "../components/Flashcard";

export default function Study() {
    // Layout: fondo con gradiente
    // (no requiere cambios reales de código, solo clases en el contenedor principal)

    // Selector de deck
    const [deckId, setDeckId] = useState<string>("");
    const { data: decks } = useDecks(0, 100);

    // Opciones
    const [shuffle, setShuffle] = useState(true);
    const [skipMin, setSkipMin] = useState(10);

    // Estado tarjeta
    const { data: card, isFetching, refetch, isError } = useNextReview(deckId || undefined, shuffle);
    const answer = useAnswerReview();
    const skip = useSkipReview(skipMin);

    const [showBack, setShowBack] = useState(false);

    // Al cambiar de deck o shuffle, resetea y pide next
    useEffect(() => {
        setShowBack(false);
        if (deckId) refetch();
    }, [deckId, shuffle, refetch]);

    const onAnswer = async (grade: number) => {
        if (!card) return;
        try {
            await answer.mutateAsync({ cardId: card.cardId, grade });
            setShowBack(false);
            await refetch();
        } catch {
            toast.error("No se pudo registrar la respuesta");
        }
    };

    const onSkip = async () => {
        if (!card) return;
        try {
            await skip.mutateAsync({ cardId: card.cardId, minutes: skipMin });
            setShowBack(false);
            await refetch();
        } catch {
            toast.error("No se pudo posponer la tarjeta");
        }
    };

    // Atajos de teclado: Space → flip; 0..5 → answer
    useEffect(() => {
        const onKey = (e: KeyboardEvent) => {
            if (!deckId || !card) return;
            if (e.code === "Space") {
                e.preventDefault();
                setShowBack((s) => !s);
            } else if (/^[0-5]$/.test(e.key)) {
                e.preventDefault();
                onAnswer(Number(e.key));
            }
        };
        window.addEventListener("keydown", onKey);
        return () => window.removeEventListener("keydown", onKey);
    }, [deckId, card]); // eslint-disable-line

    const disabled = useMemo(
        () => isFetching || answer.isPending || skip.isPending,
        [isFetching, answer.isPending, skip.isPending]
    );

    return (
        <div className="min-h-[calc(100vh-56px)] bg-gradient-to-br from-emerald-50 via-white to-sky-50">
            <div className="max-w-3xl mx-auto p-4 md:p-6 space-y-4">
                {/* Panel superior: selector + opciones */}
                <div className="rounded-2xl border bg-white/70 backdrop-blur-sm p-4 md:p-5 shadow">
                    <div className="grid md:grid-cols-3 gap-3 items-end">
                        {/* Deck */}
                        <div>
                            <label className="text-xs uppercase tracking-wide text-gray-500">Deck</label>
                            <select
                                className="mt-1 w-full border rounded-xl px-3 py-2 bg-white"
                                value={deckId}
                                onChange={(e) => setDeckId(e.target.value)}
                            >
                                <option value="">— elige —</option>
                                {decks?.content?.map((d) => (
                                    <option key={d.id} value={d.id}>{d.name}</option>
                                ))}
                            </select>
                        </div>

                        {/* Shuffle */}
                        <div className="flex items-center gap-2">
                            <input
                                id="shuffle"
                                type="checkbox"
                                className="size-4"
                                checked={shuffle}
                                onChange={(e) => setShuffle(e.target.checked)}
                            />
                            <label htmlFor="shuffle" className="text-sm">Shuffle</label>
                        </div>

                        {/* Skip mins + Next */}
                        <div className="flex items-center gap-3">
                            <label className="flex items-center gap-2 text-sm">
                                Skip (min):
                                <input
                                    type="number"
                                    min={1}
                                    className="w-20 border rounded-xl px-2 py-1"
                                    value={skipMin}
                                    onChange={(e) => setSkipMin(Math.max(1, Number(e.target.value) || 1))}
                                />
                            </label>
                            <button
                                className="ml-auto px-3 py-2 rounded-xl border bg-white hover:bg-gray-50 active:scale-95 transition text-sm"
                                onClick={() => refetch()}
                                disabled={!deckId || isFetching}
                                title="Forzar siguiente"
                            >
                                Next
                            </button>
                        </div>
                    </div>
                </div>

                {/* Estados */}
                {!deckId && <div className="text-sm text-gray-500">Elige un deck para empezar.</div>}
                {deckId && isFetching && <div className="text-sm text-gray-500">Cargando siguiente tarjeta…</div>}
                {isError && <div className="text-sm text-red-600">Error obteniendo la tarjeta.</div>}

                {/* Tarjeta */}
                {deckId && !isFetching && (
                    <>
                        {!card ? (
                            <div className="rounded-2xl border bg-white/70 backdrop-blur-sm p-8 text-center shadow">
                                <div className="text-lg">¡No hay cartas pendientes 🎉!</div>
                                <div className="text-sm text-gray-500 mt-1">Vuelve más tarde o crea nuevas tarjetas.</div>
                            </div>
                        ) : (
                            <>
                                <Flashcard
                                    front={card.front}
                                    back={card.back ?? " "}
                                    showBack={showBack}
                                    onToggle={() => setShowBack((s) => !s)}
                                />

                                {/* Botonera de respuesta */}
                                <div className="flex flex-wrap gap-2 justify-center">
                                    {[0, 1, 2, 3, 4, 5].map((g) => (
                                        <button
                                            key={g}
                                            onClick={() => onAnswer(g)}
                                            disabled={disabled}
                                            className={[
                                                "px-3 py-2 rounded-xl border shadow-sm",
                                                "bg-white hover:bg-gray-50 active:scale-95 transition",
                                                "disabled:opacity-50"
                                            ].join(" ")}
                                            title={`Respuesta ${g}`}
                                        >
                                            {g}
                                        </button>
                                    ))}
                                    <button
                                        onClick={onSkip}
                                        disabled={disabled}
                                        className="px-3 py-2 rounded-xl border shadow-sm bg-white hover:bg-gray-50 active:scale-95 transition disabled:opacity-50"
                                    >
                                        Skip
                                    </button>
                                </div>
                            </>
                        )}
                    </>
                )}
            </div>
        </div>
    );
}
