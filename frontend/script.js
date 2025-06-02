// 🌐 ===== CONFIGURACIÓN GLOBAL =====

// Estado de las tarjetas cargadas
let flashcards = [];
let currentIndex = 0;
let currentCollection = "bases-datos";

// Referencias DOM principales
const flashcard = document.getElementById("flashcard");
const cardInner = flashcard.querySelector(".card-inner");
const questionEl = document.getElementById("question");
const answerEl = document.getElementById("answer");
const selector = document.getElementById("themeSelector");

// 🔁 ===== FUNCIONES DE NEGOCIO =====

/**
 * 🚀 Carga las flashcards de una colección específica desde el backend.
 * @param {string} collection - Nombre de la colección (ej. "bases-datos")
 */
async function loadFlashcardsByCollection(collection) {
    try {
        const res = await fetch(`http://localhost:8080/api/flashcards/?collection=${collection}`);
        if (!res.ok) throw new Error("Error al cargar la colección");
        flashcards = await res.json();
    } catch (err) {
        console.error(err);
        alert("⚠️ No se pudieron cargar las tarjetas");
    }
}

/**
 * 🔄 Actualiza la colección cuando el usuario selecciona otra en el menú.
 */
async function changeCollection() {
    const select = document.getElementById("collectionSelect");
    currentCollection = select.value;
    await loadFlashcardsByCollection(currentCollection);
    currentIndex = 0;
    showCard();
}

/**
 * ✅ Informa al backend del resultado del repaso actual y actualiza la tarjeta.
 * @param {boolean} remembered - true si se recordó correctamente, false si se falló
 */
async function updateProgress(remembered) {
    const currentCard = flashcards[currentIndex];
    try {
        const res = await fetch(
            `http://localhost:8080/api/flashcards/${currentCard.id}/progress?remembered=${remembered}`,
            { method: "POST" }
        );

        if (!res.ok) throw new Error("Error actualizando el progreso");

        const updatedCard = await res.json();
        flashcards[currentIndex] = updatedCard;
        showCard();
        alert(`🔁 Nivel actualizado: ${updatedCard.level}`);
    } catch (err) {
        console.error(err);
        alert("⚠️ No se pudo actualizar el progreso");
    }
}

/**
 * 📄 Muestra la tarjeta actual (pregunta, respuesta y metadatos).
 */
function showCard() {
    const card = flashcards[currentIndex];
    questionEl.textContent = card.question;
    answerEl.textContent = card.answer;
    flashcard.classList.remove("flipped");

    const badge = document.getElementById("reviewInfo");
    badge.textContent = `Nivel: ${card.level} | Revisión: ${card.nextReviewDate}`;
    badge.className = "badge";
}

/**
 * ⬅ Muestra la tarjeta anterior del mazo (circular).
 */
function prevCard() {
    currentIndex = (currentIndex - 1 + flashcards.length) % flashcards.length;
    showCard();
}

/**
 * ➡ Muestra la siguiente tarjeta del mazo (circular).
 */
function nextCard() {
    currentIndex = (currentIndex + 1) % flashcards.length;
    showCard();
}

// 🧠 ===== EVENTOS DE INTERACCIÓN =====

/**
 * 🧠 Permite girar la tarjeta para ver la respuesta.
 */
flashcard.addEventListener("click", () => {
    flashcard.classList.toggle("flipped");
});

/**
 * 🎨 Permite cambiar el tema visual seleccionado por el usuario.
 */
selector.addEventListener("change", (e) => {
    document.body.classList.remove("theme-1", "theme-2", "theme-3");
    document.body.classList.add(e.target.value);
});

// 🎯 ===== INICIALIZACIÓN =====

// Tema visual por defecto
document.body.classList.add("theme-2");

// Carga inicial de tarjetas desde la colección base
loadFlashcardsByCollection(currentCollection);
