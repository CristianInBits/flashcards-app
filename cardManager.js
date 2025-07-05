let cards = [];
let currentIndex = 0;
let seenCards = new Set(JSON.parse(localStorage.getItem('seen-cards') || '[]'));

export async function loadCards() {
    try {
        const response = await fetch('cards.json');
        cards = await response.json();
        updateProgressUI();
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

    markCardAsSeen(currentIndex);
    updateProgressUI();
}

function markCardAsSeen(index) {
    seenCards.add(index);
    localStorage.setItem('seen-cards', JSON.stringify([...seenCards]));
    console.log(`Tarjetas vistas: ${seenCards.size} de ${cards.length}`);
}

function updateProgressUI() {
    const progressContainer = document.getElementById('progress');
    if (!progressContainer) return;
    progressContainer.textContent = `Progreso: ${seenCards.size} / ${cards.length}`;
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
