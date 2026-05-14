function TokensTable({ tokens }) {
  if (!tokens || tokens.length === 0) return null;

  return (
    <div className="table-container">
      <h3>📋 Tabla de Tokens</h3>
      <table className="data-table tokens-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Palabra</th>
            <th>Tipo</th>
            <th>Traducción</th>
            <th>Línea</th>
            <th>Columna</th>
          </tr>
        </thead>
        <tbody>
          {tokens.map((token, i) => (
            <tr key={`${token.valor}-${token.linea}-${token.columna}-${i}`}>
              <td>{i + 1}</td>
              <td className="word-cell"><strong>{token.valor}</strong></td>
              <td>
                <span className={`badge badge-${token.tipo.toLowerCase()}`}>
                  {token.tipo}
                </span>
              </td>
              <td className="word-cell">{token.traduccion}</td>
              <td>{token.linea}</td>
              <td>{token.columna}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default TokensTable;
