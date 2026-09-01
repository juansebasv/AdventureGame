-- ---------------------------------------------------------------------------
-- Esquema local para AdventureGame / canivales
--
-- Se crea a mano (en lugar de dejar que Hibernate lo genere con
-- ddl-auto: update) para tener un entorno local 100% reproducible.
-- Los nombres de columna coinciden EXACTAMENTE con lo que esperan las
-- entidades JPA (co.com.adventure.model.Options y co.com.adventure.model.Score),
-- teniendo en cuenta que PostgreSQL pasa a minúsculas los identificadores
-- sin comillas.
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS options (
    id           integer PRIMARY KEY,          -- id del nodo de la historia (se referencia desde opt_N_value)
    description  text,
    opt_1_text   text,
    opt_2_text   text,
    opt_3_text   text,
    opt_1_value  integer DEFAULT 0,            -- id del siguiente nodo (0 = no hay opción / final)
    opt_2_value  integer DEFAULT 0,
    opt_3_value  integer DEFAULT 0
);

CREATE TABLE IF NOT EXISTS scores (
    id           serial PRIMARY KEY,           -- GenerationType.IDENTITY -> serial
    name         varchar(255) NOT NULL,
    s_hour       integer DEFAULT 0,
    s_minute     integer DEFAULT 0,
    s_second     integer DEFAULT 0,
    s_timestamp  timestamp,
    cellphone    varchar(255)
);
