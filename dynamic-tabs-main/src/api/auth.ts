import { apiClient } from "./client";

export const login = (email: string, password: string) =>
  apiClient.post("/auth", { email, password });
