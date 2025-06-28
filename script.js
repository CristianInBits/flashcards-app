// Carga del JSON local
fetch('cards.json')
    .then(response => response.json())
    .then(cards => {
        renderCard(cards[0]); // mostramos solo la primera tarjeta de ejemplo
    })
    .catch(error => console.error('Error cargando JSON:', error));

function renderCard(card) {
    const container = document.getElementById('card-container');
    container.innerHTML = `
    <div class="flashcard">
      <div class="front">${card.question}</div>
      <div class="back">${card.answer}</div>
    </div>
  `;

    // Animación de flip
    const flashcard = container.querySelector('.flashcard');
    flashcard.addEventListener('click', () => {
        flashcard.classList.toggle('flipped');
    });
}
