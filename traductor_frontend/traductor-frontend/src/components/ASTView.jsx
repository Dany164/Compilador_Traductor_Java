import { useState } from "react";

const COLORES_TIPO = {
  PROGRAMA: "#2c3e50",
  ORACION: "#2980b9",
  SUJETO: "#27ae60",
  PREDICADO: "#8e44ad",
  FRASE_NOMINAL: "#16a085",
  FRASE_VERBAL: "#d35400",
  VERBO: "#e74c3c",
  OBJETO_DIRECTO: "#f39c12",
  COMPLEMENTO: "#7f8c8d",
  ADJETIVO: "#1abc9c",
  ADVERBIO: "#c0392b",
  CONJUNCION: "#e67e22",
  HOJA: "#555",
};

function LegendItem({ label, color }) {
  const [hovered, setHovered] = useState(false);

  return (
    <span
      style={{
        background: `linear-gradient(135deg, ${color} 0%, ${color}dd 100%)`,
        color: "white",
        padding: "4px 12px",
        borderRadius: "12px",
        fontSize: "0.75rem",
        fontWeight: "600",
        textTransform: "uppercase",
        letterSpacing: "0.3px",
        boxShadow: hovered ? `0 4px 12px ${color}60` : `0 2px 8px ${color}40`,
        transform: hovered ? "scale(1.05)" : "scale(1)",
        transition: "transform 200ms ease, box-shadow 200ms ease",
        cursor: "default",
      }}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
    >
      {label}
    </span>
  );
}

function NodoVisual({ nodo, color, tieneHijos }) {
  const [hovered, setHovered] = useState(false);

  return (
    <div
      className="ast-node-pill"
      style={{
        background: `linear-gradient(135deg, ${color} 0%, ${color}dd 100%)`,
        boxShadow: hovered
          ? `0 6px 16px rgba(0,0,0,0.5), 0 0 30px ${color}60`
          : `0 4px 12px rgba(0,0,0,0.4), 0 0 20px ${color}40`,
        transform: hovered ? "scale(1.05) translateY(-2px)" : "scale(1) translateY(0)",
        cursor: tieneHijos ? "pointer" : "default",
      }}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
    >
      <span>{nodo.tipo}</span>
      {nodo.valor && <span className="ast-node-value">{nodo.valor}</span>}
    </div>
  );
}

function NodoArbol({ nodo, nivel = 0 }) {
  if (!nodo) return null;

  const hijos = Array.isArray(nodo.hijos) ? nodo.hijos : [];
  const tieneHijos = hijos.length > 0;
  const color = COLORES_TIPO[nodo.tipo] || "#3498db";

  return (
    <div
      className="ast-node-group"
      style={{
        "--ast-color": color,
        animationDelay: `${nivel * 80}ms`,
      }}
    >
      <NodoVisual nodo={nodo} color={color} tieneHijos={tieneHijos} />

      {tieneHijos && (
        <div
          className={`ast-children ${hijos.length === 1 ? "single-child" : ""}`}
          style={{ "--ast-color": color }}
        >
          {hijos.map((hijo, i) => (
            <div
              className="ast-child-branch"
              key={`${hijo.tipo}-${hijo.valor || ""}-${i}`}
              style={{ "--ast-color": color }}
            >
              <NodoArbol nodo={hijo} nivel={nivel + 1} />
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function ASTView({ astJson }) {
  if (!astJson) {
    return (
      <div
        style={{
          padding: "40px 20px",
          color: "#666",
          textAlign: "center",
          animation: "fadeIn 0.4s ease",
        }}
      >
        <p
          style={{
            fontSize: "3rem",
            marginBottom: "12px",
            animation: "float 0.8s ease-in-out infinite",
          }}
        >
          🌳
        </p>
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
      <div
        style={{
          padding: "20px",
          color: "#e74c3c",
          background: "rgba(231,76,60,0.1)",
          borderRadius: "8px",
          border: "1px solid #e74c3c40",
          animation: "fadeIn 0.4s ease",
        }}
      >
        <div style={{ fontWeight: 600, marginBottom: "8px" }}>
          Error al parsear el AST: {e.message}
        </div>
        <pre
          style={{
            fontSize: "0.875rem",
            marginTop: "8px",
            overflow: "auto",
            padding: "8px",
            background: "rgba(0,0,0,0.2)",
            borderRadius: "4px",
          }}
        >
          {astJson}
        </pre>
      </div>
    );
  }

  return (
    <div className="table-container">
      <h3 style={{ marginBottom: "16px" }}>🌳 Árbol Sintáctico (AST)</h3>

      <div className="ast-legend">
        {[
          ["ORACION", "#2980b9"],
          ["SUJETO", "#27ae60"],
          ["PREDICADO", "#8e44ad"],
          ["FRASE_NOMINAL", "#16a085"],
          ["VERBO", "#e74c3c"],
          ["OBJETO_DIRECTO", "#f39c12"],
          ["COMPLEMENTO", "#7f8c8d"],
        ].map(([label, color]) => (
          <LegendItem key={label} label={label} color={color} />
        ))}
      </div>

      <div className="ast-canvas">
        <NodoArbol nodo={ast} nivel={0} />
      </div>
    </div>
  );
}

export default ASTView;
