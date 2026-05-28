import { useEffect, useRef, useState } from "react";
import logoCompiTrad from "../../img/LogoCompiTrad.png";
import { PLAN_OPTIONS } from "../auth/plans";
import CircuitDecor from "./CircuitDecor";

function WelcomePlans({ onSelectDemo, onSelectLoginPlan }) {
  const [confirmedPlan, setConfirmedPlan] = useState(null);
  const redirectTimerRef = useRef(null);

  useEffect(() => {
    return () => {
      if (redirectTimerRef.current) {
        window.clearTimeout(redirectTimerRef.current);
      }
    };
  }, []);

  const handlePlanAction = (plan) => {
    if (plan.requiresLogin) {
      setConfirmedPlan(plan);
      redirectTimerRef.current = window.setTimeout(() => {
        onSelectLoginPlan(plan.id);
      }, 1200);
      return;
    }

    onSelectDemo();
  };

  return (
    <main className="login-screen welcome-screen">
      <CircuitDecor />
      <img className="welcome-corner-logo" src={logoCompiTrad} alt="CompiTrad DMN" />
      {confirmedPlan && (
        <div className="payment-toast payment-toast-left" role="status" aria-live="polite">
          <strong>Pago realizado correctamente</strong>
          <span>{confirmedPlan.loginName} activado.</span>
        </div>
      )}

      <section className="welcome-layout" aria-labelledby="welcome-title">
        <div className="login-hero welcome-hero">
          <h1 id="welcome-title" className="welcome-text">
            BIENVENIDOS
          </h1>
          <p className="welcome-system-title">Traductor Ingles - Español</p>
        </div>

        <section className="plans-section" aria-labelledby="plans-title">
          <div className="plans-heading">
            <h2 id="plans-title">Planes disponibles</h2>
          </div>

          <div className="plan-grid">
            {PLAN_OPTIONS.map((plan) => (
              <article className={`plan-card ${plan.featured ? "is-featured" : ""}`} key={plan.id}>
                <div className="plan-card-header">
                  <h3>{plan.name}</h3>
                  <p>{plan.price}</p>
                </div>

                <ul className="plan-features">
                  {plan.features.map((feature) => (
                    <li key={feature}>{feature}</li>
                  ))}
                </ul>

                <button
                  className="plan-action"
                  type="button"
                  onClick={() => handlePlanAction(plan)}
                  disabled={Boolean(confirmedPlan)}
                >
                  {plan.buttonLabel}
                </button>
              </article>
            ))}
          </div>
        </section>
      </section>
    </main>
  );
}

export default WelcomePlans;
