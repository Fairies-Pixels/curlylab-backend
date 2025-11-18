CREATE TYPE IF NOT EXISTS public.porosity_enum AS ENUM ('high', 'low', 'medium');
CREATE TYPE IF NOT EXISTS public.thickness_enum AS ENUM ('thick', 'thin', 'medium');

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
    porosity porosity_enum,
    is_colored boolean,
    thickness thickness_enum,
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

CREATE TABLE IF NOT EXISTS public.reviews
(
    review_id uuid NOT NULL PRIMARY KEY,
    user_id uuid NOT NULL,
    product_id uuid NOT NULL,
    date timestamp NOT NULL,
    mark numeric NOT NULL CHECK (mark >= 1 AND mark <= 5),
    review text,
    CONSTRAINT fk_user_review_user_id FOREIGN KEY (user_id)
        REFERENCES public.users(id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT fk_product_review_product_id FOREIGN KEY (product_id)
        REFERENCES public.products(id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS public.favourites
(
    user_id uuid NOT NULL,
    product_id uuid NOT NULL,
    CONSTRAINT reviews_pk PRIMARY KEY (user_id, product_id),
    CONSTRAINT fk_favourites_users_user_id FOREIGN KEY (user_id)
        REFERENCES public.users(id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT fk_favourites_products_product_id FOREIGN KEY (product_id)
        REFERENCES public.products(id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);