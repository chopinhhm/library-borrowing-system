CREATE DATABASE IF NOT EXISTS library
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'library'@'%'
  IDENTIFIED BY 'replace-with-a-strong-random-password';

GRANT ALL PRIVILEGES ON library.* TO 'library'@'%';
FLUSH PRIVILEGES;
