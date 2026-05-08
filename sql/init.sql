USE skillbuilders;

CREATE TABLE IF NOT EXISTS users (
    userid INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS instructors (
    instructorid INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS admin (
    adminid INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
    courseid INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    instructorid INT NOT NULL,
    price FLOAT DEFAULT 0,
    rating FLOAT DEFAULT 0,
    rating_count INT DEFAULT 0,
    duration FLOAT DEFAULT 0,
    module_count INT DEFAULT 0,
    enrolled_count INT DEFAULT 0,
    thumbnail TEXT,
    description TEXT,
    approved VARCHAR(10) DEFAULT 'false',
    time_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instructorid) REFERENCES instructors(instructorid)
);

CREATE TABLE IF NOT EXISTS coursestreams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    courseid INT NOT NULL,
    stream VARCHAR(100),
    FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS courseprerequisites (
    id INT AUTO_INCREMENT PRIMARY KEY,
    courseid INT NOT NULL,
    prerequisite VARCHAR(200),
    FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lectures (
    id INT AUTO_INCREMENT PRIMARY KEY,
    courseid INT NOT NULL,
    module_number INT NOT NULL,
    module_name VARCHAR(200),
    link TEXT,
    FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_number INT,
    question TEXT,
    option1 VARCHAR(255),
    option2 VARCHAR(255),
    option3 VARCHAR(255),
    option4 VARCHAR(255),
    answer VARCHAR(255),
    courseid INT NOT NULL,
    module_number INT NOT NULL,
    FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS usercourses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userid INT NOT NULL,
    courseid INT NOT NULL,
    course_type VARCHAR(50),
    FOREIGN KEY (userid) REFERENCES users(userid),
    FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
    reviewid INT AUTO_INCREMENT PRIMARY KEY,
    userid INT NOT NULL,
    courseid INT NOT NULL,
    rating FLOAT,
    review TEXT,
    FOREIGN KEY (userid) REFERENCES users(userid),
    FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS instructormessages (
    messageid INT AUTO_INCREMENT PRIMARY KEY,
    instructorid INT NOT NULL,
    courseid INT NOT NULL,
    name VARCHAR(100),
    message TEXT,
    is_read VARCHAR(10) DEFAULT 'false',
    FOREIGN KEY (instructorid) REFERENCES instructors(instructorid),
    FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

-- Default admin account
INSERT IGNORE INTO admin (email, password) VALUES ('admin@skillbuilders.com', 'admin123');
