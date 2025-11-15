package com.webFlux.webFluxCassandra.domain;


import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("message")
@Data
public class Message {

    @PrimaryKey
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID id;

    private String data;

    public Message() {
        this.id = Uuids.timeBased();
    }

}
