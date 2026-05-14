import { useState } from "react";
import logoCompiTrad from "../../img/LogoCompiTrad.png";

const TEST_USERS = [
  { email: "admin@traductor.com", password: "123456" },
  { email: "nayeli@traductor.com", password: "nayeli1234" },
  { email: "manuel@traductor.com", password: "manuel1234" },
  { email: "dany@traductor.com", password: "dany1234" },
];

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function Login({ onLogin }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const nextErrors = {};
    const cleanEmail = email.trim();

    if (!cleanEmail) {
      nextErrors.email = "Ingresa tu correo electronico.";
    } else if (!isValidEmail(cleanEmail)) {
      nextErrors.email = "El correo no tiene un formato valido.";
    }

    if (!password) {
      nextErrors.password = "Ingresa tu contrasena.";
    }

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (!validateForm()) {
      return;
    }

    const userExists = TEST_USERS.some(
      (user) => user.email === email.trim().toLowerCase() && user.password === password,
    );

    if (userExists) {
      setErrors({});
      onLogin();
      return;
    }

    setErrors({
      form: "Credenciales incorrectas. Revisa el correo y la contrasena.",
    });
  };

  return (
    <main className="login-screen">
      <div className="circuit circuit-left" aria-hidden="true">
        <span className="circuit-line line-a" />
        <span className="circuit-line line-b" />
        <span className="circuit-line line-c" />
        <span className="circuit-line line-d" />
        <span className="circuit-dot dot-a" />
        <span className="circuit-dot dot-b" />
        <span className="circuit-dot dot-c" />
      </div>

      <div className="circuit circuit-right" aria-hidden="true">
        <span className="circuit-line line-a" />
        <span className="circuit-line line-b" />
        <span className="circuit-line line-c" />
        <span className="circuit-line line-d" />
        <span className="circuit-dot dot-a" />
        <span className="circuit-dot dot-b" />
        <span className="circuit-dot dot-c" />
      </div>

      <section className="login-layout" aria-labelledby="login-title">
        <div className="login-hero">
          <p className="welcome-text">BIENVENIDOS A</p>
          <img className="login-logo" src={logoCompiTrad} alt="CompiTrad DMN" />
          <p className="login-subtitle">Traductor académico inglés - español</p>
        </div>

        <form className="login-card" onSubmit={handleSubmit} noValidate>
          <div className="lock-orbit" aria-hidden="true">
            <LockIcon />
          </div>

          <h1 id="login-title">INICIAR SESIÓN</h1>
          <p className="login-helper">Ingresa tus credenciales para continuar</p>

          {errors.form && (
            <p className="login-error login-error-box" role="alert">
              {errors.form}
            </p>
          )}

          <label className="login-field">
            <span className="sr-only">Correo electronico</span>
            <span className="field-icon" aria-hidden="true">
              <MailIcon />
            </span>
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="Correo electronico"
              autoComplete="email"
              aria-invalid={Boolean(errors.email)}
            />
          </label>
          {errors.email && (
            <small className="login-error" role="alert">
              {errors.email}
            </small>
          )}

          <label className="login-field">
            <span className="sr-only">Contrasena</span>
            <span className="field-icon" aria-hidden="true">
              <PasswordIcon />
            </span>
            <input
              type={showPassword ? "text" : "password"}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Contrasena"
              autoComplete="current-password"
              aria-invalid={Boolean(errors.password)}
            />
            <button
              type="button"
              className="password-toggle"
              onClick={() => setShowPassword((current) => !current)}
              aria-label={showPassword ? "Ocultar contrasena" : "Mostrar contrasena"}
            >
              {showPassword ? <EyeOffIcon /> : <EyeIcon />}
            </button>
          </label>
          {errors.password && (
            <small className="login-error" role="alert">
              {errors.password}
            </small>
          )}

          <button className="login-submit" type="submit">
            INICIAR SESIÓN
          </button>
        </form>
      </section>
    </main>
  );
}

function LockIcon() {
  return (
    <svg viewBox="0 0 64 64" role="img" aria-label="Candado">
      <path d="M21 29v-8c0-6.1 4.9-11 11-11s11 4.9 11 11v8" />
      <rect x="17" y="28" width="30" height="25" rx="5" />
      <path d="M32 38v8" />
      <circle cx="32" cy="37" r="3" />
    </svg>
  );
}

function MailIcon() {
  return (
    <svg viewBox="0 0 24 24" role="img" aria-label="Correo">
      <rect x="3" y="5" width="18" height="14" rx="2" />
      <path d="m4 7 8 6 8-6" />
    </svg>
  );
}

function PasswordIcon() {
  return (
    <svg viewBox="0 0 24 24" role="img" aria-label="Contrasena">
      <rect x="5" y="10" width="14" height="10" rx="2" />
      <path d="M8 10V7a4 4 0 0 1 8 0v3" />
      <path d="M12 14v3" />
    </svg>
  );
}

function EyeIcon() {
  return (
    <svg viewBox="0 0 24 24" role="img" aria-label="Mostrar">
      <path d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6Z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}

function EyeOffIcon() {
  return (
    <svg viewBox="0 0 24 24" role="img" aria-label="Ocultar">
      <path d="m3 3 18 18" />
      <path d="M10.7 5.2A10.1 10.1 0 0 1 12 5c6 0 9.5 7 9.5 7a17.8 17.8 0 0 1-3.1 4.1" />
      <path d="M6.4 6.5C3.9 8.2 2.5 12 2.5 12s3.5 7 9.5 7c1.6 0 3-.4 4.2-1" />
      <path d="M9.9 9.9a3 3 0 0 0 4.2 4.2" />
    </svg>
  );
}

export default Login;
