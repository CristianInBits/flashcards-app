type FlashcardProps = {
    front: string;
    back: string;
    showBack: boolean;
    onToggle: () => void;
};

export default function Flashcard({ front, back, showBack, onToggle }: FlashcardProps) {
    return (
        <div className="perspective-1000 w-full">
            <div
                className={[
                    "relative h-56 md:h-64 w-full",
                    "transition-transform duration-500 transform-gpu",
                ].join(" ")}
                style={{
                    // 👇 Controlamos el giro aquí (sin Tailwind arbitrary)
                    transform: showBack ? "rotateY(180deg)" : "rotateY(0deg)",
                    transformStyle: "preserve-3d",
                }}
            >
                {/* FRONT */}
                <div
                    className={[
                        "absolute inset-0",
                        "rounded-2xl border shadow-xl",
                        "bg-white/90 backdrop-blur-sm",
                        "flex items-center justify-center text-center p-6",
                        "text-xl font-semibold leading-relaxed",
                    ].join(" ")}
                    style={{ backfaceVisibility: "hidden" }}
                >
                    <span className="max-w-[90%] break-words">{front}</span>
                </div>

                {/* BACK */}
                <div
                    className={[
                        "absolute inset-0",
                        "rounded-2xl border shadow-xl",
                        "bg-emerald-600 text-white",
                        "flex items-center justify-center text-center p-6",
                        "text-xl font-semibold leading-relaxed",
                    ].join(" ")}
                    style={{
                        transform: "rotateY(180deg)",
                        backfaceVisibility: "hidden",
                    }}
                >
                    <span className="max-w-[90%] break-words">{back}</span>
                </div>
            </div>

            <div className="flex justify-center mt-4">
                <button
                    onClick={onToggle}
                    type="button"
                    className="px-4 py-2 rounded-full border bg-white hover:bg-gray-50 active:scale-95 transition"
                >
                    {showBack ? "Mostrar frente" : "Dar la vuelta"}
                </button>
            </div>
        </div>
    );
}
