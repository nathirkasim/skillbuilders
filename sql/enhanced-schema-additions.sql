-- ================================================================
-- SKILLBUILDERS — Enhanced Schema Additions
-- Run this AFTER the original init.sql
-- ================================================================

USE skillbuilders;

-- Add view_count to courses table
ALTER TABLE courses
  ADD COLUMN IF NOT EXISTS view_count INT DEFAULT 0,
  ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP NULL;

-- Add enrolled_at timestamp to usercourses for enrollment trend tracking
ALTER TABLE usercourses
  ADD COLUMN IF NOT EXISTS enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN IF NOT EXISTS progress INT DEFAULT 0;

-- Add extended fields to users (if not already present)
ALTER TABLE users
  ADD COLUMN IF NOT EXISTS gender VARCHAR(20),
  ADD COLUMN IF NOT EXISTS phone_number BIGINT,
  ADD COLUMN IF NOT EXISTS grade VARCHAR(50),
  ADD COLUMN IF NOT EXISTS stream VARCHAR(100),
  ADD COLUMN IF NOT EXISTS country VARCHAR(100),
  ADD COLUMN IF NOT EXISTS city VARCHAR(100),
  ADD COLUMN IF NOT EXISTS professional_summary TEXT,
  ADD COLUMN IF NOT EXISTS DOB VARCHAR(20),
  ADD COLUMN IF NOT EXISTS profile TEXT,
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add extended fields to instructors
ALTER TABLE instructors
  ADD COLUMN IF NOT EXISTS bio TEXT,
  ADD COLUMN IF NOT EXISTS profile TEXT,
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Certificates table (if not exists)
CREATE TABLE IF NOT EXISTS certificates (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid INT NOT NULL,
  courseid INT NOT NULL,
  path TEXT,
  issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userid) REFERENCES users(userid),
  FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

-- Transactions table (if not exists)
CREATE TABLE IF NOT EXISTS transactions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid INT NOT NULL,
  courseid INT NOT NULL,
  amount FLOAT DEFAULT 0,
  time_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userid) REFERENCES users(userid),
  FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

-- Test results table (if not exists)
CREATE TABLE IF NOT EXISTS testresult (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid INT NOT NULL,
  courseid INT NOT NULL,
  module_number INT NOT NULL,
  total_marks INT DEFAULT 0,
  user_marks INT DEFAULT 0,
  result VARCHAR(10),
  attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userid) REFERENCES users(userid),
  FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

-- User interested streams table (if not exists)
CREATE TABLE IF NOT EXISTS userinterestedstream (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid INT NOT NULL,
  stream VARCHAR(100),
  FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE
);

-- Indexes for analytics performance
CREATE INDEX IF NOT EXISTS idx_courses_instructor ON courses(instructorid);
CREATE INDEX IF NOT EXISTS idx_usercourses_user   ON usercourses(userid);
CREATE INDEX IF NOT EXISTS idx_usercourses_course  ON usercourses(courseid);
CREATE INDEX IF NOT EXISTS idx_transactions_user   ON transactions(userid);

-- Update default admin
INSERT IGNORE INTO admin (email, password) VALUES ('admin@skillbuilders.com', 'admin123');

SELECT 'Schema enhancement complete!' AS status;
