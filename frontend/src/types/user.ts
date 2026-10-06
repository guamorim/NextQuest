export type RegisterRequest = {
    name: string;
    email: string;
    password: string;
};

export type UserResponse = {
    id: number;
    name: string;
    email: string;
};