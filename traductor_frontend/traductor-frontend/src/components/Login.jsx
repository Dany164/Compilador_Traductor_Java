import { useState } from "react";

const TEST_EMAIL = "admin@traductor.com";
const TEST_PASSWORD = "123456";

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

    if (email.trim() === TEST_EMAIL && password === TEST_PASSWORD) {
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
      <section className="login-card" aria-labelledby="login-title">
        <div className="login-brand">
          <span className="login-icon" aria-hidden="true">
            TC
          </span>
          <div>
            <p className="login-kicker">Acceso seguro</p>
            <h1 id="login-title">Traductor / Compilador</h1>
          </div>
        </div>

        <form className="login-form" onSubmit={handleSubmit} noValidate>
          {errors.form && (
            <p className="login-error login-error-box" role="alert">
              {errors.form}
            </p>
          )}

          <label className="login-field">
            <span>Correo electronico</span>
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="admin@traductor.com"
              autoComplete="email"
              aria-invalid={Boolean(errors.email)}
            />
            {errors.email && (
              <small className="login-error" role="alert">
                {errors.email}
              </small>
            )}
          </label>

          <label className="login-field">
            <span>Contrasena</span>
            <div className="password-control">
              <input
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="123456"
                autoComplete="current-password"
                aria-invalid={Boolean(errors.password)}
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword((current) => !current)}
              >
                {showPassword ? "Ocultar" : "Mostrar"}
              </button>
            </div>
            {errors.password && (
              <small className="login-error" role="alert">
                {errors.password}
              </small>
            )}
          </label>

          <button className="login-submit" type="submit">
            Iniciar sesion
          </button>
        </form>
      </section>
    </main>
  );
}

export default Login;
