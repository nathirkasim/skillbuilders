-- ================================================================
-- SKILLBUILDERS — Enhanced Schema Additions (runs after 01-init.sql)
-- ================================================================

USE skillbuilders;

-- Add view_count and approved_at to courses
ALTER TABLE courses
  ADD COLUMN IF NOT EXISTS view_count INT DEFAULT 0,
  ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP NULL;

-- Add enrolled_at and progress to usercourses
ALTER TABLE usercourses
  ADD COLUMN IF NOT EXISTS enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN IF NOT EXISTS progress INT DEFAULT 0;

-- Extended user fields
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

-- Extended instructor fields
ALTER TABLE instructors
  ADD COLUMN IF NOT EXISTS bio TEXT,
  ADD COLUMN IF NOT EXISTS profile TEXT,
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Certificates table
CREATE TABLE IF NOT EXISTS certificates (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid INT NOT NULL,
  courseid INT NOT NULL,
  path TEXT,
  issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userid) REFERENCES users(userid),
  FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid INT NOT NULL,
  courseid INT NOT NULL,
  amount FLOAT DEFAULT 0,
  time_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userid) REFERENCES users(userid),
  FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

-- Test results table
CREATE TABLE IF NOT EXISTS testresult (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid INT NOT NULL,
  courseid INT NOT NULL,
  module_number INT NOT NULL,
  total_marks INT DEFAULT 0,
  user_marks INT DEFAULT 0,
  result VARCHAR(10),
  attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  -- FIXED: REPLACE INTO in UpdateTestResultDAO requires a UNIQUE key on these three columns
  UNIQUE KEY uq_test_result (userid, courseid, module_number),
  FOREIGN KEY (userid) REFERENCES users(userid),
  FOREIGN KEY (courseid) REFERENCES courses(courseid) ON DELETE CASCADE
);

-- User interested streams table
CREATE TABLE IF NOT EXISTS userinterestedstream (
  id INT AUTO_INCREMENT PRIMARY KEY,
  userid INT NOT NULL,
  stream VARCHAR(100),
  FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE
);

-- Performance indexes
CREATE INDEX IF NOT EXISTS idx_courses_instructor  ON courses(instructorid);
CREATE INDEX IF NOT EXISTS idx_usercourses_user    ON usercourses(userid);
CREATE INDEX IF NOT EXISTS idx_usercourses_course  ON usercourses(courseid);
CREATE INDEX IF NOT EXISTS idx_transactions_user   ON transactions(userid);
CREATE INDEX IF NOT EXISTS idx_transactions_course ON transactions(courseid);

SELECT 'Enhanced schema applied!' AS status;
