import { apiClient } from "./client";

export const register = (userInfo) => apiClient.post("/users", userInfo);

export const getUsers = () => {
  return apiClient.get("/users");
};
