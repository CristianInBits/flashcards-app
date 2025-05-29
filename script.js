let flashcards = [];
let currentIndex = 0;
const flashcard = document.getElementById("flashcard");
const cardInner = flashcard.querySelector(".card-inner");
const questionEl = document.getElementById("question");
const answerEl = document.getElementById("answer");

async function loadFlashcards() {
    const res = await fetch("flashcards.json");
    flashcards = await res.json();
    showCard();
}

function showCard() {
    const card = flashcards[currentIndex];
    questionEl.textContent = card.question;
    answerEl.textContent = card.answer;
    flashcard.classList.remove("flipped");
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

loadFlashcards();
