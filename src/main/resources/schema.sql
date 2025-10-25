CREATE TYPE IF NOT EXISTS public.porosity_enum AS ENUM ('high', 'low', 'medium');
CREATE TYPE IF NOT EXISTS public.thickness_enum AS ENUM ('thick', 'thin');

CREATE TABLE IF NOT EXISTS public.users
(
    id uuid NOT NULL PRIMARY KEY,
    username varchar(100) NOT NULL,
    created_at timestamp NOT NULL,
    image_url varchar(512)
);

CREATE TABLE IF NOT EXISTS public.user_hairtypes
(
    user_id uuid NOT NULL PRIMARY KEY,
    porosity porosity_enum NOT NULL,
    is_colored boolean NOT NULL,
    thickness thickness_enum NOT NULL,
    CONSTRAINT fk_user_hairtypes_user_id FOREIGN KEY (user_id)
        REFERENCES public.users(id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS public.products
(
    id uuid NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL,
    description text,
    tags text,
    image_url varchar(512)
);