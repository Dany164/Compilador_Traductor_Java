# CompiTrad DMN Frontend

Frontend React/Vite con endpoints serverless para Vercel.

## Desarrollo local

```powershell
npm ci
npm run dev
```

En desarrollo, si `VITE_API_BASE_URL` no esta configurada, el cliente usa:

```text
http://localhost:8081/api
```

## Vercel

En produccion, si `VITE_API_BASE_URL` no esta configurada, el cliente usa las funciones del mismo dominio:

```text
/api
```

Esto evita que el despliegue publico intente conectarse a `localhost:8081`.
