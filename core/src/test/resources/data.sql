INSERT INTO tag (name) VALUES ('Tag1');
INSERT INTO tag (name) VALUES ('Tag2');
INSERT INTO tag (name) VALUES ('Tag3');

INSERT INTO gift_certificate (name, description, price, duration, create_date,last_update_date) VALUES ('Certificate1', 'Certificate 1 description', 50.0, 10,NOW(),NOW());
INSERT INTO gift_certificate (name, description, price, duration, create_date,last_update_date) VALUES ('Certificate2', 'Certificate 2 description', 100.0, 20,NOW(),NOW());
INSERT INTO gift_certificate (name, description, price, duration, create_date,last_update_date) VALUES ('Certificate3', 'Certificate 3 description', 150.0, 30,NOW(),NOW());

INSERT INTO gift_certificate_has_tag (gift_certificate_id, tag_id) VALUES (1, 1);
INSERT INTO gift_certificate_has_tag (gift_certificate_id, tag_id) VALUES (1, 2);
INSERT INTO gift_certificate_has_tag (gift_certificate_id, tag_id) VALUES (2, 2);
INSERT INTO gift_certificate_has_tag (gift_certificate_id, tag_id) VALUES (2, 3);
INSERT INTO gift_certificate_has_tag (gift_certificate_id, tag_id) VALUES (3, 1);
INSERT INTO gift_certificate_has_tag (gift_certificate_id, tag_id) VALUES (3, 2);
INSERT INTO gift_certificate_has_tag (gift_certificate_id, tag_id) VALUES (3, 3);
