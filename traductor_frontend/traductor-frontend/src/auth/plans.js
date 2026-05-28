export const PLAN_KEYS = {
  DEMO: "demo",
  INDIVIDUAL: "individual",
  ACADEMICO: "academico",
};

export const STORAGE_KEYS = {
  selectedPlan: "selectedPlan",
  isAuthenticated: "isAuthenticated",
  userEmail: "userEmail",
  userRole: "userRole",
  userName: "userName",
};

export const PLAN_OPTIONS = [
  {
    id: PLAN_KEYS.DEMO,
    name: "Plan Demo",
    loginName: "Plan Demo",
    shortName: "Demo",
    price: "GRATIS",
    buttonLabel: "Entrar gratis",
    requiresLogin: false,
    features: [
      "30 dias de prueba",
      "Hasta 500 palabras/dia",
      "Analisis lexico y sintactico",
      "Sin tarjeta de credito",
      "Sin compromiso",
    ],
  },
  {
    id: PLAN_KEYS.INDIVIDUAL,
    name: "Plan Individual",
    loginName: "Plan Individual",
    shortName: "Individual",
    price: "Q149 / mes",
    buttonLabel: "Elegir individual",
    requiresLogin: true,
    featured: true,
    features: [
      "Uso individual ilimitado",
      "Todas las funciones completas",
      "Arbol de derivacion visual",
      "Soporte por correo electronico",
      "Fallback Google Cloud IA",
    ],
  },
  {
    id: PLAN_KEYS.ACADEMICO,
    name: "Plan Academico",
    loginName: "Plan Educativo",
    shortName: "Academico",
    price: "Q299 / mes",
    buttonLabel: "Elegir academico",
    requiresLogin: true,
    features: [
      "Maestro y alumnos",
      "Hasta 10 usuarios simultaneos",
      "Panel docente con reportes",
      "Estadisticas de errores por alumno",
      "Soporte prioritario",
      "Integracion con campus virtual",
    ],
  },
];

export const AUTH_USERS = [
  {
    email: "nayeli@traductor.com",
    password: "nayeli1234",
    plan: PLAN_KEYS.INDIVIDUAL,
    role: "individual",
    name: "Nayeli",
  },
  {
    email: "manuel@traductor.com",
    password: "manuel1234",
    plan: PLAN_KEYS.INDIVIDUAL,
    role: "individual",
    name: "Manuel",
  },
  {
    email: "dany@traductor.com",
    password: "dany1234",
    plan: PLAN_KEYS.INDIVIDUAL,
    role: "individual",
    name: "Dany",
  },
  {
    email: "admin@traductor.com",
    password: "123456",
    plan: PLAN_KEYS.ACADEMICO,
    role: "docente",
    name: "Docente",
  },
  {
    email: "alumno1@traductor.com",
    password: "alumno1234",
    plan: PLAN_KEYS.ACADEMICO,
    role: "alumno",
    name: "Alumno 1",
  },
  {
    email: "alumno2@traductor.com",
    password: "alumno2234",
    plan: PLAN_KEYS.ACADEMICO,
    role: "alumno",
    name: "Alumno 2",
  },
];

function getStorage() {
  if (typeof window === "undefined") {
    return null;
  }

  return window.localStorage;
}

export function getPlanById(planId) {
  return PLAN_OPTIONS.find((plan) => plan.id === planId) || null;
}

export function isLoginPlan(planId) {
  return planId === PLAN_KEYS.INDIVIDUAL || planId === PLAN_KEYS.ACADEMICO;
}

export function getAuthSession() {
  const storage = getStorage();

  if (!storage) {
    return {
      selectedPlan: null,
      isAuthenticated: false,
      userEmail: "",
      userRole: "",
      userName: "",
    };
  }

  return {
    selectedPlan: storage.getItem(STORAGE_KEYS.selectedPlan),
    isAuthenticated: storage.getItem(STORAGE_KEYS.isAuthenticated) === "true",
    userEmail: storage.getItem(STORAGE_KEYS.userEmail) || "",
    userRole: storage.getItem(STORAGE_KEYS.userRole) || "",
    userName: storage.getItem(STORAGE_KEYS.userName) || "",
  };
}

export function clearAuthSession() {
  const storage = getStorage();

  if (!storage) {
    return;
  }

  Object.values(STORAGE_KEYS).forEach((key) => storage.removeItem(key));
}

export function preparePlanLogin(planId) {
  const storage = getStorage();

  if (!storage || !isLoginPlan(planId)) {
    return;
  }

  clearAuthSession();
  storage.setItem(STORAGE_KEYS.selectedPlan, planId);
}

export function startDemoSession() {
  const storage = getStorage();

  if (!storage) {
    return;
  }

  clearAuthSession();
  storage.setItem(STORAGE_KEYS.selectedPlan, PLAN_KEYS.DEMO);
  storage.setItem(STORAGE_KEYS.isAuthenticated, "true");
  storage.setItem(STORAGE_KEYS.userRole, "demo");
  storage.setItem(STORAGE_KEYS.userName, "Invitado Demo");
}

export function saveAuthenticatedUser(user) {
  const storage = getStorage();

  if (!storage || !user) {
    return;
  }

  storage.setItem(STORAGE_KEYS.selectedPlan, user.plan);
  storage.setItem(STORAGE_KEYS.isAuthenticated, "true");
  storage.setItem(STORAGE_KEYS.userEmail, user.email);
  storage.setItem(STORAGE_KEYS.userRole, user.role);
  storage.setItem(STORAGE_KEYS.userName, user.name);
}

export function authenticateUser({ email, password, selectedPlan }) {
  const cleanEmail = email.trim().toLowerCase();
  const selectedPlanData = getPlanById(selectedPlan);

  if (!selectedPlanData || !isLoginPlan(selectedPlan)) {
    return {
      ok: false,
      message: "No se ha seleccionado ningun plan. Regresa y elige un plan para continuar.",
    };
  }

  const user = AUTH_USERS.find((item) => item.email === cleanEmail);

  if (!user) {
    return {
      ok: false,
      message: `El correo ${cleanEmail} no pertenece al ${selectedPlanData.name}.`,
    };
  }

  if (user.plan !== selectedPlan) {
    return {
      ok: false,
      message: `El correo ingresado pertenece al ${getPlanById(user.plan).name}, no al ${selectedPlanData.name}.`,
    };
  }

  if (user.password !== password) {
    return {
      ok: false,
      message: "La contrasena es incorrecta para este correo.",
    };
  }

  return { ok: true, user };
}
