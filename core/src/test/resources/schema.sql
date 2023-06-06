SET MODE MYSQL;

CREATE SCHEMA IF NOT EXISTS gift_certificates_system;
SET SCHEMA gift_certificates_system;

CREATE TABLE IF NOT EXISTS tag (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   name VARCHAR(64) NOT NULL,
                                   UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS gift_certificate (
                                                id INT AUTO_INCREMENT PRIMARY KEY,
                                                name VARCHAR(64) NOT NULL,
                                                description VARCHAR(255),
                                                price DECIMAL(10,2),
                                                duration INT,
                                                create_date TIMESTAMP AS CURRENT_TIMESTAMP(),
                                                last_update_date TIMESTAMP AS CURRENT_TIMESTAMP(),
                                                UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS gift_certificate_has_tag (
                                                        gift_certificate_id INT,
                                                        tag_id INT,
                                                        PRIMARY KEY (gift_certificate_id, tag_id),
                                                        FOREIGN KEY (gift_certificate_id) REFERENCES gift_certificate(id) ON DELETE CASCADE,
                                                        FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS role (
                                    id INT PRIMARY KEY,
                                    name VARCHAR(45)
);

CREATE TABLE IF NOT EXISTS user (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    first_name VARCHAR(45) NOT NULL,
                                    last_name VARCHAR(45),
                                    role_id INT NOT NULL DEFAULT 1,
                                    email VARCHAR(45),
                                    auth0user_id VARCHAR(45),
                                    password VARCHAR(64),
                                    FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE IF NOT EXISTS user_order (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          user_id INT NOT NULL,
                                          sum DECIMAL(10,2) NOT NULL,
                                          create_date TIMESTAMP AS CURRENT_TIMESTAMP(),
                                          last_update_date TIMESTAMP AS CURRENT_TIMESTAMP(),
                                          FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS order_has_gift_certificate (
                                                          order_id INT,
                                                          gift_certificate_id INT,
                                                          count INT NOT NULL DEFAULT 1,
                                                          PRIMARY KEY (order_id, gift_certificate_id),
                                                          FOREIGN KEY (order_id) REFERENCES user_order(id),
                                                          FOREIGN KEY (gift_certificate_id) REFERENCES gift_certificate(id)
);

CREATE TRIGGER gift_certificate_BEFORE_UPDATE BEFORE UPDATE ON gift_certificate FOR EACH ROW
    CALL "org.h2.api.Trigger".init(
            'gift_certificates_system', 'public', 'gift_certificate', 'gift_certificate_BEFORE_UPDATE', true,
            false, 1, 'SET NEW.last_update_date = CURRENT_TIMESTAMP', 'ROW', null
        );
