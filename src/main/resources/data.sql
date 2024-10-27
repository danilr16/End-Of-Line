-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO appusers(id,username,password,authority) VALUES (1,'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);

-- Ten player users, named player1 with passwor 0wn3r
INSERT INTO authorities(id,authority) VALUES (2,'PLAYER');
INSERT INTO appusers(id,image,username,password,authority) VALUES (4,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (5,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player2','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (6,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player3','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (7,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player4','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (8,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player5','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (9,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player6','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (10,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player7','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (11,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player8','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (12,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player9','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (13,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player10','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (14,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','ivamirbal','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (15,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','lwh9900','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (16,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','txh4995','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (17,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','jct6889','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (18,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','vwn3805','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,image,username,password,authority) VALUES (19,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','lfb9498','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);



INSERT INTO achievement(id,name,description,image,threshold,metric) VALUES (1,'achievement1','description1','https://play-lh.googleusercontent.com/6szjChhG7EeA15gx0eyPbagzO9Z3dgrS-GvlZy7erTaVm9lCi6prNw_AOejtz7puRfQ',50,'GAMES_PLAYED');
INSERT INTO achievement(id,name,description,image,threshold,metric) VALUES (2,'achievement2','description2','https://play-lh.googleusercontent.com/6szjChhG7EeA15gx0eyPbagzO9Z3dgrS-GvlZy7erTaVm9lCi6prNw_AOejtz7puRfQ',100,'GAMES_PLAYED');
INSERT INTO achievement(id,name,description,image,threshold,metric) VALUES (3,'achievement3','description3','https://play-lh.googleusercontent.com/6szjChhG7EeA15gx0eyPbagzO9Z3dgrS-GvlZy7erTaVm9lCi6prNw_AOejtz7puRfQ',50,'VICTORIES');

INSERT INTO appusers_achievements(user_id,achievements_id) VALUES (4,1);
INSERT INTO appusers_achievements(user_id,achievements_id) VALUES (4,2);

