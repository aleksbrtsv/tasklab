CREATE TABLE tasks (
    id BIGINT PRIMARY KEY,
    title TEXT NOT NULL,
    priority VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',

    CONSTRAINT chk_tasks_priority
       CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),

    CONSTRAINT chk_tasks_status
       CHECK (status IN ('NEW', 'IN_PROGRESS', 'DONE', 'FAILED'))
);