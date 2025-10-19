import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "../lib/api";
import type { Deck } from "../types/deck";

export function useDecks(page = 0, size = 20) {
    return useQuery({
        queryKey: ["decks", page, size],
        queryFn: async () => {
            const { data } = await api.get(`/decks`, { params: { page, size } });
            // Spring Data Page: content, totalElements, totalPages...
            return data as { content: Deck[]; totalElements: number; totalPages: number };
        },
    });
}

export function useCreateDeck() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: async (payload: { name: string; description?: string }) => {
            const { data } = await api.post<Deck>("/decks", payload);
            return data;
        },
        onSuccess: () => qc.invalidateQueries({ queryKey: ["decks"] }),
    });
}

export function useDeleteDeck() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: async (id: string) => { await api.delete(`/decks/${id}`); },
        onSuccess: () => qc.invalidateQueries({ queryKey: ["decks"] }),
    });
}
