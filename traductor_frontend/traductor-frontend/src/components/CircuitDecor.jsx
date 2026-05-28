function CircuitDecor() {
  return (
    <>
      <div className="circuit circuit-left" aria-hidden="true">
        <span className="circuit-line line-a" />
        <span className="circuit-line line-b" />
        <span className="circuit-line line-c" />
        <span className="circuit-line line-d" />
        <span className="circuit-dot dot-a" />
        <span className="circuit-dot dot-b" />
        <span className="circuit-dot dot-c" />
      </div>

      <div className="circuit circuit-right" aria-hidden="true">
        <span className="circuit-line line-a" />
        <span className="circuit-line line-b" />
        <span className="circuit-line line-c" />
        <span className="circuit-line line-d" />
        <span className="circuit-dot dot-a" />
        <span className="circuit-dot dot-b" />
        <span className="circuit-dot dot-c" />
      </div>
    </>
  );
}

export default CircuitDecor;
