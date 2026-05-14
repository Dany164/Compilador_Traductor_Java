import { useState } from "react";
import Translator from "./components/Translator";
import Login from "./components/Login";
import logoTitulo from "../img/LogoCompiTrad.png";
import "./App.css";

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  if (!isAuthenticated) {
    return <Login onLogin={() => setIsAuthenticated(true)} />;
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="brand-banner">
          <h1 className="sr-only">CompiTrad DMN</h1>
          <div className="brand-content">
            <img className="brand-logo" src={logoTitulo} alt="CompiTrad DMN" />
            <p className="eyebrow">Traductor académico inglés - español</p>
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
