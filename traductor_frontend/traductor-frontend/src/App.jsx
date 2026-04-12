import Translator from "./components/Translator";
import "./App.css";

function App() {
  return (
    <div className="app">
      <header className="app-header">
        <h1>🧠 CompilaTrad</h1>
        <p>Compilador Traductor Inglés ↔ Español</p>
      </header>
      <main>
        <Translator />
      </main>
      <footer>
        <p>Universidad Mariano Gálvez — Compiladores 2026</p>
      </footer>
    </div>
  );
}

export default App;