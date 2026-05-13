import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081/api";

export const analizarTexto = async (texto, usarIA = false) => {
  const response = await axios.post(`${BASE_URL}/analizar`, { texto, usarIA });
  return response.data;
};

export const traducirTextoLibre = async ({ texto, desde = "auto", hacia = "auto", usarIA = true }) => {
  const response = await axios.post(`${BASE_URL}/traducir`, { texto, desde, hacia, usarIA });
  return response.data;
};

export const consultarDiccionario = async ({ texto, desde = "auto" }) => {
  const response = await axios.post(`${BASE_URL}/diccionario/consultar`, { texto, desde });
  return response.data;
};
