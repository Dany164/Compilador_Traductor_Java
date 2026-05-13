import { useEffect, useReducer, useRef } from "react";
import { analizarTexto, traducirTextoLibre } from "../services/api";
import TokensTable from "./TokensTable";
import ErrorTable from "./ErrorTable";
import ASTView from "./ASTView";
import TablaSimbolosComponent from "./TablaSimbolosComponent";

const idiomas = [
  { value: "en", label: "Inglés", voz: "en-US" },
  { value: "es", label: "Español", voz: "es-ES" },
];

const Mic = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z" />
    <path d="M19 10v2a7 7 0 0 1-14 0v-2" />
    <line x1="12" y1="19" x2="12" y2="23" />
    <line x1="8" y1="23" x2="16" y2="23" />
  </svg>
);

const Trash2 = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="3 6 5 6 21 6" />
    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
    <line x1="10" y1="11" x2="10" y2="17" />
    <line x1="14" y1="11" x2="14" y2="17" />
  </svg>
);

const Volume2 = () => (
  <svg width="21" height="21" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5" />
    <path d="M15.54 8.46a5 5 0 0 1 0 7.07" />
    <path d="M19.07 4.93a10 10 0 0 1 0 14.14" />
  </svg>
);

const CopyIcon = () => (
  <svg width="21" height="21" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <rect x="9" y="9" width="13" height="13" rx="2" ry="2" />
    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
  </svg>
);

const FileText = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
    <polyline points="14 2 14 8 20 8" />
    <line x1="12" y1="13" x2="12" y2="19" />
    <line x1="9" y1="16" x2="15" y2="16" />
  </svg>
);

const Search = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="11" cy="11" r="8" />
    <line x1="21" y1="21" x2="16.65" y2="16.65" />
  </svg>
);

const Brain = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 2c-1.657 0-3 1.343-3 3 0 .364.066.712.188 1.035C6.472 6.77 4 9.762 4 13.5 4 18.467 7.58 22 12 22s8-3.533 8-8.5c0-3.738-2.472-6.73-5.188-7.465.122-.323.188-.671.188-1.035 0-1.657-1.343-3-3-3z" />
    <path d="M8 14c0 .5-.5 1-1 1s-1-.5-1-1 .5-1 1-1 1 .5 1 1z" />
    <path d="M18 14c0 .5-.5 1-1 1s-1-.5-1-1 .5-1 1-1 1 .5 1 1z" />
    <path d="M12 17c-1 0-1.5.5-1.5 1s.5 1 1.5 1 1.5-.5 1.5-1-.5-1-1.5-1z" />
  </svg>
);

const TooltipButton = ({ icon: Icon, label, tooltip, onClick, className, disabled }) => (
  <div className="tooltip-wrapper">
    <button className={`tooltip-btn ${className || "btn-secondary"}`} onClick={onClick} disabled={disabled}>
      {Icon && <span className="btn-icon">{Icon}</span>}
      <span className="btn-label">{label}</span>
    </button>
    <div className="tooltip-popup">{tooltip}</div>
  </div>
);

const initialState = {
  textoEntrada: "",
  traduccion: "",
  tokens: [],
  errores: [],
  astJson: null,
  tablaSimbolos: [],
  tabActiva: "tokens",
  cargando: false,
  analizandoResultados: false,
  exitoso: null,
  usarIA: false,
  usoIA: false,
  escuchando: false,
  idiomaOrigen: "en",
  idiomaDestino: "es",
  idiomaVoz: "en-US",
  mensajeVoz: "",
  mensajeEstado: "",
  vozDisponible: true,
  mostrarResultados: false,
};

function vozPorIdioma(idioma) {
  return idiomas.find((item) => item.value === idioma)?.voz || "en-US";
}

function translatorReducer(state, action) {
  switch (action.type) {
    case "SET_TEXTO":
      return { ...state, textoEntrada: action.payload, mostrarResultados: false };
    case "SET_IDIOMA_ORIGEN":
      return {
        ...state,
        idiomaOrigen: action.payload,
        idiomaVoz: vozPorIdioma(action.payload),
        mostrarResultados: false,
      };
    case "SET_IDIOMA_DESTINO":
      return { ...state, idiomaDestino: action.payload, mostrarResultados: false };
    case "SWAP_IDIOMAS":
      return {
        ...state,
        idiomaOrigen: state.idiomaDestino,
        idiomaDestino: state.idiomaOrigen,
        idiomaVoz: vozPorIdioma(state.idiomaDestino),
        textoEntrada: state.traduccion || state.textoEntrada,
        traduccion: state.traduccion ? state.textoEntrada : "",
        mostrarResultados: false,
      };
    case "SET_TAB":
      return { ...state, tabActiva: action.payload };
    case "TOGGLE_USAR_IA":
      return { ...state, usarIA: !state.usarIA, mostrarResultados: false };
    case "SET_ESCUCHANDO":
      return { ...state, escuchando: action.payload };
    case "SET_MENSAJE_VOZ":
      return { ...state, mensajeVoz: action.payload };
    case "SET_MENSAJE_ESTADO":
      return { ...state, mensajeEstado: action.payload };
    case "SET_VOZ_DISPONIBLE":
      return { ...state, vozDisponible: action.payload };
    case "AUTO_START":
      return { ...state, cargando: true, mensajeEstado: "Traduciendo..." };
    case "AUTO_SUCCESS":
      return {
        ...state,
        cargando: false,
        traduccion: action.payload.traduccion || "",
        exitoso: action.payload.exitoso,
        usoIA: action.payload.provider === "google",
        mensajeEstado: action.payload.mensaje || "",
      };
    case "AUTO_ERROR":
      return {
        ...state,
        cargando: false,
        traduccion: "",
        exitoso: false,
        usoIA: false,
        mensajeEstado: action.payload,
      };
    case "ANALIZAR_START":
      return {
        ...state,
        analizandoResultados: true,
        tokens: [],
        errores: [],
        astJson: null,
        tablaSimbolos: [],
        mostrarResultados: true,
      };
    case "ANALIZAR_SUCCESS":
      return {
        ...state,
        analizandoResultados: false,
        tokens: action.payload.tokens || [],
        errores: action.payload.errores || [],
        astJson: action.payload.astJson || null,
        tablaSimbolos: action.payload.tablaSimbolos || [],
        traduccion: action.payload.traduccion || state.traduccion,
        exitoso: action.payload.exitoso,
        usoIA: action.payload.usoIA || state.usoIA,
        mostrarResultados: true,
        tabActiva: (action.payload.errores || []).length > 0 ? "errores" : "tokens",
      };
    case "ANALIZAR_ERROR":
      return {
        ...state,
        analizandoResultados: false,
        errores: [action.payload],
        exitoso: false,
        mostrarResultados: true,
        tabActiva: "errores",
      };
    case "LIMPIAR":
      return {
        ...state,
        textoEntrada: "",
        traduccion: "",
        tokens: [],
        errores: [],
        astJson: null,
        tablaSimbolos: [],
        exitoso: null,
        usoIA: false,
        mensajeVoz: "",
        mensajeEstado: "",
        mostrarResultados: false,
      };
    default:
      return state;
  }
}

function Translator() {
  const [state, dispatch] = useReducer(translatorReducer, initialState);
  const {
    textoEntrada,
    traduccion,
    tokens,
    errores,
    astJson,
    tablaSimbolos,
    tabActiva,
    cargando,
    analizandoResultados,
    usarIA,
    usoIA,
    escuchando,
    idiomaOrigen,
    idiomaDestino,
    idiomaVoz,
    mensajeVoz,
    mensajeEstado,
    vozDisponible,
    mostrarResultados,
  } = state;

  const fileInputRef = useRef(null);
  const recognitionRef = useRef(null);

  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    dispatch({ type: "SET_VOZ_DISPONIBLE", payload: Boolean(SpeechRecognition) });

    return () => {
      if (recognitionRef.current) {
        recognitionRef.current.abort();
        recognitionRef.current = null;
      }
    };
  }, []);

  useEffect(() => {
    const texto = textoEntrada.trim();

    if (!texto) {
      dispatch({ type: "LIMPIAR" });
      return undefined;
    }

    let activo = true;
    const timer = window.setTimeout(async () => {
      dispatch({ type: "AUTO_START" });
      try {
        const resultado = await traducirTextoLibre({
          texto,
          desde: idiomaOrigen,
          hacia: idiomaDestino,
          usarIA,
        });
        if (activo) {
          dispatch({ type: "AUTO_SUCCESS", payload: resultado });
        }
      } catch (error) {
        if (activo) {
          dispatch({
            type: "AUTO_ERROR",
            payload: "Backend no disponible. Inicia Spring Boot en localhost:8081.",
          });
        }
      }
    }, 450);

    return () => {
      activo = false;
      window.clearTimeout(timer);
    };
  }, [textoEntrada, idiomaOrigen, idiomaDestino, usarIA]);

  const handleResultados = async () => {
    if (!textoEntrada.trim()) return;
    dispatch({ type: "ANALIZAR_START" });
    try {
      const resultado = await analizarTexto(textoEntrada, usarIA);
      dispatch({ type: "ANALIZAR_SUCCESS", payload: resultado });
    } catch (error) {
      dispatch({
        type: "ANALIZAR_ERROR",
        payload: {
          tipo: "LEXICO",
          linea: 0,
          columna: 0,
          descripcion: "No se pudo conectar al backend. Ejecuta Spring Boot en localhost:8081.",
        },
      });
    }
  };

  const handleCargarArchivo = (event) => {
    const file = event.target.files[0];
    if (!file) return;
    if (!file.name.endsWith(".txt")) {
      alert("Por favor selecciona un archivo .txt");
      return;
    }

    const reader = new FileReader();
    reader.onload = (ev) => dispatch({ type: "SET_TEXTO", payload: ev.target.result });
    reader.onerror = () => alert("Error al leer el archivo");
    reader.readAsText(file, "UTF-8");
    event.target.value = "";
  };

  const hablarTexto = (texto, idioma) => {
    const contenido = (texto || "").trim();
    if (!contenido) return;

    if (!window.speechSynthesis || !window.SpeechSynthesisUtterance) {
      dispatch({ type: "SET_MENSAJE_ESTADO", payload: "Tu navegador no permite reproducir voz." });
      return;
    }

    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(contenido);
    utterance.lang = vozPorIdioma(idioma);
    utterance.rate = 0.95;
    utterance.pitch = 1;
    window.speechSynthesis.speak(utterance);
  };

  const copiarTraduccion = async () => {
    const contenido = traduccion.trim();
    if (!contenido) return;

    try {
      await navigator.clipboard.writeText(contenido);
      dispatch({ type: "SET_MENSAJE_ESTADO", payload: "Traduccion copiada." });
    } catch (error) {
      dispatch({ type: "SET_MENSAJE_ESTADO", payload: "No se pudo copiar la traduccion." });
    }
  };

  const handleVozProfesional = () => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;

    if (!SpeechRecognition) {
      dispatch({
        type: "SET_MENSAJE_VOZ",
        payload: "Reconocimiento de voz no disponible en este navegador.",
      });
      return;
    }

    if (escuchando && recognitionRef.current) {
      recognitionRef.current.stop();
      return;
    }

    const recognition = new SpeechRecognition();
    recognition.lang = idiomaVoz;
    recognition.interimResults = true;
    recognition.maxAlternatives = 1;
    recognition.continuous = false;
    recognitionRef.current = recognition;
    let huboError = false;

    recognition.onstart = () => {
      dispatch({ type: "SET_ESCUCHANDO", payload: true });
      dispatch({ type: "SET_MENSAJE_VOZ", payload: "Escuchando..." });
    };

    recognition.onresult = (event) => {
      let textoFinal = "";
      let textoParcial = "";

      for (let i = event.resultIndex; i < event.results.length; i += 1) {
        const fragmento = event.results[i][0].transcript.trim();
        if (event.results[i].isFinal) {
          textoFinal += `${fragmento} `;
        } else {
          textoParcial += fragmento;
        }
      }

      if (textoParcial) {
        dispatch({ type: "SET_MENSAJE_VOZ", payload: textoParcial });
      }

      const capturado = textoFinal.trim();
      if (capturado) {
        const base = textoEntrada.trim();
        const separador = base ? " " : "";
        dispatch({ type: "SET_TEXTO", payload: `${base}${separador}${capturado}`.trim() });
      }
    };

    recognition.onerror = (event) => {
      huboError = true;
      dispatch({ type: "SET_ESCUCHANDO", payload: false });

      const mensajes = {
        "not-allowed": "Permiso de microfono denegado.",
        "audio-capture": "No se detecto un microfono disponible.",
        "no-speech": "No se detecto voz.",
        network: "No se pudo conectar al servicio de voz.",
      };

      dispatch({
        type: "SET_MENSAJE_VOZ",
        payload: mensajes[event.error] || "No se pudo usar el microfono.",
      });
    };

    recognition.onend = () => {
      dispatch({ type: "SET_ESCUCHANDO", payload: false });
      recognitionRef.current = null;
      if (!huboError) {
        dispatch({ type: "SET_MENSAJE_VOZ", payload: "" });
      }
    };

    try {
      recognition.start();
    } catch (error) {
      dispatch({ type: "SET_ESCUCHANDO", payload: false });
      dispatch({ type: "SET_MENSAJE_VOZ", payload: "El microfono ya esta iniciando." });
    }
  };

  const hayResultados = tokens.length > 0 || errores.length > 0 || tablaSimbolos.length > 0 || astJson;

  return (
    <div className="translator-wrapper">
      <div className="panels">
        <div className="panel panel-input">
          <div className="panel-header">
            <select
              className="language-select"
              value={idiomaOrigen}
              onChange={(event) => dispatch({ type: "SET_IDIOMA_ORIGEN", payload: event.target.value })}
            >
              {idiomas.map((idioma) => (
                <option key={idioma.value} value={idioma.value}>{idioma.label}</option>
              ))}
            </select>
            <button className="panel-clear" type="button" onClick={() => dispatch({ type: "LIMPIAR" })} aria-label="Limpiar texto">
              <Trash2 />
            </button>
          </div>

          <div className="text-surface input-surface">
            <textarea
              value={textoEntrada}
              onChange={(event) => dispatch({ type: "SET_TEXTO", payload: event.target.value })}
              placeholder="Escribe o habla para traducir..."
              rows={6}
            />
            <div className="panel-tools">
              <button
                className={`panel-tool ${escuchando ? "is-recording" : ""}`}
                type="button"
                onClick={handleVozProfesional}
                disabled={!vozDisponible}
                aria-label={escuchando ? "Detener voz" : "Usar microfono"}
              >
                <Mic />
              </button>
              <button
                className="panel-tool"
                type="button"
                onClick={() => hablarTexto(textoEntrada, idiomaOrigen)}
                disabled={!textoEntrada.trim()}
                aria-label="Escuchar texto ingresado"
              >
                <Volume2 />
              </button>
              {mensajeVoz && <span className="inline-status">{mensajeVoz}</span>}
            </div>
          </div>
        </div>

        <div className="middle-column">
          <button className="swap-button" type="button" onClick={() => dispatch({ type: "SWAP_IDIOMAS" })} aria-label="Intercambiar idiomas">
            <span aria-hidden="true">&#8644;</span>
          </button>
        </div>

        <div className="panel panel-output">
          <div className="panel-header">
            <select
              className="language-select"
              value={idiomaDestino}
              onChange={(event) => dispatch({ type: "SET_IDIOMA_DESTINO", payload: event.target.value })}
            >
              {idiomas.map((idioma) => (
                <option key={idioma.value} value={idioma.value}>{idioma.label}</option>
              ))}
            </select>
            {usoIA && <span className="ia-badge">IA</span>}
          </div>

          <div className="text-surface output-surface">
            <div className={`output-box ${!traduccion ? "is-empty" : ""}`}>
              {cargando
                ? "Traduciendo..."
          : traduccion || mensajeEstado || "La traducción aparecerá aquí"}
            </div>
            {mensajeEstado && !cargando && (
              <p className="translation-note">{mensajeEstado}</p>
            )}
            <div className="panel-tools output-tools">
              <button
                className="panel-tool"
                type="button"
                onClick={() => hablarTexto(traduccion, idiomaDestino)}
                disabled={!traduccion.trim()}
                aria-label="Escuchar traduccion"
              >
                <Volume2 />
              </button>
              <button
                className="panel-tool"
                type="button"
                onClick={copiarTraduccion}
                disabled={!traduccion.trim()}
                aria-label="Copiar traduccion"
              >
                <CopyIcon />
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="buttons">
        <TooltipButton
          icon={<Search />}
          label={analizandoResultados ? "Analizando..." : "Resultados"}
          tooltip="Muestra tokens, errores, AST y simbolos"
          onClick={handleResultados}
          disabled={analizandoResultados || !textoEntrada.trim()}
          className="btn-primary"
        />

        <div className="tooltip-wrapper">
          <button className="tooltip-btn btn-secondary" type="button" onClick={() => fileInputRef.current.click()}>
            <span className="btn-icon"><FileText /></span>
            <span className="btn-label">Cargar</span>
          </button>
          <input ref={fileInputRef} type="file" accept=".txt" onChange={handleCargarArchivo} />
          <div className="tooltip-popup">Carga un archivo de texto</div>
        </div>

        <div className="tooltip-wrapper">
          <button
            className={`tooltip-btn ${usarIA ? "btn-google" : "btn-secondary"}`}
            type="button"
            aria-pressed={usarIA}
            aria-label={usarIA ? "Desactivar IA" : "Activar IA"}
            onClick={() => dispatch({ type: "TOGGLE_USAR_IA" })}
          >
            <span className="btn-icon"><Brain /></span>
            <span className="btn-label">{usarIA ? "IA activa" : "Local"}</span>
          </button>
          <div className="tooltip-popup">Alterna Google Translate o diccionario local</div>
        </div>
      </div>

      {mostrarResultados && hayResultados && (
        <div className="tabs-section">
          <div className="tabs">
            <button className={tabActiva === "tokens" ? "tab active" : "tab"} onClick={() => dispatch({ type: "SET_TAB", payload: "tokens" })}>
              Tokens ({tokens.length})
            </button>
            <button className={tabActiva === "errores" ? "tab active" : "tab"} onClick={() => dispatch({ type: "SET_TAB", payload: "errores" })}>
              Errores ({errores.length})
            </button>
            <button className={tabActiva === "ast" ? "tab active" : "tab"} onClick={() => dispatch({ type: "SET_TAB", payload: "ast" })}>
              AST
            </button>
            <button className={tabActiva === "simbolos" ? "tab active" : "tab"} onClick={() => dispatch({ type: "SET_TAB", payload: "simbolos" })}>
              Simbolos ({tablaSimbolos.length})
            </button>
          </div>

          <div className="tab-content">
            {tabActiva === "tokens" && <TokensTable tokens={tokens} />}
            {tabActiva === "errores" && <ErrorTable errores={errores} />}
            {tabActiva === "ast" && <ASTView astJson={astJson} />}
            {tabActiva === "simbolos" && <TablaSimbolosComponent tablaSimbolos={tablaSimbolos} />}
          </div>
        </div>
      )}
    </div>
  );
}

export default Translator;
