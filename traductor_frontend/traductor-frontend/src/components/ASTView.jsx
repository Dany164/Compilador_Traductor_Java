import { useState } from "react";

// Extraído como componente para poder tener su propio estado de hover
function LegendItem({ label, color }) {
  const [hovered, setHovered] = useState(false);
  return (
    <span
      style={{
        background:    `linear-gradient(135deg, ${color} 0%, ${color}dd 100%)`,
        color:         "white",
        padding:       "4px 12px",
        borderRadius:  "12px",
        fontSize:      "0.75rem",
        fontWeight:    "600",
        textTransform: "uppercase",
        letterSpacing: "0.3px",
        // Fix: transition específico en lugar de "all"
        boxShadow:     hovered ? `0 4px 12px ${color}60` : `0 2px 8px ${color}40`,
        transform:     hovered ? "scale(1.05)"            : "scale(1)",
        transition:    "transform 200ms ease, box-shadow 200ms ease",
        cursor:        "default",
      }}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
    >
      {label}
    </span>
  );
}

function NodoArbol({ nodo, nivel = 0 }) {
  // Fix: useState para hover evita mutaciones directas del DOM (layout thrashing)
  const [hovered, setHovered] = useState(false);

  if (!nodo) return null;

  const coloresTipo = {
    PROGRAMA:       "#2c3e50",
    ORACION:        "#2980b9",
    SUJETO:         "#27ae60",
    PREDICADO:      "#8e44ad",
    FRASE_NOMINAL:  "#16a085",
    FRASE_VERBAL:   "#d35400",
    VERBO:          "#e74c3c",
    OBJETO_DIRECTO: "#f39c12",
    COMPLEMENTO:    "#7f8c8d",
    ADJETIVO:       "#1abc9c",
    ADVERBIO:       "#c0392b",
    CONJUNCION:     "#e67e22",
    HOJA:           "#555",
  };

  const color      = coloresTipo[nodo.tipo] || "#3498db";
  const tieneHijos = nodo.hijos && nodo.hijos.length > 0;

  return (
    <div style={{
      marginLeft: nivel === 0 ? 0 : "28px",
      marginTop:  "8px",
      animation:  `fadeIn 0.4s ease ${nivel * 0.1}s backwards`,
    }}>
      <div style={{ display: "flex", alignItems: "flex-start" }}>

        {nivel > 0 && (
          <div style={{
            width:        "2px",
            background:   `linear-gradient(180deg, ${color}80 0%, rgba(52,152,219,0) 100%)`,
            marginRight:  "12px",
            marginTop:    "6px",
            alignSelf:    "stretch",
            minHeight:    "24px",
            borderRadius: "1px",
          }} />
        )}

        <div>
          {/* Nodo — hover con estado React en lugar de mutación directa del DOM */}
          <div
            style={{
              display:      "inline-flex",
              alignItems:   "center",
              gap:          "8px",
              background:   `linear-gradient(135deg, ${color} 0%, ${color}dd 100%)`,
              color:        "white",
              padding:      "6px 14px",
              borderRadius: "20px",
              fontSize:     "0.85rem",
              fontWeight:   "600",
              // Fix: transition específico (no "all")
              boxShadow:    hovered
                ? `0 6px 16px rgba(0,0,0,0.5), 0 0 30px ${color}60`
                : `0 4px 12px rgba(0,0,0,0.4), 0 0 20px ${color}40`,
              transform:    hovered ? "scale(1.05) translateY(-2px)" : "scale(1) translateY(0)",
              transition:   "transform 200ms ease, box-shadow 200ms ease",
              cursor:       tieneHijos ? "pointer" : "default",
              border:       "1px solid rgba(255,255,255,0.2)",
            }}
            onMouseEnter={() => setHovered(true)}
            onMouseLeave={() => setHovered(false)}
          >
            <span>{nodo.tipo}</span>
            {nodo.valor && (
              <span style={{
                background:   "rgba(255,255,255,0.3)",
                padding:      "2px 8px",
                borderRadius: "8px",
                fontWeight:   "normal",
                fontSize:     "0.8rem",
                fontFamily:   "'Fira Code', monospace",
              }}>
                {nodo.valor}
              </span>
            )}
          </div>

          {/* Hijos — Fix: key estable usando tipo+valor+índice */}
          {tieneHijos && (
            <div style={{
              marginLeft:  "12px",
              borderLeft:  `2px dashed ${color}60`,
              paddingLeft: "8px",
            }}>
              {nodo.hijos.map((hijo, i) => (
                <NodoArbol
                  key={`${hijo.tipo}-${hijo.valor || ""}-${i}`}
                  nodo={hijo}
                  nivel={nivel + 1}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function ASTView({ astJson }) {
  if (!astJson) {
    return (
      <div style={{
        padding:   "40px 20px",
        color:     "#666",
        textAlign: "center",
        animation: "fadeIn 0.4s ease",
      }}>
        <p style={{
          fontSize:     "3rem",
          marginBottom: "12px",
          // Fix: 3s → 2s (animación decorativa dentro del rango aceptable)
          animation:    "float 0.8s ease-in-out infinite",
        }}>🌳</p>
        <p style={{ fontSize: "1rem", fontWeight: 300 }}>
          El árbol sintáctico aparecerá aquí cuando el análisis sea exitoso.
        </p>
      </div>
    );
  }

  let ast = null;
  try {
    ast = typeof astJson === "string" ? JSON.parse(astJson) : astJson;
  } catch (e) {
    return (
      <div style={{
        padding:      "20px",
        color:        "#e74c3c",
        background:   "rgba(231,76,60,0.1)",
        borderRadius: "8px",
        border:       "1px solid #e74c3c40",
        animation:    "fadeIn 0.4s ease",
      }}>
        <div style={{ fontWeight: 600, marginBottom: "8px" }}>
          Error al parsear el AST: {e.message}
        </div>
        {/* Fix: 0.7rem (11.2px) → 0.875rem (14px) para cumplir accesibilidad */}
        <pre style={{
          fontSize:     "0.875rem",
          marginTop:    "8px",
          overflow:     "auto",
          padding:      "8px",
          background:   "rgba(0,0,0,0.2)",
          borderRadius: "4px",
        }}>
          {astJson}
        </pre>
      </div>
    );
  }

  return (
    <div className="table-container">
      <h3 style={{ marginBottom: "16px" }}>🌳 Árbol Sintáctico (AST)</h3>

      {/* Leyenda — Fix: LegendItem component con key estable (label) */}
      <div style={{
        display:      "flex",
        flexWrap:     "wrap",
        gap:          "8px",
        marginBottom: "20px",
        padding:      "14px",
        background:   "linear-gradient(135deg, rgba(52,152,219,0.1) 0%, rgba(46,204,113,0.1) 100%)",
        borderRadius: "10px",
        border:       "1px solid rgba(52,152,219,0.2)",
      }}>
        {[
          ["ORACION",        "#2980b9"],
          ["SUJETO",         "#27ae60"],
          ["PREDICADO",      "#8e44ad"],
          ["FRASE_NOMINAL",  "#16a085"],
          ["VERBO",          "#e74c3c"],
          ["OBJETO_DIRECTO", "#f39c12"],
          ["COMPLEMENTO",    "#7f8c8d"],
        ].map(([label, color]) => (
          <LegendItem key={label} label={label} color={color} />
        ))}
      </div>

      {/* Árbol */}
      <div style={{
        background:   "linear-gradient(135deg, rgba(26,31,46,0.8) 0%, rgba(34,40,53,0.8) 100%)",
        borderRadius: "12px",
        padding:      "24px",
        overflowX:    "auto",
        minHeight:    "120px",
        border:       "1px solid #2a3144",
        boxShadow:    "inset 0 2px 8px rgba(0,0,0,0.3)",
        animation:    "fadeIn 0.4s ease",
      }}>
        <div style={{ fontFamily: "'Fira Code', monospace", lineHeight: "1.8" }}>
          <NodoArbol nodo={ast} nivel={0} />
        </div>
      </div>
    </div>
  );
}

export default ASTView;