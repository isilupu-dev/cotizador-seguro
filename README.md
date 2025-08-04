# Cotizador de Seguros Vehiculares

API reactiva desarrollada con Java y Spring WebFlux para cotizar seguros vehiculares. Utiliza MongoDB para persistencia, Redis para caché de resultados por 5 minutos, seguridad por API Key y Docker para despliegue.

---

## Tecnologías utilizadas

- Java 17
- Spring Boot 3.5.4
- Spring WebFlux
- MongoDB
- Redis
- Seguridad con API Key
- Logging y trazabilidad
- Docker & Docker Compose
- Swagger / OpenAPI
- Arquitectura limpia
- JUnit 5 + Mockito + StepVerifier
- Jacoco (reporte de cobertura)

---

## Levantar el proyecto con Docker Compose

### 1. Pre-requisitos
- Docker instalado y ejecutándose
- Docker Compose instalado

### 2. Clona el repositorio
```
git clone https://github.com/isilupu-dev/cotizador-seguro.git
cd cotizador-seguro
```

### 3. Levantar la solución con Docker Compose
```
docker-compose up --build
```

Esto levantará:
- La API (http://localhost:8080)
- MongoDB (localhost:27018)
- Redis (localhost:6380)

### 4. Acceder a la aplicación
```
http://localhost:8080/swagger-ui.html
```

### 5. Incluir en las solicitudes el header HTTP
```
x-api-key: 123456-secret
```

### 6. Envío de solicitud
```
POST /api/v1/cotizaciones
```

Cotiza un seguro enviando un CotizacionRequest:
```
{
  "marca": "Toyota",
  "modelo": "Corolla",
  "anio": 2023,
  "tipoUso": "familiar",
  "edadConductor": 23
}
```

Respuesta:
```
{
  "primaBase": 500,
  "ajustes": [
   {
      "motivo": "Vehiculo posterior a 2015",
      "porcentaje": 0.15,
      "monto": 75
    }
  ],
  "primaTotal": 575
}
```
⚠️ Si una solicitud se repite con los mismos datos en menos de 5 minutos, se responderá desde Redis (caché).
