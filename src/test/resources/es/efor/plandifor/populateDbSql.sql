INSERT INTO plandifor_2.roles
(ID_Rol, Nombre) 
VALUES (1, 'RRHH');

INSERT INTO plandifor_2.roles
(ID_Rol, Nombre) 
VALUES (2, 'Empleado');

INSERT INTO plandifor_2.usuarios
(ID_Usuario, Login, Password, ID_Rol, ID_Trabajador) 
VALUES (1, 'RRHH', 'rrhh', 1, null);

INSERT INTO plandifor_2.usuarios
(ID_Usuario, Login, Password, ID_Rol, ID_Trabajador) 
VALUES (2, 'E1', 'e1', 2, null);