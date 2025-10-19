import { useMutation } from "@tanstack/react-query";
import api from "../lib/api";

// El backend espera { email, password } en login y register
type LoginPayload = { email: string; password: string };
type LoginResponse = { token: string; tokenType: string; expiresInSec: number };

export function useLogin() {
  return useMutation({
    mutationFn: async (payload: LoginPayload) => {
      const { data } = await api.post<LoginResponse>("/auth/login", payload);
      return data;
    },
  });
}

export function useRegister() {
  return useMutation({
    mutationFn: async (payload: { email: string; password: string }) => {
      await api.post("/auth/register", payload);
    },
  });
}
