-- Optional: run AFTER the application has started once (tables must exist).
USE student_db;

INSERT INTO courses (course_code, name, description, duration_in_months) VALUES
 ('BCA101', 'Bachelor of Computer Applications', 'Three-year undergraduate computer applications programme', 36),
 ('MCA201', 'Master of Computer Applications', 'Two-year postgraduate programme', 24),
 ('BSC301', 'Bachelor of Science (Physics)', 'Three-year undergraduate science programme', 36);

INSERT INTO students (first_name, last_name, email, phone, date_of_birth, gender, address, enrollment_date, course_id, created_at, updated_at) VALUES
 ('Aarav', 'Sharma', 'aarav.sharma@example.com', '9876543210', '2004-05-17', 'MALE',   'Sri Ganganagar, Rajasthan', '2024-07-01', 1, NOW(), NOW()),
 ('Priya', 'Verma',  'priya.verma@example.com',  '9123456780', '2003-11-02', 'FEMALE', 'Bikaner, Rajasthan',       '2024-07-01', 1, NOW(), NOW()),
 ('Rohan', 'Singh',  'rohan.singh@example.com',  '9988776655', '2000-01-25', 'MALE',   'Jaipur, Rajasthan',        '2025-07-15', 2, NOW(), NOW());
