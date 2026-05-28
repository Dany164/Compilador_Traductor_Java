# CompilaTrad

Backend Spring Boot + frontend React/Vite para analizar y traducir texto.

## Backend local

Requiere JDK 17 y Maven en el PATH.

La llave de Google Translate queda configurada de forma directa y academica en:

```text
src/main/resources/application.properties
```

Para arrancar Spring Boot:

```powershell
mvn spring-boot:run
```

El backend queda en:

```text
http://localhost:8081
```

Para verificar que la llave fue recibida sin mostrarla:

```powershell
Invoke-RestMethod http://localhost:8081/api/traductor/status
```

Debe responder algo como:

```json
{"provider":"google","configured":true}
```

## Endpoint full translator

Usa este endpoint cuando quieras traduccion profesional usando el diccionario como base local y Google Translate como motor completo cuando la API este configurada:

```powershell
Invoke-RestMethod `
  -Uri http://localhost:8081/api/traducir `
  -Method Post `
  -ContentType "application/json;charset=UTF-8" `
  -Body '{"texto":"I will have been studying English grammar.","desde":"en","hacia":"es"}'
```

Tambien puedes omitir `desde` y `hacia`; el backend detecta si el texto esta en ingles o espanol y traduce al otro idioma.

Con la API configurada, este modo permite traducir texto libre:

- palabras fuera del diccionario local
- verbos en pasado, presente, futuro, perfectos y continuos
- oraciones compuestas y preguntas
- texto en ingles o espanol con deteccion automatica

El analizador local sigue generando tokens, tabla de simbolos y AST cuando aplica.

## Endpoint del diccionario

El frontend puede consultar la misma clase `Diccionario` del backend:

```powershell
Invoke-RestMethod `
  -Uri http://localhost:8081/api/diccionario/consultar `
  -Method Post `
  -ContentType "application/json;charset=UTF-8" `
  -Body '{"texto":"I am studying English grammar.","desde":"en"}'
```

Cada entrada devuelve palabra, normalizacion, tipo, categoria, traduccion, si existe en la base y si viene del diccionario o de una inferencia.

## Frontend local

```powershell
cd traductor_frontend\traductor-frontend
npm ci
npm run dev
```

Abre la URL que muestre Vite, normalmente:

```text
http://localhost:5173
```

El boton de voz funciona en navegadores con Web Speech API, principalmente Chrome o Edge. En produccion debe usarse HTTPS; en local funciona con `localhost`.

## Despliegue

El frontend ahora incluye funciones serverless compatibles con Vercel en:

```text
traductor_frontend/traductor-frontend/api
```

En Vercel deja sin configurar `VITE_API_BASE_URL` para que el build use `/api` del mismo dominio y no intente llamar a `localhost:8081`. Si quieres usar un backend Spring Boot externo, entonces si configura:

```text
VITE_API_BASE_URL=https://tu-backend/api
```

Para desarrollo local, el frontend sigue usando `http://localhost:8081/api` cuando no esta en modo produccion.

El backend Spring Boot se conserva para desarrollo local o para desplegarlo como servicio dedicado en Render, Railway o Fly.io. Si cambias `src/main/java/com/compilador/lexer/Diccionario.java`, regenera la copia serverless con:

```powershell
node tools\generate-vercel-dictionary.cjs
```
