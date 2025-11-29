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
        ON DELETE CASCADE
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
        ON DELETE CASCADE,
    CONSTRAINT fk_product_review_product_id FOREIGN KEY (product_id)
        REFERENCES public.products(id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS public.favourites
(
    user_id uuid NOT NULL,
    product_id uuid NOT NULL,
    CONSTRAINT favourites_pk PRIMARY KEY (user_id, product_id),
    CONSTRAINT fk_favourites_users_user_id FOREIGN KEY (user_id)
        REFERENCES public.users(id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE,
    CONSTRAINT fk_favourites_products_product_id FOREIGN KEY (product_id)
        REFERENCES public.products(id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS public.user_auth
(
    user_id uuid NOT NULL,
    email character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    salt character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_auth_pkey PRIMARY KEY (user_id),
    CONSTRAINT user_auth_email_unique UNIQUE (email),
    CONSTRAINT fk_user_auth_user_id FOREIGN KEY (user_id)
        REFERENCES public.users (id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.user_refresh_tokens
(
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    token character varying(255) NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    created_at timestamp without time zone NOT NULL,
    revoked boolean NOT NULL DEFAULT false,
    CONSTRAINT user_refresh_tokens_pkey PRIMARY KEY (id),
    CONSTRAINT fk_user_refresh_tokens_user_id FOREIGN KEY (user_id)
        REFERENCES public.users (id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.user_providers
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    provider character varying(50) NOT NULL,
    provider_user_id character varying(255) NOT NULL,
    email character varying(255),
    created_at timestamp without time zone DEFAULT now(),
    CONSTRAINT user_providers_pkey PRIMARY KEY (id),
    CONSTRAINT user_providers_provider_provider_user_id_key UNIQUE (provider, provider_user_id),
    CONSTRAINT fk_user_providers_user_id FOREIGN KEY (user_id)
        REFERENCES public.users (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);
