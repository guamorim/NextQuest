import type { RegisterRequest, UserResponse } from "../types/user";
import type { LoginRequest, LoginResponse } from "../types/auth";

const API_BASE_URL = "http://localhost:8081/api";

export async function register(
  request: RegisterRequest,
): Promise<UserResponse> {
  const response = await fetch(`${API_BASE_URL}/users`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    throw new Error("Unable to create account.");
  }

  return (await response.json()) as UserResponse;
}

export async function login(
  request: LoginRequest,
): Promise<LoginResponse> {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    throw new Error("Invalid email or password.")
  }

  return (await response.json()) as LoginResponse;
}