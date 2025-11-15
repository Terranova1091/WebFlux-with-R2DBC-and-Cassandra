// Внутри контейнера (docker exec -it my_cassandra cqlsh)
CREATE KEYSPACE IF NOT EXISTS bank_db
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
        };

USE bank_db;

CREATE TABLE IF NOT EXISTS message (
    id UUID PRIMARY KEY,
    data text
);
