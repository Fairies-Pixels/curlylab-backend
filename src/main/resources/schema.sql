ALTER TABLE public.user_hairtypes
    DROP CONSTRAINT fk_user_hairtypes_user_id,
    ADD CONSTRAINT fk_user_hairtypes_user_id
    FOREIGN KEY (user_id) REFERENCES public.users(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

ALTER TABLE public.reviews
    DROP CONSTRAINT fk_user_review_user_id,
    ADD CONSTRAINT fk_user_review_user_id
    FOREIGN KEY (user_id) REFERENCES public.users(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

ALTER TABLE public.favourites
    DROP CONSTRAINT fk_favourites_users_user_id,
    ADD CONSTRAINT fk_favourites_users_user_id
    FOREIGN KEY (user_id) REFERENCES public.users(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

ALTER TABLE public.user_auth
    DROP CONSTRAINT fk_user_auth_user_id,
    ADD CONSTRAINT fk_user_auth_user_id
    FOREIGN KEY (user_id) REFERENCES public.users(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

ALTER TABLE public.user_refresh_tokens
    DROP CONSTRAINT fk_user_refresh_tokens_user_id,
    ADD CONSTRAINT fk_user_refresh_tokens_user_id
    FOREIGN KEY (user_id) REFERENCES public.users(id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;
