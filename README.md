# GymApp
> [!WARNING]
> Proyecto en desarrollo APF2
> Programado y testeado en VS Code

## Ejecutar la aplicación

El proyecto usa Maven y Spring Boot; no se debe ejecutar una clase suelta con `javac`.
Configura estas variables de entorno antes de iniciar la aplicación:

- `GYMAPP_DB_URL`, `GYMAPP_DB_USER` y `GYMAPP_DB_PASSWORD`: conexión a PostgreSQL.
- `RENIEC_API_TOKEN`: token para consultar RENIEC (se mantiene en el backend, no en la interfaz).
- `GYMAPP_API_BASE_URL`: opcional; URL del backend para la consulta del DNI. Por defecto es `http://localhost:8080`.

Desde PowerShell, situado en la raíz del repositorio, configura las variables para la sesión
actual con los datos de tu PostgreSQL (por ejemplo, los de Render):

```powershell
cd .\gymApp
$env:GYMAPP_DB_URL = "jdbc:postgresql://HOST:5432/BASE_DE_DATOS?sslmode=require"
$env:GYMAPP_DB_USER = "USUARIO"
$env:GYMAPP_DB_PASSWORD = "CONTRASENA"
$env:RENIEC_API_TOKEN = "TOKEN_RENIEC"
.\mvnw.cmd spring-boot:run
```

Reemplaza los valores de ejemplo por las credenciales de tu base de datos y tu token; no
los publiques ni los agregues al repositorio. Estas variables solo duran mientras esté
abierta esa terminal. El arranque de Spring Boot inicia también la interfaz gráfica. Al
registrar un socio, ingresa un DNI de 8 dígitos y pulsa **Consultar** para completar el
nombre y los apellidos antes de guardar.

**Proyecto para el curso de Algoritmos y Estructuras de Datos**

## Requerimientos funcionales

### 1. Gestión de socios
  - RF01. Registrar un nuevo socio con sus datos personales y código único.
  - RF02. Buscar un socio mediante su código (búsqueda eficiente vía ABB/AVL).
  - RF03. Actualizar los datos de un socio.
  - RF04. Eliminar o dar de baja a un socio.
  - RF05. Listar todos los socios activos de la sede.
### 2. Gestión de membresías y pagos
  - RF06. Registrar la membresía y el plan contratado por un socio.
  - RF07. Registrar el pago de una membresía.
  - RF08. Consultar los socios cuya membresía está próxima a vencer o ya venció.
### 3. Gestión de clases y reservas
  - RF09. Registrar las clases grupales disponibles en la sede (horario, entrenador, cupo máximo).
  - RF10. Reservar un cupo en una clase respetando el orden de llegada (cola FIFO).
  - RF11. Gestionar una lista de espera cuando la clase alcanza su cupo máximo.
  - RF12. Consultar la matriz de horarios de clases por día/hora.
### 4. Gestión de asistencia
  - RF13. Registrar la asistencia diaria de un socio.
  - RF14. Consultar el historial de asistencia de un socio.
### 5. Gestión de rutinas
  - RF15. Asignar una rutina de ejercicios a un socio.
  - RF16. Consultar y modificar la secuencia de ejercicios de una rutina (lista enlazada).
### 6. Gestión de equipos e incidencias
  - RF17. Registrar una incidencia de mantenimiento de un equipo.
  - RF18. Atender las incidencias según su nivel de prioridad (cola de prioridad).
  - RF19. Consultar el historial de incidencias atendidas.
### 7. Trazabilidad
  - RF20. Registrar en un historial (pila) las últimas acciones realizadas por el personal en el sistema, permitiendo revertir la más reciente.



## Estructura del Proyecto

```
├───main
│   ├───java
│   │   └───com
│   │       └───smartfit
│   │           └───gymApp
│   │               ├───config
│   │               ├───controller
│   │               ├───db
│   │               ├───dto
│   │               ├───model
│   │               ├───repository
│   │               ├───service
│   │               ├───structures
│   │               │   ├───arboles
│   │               │   ├───arreglos
│   │               │   ├───avl
│   │               │   ├───colas
│   │               │   ├───listas
│   │               │   ├───pilas
│   │               │   └───tad
│   │               ├───utils
│   │               └───views
│   └───resources
└───test
    └───java
        └───com
            └───smartfit
                └───gymApp
                    └───service                    
´´´
