Este un prototipo de aplicación Android. Su objetivo es facilitar la consulta y el seguimiento de compromisos de pago, ofrecer pagos simulados y promover la puntualidad mediante reconocimientos.

Funcionalidades:

**Usuario final**

- Registro de cuenta, inicio de sesión y recuperación/cambio de contraseña.
- Consulta de compromisos por empresa, fechas y estados; resumen de importes en CLP.
- Detalle de vencimientos, estado de compromisos y registro de pagos simulados.
- Consulta del historial de pagos y comprobantes disponibles.
- Reconocimientos por alcanzar 1, 3 y 5 pagos puntuales en compromisos distintos.
- Creación y seguimiento de reportes e incidencias.
- Edición de perfil y selección o captura de fotografía.
- Cotizaciones informativas CLP/EUR/USD mediante servicio externo; los compromisos y pagos permanecen en CLP.
- Opción de agregar vencimientos al calendario del dispositivo.

**Agente**

- Inicio de sesión con interfaz y permisos diferentes.
- Consulta de reportes recibidos.
- Toma de casos, registro de respuestas y resolución de reportes.
- Persistencia de cambios para su posterior consulta por el usuario final.

**Administrador**

- Gestión de usuarios y empresas.
- Asignación, consulta, actualización y anulación de compromisos.
- Consulta y administración de operaciones

**Cuentas de demostración**

Se crean al inicializar la base de datos por primera vez:

Usuario final: crojas@correo.ejemplo | PagoFlex123
Agente: alagos@correo.ejemplo | PagoFlex123
Administrador: admin@correo.ejemplo | PagoFlex123

Estas credenciales son exclusivamente para demostración.

**Microservicios/Puertos**

Componente Puerto local Base de datos
Eureka Server 8761 —
API Gateway 8080 —
usuarios-service 8081 pagoflex_usuarios
empresas-service 8082 pagoflex_empresas
compromisos-service 8083 pagoflex_compromisos
pagos-service 8084 pagoflex_pagos
reportes-service 8085 pagoflex_reportes
archivos-service 8086 pagoflex_archivos

**Documentación Swagger**

Con los microservicios encendidos, abrir http://localhost:<puerto>/swagger-ui/index.html en los puertos 8081–8086.

**Evidencias**

El documento de sustento técnico se entrega en 'docs/PagoFlex_Documento_Sustento_Tecnico.docx'.
