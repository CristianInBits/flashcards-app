import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "../lib/api";

export type NextCard = {
    cardId: string;
    deckId: string;
    front: string;
    back?: string; // a veces el back no viene en /next; si tu backend ya lo manda, perfecto
};

export function useNextReview(deckId?: string, shuffle = true) {
    return useQuery({
        queryKey: ["reviews", "next", deckId, shuffle],
        queryFn: async () => {
            const params: any = { shuffle };
            if (deckId) params.deckId = deckId; // ⬅️ solo si existe

            const { data, status } = await api.get("/reviews/next", {
                params,
                validateStatus: (s) => s === 200 || s === 204,
            });
            if (status === 204) return null;
            return data as NextCard;
        },
        enabled: !!deckId,
    });
}


export function useAnswerReview() {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: async (payload: { cardId: string; grade: number }) => {
            await api.post(`/reviews/${payload.cardId}/answer`, { grade: payload.grade });
        },
        onSuccess: () => {
            qc.invalidateQueries({ queryKey: ["reviews", "next"] });
        },
    });
}

export function useSkipReview(defaultMinutes = 10) {
    const qc = useQueryClient();
    return useMutation({
        mutationFn: async (payload: { cardId: string; minutes?: number }) => {
            const params = { minutes: payload.minutes ?? defaultMinutes };
            await api.post(`/reviews/${payload.cardId}/skip`, null, { params });
        },
        onSuccess: () => {
            qc.invalidateQueries({ queryKey: ["reviews", "next"] });
        },
    });
}
