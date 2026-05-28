const dictionaryData = require("./dictionary.generated.cjs");

const WORD_TOKEN_RE = /\p{L}+(?:['’]\p{L}+)?|\d+|[^\s]/gu;
const WORD_OR_NUMBER_RE = /^\p{L}+(?:['’]\p{L}+)?$|^\d+$/u;
const PUNCTUATION_TYPES = new Map([
  [".", "PUNTO"],
  [";", "PUNTO"],
  [",", "COMA"],
  ["?", "INTERROGACION"],
  ["!", "EXCLAMACION"],
]);

const MARKERS_EN = new Set([
  "the", "a", "an", "in", "on", "at", "with", "from", "to", "and", "or", "but",
  "is", "are", "was", "were", "have", "has", "do", "does", "i", "you", "he", "she",
  "it", "they", "we", "hello", "thanks",
]);

const MARKERS_ES = new Set([
  "el", "la", "los", "las", "un", "una", "unos", "unas", "en", "con", "de", "por",
  "para", "y", "o", "pero", "es", "son", "fue", "eran", "yo", "tu", "tú", "él",
  "ella", "ellos", "nosotros", "hola", "gracias", "adios", "adiós",
]);

const entries = dictionaryData.entries || {};
const forcedEsEn = dictionaryData.forcedEsEn || {};
const forcedEnEs = dictionaryData.forcedEnEs || {};
const expressionKeys = Object.keys(entries)
  .filter((key) => key.includes(" "))
  .sort((a, b) => b.length - a.length);
const compactExpressions = Object.fromEntries(
  expressionKeys.map((key) => [key.replace(/\s+/g, ""), key]),
);

function setApiHeaders(res) {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
  res.setHeader("Cache-Control", "no-store");
}

function sendJson(res, status, payload) {
  setApiHeaders(res);
  if (typeof res.status === "function" && typeof res.json === "function") {
    return res.status(status).json(payload);
  }

  res.statusCode = status;
  res.setHeader("Content-Type", "application/json; charset=utf-8");
  return res.end(JSON.stringify(payload));
}

function sendNoContent(res) {
  setApiHeaders(res);
  res.statusCode = 204;
  return res.end();
}

function methodNotAllowed(res) {
  return sendJson(res, 405, { error: "Metodo no permitido" });
}

function parseBody(req) {
  if (!req.body) {
    return {};
  }

  if (typeof req.body === "object") {
    return req.body;
  }

  try {
    return JSON.parse(req.body);
  } catch {
    return {};
  }
}

function normalizeLanguage(language) {
  if (!language || String(language).toLowerCase() === "auto") {
    return null;
  }

  return String(language).toLowerCase().startsWith("es") ? "es" : "en";
}

function detectLanguage(text) {
  if (!text || !text.trim()) {
    return "en";
  }

  const clean = text.toLowerCase().replace(/[^\p{L}\s]/gu, " ");
  const words = clean.trim().split(/\s+/).filter(Boolean);
  let en = 0;
  let es = 0;

  for (const word of words) {
    if (MARKERS_EN.has(word)) en += 1;
    if (MARKERS_ES.has(word)) es += 1;
  }

  if (es > en) {
    return "es";
  }

  if (en === 0 && /[áéíóúñü]/i.test(text)) {
    return "es";
  }

  return "en";
}

function resolveSourceLanguage(text, requested) {
  return normalizeLanguage(requested) || detectLanguage(text);
}

function resolveTargetLanguage(source, requested) {
  return normalizeLanguage(requested) || (source === "es" ? "en" : "es");
}

function cleanWord(word) {
  return String(word || "")
    .replace(/^[^\p{L}\d]+/gu, "")
    .replace(/[^\p{L}\d]+$/gu, "")
    .toLowerCase();
}

function normalizeKey(word) {
  const key = String(word || "").trim().toLowerCase();
  return compactExpressions[key] || key;
}

function cleanAlternatives(value) {
  if (!value) {
    return "";
  }

  return String(value).includes("/") ? String(value).split("/")[0] : String(value);
}

function inferType(key) {
  if (!key) {
    return "DESCONOCIDO";
  }
  if (/^\d+$/.test(key)) {
    return "NUMERAL_CARDINAL";
  }
  if (/(ing|ed|en)$/.test(key)) {
    return "VERBO";
  }
  if (/ly$/.test(key)) {
    return "ADVERBIO_MODO";
  }
  if (/(able|ible|ous|ful|less|ive|al|ic|ish)$/.test(key)) {
    return "ADJETIVO_CALIFICATIVO";
  }
  return "SUSTANTIVO";
}

function category(type) {
  if (!type) return "OTRO";
  if (type.startsWith("PRONOMBRE")) return "PRONOMBRE";
  if (type.startsWith("VERBO")) return "VERBO";
  if (type.startsWith("SUSTANTIVO")) return "SUSTANTIVO";
  if (type.startsWith("ADJETIVO")) return "ADJETIVO";
  if (type.startsWith("ADVERBIO")) return "ADVERBIO";
  if (type.startsWith("ARTICULO")) return "ARTICULO";
  if (type.startsWith("CONJUNCION")) return "CONJUNCION";
  if (type.startsWith("PREPOSICION")) return "PREPOSICION";
  if (type.startsWith("NUMERAL")) return "NUMERAL";
  if (type === "POSESIVO" || type === "DEMOSTRATIVO") return "DETERMINANTE";
  if (type === "CONTRACCION") return "CONTRACCION";
  if (type === "INTERJECCION") return "INTERJECCION";
  if (type === "EXPRESION" || type === "MODISMO") return "EXPRESION";
  if (type === "ABREVIATURA") return "ABREVIATURA";
  return "OTRO";
}

function translationOverride(key, sourceLanguage) {
  if (sourceLanguage === "es") {
    return forcedEsEn[key];
  }
  if (sourceLanguage === "en") {
    return forcedEnEs[key];
  }
  return null;
}

function resolveEnglishBase(base) {
  if (entries[base]) return base;
  if (base.endsWith("i") && entries[`${base.slice(0, -1)}y`]) return `${base.slice(0, -1)}y`;
  if (base.length > 2 && entries[base.slice(0, -1)]) return base.slice(0, -1);
  if (entries[`${base}e`]) return `${base}e`;
  return base;
}

function toSpanishGerund(infinitive) {
  if (!infinitive) return infinitive;
  if (infinitive.endsWith("ar")) return `${infinitive.slice(0, -2)}ando`;
  if (infinitive.endsWith("er") || infinitive.endsWith("ir")) return `${infinitive.slice(0, -2)}iendo`;
  return infinitive;
}

function toSpanishPast(infinitive) {
  if (!infinitive) return infinitive;
  if (infinitive.endsWith("ar")) return `${infinitive.slice(0, -2)}o`;
  if (infinitive.endsWith("er") || infinitive.endsWith("ir")) return `${infinitive.slice(0, -2)}io`;
  return infinitive;
}

function toSpanishPlural(singular) {
  if (!singular) return singular;
  return /[aeiou]$/i.test(singular) ? `${singular}s` : `${singular}es`;
}

function translateDerivedForm(key, sourceLanguage) {
  if (sourceLanguage !== "en" || !key) {
    return null;
  }

  if (key.endsWith("ing") && key.length > 4) {
    const base = resolveEnglishBase(key.slice(0, -3));
    const entry = entries[base];
    if (entry && entry.tipo.startsWith("VERBO")) {
      return toSpanishGerund(cleanAlternatives(entry.traduccion));
    }
  }

  if (key.endsWith("ed") && key.length > 3) {
    const base = resolveEnglishBase(key.slice(0, -2));
    const entry = entries[base];
    if (entry && entry.tipo.startsWith("VERBO")) {
      return toSpanishPast(cleanAlternatives(entry.traduccion));
    }
  }

  if (key.endsWith("s") && key.length > 2) {
    const entry = entries[key.slice(0, -1)];
    if (entry && entry.tipo.startsWith("SUSTANTIVO")) {
      return toSpanishPlural(cleanAlternatives(entry.traduccion));
    }
  }

  return null;
}

function consultWord(word, sourceLanguage = "auto") {
  const original = String(word || "").trim();
  const key = normalizeKey(cleanWord(original));
  const entry = entries[key];
  const forced = translationOverride(key, sourceLanguage);
  const derived = translateDerivedForm(key, sourceLanguage);
  const type = entry ? entry.tipo : inferType(key);
  const translation = forced || (entry ? cleanAlternatives(entry.traduccion) : derived) || original;

  return {
    palabra: original,
    normalizada: key,
    tipo: type,
    categoria: category(type),
    traduccion: translation,
    conocida: Boolean(entry || forced),
    origen: entry ? "DICCIONARIO" : forced ? "FORZADA" : "INFERIDA",
  };
}

function escapeRegExp(text) {
  return text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

function preserveCapitalization(replacement, found) {
  if (!replacement || !found) return replacement;
  return /^[A-ZÁÉÍÓÚÑÜ]/.test(found)
    ? replacement.charAt(0).toUpperCase() + replacement.slice(1)
    : replacement;
}

function normalizeExpressions(text) {
  let result = String(text || "");

  for (const expression of expressionKeys) {
    const compact = expression.replace(/\s+/g, "");
    const pattern = new RegExp(`(?<!\\p{L})${escapeRegExp(expression)}(?!\\p{L})`, "giu");
    result = result.replace(pattern, (found) => preserveCapitalization(compact, found));
  }

  return result;
}

function capitalizeLike(output, original) {
  if (!output || !original) return output;
  return /^[A-ZÁÉÍÓÚÑÜ]/.test(original)
    ? output.charAt(0).toUpperCase() + output.slice(1)
    : output;
}

function isAttachedPunctuation(token) {
  return /^[.,;:!?)]$/.test(token);
}

function consultText(text, sourceLanguage) {
  const results = [];
  const normalized = normalizeExpressions(text);
  const matches = normalized.matchAll(WORD_TOKEN_RE);

  for (const match of matches) {
    const token = match[0];
    if (WORD_OR_NUMBER_RE.test(token)) {
      results.push(consultWord(token, sourceLanguage));
    }
  }

  return results;
}

function translateWithDictionary(text, sourceLanguage) {
  const parts = [];
  const normalized = normalizeExpressions(text);
  const matches = normalized.matchAll(WORD_TOKEN_RE);

  for (const match of matches) {
    const token = match[0];
    let output = token;
    if (WORD_OR_NUMBER_RE.test(token)) {
      output = consultWord(token, sourceLanguage).traduccion;
    }

    if (parts.length === 0) {
      parts.push(capitalizeLike(output, token));
    } else if (isAttachedPunctuation(output)) {
      parts[parts.length - 1] = `${parts[parts.length - 1]}${output}`;
    } else {
      parts.push(output);
    }
  }

  return parts.join(" ").trim();
}

function positionFromIndex(text, index) {
  const prefix = text.slice(0, index);
  const lines = prefix.split(/\r\n|\r|\n/);
  return {
    linea: lines.length,
    columna: lines[lines.length - 1].length + 1,
  };
}

function tokenize(text, sourceLanguage) {
  const tokens = [];
  const errores = [];
  const normalized = normalizeExpressions(text);
  const matches = normalized.matchAll(WORD_TOKEN_RE);

  for (const match of matches) {
    const value = match[0];
    const position = positionFromIndex(normalized, match.index || 0);

    if (WORD_OR_NUMBER_RE.test(value)) {
      const entry = consultWord(value, sourceLanguage);
      tokens.push({
        valor: value,
        tipo: /^\d+$/.test(value) ? "NUMERAL_CARDINAL" : entry.tipo,
        traduccion: entry.traduccion,
        linea: position.linea,
        columna: position.columna,
      });
      continue;
    }

    const punctuationType = PUNCTUATION_TYPES.get(value);
    if (punctuationType) {
      tokens.push({
        valor: value,
        tipo: punctuationType,
        traduccion: value,
        linea: position.linea,
        columna: position.columna,
      });
      continue;
    }

    errores.push({
      tipo: "LEXICO",
      linea: position.linea,
      columna: position.columna,
      descripcion: `Caracter no reconocido: '${value}'`,
    });
  }

  return { tokens, errores };
}

function leaf(tipo, valor) {
  return { tipo, valor };
}

function node(tipo, hijos = []) {
  return { tipo, hijos: hijos.filter(Boolean) };
}

function buildNominalPhrase(label, tokens) {
  if (!tokens.length) return node(label, []);
  if (tokens.length === 1) return node(label, [leaf(tokens[0].tipo, tokens[0].valor)]);
  return node(label, [node("FRASE_NOMINAL", tokens.map((token) => leaf(token.tipo, token.valor)))]);
}

function buildComplement(tokens) {
  return node("COMPLEMENTO", tokens.map((token) => leaf(token.tipo, token.valor)));
}

function buildAstJson(tokens) {
  const words = tokens.filter((token) => !PUNCTUATION_TYPES.has(token.valor));
  if (!words.length) {
    return null;
  }

  const verbIndex = words.findIndex((token) => token.tipo === "VERBO");
  if (verbIndex === -1) {
    return JSON.stringify(node("ORACION", words.map((token) => leaf(token.tipo, token.valor))));
  }

  const subject = buildNominalPhrase("SUJETO", words.slice(0, verbIndex));
  const verb = leaf("VERBO", words[verbIndex].valor);
  const rest = words.slice(verbIndex + 1);
  let predicate;

  if (!rest.length) {
    predicate = node("PREDICADO", [verb]);
  } else {
    const prepIndex = rest.findIndex((token) => token.tipo === "PREPOSICION");
    if (prepIndex === -1) {
      predicate = node("PREDICADO", [verb, buildNominalPhrase("OBJETO_DIRECTO", rest)]);
    } else if (prepIndex === 0) {
      predicate = node("PREDICADO", [verb, buildComplement(rest)]);
    } else {
      predicate = node("PREDICADO", [
        verb,
        buildNominalPhrase("OBJETO_DIRECTO", rest.slice(0, prepIndex)),
        buildComplement(rest.slice(prepIndex)),
      ]);
    }
  }

  return JSON.stringify(node("ORACION", [subject, predicate]));
}

function symbolTable(tokens) {
  return tokens.map((token, index) => ({
    numero: index + 1,
    palabra: token.valor,
    categoria: category(token.tipo),
    subcategoria: token.tipo,
    traduccion: token.traduccion,
    linea: token.linea,
    columna: token.columna,
  }));
}

async function translateWithOfficialGoogle(text, sourceLanguage, targetLanguage) {
  if (!process.env.GOOGLE_API_KEY) {
    return null;
  }

  const url = `https://translation.googleapis.com/language/translate/v2?key=${encodeURIComponent(process.env.GOOGLE_API_KEY)}`;
  const response = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      q: [text],
      source: sourceLanguage,
      target: targetLanguage,
      format: "text",
    }),
  });

  if (!response.ok) {
    return null;
  }

  const payload = await response.json();
  return payload?.data?.translations?.[0]?.translatedText || null;
}

async function translateWithPublicGoogle(text, sourceLanguage, targetLanguage) {
  const url = new URL("https://translate.googleapis.com/translate_a/single");
  url.searchParams.set("client", "gtx");
  url.searchParams.set("sl", sourceLanguage);
  url.searchParams.set("tl", targetLanguage);
  url.searchParams.set("dt", "t");
  url.searchParams.set("q", text);

  const response = await fetch(url, { headers: { "User-Agent": "CompiTradDMN/1.0" } });
  if (!response.ok) {
    return null;
  }

  const payload = await response.json();
  const translated = Array.isArray(payload?.[0])
    ? payload[0].map((part) => part?.[0] || "").join("")
    : "";
  return translated.trim() || null;
}

async function translateWithMyMemory(text, sourceLanguage, targetLanguage) {
  const url = new URL("https://api.mymemory.translated.net/get");
  url.searchParams.set("q", text);
  url.searchParams.set("langpair", `${sourceLanguage}|${targetLanguage}`);

  const response = await fetch(url);
  if (!response.ok) {
    return null;
  }

  const payload = await response.json();
  return payload?.responseData?.translatedText || null;
}

async function translateWithCloud(text, sourceLanguage, targetLanguage) {
  const translators = [
    ["google", translateWithOfficialGoogle],
    ["google-public", translateWithPublicGoogle],
    ["mymemory", translateWithMyMemory],
  ];

  for (const [provider, translator] of translators) {
    try {
      const translated = await translator(text, sourceLanguage, targetLanguage);
      if (translated && translated.trim()) {
        return { provider, text: translated.trim() };
      }
    } catch {
      continue;
    }
  }

  return null;
}

async function translateRequest(body) {
  const originalText = String(body?.texto || "").trim();
  const sourceLanguage = resolveSourceLanguage(originalText, body?.desde);
  const targetLanguage = resolveTargetLanguage(sourceLanguage, body?.hacia);

  if (!originalText) {
    return {
      exitoso: false,
      textoOriginal: originalText,
      traduccion: null,
      traduccionDiccionario: null,
      idiomaOrigen: sourceLanguage,
      idiomaDestino: targetLanguage,
      provider: "serverless",
      mensaje: "El texto no puede estar vacio.",
      diccionario: [],
    };
  }

  const dictionary = consultText(originalText, sourceLanguage);
  const dictionaryTranslation = translateWithDictionary(originalText, sourceLanguage);
  const wantsCloud = body?.usarIA === true;
  const cloud = wantsCloud ? await translateWithCloud(originalText, sourceLanguage, targetLanguage) : null;
  const translation = cloud?.text || dictionaryTranslation;
  const provider = cloud?.provider || "diccionario";

  return {
    exitoso: Boolean(translation),
    textoOriginal: originalText,
    traduccion: translation,
    traduccionDiccionario: dictionaryTranslation,
    idiomaOrigen: sourceLanguage,
    idiomaDestino: targetLanguage,
    provider,
    mensaje: cloud
      ? "Traduccion realizada en la nube y respaldada por el diccionario local."
      : "Traduccion realizada con el diccionario local.",
    diccionario: dictionary,
  };
}

async function analyzeRequest(body) {
  const originalText = String(body?.texto || "").trim();
  if (!originalText) {
    return {
      tokens: [],
      tablaSimbolos: [],
      errores: [],
      traduccion: "",
      astJson: null,
      exitoso: true,
      usoIA: false,
    };
  }

  const sourceLanguage = resolveSourceLanguage(originalText, body?.desde);
  const targetLanguage = resolveTargetLanguage(sourceLanguage, body?.hacia);
  const { tokens, errores } = tokenize(originalText, sourceLanguage);
  const cloud = body?.usarIA === true
    ? await translateWithCloud(originalText, sourceLanguage, targetLanguage)
    : null;
  const translation = cloud?.text || translateWithDictionary(originalText, sourceLanguage);

  return {
    tokens,
    tablaSimbolos: symbolTable(tokens),
    errores,
    traduccion: errores.length ? null : translation,
    astJson: buildAstJson(tokens),
    exitoso: errores.length === 0,
    usoIA: Boolean(cloud),
  };
}

async function handleTraducir(req, res) {
  if (req.method === "OPTIONS") return sendNoContent(res);
  if (req.method !== "POST") return methodNotAllowed(res);
  return sendJson(res, 200, await translateRequest(parseBody(req)));
}

async function handleAnalizar(req, res) {
  if (req.method === "OPTIONS") return sendNoContent(res);
  if (req.method !== "POST") return methodNotAllowed(res);
  return sendJson(res, 200, await analyzeRequest(parseBody(req)));
}

async function handleDiccionario(req, res) {
  if (req.method === "OPTIONS") return sendNoContent(res);
  if (req.method !== "POST") return methodNotAllowed(res);
  const body = parseBody(req);
  const text = String(body?.texto || "").trim();
  const sourceLanguage = resolveSourceLanguage(text, body?.desde);
  return sendJson(res, 200, consultText(text, sourceLanguage));
}

async function handleStatus(req, res) {
  if (req.method === "OPTIONS") return sendNoContent(res);
  if (req.method !== "GET") return methodNotAllowed(res);
  return sendJson(res, 200, {
    provider: process.env.GOOGLE_API_KEY ? "google" : "diccionario+google-public",
    configured: true,
    runtime: "vercel-serverless",
  });
}

async function handleHealth(req, res) {
  if (req.method === "OPTIONS") return sendNoContent(res);
  if (req.method !== "GET") return methodNotAllowed(res);
  return sendJson(res, 200, { status: "ok", runtime: "vercel-serverless" });
}

module.exports = {
  analyzeRequest,
  consultText,
  handleAnalizar,
  handleDiccionario,
  handleHealth,
  handleStatus,
  handleTraducir,
  translateRequest,
};
