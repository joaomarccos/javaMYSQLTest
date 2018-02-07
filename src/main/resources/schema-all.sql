CREATE TABLE IF NOT EXISTS t_logs(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    dt DATETIME,
    ip VARCHAR(20),
    request VARCHAR(20),
    status VARCHAR(20),
    user_agent VARCHAR(255)
);
