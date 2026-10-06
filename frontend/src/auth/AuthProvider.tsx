import { type ReactNode, useState } from "react";
import {
    AuthContext,
    type AuthContextValue,
} from "./authContext";
import {
    getToken,
    removeToken,
    setToken
} from "./tokenStorage";

type AuthProviderProps = {
    children: ReactNode;
};

function AuthProvider({ children }: AuthProviderProps) {
    const [token, setCurrentToken] = useState<string | null>(
        () => getToken(),
    );

    function signIn(newToken: string): void {
        setToken(newToken);
        setCurrentToken(newToken);
    }

    function signOut(): void {
        removeToken();
        setCurrentToken(null);
    }

    const value: AuthContextValue = {
        isAuthenticated: token !== null,
        signIn,
        signOut,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    )
}

export default AuthProvider;