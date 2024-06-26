CREATE TABLE roles (
    role_id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(255) CHECK (name IN ('ROLE_USER','ROLE_ADMIN','ROLE_MODERATOR')),
    PRIMARY KEY (role_id)
);

CREATE TABLE staff (
    staff_id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    phone_number VARCHAR(15) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (staff_id)
);

CREATE TABLE tokens (
    is_log_out BOOLEAN,
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    staff_id BIGINT,
    token VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE users (
    birth_date DATE,
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    address VARCHAR(255),
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(255),
    PRIMARY KEY (user_id)
);

CREATE TABLE staff_roles (
    staff_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (staff_id, role_id),
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

ALTER TABLE IF EXISTS staff_roles
    ADD CONSTRAINT staff_roles_roles_fk
        FOREIGN KEY (role_id) REFERENCES roles
    ;

ALTER TABLE IF EXISTS staff_roles
    ADD CONSTRAINT staff_roles_staff_fk
        FOREIGN KEY (staff_id) REFERENCES staff
    ;

ALTER TABLE IF EXISTS tokens
    ADD CONSTRAINT tokens_staff_fk
        FOREIGN KEY (staff_id) REFERENCES staff
    ;
