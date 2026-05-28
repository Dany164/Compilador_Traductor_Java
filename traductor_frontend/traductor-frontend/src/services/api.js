import axios from "axios";

const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL;
const isLocalApiUrl = /^https?:\/\/(localhost|127\.0\.0\.1|\[::1\])(?::|\/|$)/i.test(configuredBaseUrl || "");
const BASE_URL = configuredBaseUrl && !(import.meta.env.PROD && isLocalApiUrl)
  ? configuredBaseUrl
  : (import.meta.env.PROD ? "/api" : "http://localhost:8081/api");
const API_TIMEOUT_MS = Number(import.meta.env.VITE_API_TIMEOUT_MS) || 15000;

const api = axios.create({
  baseURL: BASE_URL,
  timeout: API_TIMEOUT_MS,
  withCredentials: false,
  headers: {
    "Content-Type": "application/json;charset=UTF-8",
  },
});

export const analizarTexto = async (texto, usarIA = false) => {
  const response = await api.post("/analizar", { texto, usarIA });
  return response.data;
};

export const traducirTextoLibre = async ({ texto, desde = "auto", hacia = "auto", usarIA = true }) => {
  const response = await api.post("/traducir", { texto, desde, hacia, usarIA });
  return response.data;
};

export const consultarDiccionario = async ({ texto, desde = "auto" }) => {
  const response = await api.post("/diccionario/consultar", { texto, desde });
  return response.data;
};
