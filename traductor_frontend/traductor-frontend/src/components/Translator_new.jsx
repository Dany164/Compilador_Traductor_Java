import { useReducer } from "react";
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

// ── Reducer (mismo patrón que Translator.jsx) ──────────────────────────────────
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
    const reader = new FileReader();
    reader.onload = (ev) => dispatch({ type: "SET_TEXTO", payload: ev.target.result });
    reader.readAsText(file);
  };

  const handleVoz = () => {
    const SpeechRecognition =
      window.SpeechRecognition || window.webkitSpeechRecognition;

    if (!SpeechRecognition) {
      alert("Tu navegador no soporta reconocimiento de voz. Usa Edge o Chrome.");
      return;
    }

    if (escuchando) return; // Evitar múltiples instancias

    const recognition = new SpeechRecognition();
    recognition.lang = idiomaVoz;
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;
    recognition.continuous = false;

    recognition.onstart = () => {
      dispatch({ type: "SET_ESCUCHANDO", payload: true });
    };

    recognition.onresult = (event) => {
      const textoHablado = event.results[0][0].transcript;
      dispatch({ type: "SET_TEXTO", payload: textoHablado });
      // Opcional: Analizar automáticamente después de hablar
      // setTimeout(handleAnalizar, 500); 
    };

    recognition.onerror = (event) => {
      console.error("Error de voz:", event.error);
      dispatch({ type: "SET_ESCUCHANDO", payload: false });
      
      const errorMessages = {
        "not-allowed": "Permiso de micrófono denegado. Habilítalo en la configuración de tu navegador.",
        "no-speech": "No se detectó voz. Intenta de nuevo.",
        "network": "Error de red en el reconocimiento de voz."
      };
      
      if (errorMessages[event.error]) {
        alert(errorMessages[event.error]);
      }
    };

    recognition.onend = () => {
      dispatch({ type: "SET_ESCUCHANDO", payload: false });
    };

    try {
      recognition.start();
    } catch (e) {
      console.error("Error al iniciar recognition:", e);
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

        {/* Cargar archivo con label (sin useRef) */}
        <label className="tooltip-wrapper">
          <button className="tooltip-btn btn-secondary" type="button">
            <span className="btn-icon"><FileText /></span>
            <span className="btn-label">Cargar .txt</span>
          </button>
          <input
            type="file"
            accept=".txt"
            onChange={handleCargarArchivo}
            style={{ display: "none" }}
          />
          <div className="tooltip-popup">Carga un archivo de texto para analizar</div>
        </label>

        {/* Selector de idioma para voz */}
        <div className="tooltip-wrapper">
          <select 
            className="tooltip-btn btn-secondary" 
            value={idiomaVoz}
            onChange={(e) => dispatch({ type: "SET_IDIOMA_VOZ", payload: e.target.value })}
            style={{ padding: "8px", borderRadius: "8px", height: "100%" }}
          >
            <option value="en-US">🇺🇸 EN</option>
            <option value="es-ES">🇪🇸 ES</option>
          </select>
          <div className="tooltip-popup">Idioma para el micrófono</div>
        </div>

        {/* Micrófono */}
        <TooltipButton
          icon={<Mic />}
          label={escuchando ? "Escuchando..." : "Voz"}
          tooltip="Reconoce tu voz para traducir"
          onClick={handleVoz}
          disabled={escuchando || cargando}
          className={escuchando ? "btn-danger pulse" : "btn-secondary"}
          style={escuchando ? { 
            backgroundColor: "#ef4444", 
            borderColor: "#ef4444", 
            color: "white",
            animation: "pulse 1.5s infinite"
          } : {}}
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
            // Fix: transition específico
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

        {/* Checkbox IA - versión accesible con label semántico */}
        <label
          title="Usa inteligencia artificial para mejorar la traducción"
          style={{
            display:      "flex",
            alignItems:   "center",
            gap:          "10px",
            cursor:       "pointer",
            fontSize:     "0.95rem",
            userSelect:   "none",
            padding:      "6px 12px",
            borderRadius: "8px",
            background:   usarIA ? "rgba(52,152,219,0.15)" : "rgba(52,152,219,0.1)",
            border:       usarIA
              ? "1px solid rgba(52,152,219,0.4)"
              : "1px solid rgba(52,152,219,0.2)",
            // Fix: transition específico
            transition:   "background 300ms ease, border-color 300ms ease",
          }}
        >
          <input
            type="checkbox"
            checked={usarIA}
            onChange={(e) => dispatch({ type: "TOGGLE_USAR_IA" })}
            style={{ cursor: "pointer", width: "18px", height: "18px" }}
          />
          🤖 Usar IA
        </label>

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
