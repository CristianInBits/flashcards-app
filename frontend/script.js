let flashcards = [];
let currentIndex = 0;
const flashcard = document.getElementById("flashcard");
const cardInner = flashcard.querySelector(".card-inner");
const questionEl = document.getElementById("question");
const answerEl = document.getElementById("answer");
const selector = document.getElementById("themeSelector");

async function loadFlashcards() {
    try {
        const res = await fetch("http://localhost:8080/api/flashcards/");
        flashcards = await res.json();

        if (flashcards.length === 0) {
            document.getElementById("question").textContent = "No hay tarjetas disponibles.";
            document.getElementById("answer").textContent = "-";
        } else {
            showCard();
        }

    } catch (error) {
        console.error("Error cargando las tarjetas:", error);
        document.getElementById("question").textContent = "Error al cargar.";
        document.getElementById("answer").textContent = "-";
    }
}

async function updateFlashcardStatus(id, status) {
    await fetch(`http://localhost:8080/api/flashcards/${id}/status?status=${status}`, {
        method: 'POST'
    });
}

async function setStatus(status) {
    const currentCard = flashcards[currentIndex];
    try {
        const res = await fetch(`http://localhost:8080/api/flashcards/${currentCard.id}/status?status=${status}`, {
            method: "POST"
        });

        if (!res.ok) {
            throw new Error("Error actualizando el estado");
        }

        const updatedCard = await res.json();
        flashcards[currentIndex] = updatedCard;
        showCard();
        alert(`📌 Estado actualizado a: ${status}`);
    } catch (err) {
        console.error(err);
        alert("⚠️ No se pudo actualizar el estado");
    }
}

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
        showCard(); // Actualiza la vista
        alert(`🔁 Nivel actualizado: ${updatedCard.level}`);
    } catch (err) {
        console.error(err);
        alert("⚠️ No se pudo actualizar el progreso");
    }
}



function showCard() {
    const card = flashcards[currentIndex];
    document.getElementById("question").textContent = card.question;
    document.getElementById("answer").textContent = card.answer;
    document.getElementById("flashcard").classList.remove("flipped");

    const badge = document.getElementById("statusBadge");
    badge.textContent = `Nivel: ${card.level} | Revisión: ${card.nextReviewDate}`;
    badge.className = "badge";
}


function nextCard() {
    currentIndex = (currentIndex + 1) % flashcards.length;
    showCard();
}

function prevCard() {
    currentIndex = (currentIndex - 1 + flashcards.length) % flashcards.length;
    showCard();
}

flashcard.addEventListener("click", () => {
    flashcard.classList.toggle("flipped");
});

selector.addEventListener("change", (e) => {
    document.body.classList.remove("theme-1", "theme-2", "theme-3");
    document.body.classList.add(e.target.value);
});

document.body.classList.add("theme-2");

loadFlashcards();

console.log("Tarjetas cargadas:", flashcards);

