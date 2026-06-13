CREATE TABLE IF NOT EXISTS category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  slug VARCHAR(100),
  description VARCHAR(500),
  cover_image VARCHAR(500),
  sort_order INT DEFAULT 0,
  create_time TIMESTAMP,
  update_time TIMESTAMP,
  UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS article (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  content CLOB,
  cover_image VARCHAR(1000),
  category_id BIGINT,
  chapter_order INT DEFAULT 0,
  reading_minutes INT DEFAULT 8,
  is_core TINYINT DEFAULT 0,
  view_count INT DEFAULT 0,
  github_url VARCHAR(500),
  sync_status INT DEFAULT 0,
  last_sync_time TIMESTAMP,
  github_sha VARCHAR(40),
  is_comment TINYINT DEFAULT 1,
  publish_time TIMESTAMP,
  create_time TIMESTAMP,
  update_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS system_config (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  config_key VARCHAR(100) NOT NULL,
  config_value CLOB,
  config_type VARCHAR(20) DEFAULT 'STRING',
  description VARCHAR(200),
  create_time TIMESTAMP,
  update_time TIMESTAMP,
  UNIQUE (config_key)
);

