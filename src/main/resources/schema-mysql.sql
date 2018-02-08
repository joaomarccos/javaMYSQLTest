DROP TABLE IF EXISTS t_logs;

CREATE TABLE t_logs(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    dt DATETIME,
    ip VARCHAR(20),
    request VARCHAR(20),
    status VARCHAR(20),
    user_agent VARCHAR(255)
);

create index idx_date on t_logs (dt);