
DROP DATABASE IF EXISTS biblioteca_donbosco_v2;
CREATE DATABASE biblioteca_donbosco_v2;
USE biblioteca_donbosco_v2;

-- Tabla de usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(100),
    rol ENUM('administrador', 'profesor', 'alumno') NOT NULL
);

-- Tabla padre: documentos
CREATE TABLE documentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200),
    autor VARCHAR(100),
    anio_publicacion INT,
    tipo ENUM('libro', 'revista', 'cd', 'dvd', 'pdf', 'tesis') NOT NULL
);

-- Tabla hija: libros
CREATE TABLE libros (
    id INT PRIMARY KEY,
    isbn VARCHAR(20),
    editorial VARCHAR(100),
    numero_paginas INT,
    FOREIGN KEY (id) REFERENCES documentos(id) ON DELETE CASCADE
);

-- Tabla hija: revistas
CREATE TABLE revistas (
    id INT PRIMARY KEY,
    numero INT,
    mes VARCHAR(20),
    categoria VARCHAR(50),
    editorial VARCHAR(100),
    FOREIGN KEY (id) REFERENCES documentos(id) ON DELETE CASCADE
);

-- Tabla hija: cds
CREATE TABLE cds (
    id INT PRIMARY KEY,
    genero VARCHAR(50),
    duracion VARCHAR(20),
    artista VARCHAR(100),
    FOREIGN KEY (id) REFERENCES documentos(id) ON DELETE CASCADE
);

-- Tabla hija: dvds
CREATE TABLE dvds (
    id INT PRIMARY KEY,
    director VARCHAR(100),
    duracion VARCHAR(20),
    productora VARCHAR(100),
    FOREIGN KEY (id) REFERENCES documentos(id) ON DELETE CASCADE
);

-- Tabla hija: pdfs
CREATE TABLE pdfs (
    id INT PRIMARY KEY,
    tema VARCHAR(100),
    numero_paginas INT,
    autor_original VARCHAR(100),
    FOREIGN KEY (id) REFERENCES documentos(id) ON DELETE CASCADE
);

-- Tabla hija: tesis
CREATE TABLE tesis (
    id INT PRIMARY KEY,
    carrera VARCHAR(100),
    universidad VARCHAR(100),
    asesor_academico VARCHAR(100),
    FOREIGN KEY (id) REFERENCES documentos(id) ON DELETE CASCADE
);

-- Tabla de préstamos
CREATE TABLE prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    id_documento INT,
    fecha_prestamo DATE,
    fecha_devolucion DATE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_documento) REFERENCES documentos(id)
);
