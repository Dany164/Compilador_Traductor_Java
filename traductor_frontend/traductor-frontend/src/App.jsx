import Translator from "./components/Translator";
import logoTitulo from "../img/LogoCompiTrad.png";
import "./App.css";

function App() {
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
