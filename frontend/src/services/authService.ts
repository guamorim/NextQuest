import type { RegisterRequest, UserResponse } from "../types/user";
import type { LoginRequest, LoginResponse } from "../types/auth";
import { apiRequest } from "./api";

export function register(
  request: RegisterRequest,
): Promise<UserResponse> {
  return apiRequest<UserResponse>("/users", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

export function login(
  request: LoginRequest,
): Promise<LoginResponse> {
  return apiRequest<LoginResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify(request),
  });
}
