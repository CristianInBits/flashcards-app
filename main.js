import { loadCards, renderCard, getCards, getCurrentIndex, nextCard, randomCard } from './cardManager.js';
import { applySavedTheme, setupThemeSelector } from './themeManager.js';

// Estado global
let allCards = [];

// Inicialización
window.addEventListener('DOMContentLoaded', async () => {
    applySavedTheme();
    setupThemeSelector();

    allCards = await loadCards();
    renderCard(allCards[getCurrentIndex()]);

    document.getElementById('next-btn').addEventListener('click', () => {
        nextCard();
        renderCard(getCards()[getCurrentIndex()]);
    });

    document.getElementById('random-btn').addEventListener('click', () => {
        randomCard();
        renderCard(getCards()[getCurrentIndex()]);
    });
});
