let cards = [];
let currentIndex = 0;

export async function loadCards() {
    try {
        const response = await fetch('cards.json');
        cards = await response.json();
        return cards;
    } catch (error) {
        console.error('Error cargando tarjetas:', error);
        return [];
    }
}

export function renderCard(card) {
    const container = document.getElementById('card-container');
    container.innerHTML = `
    <div class="flashcard">
      <div class="front">${card.question}</div>
      <div class="back">${card.answer}</div>
    </div>
  `;

    const flashcard = container.querySelector('.flashcard');
    flashcard.addEventListener('click', () => {
        flashcard.classList.toggle('flipped');
    });
}

export function getCards() {
    return cards;
}

export function getCurrentIndex() {
    return currentIndex;
}

export function nextCard() {
    currentIndex = (currentIndex + 1) % cards.length;
}

export function randomCard() {
    let randomIndex;
    do {
        randomIndex = Math.floor(Math.random() * cards.length);
    } while (randomIndex === currentIndex);
    currentIndex = randomIndex;
}
