import { type FormEvent, useState } from "react";
import { register } from "../services/authService";
import type { RegisterRequest } from "../types/user";

function RegisterPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (isSubmitting) {
      return;
    }

    const request: RegisterRequest = {
      name,
      email,
      password,
    };

    setIsSubmitting(true);
    setErrorMessage(null);
    setSuccessMessage(null);

    try {
      const user = await register(request);

      setSuccessMessage(`Account created for ${user.name}.`);
      setName("");
      setEmail("");
      setPassword("");
    } catch {
      setErrorMessage("Unable to create account.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main>
      <h1>Create Account</h1>

      {errorMessage && <p role="alert">{errorMessage}</p>}
      {successMessage && <p role="status">{successMessage}</p>}

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Name</label>
          <input
            id="name"
            name="name"
            type="text"
            autoComplete="name"
            value={name}
            onChange={(event) => setName(event.target.value)}
            required
          />
        </div>

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
            autoComplete="new-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
          />
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Creating..." : "Create Account"}
        </button>
      </form>
    </main>
  );
}

export default RegisterPage;