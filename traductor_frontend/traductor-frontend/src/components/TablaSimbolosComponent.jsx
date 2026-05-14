function TablaSimbolosComponent({ tablaSimbolos }) {
  if (!tablaSimbolos || tablaSimbolos.length === 0) return null;

  const coloresCategoria = {
    PRONOMBRE:    "#2980b9",
    VERBO:        "#27ae60",
    SUSTANTIVO:   "#8e44ad",
    ADJETIVO:     "#16a085",
    ADVERBIO:     "#c0392b",
    ARTICULO:     "#d35400",
    CONJUNCION:   "#e67e22",
    PREPOSICION:  "#7f8c8d",
    NUMERAL:      "#2c3e50",
    DETERMINANTE: "#1abc9c",
    CONTRACCION:  "#e74c3c",
    INTERJECCION: "#f1c40f",
    OTRO:         "#555",
  };

  const colorTextoCategoria = (categoria) => (
    categoria === "INTERJECCION" ? "#202124" : "#ffffff"
  );

  return (
    <div className="table-container">
      <h3>📖 Tabla de Símbolos</h3>
      <table className="data-table symbols-table">
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
            <tr key={`${s.palabra}-${s.linea}-${s.columna}-${i}`}>
              <td>{s.numero}</td>
              <td className="word-cell"><strong>{s.palabra}</strong></td>
              <td>
                <span
                  className="symbol-badge"
                  style={{
                    backgroundColor: coloresCategoria[s.categoria] || "#555",
                    color: colorTextoCategoria(s.categoria),
                  }}
                >
                  {s.categoria}
                </span>
              </td>
              <td className="symbol-subcategory">
                {s.subcategoria}
              </td>
              <td className="word-cell">{s.traduccion}</td>
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
