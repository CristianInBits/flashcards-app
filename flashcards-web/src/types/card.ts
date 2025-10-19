export type Card = {
    id: string;
    deckId: string;
    front: string;
    back: string;
    tags?: string | null;
    createdAt: string;
};
