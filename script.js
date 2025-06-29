let allCards = [];
let currentIndex = 0;

// Cargar las tarjetas desde JSON local
fetch('cards.json')
  .then(response => response.json())
  .then(cards => {
    allCards = cards;
    renderCard(allCards[currentIndex]);
  })
  .catch(error => console.error('Error cargando JSON:', error));

// Mostrar una tarjeta
function renderCard(card) {
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

// Botón "Siguiente"
document.getElementById('next-btn').addEventListener('click', () => {
  currentIndex = (currentIndex + 1) % allCards.length;
  renderCard(allCards[currentIndex]);
});

// Botón "Aleatoria"
document.getElementById('random-btn').addEventListener('click', () => {
  let randomIndex;
  do {
    randomIndex = Math.floor(Math.random() * allCards.length);
  } while (randomIndex === currentIndex); // evita repetir la misma tarjeta

  currentIndex = randomIndex;
  renderCard(allCards[currentIndex]);
});
