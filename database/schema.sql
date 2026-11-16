--
-- PostgreSQL database dump
--

\restrict tryrb6nx8NGS7tbtpdW630n9YsxgflC7YbA8UzHTfJYzlLv5kh9fuThTRkM9adS

-- Dumped from database version 18.6 (Debian 18.6-1.pgdg12+2)
-- Dumped by pg_dump version 18.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS public;


--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: asistencia; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.asistencia (
    id_asistencia integer NOT NULL,
    codigo_socio character varying(20) NOT NULL,
    fecha date DEFAULT CURRENT_DATE,
    hora_entrada time without time zone,
    hora_salida time without time zone
);


--
-- Name: asistencia_id_asistencia_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.asistencia_id_asistencia_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: asistencia_id_asistencia_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.asistencia_id_asistencia_seq OWNED BY public.asistencia.id_asistencia;


--
-- Name: auditoria; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.auditoria (
    id_auditoria integer NOT NULL,
    id_usuario integer,
    accion character varying(100),
    fecha_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    detalle character varying(255),
    activo boolean DEFAULT true NOT NULL
);


--
-- Name: auditoria_id_auditoria_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.auditoria_id_auditoria_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: auditoria_id_auditoria_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.auditoria_id_auditoria_seq OWNED BY public.auditoria.id_auditoria;


--
-- Name: clase; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clase (
    id_clase integer NOT NULL,
    nombre character varying(100) NOT NULL,
    id_entrenador integer,
    dia_semana character varying(20),
    hora_inicio time without time zone,
    hora_fin time without time zone,
    cupo_maximo integer DEFAULT 20,
    cupo_actual integer DEFAULT 0,
    activo boolean DEFAULT true NOT NULL
);


--
-- Name: clase_id_clase_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.clase_id_clase_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: clase_id_clase_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.clase_id_clase_seq OWNED BY public.clase.id_clase;


--
-- Name: cliente; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cliente (
    id_cliente integer NOT NULL,
    nombre character varying(100) NOT NULL,
    apellido character varying(100) NOT NULL,
    dni character varying(8) NOT NULL,
    telefono character varying(15),
    correo character varying(100),
    fecha_registro date DEFAULT CURRENT_DATE
);


--
-- Name: cliente_id_cliente_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cliente_id_cliente_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cliente_id_cliente_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cliente_id_cliente_seq OWNED BY public.cliente.id_cliente;


--
-- Name: ejercicio; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ejercicio (
    id_ejercicio integer NOT NULL,
    nombre character varying(100) NOT NULL,
    series integer,
    repeticiones integer,
    descanso_segundos integer,
    grupo_muscular character varying(50)
);


--
-- Name: ejercicio_id_ejercicio_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.ejercicio_id_ejercicio_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ejercicio_id_ejercicio_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.ejercicio_id_ejercicio_seq OWNED BY public.ejercicio.id_ejercicio;


--
-- Name: entrenador; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.entrenador (
    id_entrenador integer NOT NULL,
    nombre character varying(100) NOT NULL,
    apellido character varying(100) NOT NULL,
    especialidad character varying(100),
    telefono character varying(15),
    activo boolean DEFAULT true
);


--
-- Name: entrenador_id_entrenador_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.entrenador_id_entrenador_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: entrenador_id_entrenador_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.entrenador_id_entrenador_seq OWNED BY public.entrenador.id_entrenador;


--
-- Name: equipo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.equipo (
    codigo_equipo character varying(20) NOT NULL,
    nombre character varying(100) NOT NULL,
    tipo character varying(50),
    ubicacion character varying(100),
    fecha_adquisicion date,
    estado character varying(30) DEFAULT 'OPERATIVO'::character varying
);


--
-- Name: incidencia; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.incidencia (
    id_incidencia integer NOT NULL,
    codigo_equipo character varying(20),
    descripcion character varying(200),
    prioridad integer DEFAULT 3,
    fecha_reporte date DEFAULT CURRENT_DATE,
    fecha_atencion date,
    estado character varying(20) DEFAULT 'PENDIENTE'::character varying
);


--
-- Name: incidencia_id_incidencia_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.incidencia_id_incidencia_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: incidencia_id_incidencia_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.incidencia_id_incidencia_seq OWNED BY public.incidencia.id_incidencia;


--
-- Name: membresia; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.membresia (
    id_membresia integer NOT NULL,
    codigo_socio character varying(20) NOT NULL,
    id_plan integer,
    fecha_inicio date NOT NULL,
    fecha_fin date NOT NULL,
    estado character varying(20) DEFAULT 'ACTIVA'::character varying
);


--
-- Name: membresia_id_membresia_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.membresia_id_membresia_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: membresia_id_membresia_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.membresia_id_membresia_seq OWNED BY public.membresia.id_membresia;


--
-- Name: pago; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pago (
    id_pago integer NOT NULL,
    id_membresia integer,
    monto numeric(10,2) NOT NULL,
    fecha_pago date DEFAULT CURRENT_DATE,
    metodo_pago character varying(20)
);


--
-- Name: pago_id_pago_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.pago_id_pago_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pago_id_pago_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.pago_id_pago_seq OWNED BY public.pago.id_pago;


--
-- Name: plan; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.plan (
    id_plan integer NOT NULL,
    nombre character varying(50) NOT NULL,
    descripcion character varying(200),
    duracion_meses integer NOT NULL,
    precio numeric(10,2) NOT NULL
);


--
-- Name: plan_id_plan_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.plan_id_plan_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: plan_id_plan_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.plan_id_plan_seq OWNED BY public.plan.id_plan;


--
-- Name: reserva_clase; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reserva_clase (
    id_reserva integer NOT NULL,
    id_clase integer,
    codigo_socio character varying(20) NOT NULL,
    fecha_reserva date DEFAULT CURRENT_DATE,
    estado character varying(20) DEFAULT 'CONFIRMADA'::character varying,
    posicion_lista_espera integer DEFAULT '-1'::integer
);


--
-- Name: reserva_clase_id_reserva_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.reserva_clase_id_reserva_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: reserva_clase_id_reserva_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.reserva_clase_id_reserva_seq OWNED BY public.reserva_clase.id_reserva;


--
-- Name: rutina; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rutina (
    id_rutina integer NOT NULL,
    codigo_socio character varying(20) NOT NULL,
    nombre character varying(100),
    fecha_asignacion date DEFAULT CURRENT_DATE,
    id_entrenador integer
);


--
-- Name: rutina_id_rutina_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.rutina_id_rutina_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: rutina_id_rutina_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.rutina_id_rutina_seq OWNED BY public.rutina.id_rutina;


--
-- Name: socio; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.socio (
    id_socio bigint NOT NULL,
    activo boolean NOT NULL,
    apellido character varying(100) NOT NULL,
    codigo_socio character varying(20) NOT NULL,
    correo character varying(100),
    direccion character varying(200),
    dni character varying(8) NOT NULL,
    fecha_nacimiento date,
    fecha_registro timestamp(6) without time zone,
    nombre character varying(100) NOT NULL,
    telefono character varying(15)
);


--
-- Name: socio_id_socio_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.socio ALTER COLUMN id_socio ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.socio_id_socio_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: usuario; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuario (
    id_usuario integer NOT NULL,
    id_cliente integer,
    nombre_usuario character varying(50) NOT NULL,
    contrasena character varying(255) NOT NULL,
    rol character varying(30) DEFAULT 'cliente'::character varying NOT NULL,
    estado boolean DEFAULT true
);


--
-- Name: usuario_id_usuario_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.usuario_id_usuario_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: usuario_id_usuario_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.usuario_id_usuario_seq OWNED BY public.usuario.id_usuario;


--
-- Name: asistencia id_asistencia; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asistencia ALTER COLUMN id_asistencia SET DEFAULT nextval('public.asistencia_id_asistencia_seq'::regclass);


--
-- Name: auditoria id_auditoria; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.auditoria ALTER COLUMN id_auditoria SET DEFAULT nextval('public.auditoria_id_auditoria_seq'::regclass);


--
-- Name: clase id_clase; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clase ALTER COLUMN id_clase SET DEFAULT nextval('public.clase_id_clase_seq'::regclass);


--
-- Name: cliente id_cliente; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cliente ALTER COLUMN id_cliente SET DEFAULT nextval('public.cliente_id_cliente_seq'::regclass);


--
-- Name: ejercicio id_ejercicio; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ejercicio ALTER COLUMN id_ejercicio SET DEFAULT nextval('public.ejercicio_id_ejercicio_seq'::regclass);


--
-- Name: entrenador id_entrenador; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.entrenador ALTER COLUMN id_entrenador SET DEFAULT nextval('public.entrenador_id_entrenador_seq'::regclass);


--
-- Name: incidencia id_incidencia; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.incidencia ALTER COLUMN id_incidencia SET DEFAULT nextval('public.incidencia_id_incidencia_seq'::regclass);


--
-- Name: membresia id_membresia; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.membresia ALTER COLUMN id_membresia SET DEFAULT nextval('public.membresia_id_membresia_seq'::regclass);


--
-- Name: pago id_pago; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pago ALTER COLUMN id_pago SET DEFAULT nextval('public.pago_id_pago_seq'::regclass);


--
-- Name: plan id_plan; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.plan ALTER COLUMN id_plan SET DEFAULT nextval('public.plan_id_plan_seq'::regclass);


--
-- Name: reserva_clase id_reserva; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reserva_clase ALTER COLUMN id_reserva SET DEFAULT nextval('public.reserva_clase_id_reserva_seq'::regclass);


--
-- Name: rutina id_rutina; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rutina ALTER COLUMN id_rutina SET DEFAULT nextval('public.rutina_id_rutina_seq'::regclass);


--
-- Name: usuario id_usuario; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario ALTER COLUMN id_usuario SET DEFAULT nextval('public.usuario_id_usuario_seq'::regclass);


--
-- Name: asistencia asistencia_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asistencia
    ADD CONSTRAINT asistencia_pkey PRIMARY KEY (id_asistencia);


--
-- Name: auditoria auditoria_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.auditoria
    ADD CONSTRAINT auditoria_pkey PRIMARY KEY (id_auditoria);


--
-- Name: clase clase_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clase
    ADD CONSTRAINT clase_pkey PRIMARY KEY (id_clase);


--
-- Name: cliente cliente_dni_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT cliente_dni_key UNIQUE (dni);


--
-- Name: cliente cliente_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT cliente_pkey PRIMARY KEY (id_cliente);


--
-- Name: ejercicio ejercicio_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ejercicio
    ADD CONSTRAINT ejercicio_pkey PRIMARY KEY (id_ejercicio);


--
-- Name: entrenador entrenador_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.entrenador
    ADD CONSTRAINT entrenador_pkey PRIMARY KEY (id_entrenador);


--
-- Name: equipo equipo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.equipo
    ADD CONSTRAINT equipo_pkey PRIMARY KEY (codigo_equipo);


--
-- Name: incidencia incidencia_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.incidencia
    ADD CONSTRAINT incidencia_pkey PRIMARY KEY (id_incidencia);


--
-- Name: membresia membresia_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.membresia
    ADD CONSTRAINT membresia_pkey PRIMARY KEY (id_membresia);


--
-- Name: pago pago_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pago
    ADD CONSTRAINT pago_pkey PRIMARY KEY (id_pago);


--
-- Name: plan plan_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.plan
    ADD CONSTRAINT plan_pkey PRIMARY KEY (id_plan);


--
-- Name: reserva_clase reserva_clase_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reserva_clase
    ADD CONSTRAINT reserva_clase_pkey PRIMARY KEY (id_reserva);


--
-- Name: rutina rutina_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rutina
    ADD CONSTRAINT rutina_pkey PRIMARY KEY (id_rutina);


--
-- Name: socio socio_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio
    ADD CONSTRAINT socio_pkey PRIMARY KEY (id_socio);


--
-- Name: socio ukiyfehmpa2qw82c2h79wve9aab; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio
    ADD CONSTRAINT ukiyfehmpa2qw82c2h79wve9aab UNIQUE (dni);


--
-- Name: socio uksrqk2t9evam5ggh5gxg2qfupw; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio
    ADD CONSTRAINT uksrqk2t9evam5ggh5gxg2qfupw UNIQUE (codigo_socio);


--
-- Name: usuario usuario_nombre_usuario_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_nombre_usuario_key UNIQUE (nombre_usuario);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id_usuario);


--
-- Name: reserva_vigente_socio_clase; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX reserva_vigente_socio_clase ON public.reserva_clase USING btree (id_clase, codigo_socio) WHERE ((estado)::text = ANY ((ARRAY['CONFIRMADA'::character varying, 'LISTA_ESPERA'::character varying])::text[]));


--
-- Name: auditoria auditoria_id_usuario_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.auditoria
    ADD CONSTRAINT auditoria_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);


--
-- Name: clase clase_id_entrenador_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clase
    ADD CONSTRAINT clase_id_entrenador_fkey FOREIGN KEY (id_entrenador) REFERENCES public.entrenador(id_entrenador);


--
-- Name: incidencia incidencia_codigo_equipo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.incidencia
    ADD CONSTRAINT incidencia_codigo_equipo_fkey FOREIGN KEY (codigo_equipo) REFERENCES public.equipo(codigo_equipo);


--
-- Name: membresia membresia_id_plan_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.membresia
    ADD CONSTRAINT membresia_id_plan_fkey FOREIGN KEY (id_plan) REFERENCES public.plan(id_plan);


--
-- Name: pago pago_id_membresia_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pago
    ADD CONSTRAINT pago_id_membresia_fkey FOREIGN KEY (id_membresia) REFERENCES public.membresia(id_membresia);


--
-- Name: reserva_clase reserva_clase_id_clase_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reserva_clase
    ADD CONSTRAINT reserva_clase_id_clase_fkey FOREIGN KEY (id_clase) REFERENCES public.clase(id_clase);


--
-- Name: rutina rutina_id_entrenador_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rutina
    ADD CONSTRAINT rutina_id_entrenador_fkey FOREIGN KEY (id_entrenador) REFERENCES public.entrenador(id_entrenador);


--
-- Name: usuario usuario_id_cliente_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_id_cliente_fkey FOREIGN KEY (id_cliente) REFERENCES public.cliente(id_cliente) ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

\unrestrict tryrb6nx8NGS7tbtpdW630n9YsxgflC7YbA8UzHTfJYzlLv5kh9fuThTRkM9adS

