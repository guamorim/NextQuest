import { getToken } from "../auth/tokenStorage";

const API_BASE_URL = "http://localhost:8081/api";

type ApiRequestOptions = RequestInit & {
    authenticated?: boolean;
};

export async function apiRequest<T>(
    path: string,
    options: ApiRequestOptions = {},
): Promise<T> {
    const {
        authenticated = false,
        headers: customHeaders,
        ...requestOptions
    } = options;

    const headers = new Headers(customHeaders);

    if (
        requestOptions.body !== undefined &&
        requestOptions.body !== null &&
        !headers.has("Content-Type")
    ) {
        headers.set("Content-Type", "application/json");
    }

    if (authenticated) {
        const token = getToken();

        if (token === null) {
            throw new Error("Authentication required.");
        }

        headers.set("Authorization", `Bearer ${token}`);
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...requestOptions,
        headers,
    });

    if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}.`);
    }

    if (response.status === 204) {
        return undefined as T;
    }
    return (await response.json()) as T;
}