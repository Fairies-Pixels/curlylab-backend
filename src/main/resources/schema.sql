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

INSERT INTO products (id, name, description, tags, image_url)
VALUES
(gen_random_uuid(), 'Гель Kapous', 'Сильная фиксация', '{Низкая, Толстые, Нет}', 'https://storage.yandexcloud.net/curlylab/products/kapous.jpg'),
(gen_random_uuid(), 'Гель BeCurly', 'Легкая фиксация', '{Высокая, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/becurly.png'),
(gen_random_uuid(), 'Шампунь Curl Rock And Roll', 'Интенсивное очищение', '{Средняя, Толстые, Нет}', 'https://storage.yandexcloud.net/curlylab/products/curlrockroll.jpg'),
(gen_random_uuid(), 'Кондиционер PROКУДРИ', 'С экстрактом грейпфрута', '{Высокая, Толстые, Да}', 'https://storage.yandexcloud.net/curlylab/products/condprokudry.jpg'),
(gen_random_uuid(), 'Пилинг Clan', 'Глубокое очищение кожи головы', '{Средняя, Тонкие, Нет}', 'https://storage.yandexcloud.net/curlylab/products/pealclan.jpg'),
(gen_random_uuid(), 'Крем для волос Tresemme Stop Пушистость', 'С гиалуроном', '{Низкая, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/creamstoppuh.jpeg'),
(gen_random_uuid(), 'Мусс Natura Siberica', 'Для максимального объема', '{Средняя, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/naturasiberica.jpg'),
(gen_random_uuid(), 'Гель-кастард Кудрявый Метод', 'Обеспечивает память завитка', '{Средняя, Толстые, Да}', 'https://storage.yandexcloud.net/curlylab/products/custardcurlymethod.jpg'),
(gen_random_uuid(), 'Гель Borodatos', 'Сильная фиксация', '{Высокая, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/gelborodatos.jpg'),
(gen_random_uuid(), 'Маска Superfood Банан', 'Глубокое увлажнение', '{Низкая, Тонкие, Нет}', 'https://storage.yandexcloud.net/curlylab/products/superfoodbanana.png'),
(gen_random_uuid(), 'Шампунь Aveda Be Curly', 'Бессульфатный шампунь для кудрявых волос', '{Низкая, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/avedabecurlyshampoo.jpg'),
(gen_random_uuid(), 'Сухой шампунь Batiste Original', 'Сухой шампунь для свежести волос', '{Все, Все, Нет}', 'https://storage.yandexcloud.net/curlylab/products/batiste.jpg'),
(gen_random_uuid(), 'Крем для укладки Cantu Shea Butter Coconut', 'Крем для завивки с кокосом и маслом ши', '{Средняя, Толстые, Да}', 'https://storage.yandexcloud.net/curlylab/products/cantucream.png'),
(gen_random_uuid(), 'Кондиционер для окрашенных волос', 'Кондиционер для закрепления цвета', '{Средняя, Все, Да}', 'https://storage.yandexcloud.net/curlylab/products/conditionertakihodi.jpg'),
(gen_random_uuid(), 'Кондиционер Shea Moisture Raw Shea Butter', 'Восстанавливающий кондиционер с маслом ши', '{Высокая, Толстые, Нет}', 'https://storage.yandexcloud.net/curlylab/products/condsheabutter.png'),
(gen_random_uuid(), 'Шампунь-клинзер As I Am Coconut Cowash', 'Очищающий крем-ко-вош для кудрявых волос', '{Средняя, Толстые, Да}', 'https://storage.yandexcloud.net/curlylab/products/cowashyesiam.jpg'),
(gen_random_uuid(), 'Гель для укладки Eco Styler Olive Oil', 'Гель с оливковым маслом для фиксации', '{Высокая, Толстые, Нет}', 'https://storage.yandexcloud.net/curlylab/products/ecostyler.jpg'),
(gen_random_uuid(), 'Шампунь Giovanni Smooth as Silk', 'Разглаживающий шампунь для блеска', '{Низкая, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/giovannisilk.jpeg'),
(gen_random_uuid(), 'Маска Kerastase Nutritive', 'Восстанавливающая маска для сухих волос', '{Высокая, Толстые, Да}', 'https://storage.yandexcloud.net/curlylab/products/kerastasemask.jpg'),
(gen_random_uuid(), 'Спрей для объема Schwarzkopf OSIS+', 'Спрей для прикорневого объема', '{Все, Все, Да}', 'https://storage.yandexcloud.net/curlylab/products/osis.png'),
(gen_random_uuid(), 'Мусс для объема Pantene Pro-V Curl Perfection', 'Мусс для совершенства кудрей', '{Средняя, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/panteneprov.jpg'),
(gen_random_uuid(), 'Кондиционер Pantene', 'Увлажняющий кондиционер', '{Средняя, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/penatakihodi.png'),
(gen_random_uuid(), 'Сыворотка Moroccanoil Treatment', 'Восстанавливающая сыворотка с аргановым маслом', '{Средняя, Все, Да}', 'https://storage.yandexcloud.net/curlylab/products/serummoroccanoil.jpg'),
(gen_random_uuid(), 'Шампунь Maui Moisture Lightweight Curls', 'Бессульфатный шампунь для легких кудрей', '{Низкая, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/shampoomaui.png'),
(gen_random_uuid(), 'Шампунь для окрашенных волос', 'Бессульфатный шампунь для сохранения цвета', '{Низкая, Все, Да}', 'https://storage.yandexcloud.net/curlylab/products/shampootakihodi.png'),
(gen_random_uuid(), 'Шампунь Shea Moisture Coconut & Hibiscus', 'Восстанавливающий шампунь для кудрявых волос', '{Высокая, Толстые, Нет}', 'https://storage.yandexcloud.net/curlylab/products/sheashampoo.jpg'),
(gen_random_uuid(), 'Крем для объема Sowell Wonder Volume', 'Крем для прикорневого объема', '{Все, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/sowellwondervolume.jpg'),
(gen_random_uuid(), 'Термозащита Syoss Heat Protect', 'Спрей для защиты от высоких температур', '{Средняя, Все, Да}', 'https://storage.yandexcloud.net/curlylab/products/sysosheatprot.jpg'),
(gen_random_uuid(), 'Спрей Wella Professionals EIMI', 'Спрей для фиксации и сияния', '{Высокая, Тонкие, Да}', 'https://storage.yandexcloud.net/curlylab/products/wellaeimi.jpg');

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
