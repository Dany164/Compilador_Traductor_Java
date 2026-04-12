function TablaSimbolosComponent({ tablaSimbolos }) {
  if (!tablaSimbolos || tablaSimbolos.length === 0) return null;

  const coloresCategoria = {
    PRONOMBRE:   "#2980b9",
    VERBO:       "#27ae60",
    SUSTANTIVO:  "#8e44ad",
    ADJETIVO:    "#16a085",
    ADVERBIO:    "#c0392b",
    ARTICULO:    "#d35400",
    CONJUNCION:  "#e67e22",
    PREPOSICION: "#7f8c8d",
    NUMERAL:     "#2c3e50",
    DETERMINANTE:"#1abc9c",
    CONTRACCION: "#e74c3c",
    INTERJECCION:"#f1c40f",
    OTRO:        "#555",
  };

  return (
    <div className="table-container">
      <h3>📖 Tabla de Símbolos</h3>
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>Palabra</th>
            <th>Categoría</th>
            <th>Subcategoría</th>
            <th>Traducción</th>
            <th>Línea</th>
            <th>Columna</th>
          </tr>
        </thead>
        <tbody>
          {tablaSimbolos.map((s, i) => (
            <tr key={i}>
              <td>{s.numero}</td>
              <td><strong>{s.palabra}</strong></td>
              <td>
                <span style={{
                  background: coloresCategoria[s.categoria] || "#555",
                  color: "white",
                  padding: "2px 8px",
                  borderRadius: "4px",
                  fontSize: "0.78rem",
                  fontWeight: "bold"
                }}>
                  {s.categoria}
                </span>
              </td>
              <td style={{ fontSize: "0.82rem", color: "#aaa" }}>
                {s.subcategoria}
              </td>
              <td>{s.traduccion}</td>
              <td>{s.linea}</td>
              <td>{s.columna}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default TablaSimbolosComponent;