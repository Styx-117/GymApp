
-- 1. cliente
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(8) UNIQUE NOT NULL,
    telefono VARCHAR(15),
    correo VARCHAR(100),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. usuario
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario SERIAL PRIMARY KEY,
    id_cliente INT REFERENCES cliente(id_cliente) ON DELETE SET NULL,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL DEFAULT 'cliente',
    estado BOOLEAN DEFAULT TRUE
);

-- 3. entrenador
CREATE TABLE IF NOT EXISTS entrenador (
    id_entrenador SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100),
    telefono VARCHAR(15),
    activo BOOLEAN DEFAULT TRUE
);

-- 4. plan
CREATE TABLE IF NOT EXISTS plan (
    id_plan SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(200),
    duracion_meses INT NOT NULL,
    precio NUMERIC(10,2) NOT NULL
);

-- 5. membresia
CREATE TABLE IF NOT EXISTS membresia (
    id_membresia SERIAL PRIMARY KEY,
    codigo_socio VARCHAR(20) NOT NULL,
    id_plan INT REFERENCES plan(id_plan),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVA'
);

-- 6. pago
CREATE TABLE IF NOT EXISTS pago (
    id_pago SERIAL PRIMARY KEY,
    id_membresia INT REFERENCES membresia(id_membresia),
    monto NUMERIC(10,2) NOT NULL,
    fecha_pago DATE DEFAULT CURRENT_DATE,
    metodo_pago VARCHAR(20)
);

-- 7. asistencia
CREATE TABLE IF NOT EXISTS asistencia (
    id_asistencia SERIAL PRIMARY KEY,
    codigo_socio VARCHAR(20) NOT NULL,
    fecha DATE DEFAULT CURRENT_DATE,
    hora_entrada TIME,
    hora_salida TIME
);

-- 8. clase
CREATE TABLE IF NOT EXISTS clase (
    id_clase SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    id_entrenador INT REFERENCES entrenador(id_entrenador),
    dia_semana VARCHAR(20),
    hora_inicio TIME,
    hora_fin TIME,
    cupo_maximo INT DEFAULT 20,
    cupo_actual INT DEFAULT 0
);

-- 9. reserva_clase
CREATE TABLE IF NOT EXISTS reserva_clase (
    id_reserva SERIAL PRIMARY KEY,
    id_clase INT REFERENCES clase(id_clase),
    codigo_socio VARCHAR(20) NOT NULL,
    fecha_reserva DATE DEFAULT CURRENT_DATE,
    estado VARCHAR(20) DEFAULT 'CONFIRMADA',
    posicion_lista_espera INT DEFAULT -1
);

-- 10. rutina
CREATE TABLE IF NOT EXISTS rutina (
    id_rutina SERIAL PRIMARY KEY,
    codigo_socio VARCHAR(20) NOT NULL,
    nombre VARCHAR(100),
    fecha_asignacion DATE DEFAULT CURRENT_DATE,
    id_entrenador INT REFERENCES entrenador(id_entrenador)
);

-- 11. ejercicio
CREATE TABLE IF NOT EXISTS ejercicio (
    id_ejercicio SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    series INT,
    repeticiones INT,
    descanso_segundos INT,
    grupo_muscular VARCHAR(50)
);

-- 12. equipo
CREATE TABLE IF NOT EXISTS equipo (
    codigo_equipo VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50),
    ubicacion VARCHAR(100),
    fecha_adquisicion DATE,
    estado VARCHAR(30) DEFAULT 'OPERATIVO'
);

-- 13. incidencia
CREATE TABLE IF NOT EXISTS incidencia (
    id_incidencia SERIAL PRIMARY KEY,
    codigo_equipo VARCHAR(20) REFERENCES equipo(codigo_equipo),
    descripcion VARCHAR(200),
    prioridad INT DEFAULT 3,
    fecha_reporte DATE DEFAULT CURRENT_DATE,
    fecha_atencion DATE,
    estado VARCHAR(20) DEFAULT 'PENDIENTE'
);

-- 14. auditoria
CREATE TABLE IF NOT EXISTS auditoria (
    id_auditoria SERIAL PRIMARY KEY,
    id_usuario INT REFERENCES usuario(id_usuario),
    accion VARCHAR(100),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    detalle VARCHAR(255)
);


-- DATOS DE PRUEBA

INSERT INTO cliente (nombre, apellido, dni, telefono, correo) VALUES
('Juan', 'Perez', '12345678', '987654321', 'juan@mail.com')
ON CONFLICT (dni) DO NOTHING;

INSERT INTO usuario (id_cliente, nombre_usuario, contrasena, rol) VALUES
(1, 'jperez', '123456', 'cliente'),
(NULL, 'admin', 'admin123', 'administrador')
ON CONFLICT (nombre_usuario) DO NOTHING;

INSERT INTO plan (nombre, descripcion, duracion_meses, precio) VALUES
('Mensual', 'Acceso 1 mes', 1, 80.00),
('Trimestral', 'Acceso 3 meses', 3, 220.00),
('Anual', 'Acceso 12 meses', 12, 800.00)
ON CONFLICT DO NOTHING;