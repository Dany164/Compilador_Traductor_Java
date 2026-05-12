import { useReducer, useRef } from "react";
import { analizarTexto } from "../services/api";
import TokensTable from "./TokensTable";
import ErrorTable from "./ErrorTable";
import ASTView from "./ASTView";
import TablaSimbolosComponent from "./TablaSimbolosComponent";

// ── Iconos SVG ────────────────────────────────────────────────────────────────
const Mic = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 1a3 3 0 0 0-3 3v12a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"></path>
    <path d="M19 10v2a7 7 0 0 1-14 0v-2"></path>
    <line x1="12" y1="19" x2="12" y2="23"></line>
    <line x1="8"  y1="23" x2="16" y2="23"></line>
  </svg>
);

const Trash2 = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="3 6 5 6 21 6"></polyline>
    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
    <line x1="10" y1="11" x2="10" y2="17"></line>
    <line x1="14" y1="11" x2="14" y2="17"></line>
  </svg>
);

const FileText = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
    <polyline points="14 2 14 8 20 8"></polyline>
    <line x1="12" y1="13" x2="12" y2="19"></line>
    <line x1="9"  y1="16" x2="15" y2="16"></line>
  </svg>
);

const Search = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="11" cy="11" r="8"></circle>
    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
  </svg>
);

const Brain = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 2c-1.657 0-3 1.343-3 3 0 .364.066.712.188 1.035C6.472 6.77 4 9.762 4 13.5 4 18.467 7.58 22 12 22s8-3.533 8-8.5c0-3.738-2.472-6.73-5.188-7.465.122-.323.188-.671.188-1.035 0-1.657-1.343-3-3-3z"></path>
    <path d="M8 14c0 .5-.5 1-1 1s-1-.5-1-1 .5-1 1-1 1 .5 1 1z"></path>
    <path d="M18 14c0 .5-.5 1-1 1s-1-.5-1-1 .5-1 1-1 1 .5 1 1z"></path>
    <path d="M12 17c-1 0-1.5.5-1.5 1s.5 1 1.5 1 1.5-.5 1.5-1-.5-1-1.5-1z"></path>
  </svg>
);

// ── Tooltip Button ─────────────────────────────────────────────────────────────
const TooltipButton = ({ icon: Icon, label, tooltip, onClick, className, disabled, style }) => (
  <div className="tooltip-wrapper">
    <button
      className={`tooltip-btn ${className || "btn-secondary"}`}
      onClick={onClick}
      disabled={disabled}
      style={style}
    >
      {Icon && <span className="btn-icon">{Icon}</span>}
      <span className="btn-label">{label}</span>
    </button>
    <div className="tooltip-popup">{tooltip}</div>
  </div>
);

// ── Reducer ────────────────────────────────────────────────────────────────────
// Fix: 13 useState consolidados en un único useReducer para evitar re-renders
// en cascada y mantener el estado relacionado agrupado.
const initialState = {
  textoEntrada:  "",
  traduccion:    "",
  tokens:        [],
  errores:       [],
  astJson:       null,
  tablaSimbolos: [],
  tabActiva:     "tokens",
  cargando:      false,
  exitoso:       null,
  usarIA:        false,
  usoIA:         false,
  escuchando:    false,
  idiomaVoz:     "en-US",
};

function translatorReducer(state, action) {
  switch (action.type) {
    case "SET_TEXTO":
      return { ...state, textoEntrada: action.payload };
    case "SET_TAB":
      return { ...state, tabActiva: action.payload };
    case "SET_IDIOMA_VOZ":
      return { ...state, idiomaVoz: action.payload };
    case "TOGGLE_USAR_IA":
      return { ...state, usarIA: !state.usarIA };
    case "SET_ESCUCHANDO":
      return { ...state, escuchando: action.payload };
    case "ANALIZAR_START":
      return {
        ...state,
        cargando:      true,
        traduccion:    "",
        tokens:        [],
        errores:       [],
        astJson:       null,
        tablaSimbolos: [],
        exitoso:       null,
        usoIA:         false,
      };
    case "ANALIZAR_SUCCESS":
      return {
        ...state,
        cargando:      false,
        tokens:        action.payload.tokens        || [],
        errores:       action.payload.errores       || [],
        traduccion:    action.payload.traduccion    || "",
        astJson:       action.payload.astJson       || null,
        tablaSimbolos: action.payload.tablaSimbolos || [],
        exitoso:       action.payload.exitoso,
        usoIA:         action.payload.usoIA         || false,
      };
    case "ANALIZAR_ERROR":
      return {
        ...state,
        cargando: false,
        errores:  [action.payload],
        exitoso:  false,
      };
    case "LIMPIAR":
      return {
        ...state,
        textoEntrada:  "",
        traduccion:    "",
        tokens:        [],
        errores:       [],
        astJson:       null,
        tablaSimbolos: [],
        exitoso:       null,
        usoIA:         false,
      };
    default:
      return state;
  }
}

// ── Componente principal ───────────────────────────────────────────────────────
function Translator() {
  const [state, dispatch] = useReducer(translatorReducer, initialState);
  const {
    textoEntrada, traduccion, tokens, errores, astJson,
    tablaSimbolos, tabActiva, cargando, exitoso,
    usarIA, usoIA, escuchando, idiomaVoz,
  } = state;

  const fileInputRef = useRef(null);

  // ── Handlers ───────────────────────────────────────────────────────────────
  const handleAnalizar = async () => {
    if (!textoEntrada.trim()) return;
    dispatch({ type: "ANALIZAR_START" });
    try {
      const resultado = await analizarTexto(textoEntrada, usarIA);
      console.log("Respuesta completa:", resultado);
      console.log("astJson recibido:",   resultado.astJson);
      console.log("tablaSimbolos:",      resultado.tablaSimbolos);
      console.log("usoIA:",              resultado.usoIA);
      dispatch({ type: "ANALIZAR_SUCCESS", payload: resultado });
    } catch (error) {
      dispatch({
        type:    "ANALIZAR_ERROR",
        payload: {
          tipo:        "LEXICO",
          linea:       0,
          columna:     0,
          descripcion: "No se pudo conectar al backend: " + error.message,
        },
      });
    }
  };

  const handleCargarArchivo = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    if (!file.name.endsWith(".txt")) {
      alert("Por favor selecciona un archivo .txt");
      return;
    }
    const reader = new FileReader();
    reader.onload  = (ev) => {
      dispatch({ type: "SET_TEXTO", payload: ev.target.result });
      console.log("📁 Archivo cargado:", file.name);
    };
    reader.onerror = () => alert("Error al leer el archivo");
    reader.readAsText(file, "UTF-8");
  };

  const handleVoz = () => {
    const SpeechRecognition =
      window.SpeechRecognition || window.webkitSpeechRecognition;

    if (!SpeechRecognition) {
      alert("Tu navegador no soporta reconocimiento de voz. Usa Edge o Chrome.");
      return;
    }

    if (escuchando) return;

    const recognition = new SpeechRecognition();
    recognition.lang = idiomaVoz;
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;
    recognition.continuous = false; // Chrome bloquea continuous=true en localhost (HTTP)

    recognition.onstart = () => {
      dispatch({ type: "SET_ESCUCHANDO", payload: true });
    };

    recognition.onresult = (event) => {
      const textoHablado = event.results[0][0].transcript;
      dispatch({ type: "SET_TEXTO", payload: textoHablado });
    };

    recognition.onerror = (event) => {
      console.error("Error de voz:", event.error);
      dispatch({ type: "SET_ESCUCHANDO", payload: false });
      if (event.error === "not-allowed") {
        alert("Permiso de micrófono denegado. Habilítalo en tu navegador.");
      }
    };

    recognition.onend = () => {
      dispatch({ type: "SET_ESCUCHANDO", payload: false });
    };

    try {
      recognition.start();
    } catch (e) {
      console.error("Error al iniciar micrófono:", e);
      dispatch({ type: "SET_ESCUCHANDO", payload: false });
    }
  };

  // ── Render ─────────────────────────────────────────────────────────────────
  return (
    <div className="translator-wrapper">

      {/* ── Paneles de traducción ── */}
      <div className="panels">

        <div className="panel">
          <div className="panel-header">🇺🇸 Inglés / 🇪🇸 Español (entrada)</div>
          <textarea
            value={textoEntrada}
            onChange={(e) => dispatch({ type: "SET_TEXTO", payload: e.target.value })}
            placeholder="Escribe o habla tu oración en inglés o español..."
            rows={6}
          />
        </div>

        <div className="panel-arrow">→</div>

        <div className="panel">
          <div className="panel-header">
            {exitoso === true  && "✅ "}
            {exitoso === false && "❌ "}
            {exitoso === true
              ? "🇪🇸 Español / 🇺🇸 Inglés (traducción)"
              : "Resultado"}
            {usoIA && (
              <span style={{
                marginLeft:      "10px",
                backgroundColor: "#6366f1",
                color:           "white",
                padding:         "2px 8px",
                borderRadius:    "12px",
                fontSize:        "12px",
                fontWeight:      "bold",
              }}>
                🤖 IA
              </span>
            )}
          </div>
          <div className="output-box">
            {cargando
              ? "Analizando..."
              : traduccion
              ? traduccion
              : exitoso === false
              ? "Se encontraron errores. Revisa la tabla de errores."
              : "La traducción aparecerá aquí"}
          </div>
        </div>
      </div>

      {/* ── Botones ── */}
      <div className="buttons">

        <TooltipButton
          icon={<Search />}
          label="Traducir"
          tooltip="Traduce y analiza el texto ingresado"
          onClick={handleAnalizar}
          disabled={cargando}
          className="btn-primary"
          style={{ opacity: cargando ? 0.7 : 1 }}
        />

        <div className="tooltip-wrapper">
          <button
            className="tooltip-btn btn-secondary"
            onClick={() => fileInputRef.current.click()}
          >
            <span className="btn-icon"><FileText /></span>
            <span className="btn-label">Cargar</span>
          </button>
          <input
            ref={fileInputRef}
            type="file"
            accept=".txt"
            onChange={handleCargarArchivo}
            style={{ display: "none" }}
          />
          <div className="tooltip-popup">Carga un archivo de texto</div>
        </div>

        {/* Micrófono */}
        <TooltipButton
          icon={<Mic />}
          label="Voz"
          tooltip="Reconoce tu voz para traducir"
          onClick={handleVoz}
          disabled={escuchando || cargando}
          className="btn-secondary"
          style={{
            background: escuchando
              ? "linear-gradient(135deg, #ef4444 0%, #c0392b 100%)"
              : "linear-gradient(135deg, #2a3144 0%, #252e3e 100%)",
            color:     escuchando ? "white" : "#ddd",
            // Fix: transition específico en lugar de "all"
            transition: "background 300ms ease, color 300ms ease, box-shadow 300ms ease",
            boxShadow:  escuchando
              ? "0 4px 12px rgba(239,68,68,0.3)"
              : "0 2px 8px rgba(0,0,0,0.2)",
          }}
        />

        {/* Selector idioma de voz */}
        <select
          value={idiomaVoz}
          onChange={(e) => dispatch({ type: "SET_IDIOMA_VOZ", payload: e.target.value })}
          disabled={escuchando}
          style={{
            padding:      "10px 14px",
            borderRadius: "10px",
            border:       "1px solid #3a4558",
            fontSize:     "0.95rem",
            cursor:       "pointer",
            background:   "linear-gradient(135deg, #2a3144 0%, #252e3e 100%)",
            color:        "#ddd",
            fontWeight:   "500",
            // Fix: transition específico en lugar de "all"
            transition:   "box-shadow 300ms ease, border-color 300ms ease",
            boxShadow:    "0 2px 8px rgba(0,0,0,0.2)",
          }}
          onMouseEnter={(e) => {
            if (!escuchando) {
              e.currentTarget.style.cssText +=
                "; box-shadow: 0 4px 12px rgba(52,152,219,0.3); border-color: #3498db";
            }
          }}
          onMouseLeave={(e) => {
            // Fix: batch de escrituras con cssText para evitar layout thrashing
            e.currentTarget.style.cssText +=
              "; box-shadow: 0 2px 8px rgba(0,0,0,0.2); border-color: #3a4558";
          }}
        >
          <option value="en-US">🇺🇸 English</option>
          <option value="es-ES">🇪🇸 Español</option>
        </select>

        <TooltipButton
          icon={<Trash2 />}
          label="Limpiar"
          tooltip="Borra todo el contenido"
          onClick={() => dispatch({ type: "LIMPIAR" })}
          className="btn-secondary"
        />

        {/* ── Botón IA ──
            Fix a11y: role="button" + tabIndex + onKeyDown para navegación por teclado */}
        <div
          className="tooltip-wrapper"
          role="button"
          tabIndex={0}
          aria-pressed={usarIA}
          aria-label={usarIA ? "Desactivar IA" : "Activar IA"}
          onClick={() => dispatch({ type: "TOGGLE_USAR_IA" })}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") {
              e.preventDefault();
              dispatch({ type: "TOGGLE_USAR_IA" });
            }
          }}
          style={{ cursor: "pointer" }}
        >
          <button
            className={`tooltip-btn ${usarIA ? "btn-primary" : "btn-secondary"}`}
            tabIndex={-1}
            style={{
              background: usarIA
                ? "linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%)"
                : "linear-gradient(135deg, #2a3144 0%, #252e3e 100%)",
              color:     usarIA ? "#fff" : "#ddd",
              // Fix: transition específico en lugar de "all"
              transition: "background 300ms ease, color 300ms ease, box-shadow 300ms ease",
              boxShadow:  usarIA
                ? "0 4px 12px rgba(155,89,182,0.3)"
                : "0 2px 8px rgba(0,0,0,0.2)",
            }}
          >
            <span className="btn-icon"><Brain /></span>
            <span className="btn-label">IA</span>
          </button>
          <div className="tooltip-popup">Usa inteligencia artificial para mejorar la traducción</div>
        </div>

      </div>

      {/* ── Tabs ── */}
      {(tokens.length > 0 || errores.length > 0 || tablaSimbolos.length > 0) && (
        <div className="tabs-section">

          <div className="tabs">
            <button
              className={tabActiva === "tokens"   ? "tab active" : "tab"}
              onClick={() => dispatch({ type: "SET_TAB", payload: "tokens" })}
            >
              📋 Tokens ({tokens.length})
            </button>
            <button
              className={tabActiva === "errores"  ? "tab active" : "tab"}
              onClick={() => dispatch({ type: "SET_TAB", payload: "errores" })}
            >
              Errores ({errores.length})
            </button>
            <button
              className={tabActiva === "ast"      ? "tab active" : "tab"}
              onClick={() => dispatch({ type: "SET_TAB", payload: "ast" })}
            >
              🌳 AST
            </button>
            <button
              className={tabActiva === "simbolos" ? "tab active" : "tab"}
              onClick={() => dispatch({ type: "SET_TAB", payload: "simbolos" })}
            >
              📖 Símbolos ({tablaSimbolos.length})
            </button>
          </div>

          <div className="tab-content">
            {tabActiva === "tokens"   && <TokensTable           tokens={tokens} />}
            {tabActiva === "errores"  && <ErrorTable            errores={errores} />}
            {tabActiva === "ast"      && <ASTView               astJson={astJson} />}
            {tabActiva === "simbolos" && (
              <TablaSimbolosComponent tablaSimbolos={tablaSimbolos} />
            )}
          </div>

        </div>
      )}
    </div>
  );
}

export default Translator;