export function applySavedTheme() {
    const savedTheme = localStorage.getItem('flashcards-theme');
    const themeSelector = document.getElementById('theme-selector');

    if (savedTheme && savedTheme !== 'default') {
        document.body.classList.add(savedTheme);
        if (themeSelector) themeSelector.value = savedTheme;
    }
}

export function setupThemeSelector() {
    const themeSelector = document.getElementById('theme-selector');
    if (!themeSelector) return;

    themeSelector.addEventListener('change', (e) => {
        document.body.classList.remove('theme-1', 'theme-2', 'theme-3');
        const selected = e.target.value;
        if (selected !== 'default') {
            document.body.classList.add(selected);
        }
        localStorage.setItem('flashcards-theme', selected);
    });
}
