// 🌐 Configuración global
let flashcards = [];
let currentIndex = 0;
let currentCollection = "bases-datos";

const flashcard = document.getElementById("flashcard");
const cardInner = flashcard.querySelector(".card-inner");
const questionEl = document.getElementById("question");
const answerEl = document.getElementById("answer");
const selector = document.getElementById("themeSelector");

// 🚀 Carga de tarjetas desde backend por colección
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

// 🔄 Cambio de colección desde el selector
async function changeCollection() {
    const select = document.getElementById("collectionSelect");
    currentCollection = select.value;
    await loadFlashcardsByCollection(currentCollection);
    currentIndex = 0;
    showCard();
}

// ✅ Actualiza el progreso de aprendizaje (nivel y fecha)
async function updateProgress(remembered) {
    const currentCard = flashcards[currentIndex];
    try {
        const res = await fetch(`http://localhost:8080/api/flashcards/${currentCard.id}/progress?remembered=${remembered}`, {
            method: "POST"
        });

        if (!res.ok) {
            throw new Error("Error actualizando el progreso");
        }

        const updatedCard = await res.json();
        flashcards[currentIndex] = updatedCard;
        showCard();
        alert(`🔁 Nivel actualizado: ${updatedCard.level}`);
    } catch (err) {
        console.error(err);
        alert("⚠️ No se pudo actualizar el progreso");
    }
}

// 📄 Muestra la tarjeta actual
function showCard() {
    const card = flashcards[currentIndex];
    document.getElementById("question").textContent = card.question;
    document.getElementById("answer").textContent = card.answer;
    document.getElementById("flashcard").classList.remove("flipped");

    const badge = document.getElementById("reviewInfo");
    badge.textContent = `Nivel: ${card.level} | Revisión: ${card.nextReviewDate}`;
    badge.className = "badge";
}

// ⬅ Muestra la tarjeta anterior
function prevCard() {
    currentIndex = (currentIndex - 1 + flashcards.length) % flashcards.length;
    showCard();
}

// ➡ Muestra la tarjeta siguiente
function nextCard() {
    currentIndex = (currentIndex + 1) % flashcards.length;
    showCard();
}

// 🧠 Gira la tarjeta al hacer clic
flashcard.addEventListener("click", () => {
    flashcard.classList.toggle("flipped");
});

// 🎨 Cambia el tema desde el selector
selector.addEventListener("change", (e) => {
    document.body.classList.remove("theme-1", "theme-2", "theme-3");
    document.body.classList.add(e.target.value);
});

// 🎯 Tema inicial y carga de tarjetas por colección
document.body.classList.add("theme-2");
loadFlashcardsByCollection(currentCollection);
