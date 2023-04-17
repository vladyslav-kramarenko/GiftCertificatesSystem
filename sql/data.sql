-- Clear all existing data
USE gift_certificates_system;
DELETE
FROM gift_certificate_has_tag;
DELETE
FROM gift_certificate;
DELETE
FROM tag;

-- Insert tags
INSERT INTO tag (name)
VALUES ('Adventure'),
       ('Foodie'),
       ('Sports'),
       ('Music Lover'),
       ('Bookworm'),
       ('Movie Buff'),
       ('Gamer'),
       ('Fitness Enthusiast'),
       ('Beauty & Wellness'),
       ('Art & Culture');

-- Insert gift certificates
INSERT INTO gift_certificate (name, description, price, duration)
VALUES ('Luxury Spa Package', 'Indulge in a relaxing spa day with our luxury package', 299.99, 180),
       ('Gourmet Dinner for Two', 'Savor a delectable 3-course dinner at a top-rated restaurant', 149.99, 120),
       ('Hot Air Balloon Ride', 'Experience the thrill of soaring through the skies with a hot air balloon ride',
        199.99, 60),
       ('Concert Tickets', 'Enjoy front-row seats to see your favorite musician in concert', 249.99, 240),
       ('Wine Tasting Tour', 'Explore local wineries and savor a variety of delicious wines', 99.99, 180),
       ('Private Photography Lesson', 'Learn the art of photography from a professional photographer', 199.99, 120),
       ('Virtual Reality Gaming Session',
        'Experience the latest in gaming technology with a virtual reality gaming session', 79.99, 60),
       ('Yoga Retreat Weekend', 'Escape to a tranquil yoga retreat and rejuvenate your mind and body', 399.99, 480),
       ('Luxury Skincare Package', 'Treat yourself to a luxurious skincare routine with our premium package', 149.99,
        90),
       ('Art Museum Membership', 'Enjoy unlimited access to a local art museum with an annual membership', 129.99, 365),
       ('Cooking Class with a Celebrity Chef', 'Learn the secrets of gourmet cooking from a celebrity chef', 299.99,
        180),
       ('Guided Hiking Tour', 'Explore scenic trails and stunning landscapes with a guided hiking tour', 99.99, 120),
       ('Personal Styling Consultation',
        'Receive expert advice on fashion and style with a personal styling consultation', 149.99, 90),
       ('Ski or Snowboard Lesson', 'Hit the slopes with confidence after a professional ski or snowboard lesson',
        199.99, 120),
       ('Fitness Bootcamp', 'Get in shape with a high-intensity fitness bootcamp program', 299.99, 240),
       ('Language Learning Course', 'Learn a new language and expand your horizons with a language learning course',
        99.99, 180),
       ('Connoisseur Coffee Tasting', 'Savor a variety of artisanal coffee blends with a connoisseur coffee tasting',
        49.99, 60),
       ('Scuba Diving Lesson', 'Explore the ocean depths with a professional scuba diving lesson', 149.99, 120),
       ('Personalized Wine Collection', 'Discover a curated selection of premium wines tailored to your taste', 499.99,
        365),
       ('Private Helicopter Tour', 'Soar above the city and enjoy stunning aerial views with a private helicopter tour',
        999.99, 60);

-- Add tags to gift certificates
INSERT INTO gift_certificate_has_tag (gift_certificate_id, tag_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 1),
       (2, 2),
       (2, 4),
       (3, 1),
       (3, 5),
       (3, 6),
       (4, 2),
       (4, 5),
       (4, 7),
       (5, 3),
       (5, 4),
       (5, 7),
       (6, 4),
       (6, 5),
       (6, 8),
       (7, 1),
       (7, 6),
       (7, 10),
       (8, 2),
       (8, 3),
       (8, 9),
       (9, 3),
       (9, 5),
       (9, 8),
       (10, 1),
       (10, 4),
       (10, 7),
       (11, 1),
       (11, 2),
       (11, 10),
       (12, 2),
       (12, 4),
       (12, 8),
       (13, 1),
       (13, 5),
       (13, 6),
       (14, 3),
       (14, 4),
       (14, 9),
       (15, 5),
       (15, 6),
       (15, 10),
       (16, 2),
       (16, 3),
       (16, 7),
       (17, 1),
       (17, 4),
       (17, 8),
       (18, 3),
       (18, 5),
       (18, 9),
       (19, 2),
       (19, 6),
       (19, 10),
       (20, 1),
       (20, 3),
       (20, 7);

-- Insert users
INSERT INTO user (first_name, last_name)
VALUES ('John', 'Doe'),
       ('Jane', 'Smith'),
       ('Alice', 'Johnson'),
       ('Bob', 'Williams'),
       ('Charlie', 'Brown');

-- Insert orders
INSERT INTO `user_order` (user_id, sum)
VALUES (1, 500.00),
       (2, 250.00),
       (3, 300.00),
       (4, 150.00),
       (5, 200.00);

-- Insert order-gift_certificate relations (order_has_gift_certificate)
INSERT INTO order_has_gift_certificate (order_id, gift_certificate_id, count)
VALUES (1, 1, 2),
       (1, 2, 1),
       (2, 3, 1),
       (2, 4, 1),
       (3, 5, 1),
       (3, 6, 1),
       (4, 7, 1),
       (4, 8, 1),
       (5, 9, 1),
       (5, 10, 1);
