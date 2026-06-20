# Sistema de Caja de Ahorro y Crédito

Backend desarrollado en Java con Spring Boot para la gestión de un sistema de caja de ahorro y crédito. El proyecto implementa servicios REST documentados con Swagger y organizados bajo una estructura por capas: modelo, repositorio, servicio y controlador.

## Información general

* Proyecto: Sistema de Caja de Ahorro y Crédito
* Materia: Ingeniería de Software
* Tarea: T02.03 Construcción de aplicación de software
* Grupo: Grupo 05
* Framework: Spring Boot
* Lenguaje: Java
* Base de datos: PostgreSQL en Neon
* Documentación de servicios: Swagger / OpenAPI

## Integrantes

* Misael Ariel Delgado Reyes
* Denisse Andrea Pazmiño Méndez
* Alex Rafael Balon Garofalo
* Andres Paul Moran Castillo

## Objetivo del proyecto

Construir el backend del Sistema de Caja de Ahorro y Crédito, cumpliendo los requerimientos definidos en la Tarea T02.01 y el diseño planteado en la Tarea T02.02. El sistema busca gestionar módulos relacionados con usuarios, perfiles, socios, aportaciones, créditos, amortización y operaciones financieras básicas.

## Arquitectura del backend

El proyecto sigue una estructura por capas:

```text
controller  -> Expone los endpoints REST.
service     -> Contiene la lógica de negocio.
repository  -> Gestiona el acceso a la base de datos.
model       -> Define las entidades del sistema.
request     -> Define los objetos de entrada para los servicios.
response    -> Define los objetos de respuesta.
config      -> Configuración de seguridad y Swagger.
```

## Tecnologías utilizadas

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* PostgreSQL
* Neon Database
* Maven
* Swagger / OpenAPI
* Git y GitHub

## Ejecución del proyecto

Para ejecutar el proyecto localmente:

```bash
./mvnw spring-boot:run
```

Si el archivo `mvnw` no tiene permisos de ejecución:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

## Swagger

La documentación de los endpoints está disponible en:

```text
http://localhost:8080/swagger-ui/index.html
```

Credenciales de prueba:

```text
Usuario: admin
Contraseña: n12345
```

## Módulo de Créditos y Amortización

Responsable del módulo:

```text
Denisse Andrea Pazmiño Méndez
```

Este módulo implementa el ciclo de vida básico de un crédito dentro del Sistema de Caja de Ahorro. Permite registrar solicitudes, simular tablas de amortización, aprobar créditos, rechazar solicitudes, desembolsar préstamos, consultar cuotas y registrar pagos.

## Alcance implementado del módulo de créditos

El módulo cubre las siguientes funcionalidades:

* Registro de solicitud de crédito en estado `PENDIENTE`.
* Simulación de tabla de amortización sin guardar datos en base.
* Aprobación de créditos pendientes.
* Generación automática de cuotas al aprobar el crédito.
* Rechazo de solicitudes pendientes.
* Desembolso de créditos aprobados.
* Consulta de créditos por ID.
* Consulta de créditos por cédula del socio.
* Consulta de tabla de amortización.
* Registro de pago de cuotas.
* Cambio automático a estado `LIQUIDADO` cuando todas las cuotas son pagadas.
* Marcado de créditos en estado `EN_MORA` cuando existen cuotas vencidas.

## Estados del crédito

El crédito maneja los siguientes estados:

```text
PENDIENTE
APROBADO
RECHAZADO
VIGENTE
EN_MORA
LIQUIDADO
```

Flujo principal:

```text
PENDIENTE -> APROBADO -> VIGENTE -> LIQUIDADO
PENDIENTE -> RECHAZADO
VIGENTE -> EN_MORA -> VIGENTE / LIQUIDADO
```

## Estados de cuota

```text
PENDIENTE
PAGADA
VENCIDA
```

## Sistemas de amortización soportados

El módulo permite generar tablas de amortización mediante:

```text
FRANCES
ALEMAN
```

En el sistema francés se calcula una cuota base estable, separando capital, interés, seguro de desgravamen y saldo pendiente.

En el sistema alemán se calcula una amortización de capital fija por período, con intereses decrecientes según el saldo pendiente.

## Estructura del módulo de créditos

```text
src/main/java/com/grupo5/caja_ahorro
├── controller
│   └── CreditoRestController.java
├── model
│   ├── Credito.java
│   ├── Cuota.java
│   ├── EstadoCredito.java
│   ├── EstadoCuota.java
│   └── SistemaAmortizacion.java
├── repository
│   ├── CreditoRepository.java
│   └── CuotaRepository.java
├── request
│   ├── AprobarCreditoRequest.java
│   ├── PagoCuotaRequest.java
│   ├── RechazarCreditoRequest.java
│   └── SolicitudCreditoRequest.java
├── response
│   └── CuotaAmortizacionResponse.java
└── service
    ├── IAmortizacionService.java
    ├── AmortizacionServiceImpl.java
    ├── ICreditoService.java
    └── CreditoServiceImpl.java
```

## Endpoints principales del módulo de créditos

| Método | Endpoint                                    | Descripción                              |
| ------ | ------------------------------------------- | ---------------------------------------- |
| GET    | `/api/v1/creditos/consultar`                | Consulta todos los créditos registrados. |
| GET    | `/api/v1/creditos/consultar/{idCredito}`    | Consulta un crédito por ID.              |
| GET    | `/api/v1/creditos/socio/{cedulaSocio}`      | Consulta créditos asociados a un socio.  |
| POST   | `/api/v1/creditos/simular`                  | Simula una tabla de amortización.        |
| POST   | `/api/v1/creditos/solicitar`                | Registra una solicitud de crédito.       |
| PUT    | `/api/v1/creditos/{idCredito}/aprobar`      | Aprueba una solicitud pendiente.         |
| PUT    | `/api/v1/creditos/{idCredito}/rechazar`     | Rechaza una solicitud pendiente.         |
| PUT    | `/api/v1/creditos/{idCredito}/desembolsar`  | Desembolsa un crédito aprobado.          |
| GET    | `/api/v1/creditos/{idCredito}/amortizacion` | Consulta la tabla de amortización.       |
| POST   | `/api/v1/creditos/cuotas/{idCuota}/pagar`   | Registra el pago de una cuota.           |
| PUT    | `/api/v1/creditos/{idCredito}/marcar-mora`  | Marca un crédito como en mora.           |

## Ejemplo de simulación de crédito

Endpoint:

```http
POST /api/v1/creditos/simular
```

Body:

```json
{
  "cedulaSocio": "0923456789",
  "numeroCuentaDesembolso": "AHO-0001",
  "montoSolicitado": 1000.00,
  "plazoMeses": 6,
  "tasaInteresAnual": 12.00,
  "seguroDesgravamen": 0.50,
  "sistemaAmortizacion": "FRANCES",
  "comentarioOficial": "Simulación inicial del crédito"
}
```

Resultado esperado:

```text
El sistema devuelve una lista de cuotas con capital, interés, seguro de desgravamen, valor total de cuota, fecha de vencimiento y saldo de capital.
```

## Flujo recomendado de prueba

```text
1. Simular crédito.
2. Registrar solicitud.
3. Aprobar crédito.
4. Desembolsar crédito.
5. Consultar tabla de amortización.
6. Pagar una cuota.
7. Consultar crédito actualizado.
```

## Validaciones implementadas

El módulo valida:

* Cédula obligatoria de 10 dígitos.
* Monto solicitado mayor a cero.
* Plazo mayor a cero.
* Tasa de interés anual no negativa.
* Seguro de desgravamen no negativo.
* Sistema de amortización obligatorio.
* Solo créditos `PENDIENTE` pueden aprobarse o rechazarse.
* Solo créditos `APROBADO` pueden desembolsarse.
* Solo créditos `VIGENTE` o `EN_MORA` permiten pago de cuotas.
* Una cuota pagada no puede volver a pagarse.
* El monto pagado debe cubrir el valor total de la cuota.

## Control de versiones

El proyecto utiliza Git y GitHub para el trabajo colaborativo. Cada integrante debe trabajar en una rama propia y subir sus avances mediante commits.

Rama del módulo de créditos:

```text
feature/ciclo-credito-amortizacion
```

## Evidencias de prueba

El módulo fue probado desde Swagger mediante los siguientes flujos:

* Simulación de crédito.
* Registro de solicitud.
* Aprobación de crédito.
* Desembolso de crédito.
* Consulta de tabla de amortización.
* Pago de cuota.
* Consulta del crédito actualizado.

## Relación con los requerimientos

El módulo implementa la parte correspondiente al ciclo de vida del crédito y amortización, según lo definido en la especificación del sistema. Se cubren las operaciones de solicitud, aprobación, tabla de amortización, desembolso y pago de cuotas.

## Estado actual

El backend cuenta con servicios funcionales para el módulo de perfiles y el módulo de créditos. El módulo de créditos queda listo para integrarse posteriormente con socios, cuentas de ahorro, aportaciones y contabilidad.
