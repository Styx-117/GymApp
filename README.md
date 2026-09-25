# GymApp
> [!WARNING]
> Proyecto en desarrollo

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
GimnasioApp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── gimnasio/
│   │   │           ├── App.java
│   │   │           ├── config/
│   │   │           │   └── ConfiguracionDB.java
│   │   │           ├── db/
│   │   │           │   ├── ConexionDB.java
│   │   │           │   └── ScriptBD.java
│   │   │           ├── models/ (Hecho)
│   │   │           │   ├── Usuario.java
│   │   │           │   ├── Socio.java
│   │   │           │   ├── Entrenador.java
│   │   │           │   ├── Plan.java
│   │   │           │   ├── Membresia.java
│   │   │           │   ├── Pago.java
│   │   │           │   ├── Asistencia.java
│   │   │           │   ├── Clase.java
│   │   │           │   ├── ReservaClase.java
│   │   │           │   ├── Rutina.java
│   │   │           │   ├── Ejercicio.java
│   │   │           │   ├── Equipo.java
│   │   │           │   ├── Incidencia.java
│   │   │           │   └── Auditoria.java
│   │   │           ├── structures/
│   │   │           │   ├── arreglos/
│   │   │           │   │   ├── ArregloSocios.java
│   │   │           │   │   └── MatrizHorarios.java
│   │   │           │   ├── tad/
│   │   │           │   │   ├── TADLista.java
│   │   │           │   │   └── TADSocio.java
│   │   │           │   ├── listas/
│   │   │           │   │   ├── Nodo.java
│   │   │           │   │   ├── ListaEnlazada.java
│   │   │           │   │   ├── ListaDoble.java
│   │   │           │   │   └── ListaCircular.java
│   │   │           │   ├── pilas/
│   │   │           │   │   ├── NodoPila.java
│   │   │           │   │   └── Pila.java
│   │   │           │   ├── colas/
│   │   │           │   │   ├── NodoCola.java
│   │   │           │   │   ├── Cola.java
│   │   │           │   │   └── ColaPrioridad.java
│   │   │           │   ├── arboles/
│   │   │           │   │   ├── NodoArbol.java
│   │   │           │   │   ├── ArbolBinario.java
│   │   │           │   │   └── ABB.java
│   │   │           │   └── avl/
│   │   │           │       ├── NodoAVL.java
│   │   │           │       └── ArbolAVL.java
│   │   │           ├── algorithms/
│   │   │           │   ├── Busqueda.java
│   │   │           │   ├── Ordenamiento.java
│   │   │           │   └── Validaciones.java
│   │   │           ├── services/
│   │   │           │   ├── UsuarioService.java
│   │   │           │   ├── SocioService.java
│   │   │           │   ├── MembresiaService.java
│   │   │           │   ├── PagoService.java
│   │   │           │   ├── AsistenciaService.java
│   │   │           │   ├── ClaseService.java
│   │   │           │   ├── RutinaService.java
│   │   │           │   ├── EquipoService.java
│   │   │           │   └── IncidenciaService.java
│   │   │           ├── controllers/
│   │   │           │   ├── LoginController.java
│   │   │           │   ├── SocioController.java
│   │   │           │   ├── MembresiaController.java
│   │   │           │   ├── PagoController.java
│   │   │           │   ├── AsistenciaController.java
│   │   │           │   ├── ClaseController.java
│   │   │           │   ├── RutinaController.java
│   │   │           │   ├── EquipoController.java
│   │   │           │   └── IncidenciaController.java
│   │   │           ├── views/
│   │   │           │   ├── LoginView.java
│   │   │           │   ├── MainView.java
│   │   │           │   ├── SocioView.java
│   │   │           │   ├── MembresiaView.java
│   │   │           │   ├── PagoView.java
│   │   │           │   ├── AsistenciaView.java
│   │   │           │   ├── ClaseView.java
│   │   │           │   ├── RutinaView.java
│   │   │           │   ├── EquipoView.java
│   │   │           │   ├── IncidenciaView.java
│   │   │           │   └── ReporteView.java
│   │   │           ├── dao/
│   │   │           │   ├── UsuarioDAO.java
│   │   │           │   ├── SocioDAO.java
│   │   │           │   ├── MembresiaDAO.java
│   │   │           │   ├── PagoDAO.java
│   │   │           │   ├── AsistenciaDAO.java
│   │   │           │   ├── ClaseDAO.java
│   │   │           │   ├── RutinaDAO.java
│   │   │           │   ├── EquipoDAO.java
│   │   │           │   └── IncidenciaDAO.java
│   │   │           └── utils/
│   │   │               ├── FechaUtil.java
│   │   │               ├── ValidacionUtil.java
│   │   │               └── ReporteUtil.java
│   │   └── resources/
│   │       ├── images/
│   │       ├── styles/
│   │       └── reports/
│   └── test/
│       └── java/
│           └── com/gimnasio/tests/
├── db/
│   ├── script_gimnasio.sql
│   └── backups/
├── docs/
│   ├── diagramas/
│   ├── manuales/
│   └── pruebas/
└── build.gradle / pom.xml```
