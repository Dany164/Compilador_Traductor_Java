import axios from "axios";

const BASE_URL = "http://localhost:8080/api";

export const analizarTexto = async (texto, usarIA = false) => {
  const response = await axios.post(`${BASE_URL}/analizar`, { texto, usarIA });
  return response.data;
};