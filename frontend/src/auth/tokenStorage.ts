const TOKEN_KEY = "nextquest_authToken";

export function getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
    sessionStorage.setItem(TOKEN_KEY, token);
}

export function removeToken(): void {
    sessionStorage.removeItem(TOKEN_KEY);
}
