CREATE TABLE utilizador (
	util_id		int AUTO_INCREMENT,
	util_nome	 varchar(40),
	util_username	 varchar(20),
	util_password	 varchar(20),
	util_estado	 int,
	util_email	 varchar(40),
	util_tipo	 varchar(11),
	util_foto	 varchar(512) DEFAULT '/imagens/default_profile_pic.jpg',
	util_observacoes text,
	PRIMARY KEY(util_id)
);

CREATE TABLE equipamento (
	eq_id			int AUTO_INCREMENT,
	eq_marca			 varchar(20),
	eq_modelo			 varchar(30),
	eq_sku			 int,
	eq_data_fabrico		 date,
	eq_lote			 varchar(20),
	eq_data_submissao_pedido	 date,
	eq_observacoes		 text,
	cliente_utilizador_util_id int NOT NULL,
	PRIMARY KEY(eq_id)
);

CREATE TABLE reparacao (
	rep_id		int AUTO_INCREMENT,
	rep_num_reparacao	 varchar(20),
	rep_data_criacao	 timestamp,
	rep_data_fim	 timestamp,
	rep_tempo_decorrido timestamp,
	rep_custo		 float(10),
	rep_estado		 int,
	rep_observacoes	 text,
	equipamento_eq_id	 int NOT NULL,
	PRIMARY KEY(rep_id)
);

CREATE TABLE funcionario (
	func_nif			 int,
	func_contacto		 int,
	func_morada		 varchar(50),
	func_especializacao	 int,
	func_data_inicio_atividade date,
	utilizador_util_id	 int,
	PRIMARY KEY(utilizador_util_id)
);

CREATE TABLE cliente (
	cli_nif		 int,
	cli_contacto	 int,
	cli_morada		 varchar(50),
	cli_setor_atividade varchar(20),
	cli_escalao	 char(1),
	utilizador_util_id	 int,
	PRIMARY KEY(utilizador_util_id)
);

CREATE TABLE acao (
	ac_id		int AUTO_INCREMENT,
	ac_data		 timestamp,
	ac_descricao	 varchar(60),
	utilizador_util_id int,
	PRIMARY KEY(ac_id)
);

CREATE TABLE funcionario_reparacao (
	funcrep_aceite		 int,
	reparacao_rep_id		 int NOT NULL,
	funcionario_utilizador_util_id int NOT NULL
);

CREATE TABLE notificacao (
	not_id		int AUTO_INCREMENT,
	not_tipo		 int,
	not_lida		 boolean,
	reparacao_rep_id	 int,
	utilizador_util_id int,
	PRIMARY KEY(not_id)
);

ALTER TABLE equipamento ADD UNIQUE (eq_sku);
ALTER TABLE equipamento ADD CONSTRAINT equipamento_fk1 FOREIGN KEY (cliente_utilizador_util_id) REFERENCES cliente(utilizador_util_id);
ALTER TABLE reparacao ADD CONSTRAINT reparacao_fk1 FOREIGN KEY (equipamento_eq_id) REFERENCES equipamento(eq_id);
ALTER TABLE funcionario ADD CONSTRAINT funcionario_fk1 FOREIGN KEY (utilizador_util_id) REFERENCES utilizador(util_id);
ALTER TABLE cliente ADD CONSTRAINT cliente_fk1 FOREIGN KEY (utilizador_util_id) REFERENCES utilizador(util_id);
ALTER TABLE acao ADD CONSTRAINT acao_fk1 FOREIGN KEY (utilizador_util_id) REFERENCES utilizador(util_id);
ALTER TABLE funcionario_reparacao ADD CONSTRAINT funcionario_reparacao_fk1 FOREIGN KEY (reparacao_rep_id) REFERENCES reparacao(rep_id);
ALTER TABLE funcionario_reparacao ADD CONSTRAINT funcionario_reparacao_fk2 FOREIGN KEY (funcionario_utilizador_util_id) REFERENCES funcionario(utilizador_util_id);
ALTER TABLE notificacao ADD CONSTRAINT notificacao_fk1 FOREIGN KEY (reparacao_rep_id) REFERENCES reparacao(rep_id);
ALTER TABLE notificacao ADD CONSTRAINT notificacao_fk2 FOREIGN KEY (utilizador_util_id) REFERENCES utilizador(util_id);

