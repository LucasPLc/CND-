CREATE TABLE cnd_empresa (
    id SERIAL PRIMARY KEY,
    cnpj VARCHAR(18) NOT NULL,
    nome_empresa VARCHAR(255),
    id_empresa VARCHAR(6),
    status_empresa VARCHAR(50)
);

CREATE TABLE cnd_cliente (
    id SERIAL PRIMARY KEY,
    cnpj VARCHAR(18),
    periodicidade INT,
    status_cliente VARCHAR(50),
    nacional BOOLEAN,
    municipal BOOLEAN,
    estadual BOOLEAN,
    fk_empresa INT,
    FOREIGN KEY (fk_empresa) REFERENCES cnd_empresa(id)
);

CREATE TABLE cnd_resultado (
    id SERIAL PRIMARY KEY,
    data_processamento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    arquivo TEXT,
    situacao VARCHAR(100),
    data_emissao DATE,
    data_validade DATE,
    codigo_controle VARCHAR(100),
    status VARCHAR(50),
    fk_cliente INT NOT NULL,
    FOREIGN KEY (fk_cliente) REFERENCES cnd_cliente(id)
);