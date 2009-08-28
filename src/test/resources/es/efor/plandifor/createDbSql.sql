create database PlanDIFOR_2;

create table PlanDIFOR_2.Roles ( 
	ID_Rol INTEGER not null AUTO_INCREMENT,
	Nombre VARCHAR(20) not null,   
   constraint Rol_PK primary key (ID_Rol) ); 

create table PlanDIFOR_2.Usuarios ( 
	ID_Usuario INTEGER not null AUTO_INCREMENT,
	Login VARCHAR(20) not null, 
	PassWord VARCHAR(14) not null, 
	ID_Trabajador INTEGER null,
	ID_Rol INTEGER null, 
   constraint Usuario_PK primary key (ID_Usuario) );