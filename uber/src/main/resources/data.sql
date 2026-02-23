INSERT INTO app_user (id, name, email, password) VALUES
(1, 'John Driver', 'john.driver@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(2, 'Jane Rider', 'jane.rider@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(3, 'Mike Driver', 'mike.driver@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(4, 'Sarah Rider', 'sarah.rider@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(5, 'Admin User', 'admin@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(6, 'Alex Driver', 'alex.driver@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(7, 'Emma Rider', 'emma.rider@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(8, 'Chris Driver', 'chris.driver@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(9, 'Lisa Rider', 'lisa.rider@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(10, 'David Driver', 'david.driver@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(11, 'Rachel Rider', 'rachel.rider@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(12, 'Tom Driver', 'tom.driver@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(13, 'Nina Rider', 'nina.rider@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(14, 'Ryan Driver', 'ryan.driver@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(15, 'Olivia Rider', 'olivia.rider@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(16, 'Marcus Driver', 'marcus.driver@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC'),
(17, 'Sam Rider', 'sam.rider@example.com', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy7KDPC');

INSERT INTO user_roles (user_id, roles) VALUES
(1, 'DRIVER'),
(2, 'RIDER'),
(3, 'DRIVER'),
(4, 'RIDER'),
(5, 'ADMIN'),
(6, 'DRIVER'),
(7, 'RIDER'),
(8, 'DRIVER'),
(9, 'RIDER'),
(10, 'DRIVER'),
(11, 'RIDER'),
(12, 'DRIVER'),
(13, 'RIDER'),
(14, 'DRIVER'),
(15, 'RIDER'),
(16, 'DRIVER'),
(17, 'RIDER');

INSERT INTO driver (id, user_id, rating, available, vehicle_id, current_location) VALUES
(1, 1, 4.5, true,  'ABC123', ST_GeomFromText('POINT(77.2090 28.6139)', 4326)), -- Delhi
(2, 3, 4.8, true,  'XYZ789', ST_GeomFromText('POINT(72.8777 19.0760)', 4326)), -- Mumbai
(3, 6, 4.2, true,  'DEF456', ST_GeomFromText('POINT(77.5946 12.9716)', 4326)), -- Bangalore
(4, 8, 4.9, false, 'GHI789', ST_GeomFromText('POINT(80.2707 13.0827)', 4326)), -- Chennai
(5, 10, 4.6, true, 'JKL012', ST_GeomFromText('POINT(88.3639 22.5726)', 4326)), -- Kolkata
(6, 12, 4.3, true, 'MNO345', ST_GeomFromText('POINT(78.4867 17.3850)', 4326)), -- Hyderabad
(7, 14, 4.7, true, 'PQR678', ST_GeomFromText('POINT(73.8567 18.5204)', 4326)), -- Pune
(8, 16, 4.4, false,'STU901', ST_GeomFromText('POINT(72.5714 23.0225)', 4326)); -- Ahmedabad

INSERT INTO rider (id, user_id, rating) VALUES
(1, 2, 4.7),
(2, 4, 4.2),
(3, 7, 4.8),
(4, 9, 4.5),
(5, 11, 4.6),
(6, 13, 4.1),
(7, 15, 4.9),
(8, 17, 4.3);