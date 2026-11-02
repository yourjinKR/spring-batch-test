CREATE DATABASE IF NOT EXISTS batch_meta DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS batch_data DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- MYSQL_USER(batch) 계정에 두 DB 모두 권한 부여
GRANT ALL PRIVILEGES ON batch_meta.* TO 'batch'@'%';
GRANT ALL PRIVILEGES ON batch_data.* TO 'batch'@'%';
FLUSH PRIVILEGES;
