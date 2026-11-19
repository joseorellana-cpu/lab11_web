# Lab 11 – Creación de Microservicios

Spring Boot 4.0.9 · Spring Cloud 2025.1.4 (Eureka) · Java 25 · Maven · MySQL · YAML con perfiles `dev` y `prod`.

## Estructura

```
eureka-server            8761   registro y descubrimiento de servicios
microservicios/          <- procedimiento de la guía (tienda)
  categoria-service      8082   bd_categoria_dev
  cliente-service        8081   bd_cliente_dev
  producto-service       8083   bd_producto_dev
  pedido-service         8084   bd_pedido_dev
clinica/                 <- actividad: clínica "Salud Integral"
  paciente-service       8086   bd_paciente_dev
  medico-service         8087   bd_medico_dev   (especialidades, médicos y horarios)
  cita-service           8088   bd_cita_dev     (citas y atenciones; consume paciente y médico)
database/                scripts SQL
postman/                 colección con todas las pruebas
```

Cada microservicio tiene la estructura `controller`, `model`, `dto`, `exception`, `repository`, `services`, y su configuración en `application.yml` (perfil activo), `application-dev.yml` y `application-prod.yml` (puertos 909x y servidor remoto).

## Eureka: registro, descubrimiento y comunicación

- `eureka-server` (`@EnableEurekaServer`, puerto 8761). Panel: http://localhost:8761
- Cada servicio tiene `spring-cloud-starter-netflix-eureka-client` y se registra con su `spring.application.name`.
- Los servicios se llaman por **nombre**, no por host y puerto: `RestClient` con `@LoadBalanced` (`config/RestClientConfig`) y URL `http://categoria-service`, `http://cliente-service`, etc. Spring Cloud LoadBalancer consulta a Eureka y elige la instancia.

| Consumidor | Consume a | Para qué |
|---|---|---|
| producto-service | categoria-service | validar la categoría al registrar y mostrarla al consultar |
| pedido-service | cliente-service, producto-service | validar cliente activo y stock; tomar el precio actual y calcular el total; `GET /api/pedidos/{id}/detalle` |
| cita-service | paciente-service, medico-service | validar paciente y médico activos; `GET /api/citas/{id}/detalle` |

Respuestas HTTP: 200/201 (éxito), 400 (validación del DTO o parámetro inválido), 404 (no existe, también en el servicio remoto), 409 (duplicado, stock insuficiente, estado inválido), 500 (error no previsto), 503 (servicio consumido caído).

## Ejecución

0. Iniciar primero `eureka-server`.
1. Ejecutar `database/bases_de_datos.sql` en MySQL (las URL también usan `createDatabaseIfNotExist=true`).
2. Abrir la carpeta en IntelliJ y ejecutar la clase principal de cada servicio.
3. Para la clínica, iniciar primero `paciente-service` y `medico-service`, y luego `cita-service`.
4. Importar `postman/Lab11-Microservicios.postman_collection.json`.

## Actividad – Clínica Salud Integral

| Microservicio | Endpoints |
|---|---|
| paciente-service | `POST/GET /api/pacientes`, `GET/PUT/DELETE /api/pacientes/{id}` |
| medico-service | `POST/GET /api/medicos`, `GET/PUT/DELETE /api/medicos/{id}`, `GET/POST /api/medicos/{id}/horarios`, CRUD `/api/especialidades` |
| cita-service | `POST/GET /api/citas` (filtros `?idPaciente=` / `?idMedico=`), `GET /api/citas/{id}`, `GET /api/citas/{id}/detalle`, `PATCH /api/citas/{id}/estado`, `PATCH /api/citas/{id}/cancelar`, `POST/GET /api/citas/{id}/atencion` |

**Comunicación entre servicios:** al registrar una cita, `cita-service` llama con `RestClient` a `paciente-service` y `medico-service` para comprobar que existen y están activos (estado `A`). `GET /api/citas/{id}/detalle` devuelve la cita junto con los datos del paciente y del médico.

**Estados de la cita:**

```
PROGRAMADA ──► CONFIRMADA ──► ATENDIDA
     │              │
     └──────────────┴──► CANCELADA
```

`ATENDIDA` y `CANCELADA` son finales. Registrar una atención marca la cita como `ATENDIDA`.

**Reglas y errores:**

| Caso | Respuesta |
|---|---|
| Datos inválidos | 400 con el detalle por campo |
| Paciente, médico o cita inexistente | 404 |
| Cambio de estado no permitido, médico ocupado en esa fecha y hora, atención duplicada | 409 |
| `paciente-service` o `medico-service` apagado | 503 |

## Observaciones

1. Se usó Spring Boot 4.0.9 en lugar de 4.1.0: Spring Cloud 2025.1 (Eureka) solo es compatible con Spring Boot 4.0.x.
2. En el código de la guía, `Pedido` y `DetallePedido` se referencian mutuamente y el JSON entraría en un bucle infinito; se agregó `@JsonIgnore` en `DetallePedido.pedido`.
3. Las llaves foráneas entre tablas de distintos microservicios (`cita` → `paciente`/`medico`) no pueden existir en bases separadas; la integridad se valida mediante llamadas HTTP.
4. Los controladores de la guía comparaban con `null` tras `service.obtener()`, pero ese método lanza `ResourceNotFoundException`; el 404 lo devuelve el `GlobalExceptionHandler`.
5. En la terminal de Windows, curl envía mal las tildes; en Postman o con un archivo UTF-8 (`--data-binary @archivo.json`) se guardan correctamente.

## Conclusiones

1. Separar el sistema en microservicios permite desarrollar, desplegar y escalar cada área del negocio de forma independiente.
2. Tener una base de datos por servicio desacopla los datos, pero obliga a validar las relaciones mediante las APIs.
3. Los perfiles YAML (`dev`/`prod`) cambian puertos y conexiones sin tocar el código.
4. Un manejador global de excepciones da respuestas uniformes (400/404/409/503) y facilita consumir los servicios.
5. Modelar los estados de la cita como una máquina de estados evita operaciones incoherentes, como atender una cita cancelada.
