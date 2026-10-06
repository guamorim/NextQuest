import { createContext, useContext } from "react";

export type AuthContextValue = {
    isAuthenticated: boolean;
    signIn: (token: string) => void;
    signOut: () => void;
}

export const AuthContext = createContext<AuthContextValue | undefined>(
    undefined,
);

export function useAuth(): AuthContextValue {
    const context = useContext(AuthContext);

    if (context === undefined) {
        throw new Error("useAuth must be used within AuthProvider");
    }

    return context;
}

