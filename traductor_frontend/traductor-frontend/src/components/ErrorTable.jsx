function ErrorTable({ errores }) {
  if (!errores || errores.length === 0) return null;

  const colorTipo = {
    LEXICO:    "#e74c3c",
    SINTACTICO:"#e67e22",
    SEMANTICO: "#9b59b6"
  };

  return (
    <div className="table-container">
      <h3>❌ Tabla de Errores</h3>
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>Tipo</th>
            <th>Línea</th>
            <th>Columna</th>
            <th>Descripción</th>
          </tr>
        </thead>
        <tbody>
          {errores.map((error, i) => (
            <tr key={i}>
              <td>{i + 1}</td>
              <td>
                <span style={{
                  background: colorTipo[error.tipo] || "#555",
                  color: "white",
                  padding: "2px 8px",
                  borderRadius: "4px",
                  fontSize: "0.8rem"
                }}>
                  {error.tipo}
                </span>
              </td>
              <td>{error.linea}</td>
              <td>{error.columna}</td>
              <td>{error.descripcion}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ErrorTable;