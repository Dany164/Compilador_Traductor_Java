import compiler from "../_lib/compiler.cjs";

export default async function handler(req, res) {
  return compiler.handleDiccionario(req, res);
}
