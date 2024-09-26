CREATE TABLE smartdelivery_hub (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   name VARCHAR(255),
                                   hub_type VARCHAR(50),
                                   longitude DECIMAL(18, 15),
                                   latitude DECIMAL(18, 15),
                                   address VARCHAR(255),
                                   width DOUBLE PRECISION,
                                   length DOUBLE PRECISION
);

CREATE TABLE smartdelivery_bay (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   hub_id UUID,
                                   code VARCHAR(255),
                                   x INTEGER,
                                   y INTEGER,
                                   x_long INTEGER,
                                   y_long INTEGER,
                                   CONSTRAINT fk_hub
                                       FOREIGN KEY(hub_id)
                                           REFERENCES smartdelivery_hub(id)
);
