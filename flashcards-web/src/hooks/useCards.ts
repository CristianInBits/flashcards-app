import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "../lib/api";
import type { Card } from "../types/card";
import { keepPreviousData } from "@tanstack/react-query";

export function useCards(deckId: string, page = 0, size = 50, q?: string, tag?: string) {
    return useQuery({
        queryKey: ["cards", deckId, page, size, q, tag],
        queryFn: async () => {
            const base = q || tag ? `/decks/${deckId}/cards/search` : `/decks/${deckId}/cards`;
            const params: any = { page, size };
            if (q) params.q = q;
            if (tag) params.tag = tag;
            const { data } = await api.get(base, { params });
            return data as { content: Card[]; totalElements: number; totalPages: number };
        },
        enabled: !!deckId,
        placeholderData: keepPreviousData, // ⬅️ mantiene data anterior mientras trae la nueva
    });
}

export function useCreateCard(deckId: string) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: async (payload: { front: string; back: string; tags?: string }) => {
            const { data } = await api.post<Card>("/cards", { deckId, ...payload });
            return data;
        },
        onSuccess: () => qc.invalidateQueries({ queryKey: ["cards", deckId] }),
    });
}

export function useDeleteCard(deckId: string) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: async (cardId: string) => {
            await api.delete(`/cards/${cardId}`);
        },
        onSuccess: () => qc.invalidateQueries({ queryKey: ["cards", deckId] }),
    });
}
