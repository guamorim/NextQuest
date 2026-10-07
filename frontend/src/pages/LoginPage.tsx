import { type FormEvent, useState } from "react";
import { useAuth } from "../auth/authContext";
import { login } from "../services/authService";
import type { LoginRequest } from "../types/auth";
import { useNavigate } from "react-router";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const { signIn } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (isSubmitting) {
      return;
    }

    const request: LoginRequest = {
      email,
      password,
    };

    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      const response = await login(request);

      signIn(response.token);
      navigate("/library", { replace: true });
    } catch {
      setErrorMessage("Invalid email or password.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main>
      <h1>Login</h1>

      {errorMessage && <p role="alert">{errorMessage}</p>}

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="email">Email</label>
          <input
            id="email"
            name="email"
            type="email"
            autoComplete="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />
        </div>

        <div>
          <label htmlFor="password">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
          />
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Signing in..." : "Login"}
        </button>
      </form>
    </main>
  );
}

export default LoginPage;
