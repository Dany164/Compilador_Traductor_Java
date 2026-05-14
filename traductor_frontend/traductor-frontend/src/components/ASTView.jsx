import { useCallback, useEffect, useLayoutEffect, useRef, useState } from "react";

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

const MIN_ZOOM = 0.28;
const MAX_ZOOM = 1.8;
const ZOOM_STEP = 0.14;

function limitarZoom(valor) {
  return Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, Number(valor.toFixed(2))));
}

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
  const viewportRef = useRef(null);
  const treeRef = useRef(null);
  const dragRef = useRef(null);
  const zoomRef = useRef(1);
  const [zoom, setZoom] = useState(1);
  const [treeSize, setTreeSize] = useState({ width: 0, height: 0 });
  const [isDragging, setIsDragging] = useState(false);

  useEffect(() => {
    zoomRef.current = zoom;
  }, [zoom]);

  const medirArbol = useCallback(() => {
    const tree = treeRef.current;
    if (!tree) return null;

    const size = {
      width: Math.ceil(tree.offsetWidth || tree.scrollWidth || 0),
      height: Math.ceil(tree.offsetHeight || tree.scrollHeight || 0),
    };

    if (size.width > 0 && size.height > 0) {
      setTreeSize((current) => (
        current.width === size.width && current.height === size.height ? current : size
      ));
    }

    return size;
  }, []);

  const enfocarRaiz = useCallback((nextZoom = zoomRef.current, sizeArg = null) => {
    const viewport = viewportRef.current;
    const size = sizeArg || medirArbol() || treeSize;
    if (!viewport || !size.width) return;

    window.requestAnimationFrame(() => {
      const scaledWidth = size.width * nextZoom;
      viewport.scrollLeft = Math.max((scaledWidth - viewport.clientWidth) / 2, 0);
      viewport.scrollTop = 0;
    });
  }, [medirArbol, treeSize]);

  const aplicarZoom = useCallback((nextZoom, modo = "center") => {
    const viewport = viewportRef.current;
    const currentZoom = zoomRef.current;
    const clampedZoom = limitarZoom(nextZoom);

    if (!viewport) {
      zoomRef.current = clampedZoom;
      setZoom(clampedZoom);
      return;
    }

    const centerX = (viewport.scrollLeft + viewport.clientWidth / 2) / currentZoom;
    const centerY = (viewport.scrollTop + viewport.clientHeight / 2) / currentZoom;

    zoomRef.current = clampedZoom;
    setZoom(clampedZoom);

    window.requestAnimationFrame(() => {
      if (modo === "root") {
        enfocarRaiz(clampedZoom);
        return;
      }

      viewport.scrollLeft = Math.max(centerX * clampedZoom - viewport.clientWidth / 2, 0);
      viewport.scrollTop = Math.max(centerY * clampedZoom - viewport.clientHeight / 2, 0);
    });
  }, [enfocarRaiz]);

  const ajustarAPantalla = useCallback(() => {
    const viewport = viewportRef.current;
    const size = medirArbol();
    if (!viewport || !size?.width || !size?.height) return;

    const availableWidth = Math.max(viewport.clientWidth - 42, 120);
    const availableHeight = Math.max(viewport.clientHeight - 42, 120);
    const nextZoom = limitarZoom(Math.min(availableWidth / size.width, availableHeight / size.height, 1));

    zoomRef.current = nextZoom;
    setZoom(nextZoom);
    enfocarRaiz(nextZoom, size);
  }, [enfocarRaiz, medirArbol]);

  const resetZoom = useCallback(() => {
    aplicarZoom(1, "root");
  }, [aplicarZoom]);

  const handlePointerDown = (event) => {
    if (!viewportRef.current) return;

    dragRef.current = {
      pointerId: event.pointerId,
      startX: event.clientX,
      startY: event.clientY,
      scrollLeft: viewportRef.current.scrollLeft,
      scrollTop: viewportRef.current.scrollTop,
    };
    setIsDragging(true);
    event.currentTarget.setPointerCapture(event.pointerId);
  };

  const handlePointerMove = (event) => {
    const drag = dragRef.current;
    const viewport = viewportRef.current;
    if (!drag || !viewport) return;

    viewport.scrollLeft = drag.scrollLeft - (event.clientX - drag.startX);
    viewport.scrollTop = drag.scrollTop - (event.clientY - drag.startY);
  };

  const finalizarArrastre = (event) => {
    if (dragRef.current?.pointerId === event.pointerId) {
      dragRef.current = null;
      setIsDragging(false);
    }
  };

  useLayoutEffect(() => {
    if (!astJson) return undefined;

    const frame = window.requestAnimationFrame(() => {
      ajustarAPantalla();
    });

    return () => window.cancelAnimationFrame(frame);
  }, [astJson, ajustarAPantalla]);

  useEffect(() => {
    if (!astJson) return undefined;

    const handleResize = () => {
      ajustarAPantalla();
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [astJson, ajustarAPantalla]);

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

      <div className="ast-toolbar" aria-label="Controles del arbol sintactico">
        <div className="ast-zoom-readout">Zoom {Math.round(zoom * 100)}%</div>
        <div className="ast-zoom-controls">
          <button type="button" className="ast-control-btn" onClick={() => aplicarZoom(zoomRef.current - ZOOM_STEP)} aria-label="Alejar arbol">
            -
          </button>
          <button type="button" className="ast-control-btn" onClick={() => aplicarZoom(zoomRef.current + ZOOM_STEP)} aria-label="Acercar arbol">
            +
          </button>
          <button type="button" className="ast-control-btn" onClick={resetZoom} aria-label="Reiniciar zoom">
            100%
          </button>
          <button type="button" className="ast-control-btn ast-control-fit" onClick={ajustarAPantalla} aria-label="Ajustar arbol a pantalla">
            Ajustar
          </button>
        </div>
      </div>

      <div
        className={`ast-canvas ${isDragging ? "is-dragging" : ""}`}
        ref={viewportRef}
        onPointerDown={handlePointerDown}
        onPointerMove={handlePointerMove}
        onPointerUp={finalizarArrastre}
        onPointerCancel={finalizarArrastre}
        role="region"
        aria-label="Vista interactiva del arbol sintactico"
      >
        <div
          className="ast-scroll-spacer"
          style={{
            width: `${Math.max(treeSize.width * zoom + 48, 240)}px`,
            height: `${Math.max(treeSize.height * zoom + 48, 220)}px`,
          }}
        >
          <div
            className="ast-tree-zoom"
            ref={treeRef}
            style={{
              transform: `translateX(-50%) scale(${zoom})`,
            }}
          >
            <NodoArbol nodo={ast} nivel={0} />
          </div>
        </div>
      </div>
    </div>
  );
}

export default ASTView;
