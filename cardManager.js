let cards = [];
let currentIndex = 0;
let seenCards = new Set();

export async function loadCards() {
    try {
        const response = await fetch('cards.json');
        cards = await response.json();

        // Limpiar y conservar solo índices válidos
        const stored = JSON.parse(localStorage.getItem('seen-cards') || '[]');
        seenCards = new Set(stored.filter(index => index >= 0 && index < cards.length));
        localStorage.setItem('seen-cards', JSON.stringify([...seenCards]));

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

    const percent = cards.length > 0 ? (seenCards.size / cards.length) * 100 : 0;
    progressContainer.innerHTML = `<span>Progreso: ${seenCards.size} / ${cards.length}</span>`;
    progressContainer.style.setProperty('--progress', `${percent}%`);

    const progressBar = progressContainer;
    const bar = progressBar.querySelector('::before');
    if (bar) {
        bar.style.width = `${percent}%`;
    } else {
        progressBar.style.setProperty('--progress', `${percent}%`);
        progressBar.style.setProperty('background-size', `${percent}% 100%`);
        progressBar.style.setProperty('--progress-width', `${percent}%`);
        progressBar.style.setProperty('--progress-text', `'Progreso: ${seenCards.size} / ${cards.length}'`);
        progressBar.style.setProperty('position', 'relative');
    }
    progressContainer.style.setProperty('--progress-width', `${percent}%`);
    const before = progressContainer.querySelector('::before');
    if (before) before.style.width = `${percent}%`;
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
