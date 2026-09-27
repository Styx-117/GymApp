
-- 1. SOCIO 
CREATE TABLE IF NOT EXISTS socio (
    id_socio         SERIAL PRIMARY KEY,
    codigo_socio     VARCHAR(20) UNIQUE NOT NULL,  -- ej: SM-0001 (clave del ABB/AVL)
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    dni              VARCHAR(8) UNIQUE NOT NULL,
    telefono         VARCHAR(15),
    correo           VARCHAR(100),
    fecha_nacimiento DATE,
    direccion        VARCHAR(200),
    fecha_registro   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo           BOOLEAN DEFAULT TRUE
);

-- 2. USUARIO (login del sistema)
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario      SERIAL PRIMARY KEY,
    id_socio        INT REFERENCES socio(id_socio) ON DELETE SET NULL,
    nombre_usuario  VARCHAR(50) UNIQUE NOT NULL,
    contrasena      VARCHAR(255) NOT NULL,
    rol             VARCHAR(30) NOT NULL DEFAULT 'socio',  -- ADMINISTRADOR, RECEPCIONISTA, ENTRENADOR, SOCIO
    estado          BOOLEAN DEFAULT TRUE
);

-- 3. ENTRENADOR
CREATE TABLE IF NOT EXISTS entrenador (
    id_entrenador SERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    apellido      VARCHAR(100) NOT NULL,
    especialidad  VARCHAR(100),
    telefono      VARCHAR(15),
    activo        BOOLEAN DEFAULT TRUE
);

-- 4. PLAN
CREATE TABLE IF NOT EXISTS plan (
    id_plan         SERIAL PRIMARY KEY,
    nombre          VARCHAR(50) NOT NULL,
    descripcion     VARCHAR(200),
    duracion_meses  INT NOT NULL,
    precio          NUMERIC(10,2) NOT NULL
);

-- 5. MEMBRESIA
CREATE TABLE IF NOT EXISTS membresia (
    id_membresia  SERIAL PRIMARY KEY,
    id_socio      INT NOT NULL REFERENCES socio(id_socio) ON DELETE CASCADE,
    id_plan       INT NOT NULL REFERENCES plan(id_plan),
    fecha_inicio  DATE NOT NULL,
    fecha_fin     DATE NOT NULL,
    estado        VARCHAR(20) DEFAULT 'ACTIVA'  -- ACTIVA, VENCIDA, POR_VENCER
);

-- 6. PAGO
CREATE TABLE IF NOT EXISTS pago (
    id_pago        SERIAL PRIMARY KEY,
    id_membresia   INT NOT NULL REFERENCES membresia(id_membresia) ON DELETE CASCADE,
    monto          NUMERIC(10,2) NOT NULL,
    fecha_pago     DATE DEFAULT CURRENT_DATE,
    metodo_pago    VARCHAR(20)  -- EFECTIVO, TARJETA, TRANSFERENCIA
);

-- 7. ASISTENCIA
CREATE TABLE IF NOT EXISTS asistencia (
    id_asistencia SERIAL PRIMARY KEY,
    id_socio      INT NOT NULL REFERENCES socio(id_socio) ON DELETE CASCADE,
    fecha         DATE DEFAULT CURRENT_DATE,
    hora_entrada  TIME,
    hora_salida   TIME
);

-- 8. CLASE
 
CREATE TABLE IF NOT EXISTS clase (
    id_clase      SERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    id_entrenador INT REFERENCES entrenador(id_entrenador),
    dia_semana    VARCHAR(20),
    hora_inicio   TIME,
    hora_fin      TIME,
    cupo_maximo   INT DEFAULT 20,
    cupo_actual   INT DEFAULT 0
);

-- 9. RESERVA_CLASE

CREATE TABLE IF NOT EXISTS reserva_clase (
    id_reserva           SERIAL PRIMARY KEY,
    id_clase             INT NOT NULL REFERENCES clase(id_clase) ON DELETE CASCADE,
    id_socio             INT NOT NULL REFERENCES socio(id_socio) ON DELETE CASCADE,
    fecha_reserva        DATE DEFAULT CURRENT_DATE,
    estado               VARCHAR(20) DEFAULT 'CONFIRMADA',  -- CONFIRMADA, LISTA_ESPERA, CANCELADA
    posicion_lista_espera INT DEFAULT -1
);

-- 10. RUTINA
CREATE TABLE IF NOT EXISTS rutina (
    id_rutina        SERIAL PRIMARY KEY,
    id_socio         INT NOT NULL REFERENCES socio(id_socio) ON DELETE CASCADE,
    id_entrenador    INT REFERENCES entrenador(id_entrenador),
    nombre           VARCHAR(100),
    fecha_asignacion DATE DEFAULT CURRENT_DATE,
    observaciones    VARCHAR(200)
);

-- 11. EJERCICIO

CREATE TABLE IF NOT EXISTS ejercicio (
    id_ejercicio     SERIAL PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    series           INT,
    repeticiones     INT,
    descanso_segundos INT,
    grupo_muscular   VARCHAR(50)
);


-- 12. EQUIPO

CREATE TABLE IF NOT EXISTS equipo (
    codigo_equipo     VARCHAR(20) PRIMARY KEY,  -- ej: EQ-0001 (clave del ABB)
    nombre            VARCHAR(100) NOT NULL,
    tipo              VARCHAR(50),
    ubicacion         VARCHAR(100),
    fecha_adquisicion DATE,
    estado            VARCHAR(30) DEFAULT 'OPERATIVO'  -- OPERATIVO, EN_MANTENIMIENTO, FUERA_DE_SERVICIO
);

-- 13. INCIDENCIA

CREATE TABLE IF NOT EXISTS incidencia (
    id_incidencia SERIAL PRIMARY KEY,
    codigo_equipo VARCHAR(20) REFERENCES equipo(codigo_equipo) ON DELETE CASCADE,
    descripcion   VARCHAR(200),
    prioridad     INT DEFAULT 3,  -- 1=alta, 2=media, 3=baja
    fecha_reporte DATE DEFAULT CURRENT_DATE,
    fecha_atencion DATE,
    estado        VARCHAR(20) DEFAULT 'PENDIENTE'  -- PENDIENTE, ATENDIDA
);

-- 14. AUDITORIA
CREATE TABLE IF NOT EXISTS auditoria (
    id_auditoria SERIAL PRIMARY KEY,
    id_usuario   INT REFERENCES usuario(id_usuario),
    accion       VARCHAR(100),
    fecha_hora   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    detalle      VARCHAR(255)
);
