-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO appusers(id,username,image,password,authority) VALUES (1,'admin1','https://cdn-icons-png.flaticon.com/512/3135/3135768.png','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);

-- Ten player users, named player1 with passwor 0wn3r
INSERT INTO authorities(id,authority) VALUES (2,'PLAYER');
INSERT INTO appusers(id,image,username,password,authority,max_streak) VALUES (4,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','player1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2,3);
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
INSERT INTO appusers(id,image,username,password,authority) VALUES (20,'https://cdn-icons-png.flaticon.com/512/3135/3135768.png','aVeryChillGuy','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);



INSERT INTO appachievements(id,name,description,image,threshold,metric) VALUES (1,'1 partida','description1','https://play-lh.googleusercontent.com/6szjChhG7EeA15gx0eyPbagzO9Z3dgrS-GvlZy7erTaVm9lCi6prNw_AOejtz7puRfQ',1,'GAMES_PLAYED');
INSERT INTO appachievements(id,name,description,image,threshold,metric) VALUES (2,'achievement2','description2','https://play-lh.googleusercontent.com/6szjChhG7EeA15gx0eyPbagzO9Z3dgrS-GvlZy7erTaVm9lCi6prNw_AOejtz7puRfQ',10,'GAMES_PLAYED');
INSERT INTO appachievements(id,name,description,image,threshold,metric) VALUES (3,'1 victoria','description3','https://play-lh.googleusercontent.com/6szjChhG7EeA15gx0eyPbagzO9Z3dgrS-GvlZy7erTaVm9lCi6prNw_AOejtz7puRfQ',1,'VICTORIES');

INSERT INTO appusers_achievements(user_id,achievements_id) VALUES (4,1);
INSERT INTO appusers_achievements(user_id,achievements_id) VALUES (4,2);

INSERT INTO appGames(id,host_id,is_public,num_players,game_mode,duration,n_turn,game_code,game_state) VALUES (1,4,true,1,'PUZZLE_SINGLE',5,4,'ABCDE','IN_PROCESS');
INSERT INTO app_hands(id,num_cards) VALUES (1,0);
INSERT INTO app_players(id,game_id,hand_id,user_id,energy,cards_played_this_turn,energy_used_this_round,hand_changed,score,state,version) VALUES (1,1,1,4,3,0,false,false,7,'WON',0);


INSERT INTO app_hands(id,num_cards) VALUES (2,5);
INSERT INTO app_pack_cards(id,num_cards,player_id) VALUES (1,15,1);
INSERT INTO app_cards(hand_id,id,iniciative,pack_card_id,player_id,rotation,outputs,input,type) VALUES (2,1,1,1,1,0,ARRAY[2,3],0,'TYPE_1');
INSERT INTO app_table_cards(id, num_colum,num_row,type) VALUES (1,6,6,'JUGADORES_2');
INSERT INTO app_rows(id,table_id) VALUES (1,1);
INSERT INTO app_cells(card_id, id,is_full,row_id) VALUES (1,1,false,1);

INSERT INTO appusers_friends(friends_id,user_id) VALUES (1,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (4,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (5,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (6,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (7,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (8,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (10,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (11,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (12,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (13,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (14,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (15,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (16,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (17,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (18,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (19,20);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,1);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,4);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,5);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,6);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,7);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,8);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,9);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,10);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,11);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,12);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,13);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,14);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,15);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,16);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,17);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,18);
INSERT INTO appusers_friends(friends_id,user_id) VALUES (20,19);














