CREATE TABLE tag (
                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                     name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE gift_certificate (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  name VARCHAR(64) NOT NULL,
                                  description VARCHAR(255),
                                  price DECIMAL(10, 2),
                                  duration INTEGER,
                                  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE gift_certificate_has_tag (
                                          gift_certificate_id BIGINT NOT NULL,
                                          tag_id BIGINT NOT NULL,
                                          PRIMARY KEY (gift_certificate_id, tag_id),
                                          FOREIGN KEY (gift_certificate_id) REFERENCES gift_certificate(id) ON DELETE CASCADE,
                                          FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);
