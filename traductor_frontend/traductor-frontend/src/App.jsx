import { useEffect, useState } from "react";
import Translator from "./components/Translator";
import Login from "./components/Login";
import WelcomePlans from "./components/WelcomePlans";
import {
  clearAuthSession,
  getAuthSession,
  getPlanById,
  isLoginPlan,
  preparePlanLogin,
  saveAuthenticatedUser,
  startDemoSession,
} from "./auth/plans";
import logoTitulo from "../img/LogoCompiTrad.png";
import "./App.css";

const SCREEN_ROUTES = {
  welcome: "#/planes",
  login: "#/login",
  translator: "#/traductor",
};

function getRouteName() {
  if (typeof window === "undefined") {
    return "planes";
  }

  return window.location.hash.replace(/^#\/?/, "") || "planes";
}

function resolveScreenFromRoute() {
  const routeName = getRouteName();
  const session = getAuthSession();

  if (routeName === "login") {
    if (session.isAuthenticated) {
      return "translator";
    }

    return isLoginPlan(session.selectedPlan) ? "login" : "welcome";
  }

  if (routeName === "traductor") {
    return session.isAuthenticated ? "translator" : "welcome";
  }

  return "welcome";
}

function replaceRouteForScreen(screen) {
  if (typeof window === "undefined") {
    return;
  }

  const nextRoute = SCREEN_ROUTES[screen] || SCREEN_ROUTES.welcome;

  if (window.location.hash !== nextRoute) {
    window.history.replaceState(null, "", nextRoute);
  }
}

function App() {
  const [screen, setScreen] = useState(() => resolveScreenFromRoute());
  const [session, setSession] = useState(() => getAuthSession());

  useEffect(() => {
    const handleRouteChange = () => {
      const nextScreen = resolveScreenFromRoute();
      setSession(getAuthSession());
      setScreen(nextScreen);
      replaceRouteForScreen(nextScreen);
    };

    handleRouteChange();
    window.addEventListener("hashchange", handleRouteChange);

    return () => window.removeEventListener("hashchange", handleRouteChange);
  }, []);

  const navigateTo = (nextScreen) => {
    setSession(getAuthSession());
    setScreen(nextScreen);

    const nextRoute = SCREEN_ROUTES[nextScreen] || SCREEN_ROUTES.welcome;
    if (window.location.hash !== nextRoute) {
      window.location.hash = nextRoute;
    }
  };

  const handleDemoAccess = () => {
    startDemoSession();
    navigateTo("translator");
  };

  const handleLoginPlan = (planId) => {
    preparePlanLogin(planId);
    navigateTo("login");
  };

  const handleLogin = (user) => {
    saveAuthenticatedUser(user);
    navigateTo("translator");
  };

  const handleLogout = () => {
    clearAuthSession();
    navigateTo("welcome");
  };

  if (screen === "welcome") {
    return <WelcomePlans onSelectDemo={handleDemoAccess} onSelectLoginPlan={handleLoginPlan} />;
  }

  if (screen === "login") {
    return (
      <Login
        selectedPlan={session.selectedPlan}
        onLogin={handleLogin}
      />
    );
  }

  if (!session.isAuthenticated) {
    return <WelcomePlans onSelectDemo={handleDemoAccess} onSelectLoginPlan={handleLoginPlan} />;
  }

  const currentPlan = getPlanById(session.selectedPlan);
  const sessionLabel = [session.userName || "Usuario", currentPlan?.shortName].filter(Boolean).join(" - ");

  return (
    <div className="app">
      <header className="app-header">
        <div className="brand-banner">
          <h1 className="sr-only">CompiTrad DMN</h1>
          <div className="brand-content">
            <img className="brand-logo" src={logoTitulo} alt="CompiTrad DMN" />
          </div>
          <div className="session-panel" aria-label="Sesion actual">
            <span>{sessionLabel}</span>
            <button type="button" onClick={handleLogout}>
              Cerrar sesion
            </button>
          </div>
        </div>
      </header>

      <main>
        <Translator />
      </main>
    </div>
  );
}

export default App;
