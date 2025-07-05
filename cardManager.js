let cards = [];
let filteredCards = [];
let currentIndex = 0;
let seenCards = new Set();

export async function loadCards() {
    try {
        const response = await fetch('cards.json');
        cards = await response.json();

        const stored = JSON.parse(localStorage.getItem('seen-cards') || '[]');
        seenCards = new Set(stored.filter(index => index >= 0 && index < cards.length));
        localStorage.setItem('seen-cards', JSON.stringify([...seenCards]));

        filteredCards = [...cards];
        updateProgressUI();
        populateCollections();
        return filteredCards;
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
    const globalIndex = cards.indexOf(filteredCards[index]);
    if (globalIndex !== -1) {
        seenCards.add(globalIndex);
        localStorage.setItem('seen-cards', JSON.stringify([...seenCards]));
    }
}

function updateProgressUI() {
    const progressContainer = document.getElementById('progress');
    if (!progressContainer) return;

    const seenInFilter = filteredCards.filter(card => seenCards.has(cards.indexOf(card))).length;
    const percent = filteredCards.length > 0 ? (seenInFilter / filteredCards.length) * 100 : 0;

    progressContainer.innerHTML = `<span>Progreso: ${seenInFilter} / ${filteredCards.length}</span>`;
    progressContainer.style.setProperty('--progress', `${percent}%`);
    progressContainer.style.setProperty('--progress-width', `${percent}%`);
    progressContainer.querySelector('::before')?.style?.setProperty('width', `${percent}%`);
}

function populateCollections() {
    const select = document.getElementById('collection-selector');
    if (!select) return;

    const collections = [...new Set(cards.map(card => card.collection))];
    select.innerHTML = '<option value="all">Todas las barajas</option>' +
        collections.map(c => `<option value="${c}">${c}</option>`).join('');

    select.addEventListener('change', (e) => {
        const selected = e.target.value;
        if (selected === 'all') {
            filteredCards = [...cards];
        } else {
            filteredCards = cards.filter(card => card.collection === selected);
        }
        currentIndex = 0;
        if (filteredCards.length > 0) {
            renderCard(filteredCards[currentIndex]);
        } else {
            document.getElementById('card-container').innerHTML = '<p>No hay tarjetas en esta baraja.</p>';
        }
        updateProgressUI();
    });
}

export function getCards() {
    return filteredCards;
}

export function getCurrentIndex() {
    return currentIndex;
}

export function nextCard() {
    currentIndex = (currentIndex + 1) % filteredCards.length;
}

export function randomCard() {
    let randomIndex;
    do {
        randomIndex = Math.floor(Math.random() * filteredCards.length);
    } while (randomIndex === currentIndex);
    currentIndex = randomIndex;
}
