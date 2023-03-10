toc.dat                                                                                             0000600 0004000 0002000 00000314410 14351347705 0014453 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        PGDMP       ,    8                z            cinema_u49e    15.0    15.0 #   ?           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false         ?           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false         ?           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false         ?           1262    16389    cinema_u49e    DATABASE     v   CREATE DATABASE cinema_u49e WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.UTF8';
    DROP DATABASE cinema_u49e;
                mirea_4dmin    false         ?           0    0    DATABASE cinema_u49e    ACL     8   GRANT CONNECT ON DATABASE cinema_u49e TO ordinary_user;
                   mirea_4dmin    false    3461         ?           0    0    cinema_u49e    DATABASE PROPERTIES     4   ALTER DATABASE cinema_u49e SET "TimeZone" TO 'utc';
                     mirea_4dmin    false                     2615    2200    public    SCHEMA        CREATE SCHEMA public;
    DROP SCHEMA public;
                mirea_4dmin    false         ?           0    0    SCHEMA public    COMMENT     6   COMMENT ON SCHEMA public IS 'standard public schema';
                   mirea_4dmin    false    6         H           1255    17207 b   add_celebrity(character varying, smallint, date, date, text, character varying, character varying) 	   PROCEDURE     ?  CREATE PROCEDURE public.add_celebrity(IN cel_name character varying, IN cel_height smallint, IN cel_birthday date, IN cel_death date, IN cel_birthplace text, IN cel_career character varying, IN cel_img character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO celebrity(name, height, birthday, death, birthplace, career, img_link)
                        VALUES (cel_name, cel_height, cel_birthday, cel_death, cel_birthplace, cel_career, cel_img);
END; $$;
 ?   DROP PROCEDURE public.add_celebrity(IN cel_name character varying, IN cel_height smallint, IN cel_birthday date, IN cel_death date, IN cel_birthplace text, IN cel_career character varying, IN cel_img character varying);
       public          mirea_4dmin    false    6         D           1255    17209 0   add_collection(character varying, text, integer) 	   PROCEDURE     ?  CREATE PROCEDURE public.add_collection(IN coll_name character varying, IN coll_description text, IN coll_uid integer)
    LANGUAGE plpgsql
    AS $$
DECLARE user_status bool;
BEGIN
    SELECT is_banned INTO user_status FROM site_user WHERE uid = coll_uid;
    IF user_status IS true THEN
        RAISE EXCEPTION 'Ваш аккаунт заблокирован!';
    END IF;
    INSERT INTO collection(name, description, uid)
                        VALUES (coll_name, coll_description, coll_uid);
END; $$;
 u   DROP PROCEDURE public.add_collection(IN coll_name character varying, IN coll_description text, IN coll_uid integer);
       public          mirea_4dmin    false    6         F           1255    17144 e   add_content(character varying, text, bigint, bigint, time without time zone, character varying, date) 	   PROCEDURE       CREATE PROCEDURE public.add_content(IN film_name character varying, IN film_description text, IN film_budget bigint, IN film_box_office bigint, IN film_duration time without time zone, IN film_image_link character varying, IN film_release_date date)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO content(name, description, budget, box_office, duration, image_link, release_date)
                        VALUES (film_name, film_description, film_budget,film_box_office,film_duration, film_image_link, film_release_date);

END; $$;
 ?   DROP PROCEDURE public.add_content(IN film_name character varying, IN film_description text, IN film_budget bigint, IN film_box_office bigint, IN film_duration time without time zone, IN film_image_link character varying, IN film_release_date date);
       public          mirea_4dmin    false    6         ?           1255    16993 (   add_film_to_collection(integer, integer) 	   PROCEDURE       CREATE PROCEDURE public.add_film_to_collection(IN coll_id integer, IN film_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF (SELECT count(*) FROM content_in_collection WHERE collection_id = coll_id AND content_id = film_id) > 0 THEN
        RAISE EXCEPTION 'Фильм уже добавлен!';
    END IF;
    INSERT INTO content_in_collection(content_id, collection_id, film_number)
                        VALUES (film_id, coll_id, ((SELECT COUNT(*) FROM content_in_collection WHERE collection_id = coll_id)+1));
END; $$;
 V   DROP PROCEDURE public.add_film_to_collection(IN coll_id integer, IN film_id integer);
       public          mirea_4dmin    false    6         6           1255    16871    add_genre(character varying) 	   PROCEDURE     ?   CREATE PROCEDURE public.add_genre(IN genre_name character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO genre(name)
                        VALUES (genre_name);
END; $$;
 B   DROP PROCEDURE public.add_genre(IN genre_name character varying);
       public          mirea_4dmin    false    6         N           1255    17402 <   add_new(character varying, text, integer, character varying) 	   PROCEDURE     ?  CREATE PROCEDURE public.add_new(IN new_title character varying, IN new_description text, IN new_uid integer, IN image character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO news(title, description, news_date, uid, image_link)
                        VALUES (new_title, new_description, CURRENT_DATE, new_uid, image);
                                        
                                        
END; $$;
 ?   DROP PROCEDURE public.add_new(IN new_title character varying, IN new_description text, IN new_uid integer, IN image character varying);
       public          mirea_4dmin    false    6         M           1255    17281 ?   add_review(character varying, text, integer, integer, smallint) 	   PROCEDURE     ?  CREATE PROCEDURE public.add_review(IN review_title character varying, IN review_description text, IN review_content_id integer, IN review_uid integer, IN review_rating smallint)
    LANGUAGE plpgsql
    AS $$
DECLARE user_status bool;
BEGIN
    SELECT is_banned INTO user_status FROM site_user WHERE uid = review_uid;
    IF user_status IS true THEN
        RAISE EXCEPTION 'Ваш аккаунт заблокирован!';
    END IF;
    INSERT INTO review(title, description, rev_date, content_id, uid, opinion)
                        VALUES (review_title, review_description, CURRENT_DATE, review_content_id, review_uid, review_rating);
    COMMIT;
END; $$;
 ?   DROP PROCEDURE public.add_review(IN review_title character varying, IN review_description text, IN review_content_id integer, IN review_uid integer, IN review_rating smallint);
       public          mirea_4dmin    false    6         3           1255    17103 $   add_star(integer, integer, smallint) 	   PROCEDURE       CREATE PROCEDURE public.add_star(IN star_content_id integer, IN star_uid integer, IN star_rating smallint)
    LANGUAGE plpgsql
    AS $$
DECLARE user_status bool;
BEGIN
    SELECT is_banned INTO user_status FROM site_user WHERE uid = star_uid;
    IF user_status IS true THEN
        RAISE EXCEPTION 'Ваш аккаунт заблокирован!';
    END IF;
    INSERT INTO user_stars(content_id, uid, rating)
                        VALUES (star_content_id, star_uid, star_rating);                 
END; $$;
 j   DROP PROCEDURE public.add_star(IN star_content_id integer, IN star_uid integer, IN star_rating smallint);
       public          mirea_4dmin    false    6         2           1255    17106 :   assign_celebrity_to_content(integer, integer[], integer[]) 	   PROCEDURE     ?  CREATE PROCEDURE public.assign_celebrity_to_content(IN film_id integer, IN cel_id integer[], IN role_id integer[])
    LANGUAGE plpgsql
    AS $$
BEGIN
    FOR i in 1..(array_length(cel_id, 1)) LOOP
        INSERT INTO celebrity_in_content(content_id, cid, role, priority)
                                VALUES (film_id,  cel_id[i], role_id[i], ((SELECT COUNT(*) FROM celebrity_in_content WHERE content_id = film_id)+1));
    END LOOP;
    COMMIT;
END; $$;
 r   DROP PROCEDURE public.assign_celebrity_to_content(IN film_id integer, IN cel_id integer[], IN role_id integer[]);
       public          mirea_4dmin    false    6         B           1255    17021 *   assign_country_to_film(integer, integer[]) 	   PROCEDURE     Z  CREATE PROCEDURE public.assign_country_to_film(IN film_id integer, IN country_id integer[])
    LANGUAGE plpgsql
    AS $$
    DECLARE c integer;
BEGIN
    FOREACH c IN ARRAY country_id
    LOOP
        INSERT INTO countries_of_content(content_id, country_id)
                            VALUES (film_id,  c);
    END LOOP ;
    COMMIT;
END; $$;
 [   DROP PROCEDURE public.assign_country_to_film(IN film_id integer, IN country_id integer[]);
       public          mirea_4dmin    false    6         <           1255    17022 (   assign_genre_to_film(integer[], integer) 	   PROCEDURE     G  CREATE PROCEDURE public.assign_genre_to_film(IN gen_id integer[], IN film_id integer)
    LANGUAGE plpgsql
    AS $$
DECLARE g integer;
BEGIN
    FOREACH g IN ARRAY gen_id
    LOOP
        INSERT INTO genres_of_content(content_id, genre_id)
                            VALUES (film_id,  g);
    END LOOP ;
    COMMIT;
END; $$;
 U   DROP PROCEDURE public.assign_genre_to_film(IN gen_id integer[], IN film_id integer);
       public          mirea_4dmin    false    6         7           1255    16615 !   ban_user(character varying, text) 	   PROCEDURE     ?  CREATE PROCEDURE public.ban_user(IN input_login character varying, IN reason_to_ban text)
    LANGUAGE plpgsql
    AS $$
DECLARE user_ban_status bool;
BEGIN
    IF (pg_has_role(input_login, 'moderator', 'MEMBER')) THEN
        RAISE EXCEPTION 'Нельзя забанить модератора!';
    END IF;
    SELECT is_banned INTO user_ban_status FROM site_user WHERE login = input_login FOR NO KEY UPDATE ;
    IF (user_ban_status) THEN
        RAISE EXCEPTION 'Пользователь уже заблокирован!';
    END IF;
    UPDATE site_user SET is_banned = true, ban_reason = reason_to_ban, ban_date = CURRENT_DATE WHERE login = input_login;
END; $$;
 Y   DROP PROCEDURE public.ban_user(IN input_login character varying, IN reason_to_ban text);
       public          mirea_4dmin    false    6         8           1255    17361    ban_user_by_id(integer, text) 	   PROCEDURE     ?  CREATE PROCEDURE public.ban_user_by_id(IN user_id integer, IN reason text)
    LANGUAGE plpgsql
    AS $$
DECLARE user_ban_status bool;
BEGIN
    IF (pg_has_role((SELECT login FROM site_user WHERE uid = user_id), 'moderator', 'MEMBER')) THEN
        RAISE EXCEPTION 'Нельзя забанить модератора!';
    END IF;
    SELECT is_banned INTO user_ban_status FROM site_user WHERE uid = user_id FOR NO KEY UPDATE;
    IF (user_ban_status) THEN
        RAISE EXCEPTION 'Пользователь уже заблокирован!';
    END IF;
    UPDATE site_user SET is_banned = true, ban_reason = reason, ban_date = CURRENT_DATE WHERE uid = user_id;
END; $$;
 J   DROP PROCEDURE public.ban_user_by_id(IN user_id integer, IN reason text);
       public          mirea_4dmin    false    6         ,           1255    16800    banned_delete()    FUNCTION     ?   CREATE FUNCTION public.banned_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        DELETE FROM site_user WHERE NOW() - site_user.ban_date > interval '30 days' ;
        RETURN NULL;
    END;
    $$;
 &   DROP FUNCTION public.banned_delete();
       public          mirea_4dmin    false    6         ?           0    0    FUNCTION banned_delete()    ACL     ;   GRANT ALL ON FUNCTION public.banned_delete() TO moderator;
          public          mirea_4dmin    false    300         @           1255    16961    calculate_rating_insert()    FUNCTION     ?  CREATE FUNCTION public.calculate_rating_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE rating_total decimal(4, 2); id integer;
BEGIN
    SELECT SUM(user_stars.rating::numeric)/COUNT(user_stars.rating::numeric) INTO rating_total FROM user_stars WHERE content_id = NEW.content_id;
    id = NEW.content_id;
    UPDATE content SET rating = rating_total WHERE content.content_id = id;
    RETURN NEW;
END; $$;
 0   DROP FUNCTION public.calculate_rating_insert();
       public          mirea_4dmin    false    6         A           1255    17002     calculate_rating_update_delete()    FUNCTION     ?  CREATE FUNCTION public.calculate_rating_update_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE rating_total decimal(4, 2); id integer;
BEGIN
   SELECT SUM(user_stars.rating::numeric)/COUNT(user_stars.rating::numeric) INTO rating_total FROM user_stars WHERE content_id = OLD.content_id;
    id = OLD.content_id;
   IF rating_total is null THEN
       rating_total = 0;
   end if;
    UPDATE content SET rating = rating_total WHERE content.content_id = id;
    RETURN OLD;
END; $$;
 7   DROP FUNCTION public.calculate_rating_update_delete();
       public          mirea_4dmin    false    6         -           1255    16917    delete_celebrity(integer) 	   PROCEDURE     ?   CREATE PROCEDURE public.delete_celebrity(IN cel_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM celebrity WHERE cid = cel_id; 
END; $$;
 ;   DROP PROCEDURE public.delete_celebrity(IN cel_id integer);
       public          mirea_4dmin    false    6                     1255    17210    delete_collection(integer) 	   PROCEDURE     ?   CREATE PROCEDURE public.delete_collection(IN coll_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM collection WHERE collection_id = coll_id;
END; $$;
 =   DROP PROCEDURE public.delete_collection(IN coll_id integer);
       public          mirea_4dmin    false    6         9           1255    16997    delete_content(integer) 	   PROCEDURE     ?   CREATE PROCEDURE public.delete_content(IN con_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM content WHERE content_id = con_id; 
END; $$;
 9   DROP PROCEDURE public.delete_content(IN con_id integer);
       public          mirea_4dmin    false    6         :           1255    16998 -   delete_film_from_collection(integer, integer) 	   PROCEDURE     ?   CREATE PROCEDURE public.delete_film_from_collection(IN coll_id integer, IN film_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM content_in_collection WHERE content_id = film_id AND collection_id = coll_id;
    COMMIT;
END; $$;
 [   DROP PROCEDURE public.delete_film_from_collection(IN coll_id integer, IN film_id integer);
       public          mirea_4dmin    false    6         .           1255    16872    delete_genre(character varying) 	   PROCEDURE     ?   CREATE PROCEDURE public.delete_genre(IN genre_name character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM genre WHERE name = genre_name; 
END; $$;
 E   DROP PROCEDURE public.delete_genre(IN genre_name character varying);
       public          mirea_4dmin    false    6         5           1255    16921    delete_new(integer) 	   PROCEDURE     ?   CREATE PROCEDURE public.delete_new(IN new_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM news WHERE nid = new_id; 
END; $$;
 5   DROP PROCEDURE public.delete_new(IN new_id integer);
       public          mirea_4dmin    false    6         /           1255    16914    delete_review(integer) 	   PROCEDURE     ?   CREATE PROCEDURE public.delete_review(IN review_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM review WHERE rid = review_id;
END; $$;
 ;   DROP PROCEDURE public.delete_review(IN review_id integer);
       public          mirea_4dmin    false    6         ;           1255    16923    delete_star(integer) 	   PROCEDURE     ?   CREATE PROCEDURE public.delete_star(IN star_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM user_stars WHERE sid = star_id; 
END; $$;
 7   DROP PROCEDURE public.delete_star(IN star_id integer);
       public          mirea_4dmin    false    6                    1255    16614 5   hash_pass_match(character varying, character varying)    FUNCTION       CREATE FUNCTION public.hash_pass_match(input_login character varying, password character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN(SELECT (password_hash = crypt(password, password_hash)) AS pswmatch FROM site_user where login = input_login);

END; $$;
 a   DROP FUNCTION public.hash_pass_match(input_login character varying, password character varying);
       public          mirea_4dmin    false    6         =           1255    16618 3   hash_password(character varying, character varying)    FUNCTION     &  CREATE FUNCTION public.hash_password(password character varying, login character varying) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
    hashed_password varchar;
BEGIN
    hashed_password = encode(digest(concat(password, login), 'sha256'), 'hex');
    RETURN hashed_password;
END; $$;
 Y   DROP FUNCTION public.hash_password(password character varying, login character varying);
       public          mirea_4dmin    false    6         K           1255    17283 ^   register_moderator(character varying, character varying, character varying, character varying) 	   PROCEDURE       CREATE PROCEDURE public.register_moderator(IN username character varying, IN email character varying, IN u_login character varying, IN password character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    hashed_password varchar;
BEGIN
    IF (SELECT COUNT(*) FROM pg_roles WHERE rolname=u_login) THEN
        RAISE EXCEPTION 'User Already Exists!';
    ELSE
        hashed_password = encode(digest(concat(password, u_login), 'sha256'), 'hex');
        EXECUTE format('INSERT INTO site_user(nickname, email, is_banned, ban_date, ban_reason, login)
                        VALUES (%L, %L, false, null, null, %L);', username, email, u_login);
        EXECUTE format('CREATE USER %I PASSWORD %L IN ROLE moderator;',
                        u_login, hashed_password);
    END IF;
END; $$;
 ?   DROP PROCEDURE public.register_moderator(IN username character varying, IN email character varying, IN u_login character varying, IN password character varying);
       public          mirea_4dmin    false    6         P           1255    16673 Y   register_user(character varying, character varying, character varying, character varying) 	   PROCEDURE       CREATE PROCEDURE public.register_user(IN username character varying, IN email character varying, IN u_login character varying, IN password character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    decrypted_pass varchar;
    hashed_password varchar;
    rand_num char(16);
BEGIN
    IF (SELECT COUNT(*) FROM pg_roles WHERE rolname=u_login) THEN
        RAISE EXCEPTION 'Пользователь с таким логином уже существует!';
    ELSE
        IF (NOT exists(SELECT FROM reg_table WHERE login = u_login)) THEN
            RAISE EXCEPTION 'Ошибка при регистрации. Попробуйте позже!';
        END IF;
        rand_num = (SELECT r_num FROM reg_table WHERE login = u_login);
        decrypted_pass = encode(decrypt(decode(password, 'base64'), rand_num::bytea, 'aes-cbc'), 'escape');
        hashed_password = encode(digest(concat(decrypted_pass, u_login), 'sha256'), 'hex');
        EXECUTE format('INSERT INTO site_user(nickname, email, is_banned, ban_date, ban_reason, login)
                        VALUES (%L, %L, false, null, null, %L);', username, email, u_login);
        EXECUTE format('CREATE USER %I PASSWORD %L IN ROLE ordinary_user;',
                        u_login, hashed_password);
    END IF;
END; $$;
 ?   DROP PROCEDURE public.register_user(IN username character varying, IN email character varying, IN u_login character varying, IN password character varying);
       public          mirea_4dmin    false    6         >           1255    16999 (   remove_genre_from_film(integer, integer) 	   PROCEDURE     ?   CREATE PROCEDURE public.remove_genre_from_film(IN gen_id integer, IN film_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM genres_of_content WHERE content_id =  film_id AND genre_id = gen_id;
END; $$;
 U   DROP PROCEDURE public.remove_genre_from_film(IN gen_id integer, IN film_id integer);
       public          mirea_4dmin    false    6         0           1255    16657 8   start_registration(character varying, character varying)    FUNCTION     S  CREATE FUNCTION public.start_registration(u_login character varying, p_hash character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
    DECLARE
        rand_num char(16);
    BEGIN
        rand_num = left((random()::text), 16);
        CREATE TEMP TABLE IF NOT EXISTS reg_table(login varchar(20) PRIMARY KEY, r_num char(16));
        DELETE FROM reg_table WHERE reg_table.login = u_login;
        INSERT INTO reg_table(login, r_num) VALUES(u_login, rand_num);
        RETURN encode((encrypt(rand_num::bytea, decode(p_hash, 'hex'), 'aes-cbc'))::bytea, 'base64');
    END;
$$;
 ^   DROP FUNCTION public.start_registration(u_login character varying, p_hash character varying);
       public          mirea_4dmin    false    6         4           1255    16616    unban_user(character varying) 	   PROCEDURE     ?  CREATE PROCEDURE public.unban_user(IN input_login character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE user_ban_status bool;
BEGIN
    SELECT is_banned INTO user_ban_status FROM site_user WHERE login = input_login FOR NO KEY UPDATE ;
    IF (NOT user_ban_status) THEN
        RAISE EXCEPTION 'Пользователь не заблокирован!';
    ELSE
    UPDATE site_user SET is_banned = false, ban_reason = null, ban_date = null WHERE login = input_login;
    END IF;
END; $$;
 D   DROP PROCEDURE public.unban_user(IN input_login character varying);
       public          mirea_4dmin    false    6         J           1255    17374    unban_user_by_id(integer) 	   PROCEDURE     ?  CREATE PROCEDURE public.unban_user_by_id(IN id integer)
    LANGUAGE plpgsql
    AS $$
DECLARE user_ban_status bool;
BEGIN
    SELECT is_banned INTO user_ban_status FROM site_user WHERE uid = id FOR NO KEY UPDATE ;
    IF (NOT user_ban_status) THEN
        RAISE EXCEPTION 'Пользователь не заблокирован!';
    ELSE
    UPDATE site_user SET is_banned = false, ban_reason = null, ban_date = null WHERE uid = id;
    END IF;
END; $$;
 7   DROP PROCEDURE public.unban_user_by_id(IN id integer);
       public          mirea_4dmin    false    6         I           1255    17208 n   update_celebrity(character varying, integer, smallint, date, date, text, character varying, character varying) 	   PROCEDURE       CREATE PROCEDURE public.update_celebrity(IN cel_name character varying, IN id integer, IN cel_height smallint, IN cel_birthday date, IN cel_death date, IN cel_birthplace text, IN cel_career character varying, IN cel_img character varying)
    LANGUAGE plpgsql
    AS $$
    BEGIN
        UPDATE celebrity SET name = cel_name, height = cel_height, birthday = cel_birthday, death = cel_death, birthplace = cel_birthplace, 
                             career = cel_career, img_link = cel_img WHERE cid = id;
    END $$;
 ?   DROP PROCEDURE public.update_celebrity(IN cel_name character varying, IN id integer, IN cel_height smallint, IN cel_birthday date, IN cel_death date, IN cel_birthplace text, IN cel_career character varying, IN cel_img character varying);
       public          mirea_4dmin    false    6         1           1255    17280 3   update_collection(character varying, text, integer) 	   PROCEDURE     ?   CREATE PROCEDURE public.update_collection(IN nm character varying, IN descr text, IN id integer)
    LANGUAGE plpgsql
    AS $$
    BEGIN
        UPDATE collection SET name = nm, description = descr WHERE collection_id = id;
    END $$;
 `   DROP PROCEDURE public.update_collection(IN nm character varying, IN descr text, IN id integer);
       public          mirea_4dmin    false    6         G           1255    17145 q   update_content(character varying, text, bigint, bigint, time without time zone, character varying, date, integer) 	   PROCEDURE     U  CREATE PROCEDURE public.update_content(IN nm character varying, IN descr text, IN budg bigint, IN boffice bigint, IN dur time without time zone, IN image character varying, IN release date, IN id integer)
    LANGUAGE plpgsql
    AS $$
    BEGIN
        UPDATE content SET name = nm, description = descr , budget = budg, box_office = boffice, duration = dur, image_link = image, release_date = release WHERE
                                                                                                                                                                content_id = id;
    END $$;
 ?   DROP PROCEDURE public.update_content(IN nm character varying, IN descr text, IN budg bigint, IN boffice bigint, IN dur time without time zone, IN image character varying, IN release date, IN id integer);
       public          mirea_4dmin    false    6         O           1255    17403 ?   update_new(text, character varying, integer, character varying) 	   PROCEDURE     #  CREATE PROCEDURE public.update_new(IN descr text, IN tit character varying, IN id integer, IN image character varying)
    LANGUAGE plpgsql
    AS $$
    BEGIN
        UPDATE news SET title = tit, image_link = image, description = descr, news_date = CURRENT_DATE WHERE nid = id;
    END $$;
 v   DROP PROCEDURE public.update_new(IN descr text, IN tit character varying, IN id integer, IN image character varying);
       public          mirea_4dmin    false    6         L           1255    17160 9   update_review(character varying, text, smallint, integer) 	   PROCEDURE     O  CREATE PROCEDURE public.update_review(IN review_title character varying, IN review_description text, IN review_rating smallint, IN review_id integer)
    LANGUAGE plpgsql
    AS $$
DECLARE user_status bool;
BEGIN
    SELECT is_banned INTO user_status FROM site_user WHERE uid = (SELECT uid FROM review WHERE rid = review_id);
    IF user_status IS true THEN
        RAISE EXCEPTION 'Ваш аккаунт заблокирован!';
    END IF;
    UPDATE review SET title = review_title, description = review_description, opinion = review_rating WHERE rid = review_id;
    COMMIT;
END; $$;
 ?   DROP PROCEDURE public.update_review(IN review_title character varying, IN review_description text, IN review_rating smallint, IN review_id integer);
       public          mirea_4dmin    false    6         E           1255    17324    update_star(integer, smallint) 	   PROCEDURE     ?  CREATE PROCEDURE public.update_star(IN id integer, IN rate smallint)
    LANGUAGE plpgsql
    AS $$
DECLARE user_status bool;
BEGIN
    SELECT is_banned INTO user_status FROM site_user WHERE uid = (SELECT uid FROM user_stars WHERE sid = id);
    IF user_status IS true THEN
        RAISE EXCEPTION 'Ваш аккаунт заблокирован!';
    END IF;
    UPDATE user_stars SET rating = rate WHERE sid = id;
END $$;
 D   DROP PROCEDURE public.update_star(IN id integer, IN rate smallint);
       public          mirea_4dmin    false    6         C           1255    17018 :   update_user(character varying, character varying, integer) 	   PROCEDURE     ?   CREATE PROCEDURE public.update_user(IN nname character varying, IN mail character varying, IN id integer)
    LANGUAGE plpgsql
    AS $$
    BEGIN
        UPDATE site_user SET nickname = nname, email = mail WHERE uid = id;
    END $$;
 i   DROP PROCEDURE public.update_user(IN nname character varying, IN mail character varying, IN id integer);
       public          mirea_4dmin    false    6                    1255    16720    user_system_delete()    FUNCTION     ?   CREATE FUNCTION public.user_system_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    EXECUTE format('DROP USER %I;', OLD.login);
    RETURN NULL;
END; $$;
 +   DROP FUNCTION public.user_system_delete();
       public          mirea_4dmin    false    6         ?           0    0    FUNCTION user_system_delete()    ACL     ?   GRANT ALL ON FUNCTION public.user_system_delete() TO moderator;
GRANT ALL ON FUNCTION public.user_system_delete() TO ordinary_user;
GRANT ALL ON FUNCTION public.user_system_delete() TO not_login_user;
          public          mirea_4dmin    false    286         ?            1259    16417    content    TABLE     ?  CREATE TABLE public.content (
    content_id integer NOT NULL,
    name character varying(50) NOT NULL,
    description text NOT NULL,
    budget bigint,
    box_office bigint,
    duration time without time zone NOT NULL,
    rating numeric(10,2) DEFAULT 0 NOT NULL,
    image_link character varying(100),
    release_date date NOT NULL,
    CONSTRAINT content_soundtrack_rating_check CHECK ((rating <= (10)::numeric)),
    CONSTRAINT valid_values3 CHECK ((rating >= (0)::numeric))
);
    DROP TABLE public.content;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE content    COMMENT     ?   COMMENT ON TABLE public.content IS 'Объект, представляющий собой фильм, сериал и другой видеоконтент.';
          public          mirea_4dmin    false    228         ?           0    0    COLUMN content.content_id    COMMENT     ?   COMMENT ON COLUMN public.content.content_id IS 'Уникальный код видеоконтента в цифровом формате';
          public          mirea_4dmin    false    228         ?           0    0    COLUMN content.name    COMMENT     N   COMMENT ON COLUMN public.content.name IS 'Название контента';
          public          mirea_4dmin    false    228         ?           0    0    COLUMN content.description    COMMENT     `   COMMENT ON COLUMN public.content.description IS 'Краткое описание сюжета';
          public          mirea_4dmin    false    228         ?           0    0    COLUMN content.budget    COMMENT     V   COMMENT ON COLUMN public.content.budget IS 'Потраченные средства';
          public          mirea_4dmin    false    228         ?           0    0    COLUMN content.box_office    COMMENT     \   COMMENT ON COLUMN public.content.box_office IS 'Заработанные средства';
          public          mirea_4dmin    false    228         ?           0    0    COLUMN content.rating    COMMENT     ?   COMMENT ON COLUMN public.content.rating IS 'Рейтинг саундтрека по польщовательским оценкам';
          public          mirea_4dmin    false    228         ?           0    0    TABLE content    ACL     ?   GRANT SELECT,INSERT,UPDATE ON TABLE public.content TO moderator;
GRANT SELECT ON TABLE public.content TO ordinary_user;
GRANT SELECT ON TABLE public.content TO not_login_user;
          public          mirea_4dmin    false    228         ?           0    0    COLUMN content.rating    ACL     ?   GRANT UPDATE(rating) ON TABLE public.content TO ordinary_user;
          public          mirea_4dmin    false    228    3474         ?            1259    17407 
   best_films    VIEW     O  CREATE VIEW public.best_films AS
 SELECT content.content_id,
    content.name,
    content.description,
    content.budget,
    content.box_office,
    content.duration,
    content.rating,
    content.image_link,
    content.release_date
   FROM public.content
  WHERE (content.rating >= (8)::numeric)
  ORDER BY content.rating DESC;
    DROP VIEW public.best_films;
       public          mirea_4dmin    false    228    228    228    228    228    228    228    228    228    6         ?           0    0    TABLE best_films    ACL     ?   GRANT SELECT ON TABLE public.best_films TO moderator;
GRANT SELECT ON TABLE public.best_films TO ordinary_user;
GRANT SELECT ON TABLE public.best_films TO not_login_user;
          public          mirea_4dmin    false    247         ?            1259    16397 	   celebrity    TABLE     L  CREATE TABLE public.celebrity (
    cid integer NOT NULL,
    name character varying(50) NOT NULL,
    height smallint,
    birthday date,
    death date,
    birthplace text,
    career character varying(100),
    img_link character varying(100),
    CONSTRAINT valid_review CHECK (((name)::text ~ '^[A-Za-zА-Яа-я]'::text))
);
    DROP TABLE public.celebrity;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE celebrity    COMMENT     ?   COMMENT ON TABLE public.celebrity IS 'Объект, содержащий информацию об актере, режиссере, или других участниках сьемочного процесса.';
          public          mirea_4dmin    false    223         ?           0    0    COLUMN celebrity.cid    COMMENT     v   COMMENT ON COLUMN public.celebrity.cid IS 'Уникальный идентификатор знаменитости';
          public          mirea_4dmin    false    223         ?           0    0    COLUMN celebrity.name    COMMENT     N   COMMENT ON COLUMN public.celebrity.name IS 'Имя знаменитости';
          public          mirea_4dmin    false    223         ?           0    0    TABLE celebrity    ACL     ?   GRANT SELECT,INSERT,UPDATE ON TABLE public.celebrity TO moderator;
GRANT SELECT ON TABLE public.celebrity TO ordinary_user;
GRANT SELECT ON TABLE public.celebrity TO not_login_user;
          public          mirea_4dmin    false    223         ?            1259    16918    celebrity_cid_seq    SEQUENCE     z   CREATE SEQUENCE public.celebrity_cid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 (   DROP SEQUENCE public.celebrity_cid_seq;
       public          mirea_4dmin    false    223    6         ?           0    0    celebrity_cid_seq    SEQUENCE OWNED BY     G   ALTER SEQUENCE public.celebrity_cid_seq OWNED BY public.celebrity.cid;
          public          mirea_4dmin    false    243         ?           0    0    SEQUENCE celebrity_cid_seq    ACL     ?   GRANT SELECT,USAGE ON SEQUENCE public.celebrity_cid_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.celebrity_cid_seq TO ordinary_user;
          public          mirea_4dmin    false    243         ?            1259    16404    celebrity_in_content    TABLE     ?   CREATE TABLE public.celebrity_in_content (
    content_id integer NOT NULL,
    cid integer NOT NULL,
    role smallint NOT NULL,
    description character varying(100),
    priority smallint
);
 (   DROP TABLE public.celebrity_in_content;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE celebrity_in_content    COMMENT     \   COMMENT ON TABLE public.celebrity_in_content IS 'Знаменитость в фильме';
          public          mirea_4dmin    false    224         ?           0    0 &   COLUMN celebrity_in_content.content_id    COMMENT     ?   COMMENT ON COLUMN public.celebrity_in_content.content_id IS 'Уникальный код видеоконтента в цифровом формате';
          public          mirea_4dmin    false    224         ?           0    0    COLUMN celebrity_in_content.cid    COMMENT     ?   COMMENT ON COLUMN public.celebrity_in_content.cid IS 'Уникальный идентификатор знаменитости';
          public          mirea_4dmin    false    224         ?           0    0    TABLE celebrity_in_content    ACL     ?   GRANT SELECT,INSERT,UPDATE ON TABLE public.celebrity_in_content TO moderator;
GRANT SELECT ON TABLE public.celebrity_in_content TO ordinary_user;
GRANT SELECT ON TABLE public.celebrity_in_content TO not_login_user;
          public          mirea_4dmin    false    224         ?            1259    16408 
   collection    TABLE     ?   CREATE TABLE public.collection (
    collection_id integer NOT NULL,
    name character varying(30) NOT NULL,
    description text,
    uid integer,
    CONSTRAINT valid_name CHECK (((name)::text ~ '^[A-Za-zА-Яа-я0-9!".,]'::text))
);
    DROP TABLE public.collection;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE collection    COMMENT     }   COMMENT ON TABLE public.collection IS 'Фильмы, отобранные по определенному признаку';
          public          mirea_4dmin    false    226         ?           0    0    COLUMN collection.collection_id    COMMENT     f   COMMENT ON COLUMN public.collection.collection_id IS 'Идентификатор коллекции';
          public          mirea_4dmin    false    226         ?           0    0    COLUMN collection.name    COMMENT     Q   COMMENT ON COLUMN public.collection.name IS 'Название подборки';
          public          mirea_4dmin    false    226         ?           0    0    COLUMN collection.description    COMMENT     k   COMMENT ON COLUMN public.collection.description IS 'Текстовое описание подборки';
          public          mirea_4dmin    false    226         ?           0    0    COLUMN collection.uid    COMMENT     b   COMMENT ON COLUMN public.collection.uid IS 'Идентификатор пользователя';
          public          mirea_4dmin    false    226         ?           0    0    TABLE collection    ACL     ?   GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.collection TO moderator;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.collection TO ordinary_user;
GRANT SELECT ON TABLE public.collection TO not_login_user;
          public          mirea_4dmin    false    226         ?           0    0    COLUMN collection.name    ACL     @   GRANT UPDATE(name) ON TABLE public.collection TO ordinary_user;
          public          mirea_4dmin    false    226    3492         ?           0    0    COLUMN collection.description    ACL     G   GRANT UPDATE(description) ON TABLE public.collection TO ordinary_user;
          public          mirea_4dmin    false    226    3492         ?            1259    16407    collection_collection_id_seq    SEQUENCE     ?   CREATE SEQUENCE public.collection_collection_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 3   DROP SEQUENCE public.collection_collection_id_seq;
       public          mirea_4dmin    false    226    6         ?           0    0    collection_collection_id_seq    SEQUENCE OWNED BY     ]   ALTER SEQUENCE public.collection_collection_id_seq OWNED BY public.collection.collection_id;
          public          mirea_4dmin    false    225         ?           0    0 %   SEQUENCE collection_collection_id_seq    ACL     ?   GRANT SELECT,USAGE ON SEQUENCE public.collection_collection_id_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.collection_collection_id_seq TO ordinary_user;
          public          mirea_4dmin    false    225         ?            1259    16416    content_content_id_seq    SEQUENCE     ?   CREATE SEQUENCE public.content_content_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 -   DROP SEQUENCE public.content_content_id_seq;
       public          mirea_4dmin    false    6    228         ?           0    0    content_content_id_seq    SEQUENCE OWNED BY     Q   ALTER SEQUENCE public.content_content_id_seq OWNED BY public.content.content_id;
          public          mirea_4dmin    false    227         ?           0    0    SEQUENCE content_content_id_seq    ACL     ?   GRANT SELECT,USAGE ON SEQUENCE public.content_content_id_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.content_content_id_seq TO ordinary_user;
          public          mirea_4dmin    false    227         ?            1259    16431    content_in_collection    TABLE     ?   CREATE TABLE public.content_in_collection (
    content_id integer NOT NULL,
    collection_id integer NOT NULL,
    film_number integer NOT NULL
);
 )   DROP TABLE public.content_in_collection;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE content_in_collection    COMMENT     S   COMMENT ON TABLE public.content_in_collection IS 'Фильм в подборке';
          public          mirea_4dmin    false    229         ?           0    0 '   COLUMN content_in_collection.content_id    COMMENT     ?   COMMENT ON COLUMN public.content_in_collection.content_id IS 'Уникальный код видеоконтента в цифровом формате';
          public          mirea_4dmin    false    229         ?           0    0 *   COLUMN content_in_collection.collection_id    COMMENT     q   COMMENT ON COLUMN public.content_in_collection.collection_id IS 'Идентификатор коллекции';
          public          mirea_4dmin    false    229         ?           0    0    TABLE content_in_collection    ACL     ?   GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.content_in_collection TO moderator;
GRANT SELECT,INSERT,DELETE ON TABLE public.content_in_collection TO ordinary_user;
GRANT SELECT ON TABLE public.content_in_collection TO not_login_user;
          public          mirea_4dmin    false    229         ?            1259    16434    countries_of_content    TABLE     o   CREATE TABLE public.countries_of_content (
    content_id integer NOT NULL,
    country_id integer NOT NULL
);
 (   DROP TABLE public.countries_of_content;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE countries_of_content    COMMENT     ?   COMMENT ON TABLE public.countries_of_content IS 'Страна проиводства конкретного фильма/сериала/другого контента';
          public          mirea_4dmin    false    230         ?           0    0 &   COLUMN countries_of_content.content_id    COMMENT     ?   COMMENT ON COLUMN public.countries_of_content.content_id IS 'Уникальный код видеоконтента в цифровом формате';
          public          mirea_4dmin    false    230         ?           0    0 &   COLUMN countries_of_content.country_id    COMMENT     ?   COMMENT ON COLUMN public.countries_of_content.country_id IS 'Уникальный цифровой код страны производства';
          public          mirea_4dmin    false    230         ?           0    0    TABLE countries_of_content    ACL     ?   GRANT SELECT,INSERT,UPDATE ON TABLE public.countries_of_content TO moderator;
GRANT SELECT ON TABLE public.countries_of_content TO ordinary_user;
GRANT SELECT ON TABLE public.countries_of_content TO not_login_user;
          public          mirea_4dmin    false    230         ?            1259    16437    country    TABLE     k   CREATE TABLE public.country (
    country_id smallint NOT NULL,
    name character varying(20) NOT NULL
);
    DROP TABLE public.country;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE country    COMMENT     g   COMMENT ON TABLE public.country IS 'Страна производства видеоконтента';
          public          mirea_4dmin    false    231         ?           0    0    COLUMN country.country_id    COMMENT     ?   COMMENT ON COLUMN public.country.country_id IS 'Уникальный цифровой код страны производства';
          public          mirea_4dmin    false    231         ?           0    0    COLUMN country.name    COMMENT     ?   COMMENT ON COLUMN public.country.name IS 'Название страны-производителя видеоконтента';
          public          mirea_4dmin    false    231         ?           0    0    TABLE country    ACL     ?   GRANT SELECT,INSERT,UPDATE ON TABLE public.country TO moderator;
GRANT SELECT ON TABLE public.country TO ordinary_user;
GRANT SELECT ON TABLE public.country TO not_login_user;
          public          mirea_4dmin    false    231         ?            1259    16443    genre    TABLE     g   CREATE TABLE public.genre (
    genre_id smallint NOT NULL,
    name character varying(50) NOT NULL
);
    DROP TABLE public.genre;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE genre    COMMENT     n   COMMENT ON TABLE public.genre IS 'Жанр, к которому относится видеоконтент';
          public          mirea_4dmin    false    233         ?           0    0    COLUMN genre.genre_id    COMMENT     z   COMMENT ON COLUMN public.genre.genre_id IS 'Уникальный цифровой идентификатор жанра';
          public          mirea_4dmin    false    233         ?           0    0    COLUMN genre.name    COMMENT     a   COMMENT ON COLUMN public.genre.name IS 'Текстовое наименование жанра';
          public          mirea_4dmin    false    233         ?           0    0    TABLE genre    ACL     ?   GRANT SELECT,INSERT,UPDATE ON TABLE public.genre TO moderator;
GRANT SELECT ON TABLE public.genre TO ordinary_user;
GRANT SELECT ON TABLE public.genre TO not_login_user;
          public          mirea_4dmin    false    233         ?            1259    16442    genre_genre_id_seq    SEQUENCE     ?   CREATE SEQUENCE public.genre_genre_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.genre_genre_id_seq;
       public          mirea_4dmin    false    233    6         ?           0    0    genre_genre_id_seq    SEQUENCE OWNED BY     I   ALTER SEQUENCE public.genre_genre_id_seq OWNED BY public.genre.genre_id;
          public          mirea_4dmin    false    232         ?           0    0    SEQUENCE genre_genre_id_seq    ACL     ?   GRANT SELECT,USAGE ON SEQUENCE public.genre_genre_id_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.genre_genre_id_seq TO ordinary_user;
          public          mirea_4dmin    false    232         ?            1259    16449    genres_of_content    TABLE     j   CREATE TABLE public.genres_of_content (
    content_id integer NOT NULL,
    genre_id integer NOT NULL
);
 %   DROP TABLE public.genres_of_content;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE genres_of_content    COMMENT     ?   COMMENT ON TABLE public.genres_of_content IS 'Жанр конкретного фильма/сериала/другого контента';
          public          mirea_4dmin    false    234         ?           0    0 #   COLUMN genres_of_content.content_id    COMMENT     ?   COMMENT ON COLUMN public.genres_of_content.content_id IS 'Уникальный код видеоконтента в цифровом формате';
          public          mirea_4dmin    false    234         ?           0    0 !   COLUMN genres_of_content.genre_id    COMMENT     ?   COMMENT ON COLUMN public.genres_of_content.genre_id IS 'Уникальный цифровой идентификатор жанра';
          public          mirea_4dmin    false    234         ?           0    0    TABLE genres_of_content    ACL     ?   GRANT SELECT,INSERT,UPDATE ON TABLE public.genres_of_content TO moderator;
GRANT SELECT ON TABLE public.genres_of_content TO ordinary_user;
GRANT SELECT ON TABLE public.genres_of_content TO not_login_user;
          public          mirea_4dmin    false    234         ?            1259    16453    news    TABLE     ?  CREATE TABLE public.news (
    nid integer NOT NULL,
    description text NOT NULL,
    title character varying(50) NOT NULL,
    news_date date NOT NULL,
    uid integer NOT NULL,
    image_link character varying(100),
    CONSTRAINT valid_new CHECK ((description ~ '^[A-Za-zА-Яа-я0-9!".,-;:*()]'::text)),
    CONSTRAINT valid_title CHECK (((title)::text ~ '^[A-Za-zА-Яа-я0-9!".,]'::text))
);
    DROP TABLE public.news;
       public         heap    mirea_4dmin    false    6         ?           0    0 
   TABLE news    COMMENT     h   COMMENT ON TABLE public.news IS 'Объект, представляющий собой новость';
          public          mirea_4dmin    false    236         ?           0    0    COLUMN news.nid    COMMENT     g   COMMENT ON COLUMN public.news.nid IS 'Уникальный идентификатор новости';
          public          mirea_4dmin    false    236         ?           0    0    COLUMN news.description    COMMENT     ?   COMMENT ON COLUMN public.news.description IS 'Текстовая информация, представляющая новость';
          public          mirea_4dmin    false    236         ?           0    0    COLUMN news.title    COMMENT     ?   COMMENT ON COLUMN public.news.title IS 'Заголовок новости, отображающий ее основную тематику';
          public          mirea_4dmin    false    236         ?           0    0    COLUMN news.news_date    COMMENT     [   COMMENT ON COLUMN public.news.news_date IS 'Дата публикации новости';
          public          mirea_4dmin    false    236         ?           0    0 
   TABLE news    ACL     ?   GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.news TO moderator;
GRANT SELECT ON TABLE public.news TO ordinary_user;
GRANT SELECT ON TABLE public.news TO not_login_user;
          public          mirea_4dmin    false    236         ?            1259    16452    news_nid_seq    SEQUENCE     ?   CREATE SEQUENCE public.news_nid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.news_nid_seq;
       public          mirea_4dmin    false    236    6         ?           0    0    news_nid_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.news_nid_seq OWNED BY public.news.nid;
          public          mirea_4dmin    false    235         ?           0    0    SEQUENCE news_nid_seq    ACL     ?   GRANT SELECT,USAGE ON SEQUENCE public.news_nid_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.news_nid_seq TO ordinary_user;
          public          mirea_4dmin    false    235         ?            1259    17302    opinion_classifier    TABLE     v   CREATE TABLE public.opinion_classifier (
    oid integer NOT NULL,
    opinion_name character varying(10) NOT NULL
);
 &   DROP TABLE public.opinion_classifier;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE opinion_classifier    ACL     ?   GRANT SELECT ON TABLE public.opinion_classifier TO not_login_user;
GRANT SELECT ON TABLE public.opinion_classifier TO ordinary_user;
GRANT SELECT ON TABLE public.opinion_classifier TO moderator;
          public          mirea_4dmin    false    246         ?            1259    17301    opinion_classifier_oid_seq    SEQUENCE     ?   CREATE SEQUENCE public.opinion_classifier_oid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 1   DROP SEQUENCE public.opinion_classifier_oid_seq;
       public          mirea_4dmin    false    6    246         ?           0    0    opinion_classifier_oid_seq    SEQUENCE OWNED BY     Y   ALTER SEQUENCE public.opinion_classifier_oid_seq OWNED BY public.opinion_classifier.oid;
          public          mirea_4dmin    false    245         ?            1259    17411    recent_films    VIEW     d  CREATE VIEW public.recent_films AS
 SELECT content.content_id,
    content.name,
    content.description,
    content.budget,
    content.box_office,
    content.duration,
    content.rating,
    content.image_link,
    content.release_date
   FROM public.content
  WHERE ((now() - (content.release_date)::timestamp with time zone) < '30 days'::interval);
    DROP VIEW public.recent_films;
       public          mirea_4dmin    false    228    228    228    228    228    228    228    228    228    6         ?            1259    17415    recent_news    VIEW       CREATE VIEW public.recent_news AS
 SELECT news.nid,
    news.description,
    news.title,
    news.news_date,
    news.uid,
    news.image_link
   FROM public.news
  WHERE ((now() - (news.news_date)::timestamp with time zone) < '7 days'::interval)
 LIMIT 5;
    DROP VIEW public.recent_news;
       public          mirea_4dmin    false    236    236    236    236    236    236    6         ?           0    0    TABLE recent_news    ACL     ?   GRANT SELECT ON TABLE public.recent_news TO moderator;
GRANT SELECT ON TABLE public.recent_news TO ordinary_user;
GRANT SELECT ON TABLE public.recent_news TO not_login_user;
          public          mirea_4dmin    false    249         ?            1259    16462    review    TABLE     ?  CREATE TABLE public.review (
    rid integer NOT NULL,
    description text,
    rev_date date NOT NULL,
    content_id integer,
    uid integer,
    title character varying(100) DEFAULT 'Рецензия'::character varying NOT NULL,
    opinion smallint NOT NULL,
    CONSTRAINT censor_review CHECK ((description !~ similar_to_escape('%блять|пиздец|ебать|хуй%'::text))),
    CONSTRAINT valid_review CHECK ((description ~ '^[A-Za-zА-Яа-я0-9!".,]'::text))
);
    DROP TABLE public.review;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE review    COMMENT     l   COMMENT ON TABLE public.review IS 'Содержит информацию об обзорах фильма';
          public          mirea_4dmin    false    238         ?           0    0    COLUMN review.rid    COMMENT     k   COMMENT ON COLUMN public.review.rid IS 'Уникальный идентификатор рецензии';
          public          mirea_4dmin    false    238         ?           0    0    COLUMN review.description    COMMENT     {   COMMENT ON COLUMN public.review.description IS 'Текстовая рецензия фильма/сериала и тд.';
          public          mirea_4dmin    false    238         ?           0    0    COLUMN review.rev_date    COMMENT     \   COMMENT ON COLUMN public.review.rev_date IS 'Дата написания рецензии';
          public          mirea_4dmin    false    238         ?           0    0    COLUMN review.content_id    COMMENT     ?   COMMENT ON COLUMN public.review.content_id IS 'Уникальный код видеоконтента в цифровом формате';
          public          mirea_4dmin    false    238         ?           0    0    COLUMN review.uid    COMMENT     ^   COMMENT ON COLUMN public.review.uid IS 'Идентификатор пользователя';
          public          mirea_4dmin    false    238         ?           0    0    TABLE review    ACL     ?   GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.review TO moderator;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.review TO ordinary_user;
GRANT SELECT ON TABLE public.review TO not_login_user;
          public          mirea_4dmin    false    238         ?           0    0    COLUMN review.description    ACL     C   GRANT UPDATE(description) ON TABLE public.review TO ordinary_user;
          public          mirea_4dmin    false    238    3538         ?           0    0    COLUMN review.content_id    ACL     B   GRANT UPDATE(content_id) ON TABLE public.review TO ordinary_user;
          public          mirea_4dmin    false    238    3538         ?            1259    16461    review_rid_seq    SEQUENCE     ?   CREATE SEQUENCE public.review_rid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.review_rid_seq;
       public          mirea_4dmin    false    238    6         ?           0    0    review_rid_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE public.review_rid_seq OWNED BY public.review.rid;
          public          mirea_4dmin    false    237         ?           0    0    SEQUENCE review_rid_seq    ACL     ?   GRANT SELECT,USAGE ON SEQUENCE public.review_rid_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.review_rid_seq TO ordinary_user;
          public          mirea_4dmin    false    237         ?            1259    16946    role_classifier    TABLE     p   CREATE TABLE public.role_classifier (
    role_id smallint NOT NULL,
    name character varying(50) NOT NULL
);
 #   DROP TABLE public.role_classifier;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE role_classifier    ACL     ?   GRANT SELECT ON TABLE public.role_classifier TO not_login_user;
GRANT SELECT,DELETE,UPDATE ON TABLE public.role_classifier TO moderator;
GRANT SELECT ON TABLE public.role_classifier TO ordinary_user;
          public          mirea_4dmin    false    244         ?            1259    16475 	   site_user    TABLE     u  CREATE TABLE public.site_user (
    uid integer NOT NULL,
    nickname character varying(20) NOT NULL,
    email character varying(50) NOT NULL,
    is_banned boolean NOT NULL,
    ban_date date,
    ban_reason text,
    login character varying(20) NOT NULL,
    CONSTRAINT valid_ban_reason CHECK ((ban_reason ~ '^[A-Za-zА-Яа-я0-9!".,]'::text)),
    CONSTRAINT valid_email CHECK (((email)::text ~ '^(?!\.)(?:(?:[A-Za-z0-9!#$%&''*+/=?^_`{|}~]|-(?!-)|\.(?!\.)))+(?<!\.)@(?:(?!-)(?:[a-zA-Z\d]|-(?!-))+(?<!-)\.)+[a-zA-Z]{2,}$'::text)),
    CONSTRAINT valid_nickname CHECK (((nickname)::text ~ '^[A-Za-zА-Яа-я0-9]'::text))
);
    DROP TABLE public.site_user;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE site_user    COMMENT     {   COMMENT ON TABLE public.site_user IS 'Объект, представляющий пользователя сервиса';
          public          mirea_4dmin    false    240         ?           0    0    COLUMN site_user.uid    COMMENT     a   COMMENT ON COLUMN public.site_user.uid IS 'Идентификатор пользователя';
          public          mirea_4dmin    false    240         ?           0    0    COLUMN site_user.nickname    COMMENT     h   COMMENT ON COLUMN public.site_user.nickname IS 'Ник, выбранный пользователем';
          public          mirea_4dmin    false    240         ?           0    0    COLUMN site_user.email    COMMENT     n   COMMENT ON COLUMN public.site_user.email IS 'Электронная почта для оповещений ';
          public          mirea_4dmin    false    240         ?           0    0    COLUMN site_user.is_banned    COMMENT     _   COMMENT ON COLUMN public.site_user.is_banned IS '0 - не забанен
1 - забанен';
          public          mirea_4dmin    false    240         ?           0    0    COLUMN site_user.ban_date    COMMENT     ]   COMMENT ON COLUMN public.site_user.ban_date IS 'Дата бана пользователя';
          public          mirea_4dmin    false    240         ?           0    0    COLUMN site_user.ban_reason    COMMENT     ?   COMMENT ON COLUMN public.site_user.ban_reason IS 'Причина бана пользователя - указывается модератором';
          public          mirea_4dmin    false    240         ?           0    0    TABLE site_user    ACL     ?   GRANT SELECT,DELETE,UPDATE ON TABLE public.site_user TO moderator;
GRANT SELECT,INSERT,DELETE ON TABLE public.site_user TO not_login_user;
GRANT SELECT,DELETE,UPDATE ON TABLE public.site_user TO ordinary_user;
          public          mirea_4dmin    false    240         ?           0    0    COLUMN site_user.nickname    ACL     ?   GRANT UPDATE(nickname) ON TABLE public.site_user TO moderator;
GRANT UPDATE(nickname) ON TABLE public.site_user TO ordinary_user;
          public          mirea_4dmin    false    240    3551         ?           0    0    COLUMN site_user.email    ACL     @   GRANT UPDATE(email) ON TABLE public.site_user TO ordinary_user;
          public          mirea_4dmin    false    240    3551         ?           0    0    COLUMN site_user.is_banned    ACL     @   GRANT UPDATE(is_banned) ON TABLE public.site_user TO moderator;
          public          mirea_4dmin    false    240    3551         ?           0    0    COLUMN site_user.ban_date    ACL     ?   GRANT UPDATE(ban_date) ON TABLE public.site_user TO moderator;
          public          mirea_4dmin    false    240    3551         ?           0    0    COLUMN site_user.ban_reason    ACL     A   GRANT UPDATE(ban_reason) ON TABLE public.site_user TO moderator;
          public          mirea_4dmin    false    240    3551         ?            1259    16474    site_user_uid_seq    SEQUENCE     ?   CREATE SEQUENCE public.site_user_uid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 (   DROP SEQUENCE public.site_user_uid_seq;
       public          mirea_4dmin    false    6    240         ?           0    0    site_user_uid_seq    SEQUENCE OWNED BY     G   ALTER SEQUENCE public.site_user_uid_seq OWNED BY public.site_user.uid;
          public          mirea_4dmin    false    239         ?           0    0    SEQUENCE site_user_uid_seq    ACL     ?   GRANT SELECT,USAGE ON SEQUENCE public.site_user_uid_seq TO not_login_user;
GRANT USAGE ON SEQUENCE public.site_user_uid_seq TO moderator;
GRANT USAGE ON SEQUENCE public.site_user_uid_seq TO ordinary_user;
          public          mirea_4dmin    false    239         ?            1259    16844 
   user_stars    TABLE     ,  CREATE TABLE public.user_stars (
    sid integer NOT NULL,
    content_id integer NOT NULL,
    uid integer NOT NULL,
    rating smallint NOT NULL,
    CONSTRAINT user_stars_user_visual_rating_check CHECK ((rating <= 10)),
    CONSTRAINT user_stars_user_visual_rating_check1 CHECK ((rating >= 0))
);
    DROP TABLE public.user_stars;
       public         heap    mirea_4dmin    false    6         ?           0    0    TABLE user_stars    ACL     ?   GRANT SELECT ON TABLE public.user_stars TO not_login_user;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.user_stars TO ordinary_user;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.user_stars TO moderator;
          public          mirea_4dmin    false    242         ?            1259    16843    user_stars_sid_seq    SEQUENCE     ?   CREATE SEQUENCE public.user_stars_sid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.user_stars_sid_seq;
       public          mirea_4dmin    false    242    6         ?           0    0    user_stars_sid_seq    SEQUENCE OWNED BY     I   ALTER SEQUENCE public.user_stars_sid_seq OWNED BY public.user_stars.sid;
          public          mirea_4dmin    false    241         ?           0    0    SEQUENCE user_stars_sid_seq    ACL     ?   GRANT SELECT,USAGE ON SEQUENCE public.user_stars_sid_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.user_stars_sid_seq TO ordinary_user;
          public          mirea_4dmin    false    241         Q           2604    16919    celebrity cid    DEFAULT     n   ALTER TABLE ONLY public.celebrity ALTER COLUMN cid SET DEFAULT nextval('public.celebrity_cid_seq'::regclass);
 <   ALTER TABLE public.celebrity ALTER COLUMN cid DROP DEFAULT;
       public          mirea_4dmin    false    243    223         R           2604    16411    collection collection_id    DEFAULT     ?   ALTER TABLE ONLY public.collection ALTER COLUMN collection_id SET DEFAULT nextval('public.collection_collection_id_seq'::regclass);
 G   ALTER TABLE public.collection ALTER COLUMN collection_id DROP DEFAULT;
       public          mirea_4dmin    false    225    226    226         S           2604    16420    content content_id    DEFAULT     x   ALTER TABLE ONLY public.content ALTER COLUMN content_id SET DEFAULT nextval('public.content_content_id_seq'::regclass);
 A   ALTER TABLE public.content ALTER COLUMN content_id DROP DEFAULT;
       public          mirea_4dmin    false    227    228    228         U           2604    16456    news nid    DEFAULT     d   ALTER TABLE ONLY public.news ALTER COLUMN nid SET DEFAULT nextval('public.news_nid_seq'::regclass);
 7   ALTER TABLE public.news ALTER COLUMN nid DROP DEFAULT;
       public          mirea_4dmin    false    236    235    236         Z           2604    17305    opinion_classifier oid    DEFAULT     ?   ALTER TABLE ONLY public.opinion_classifier ALTER COLUMN oid SET DEFAULT nextval('public.opinion_classifier_oid_seq'::regclass);
 E   ALTER TABLE public.opinion_classifier ALTER COLUMN oid DROP DEFAULT;
       public          mirea_4dmin    false    245    246    246         V           2604    16465 
   review rid    DEFAULT     h   ALTER TABLE ONLY public.review ALTER COLUMN rid SET DEFAULT nextval('public.review_rid_seq'::regclass);
 9   ALTER TABLE public.review ALTER COLUMN rid DROP DEFAULT;
       public          mirea_4dmin    false    238    237    238         X           2604    16478    site_user uid    DEFAULT     n   ALTER TABLE ONLY public.site_user ALTER COLUMN uid SET DEFAULT nextval('public.site_user_uid_seq'::regclass);
 <   ALTER TABLE public.site_user ALTER COLUMN uid DROP DEFAULT;
       public          mirea_4dmin    false    240    239    240         Y           2604    16847    user_stars sid    DEFAULT     p   ALTER TABLE ONLY public.user_stars ALTER COLUMN sid SET DEFAULT nextval('public.user_stars_sid_seq'::regclass);
 =   ALTER TABLE public.user_stars ALTER COLUMN sid DROP DEFAULT;
       public          mirea_4dmin    false    241    242    242         h          0    16397 	   celebrity 
   TABLE DATA           e   COPY public.celebrity (cid, name, height, birthday, death, birthplace, career, img_link) FROM stdin;
    public          mirea_4dmin    false    223       3432.dat i          0    16404    celebrity_in_content 
   TABLE DATA           \   COPY public.celebrity_in_content (content_id, cid, role, description, priority) FROM stdin;
    public          mirea_4dmin    false    224       3433.dat k          0    16408 
   collection 
   TABLE DATA           K   COPY public.collection (collection_id, name, description, uid) FROM stdin;
    public          mirea_4dmin    false    226       3435.dat m          0    16417    content 
   TABLE DATA           ?   COPY public.content (content_id, name, description, budget, box_office, duration, rating, image_link, release_date) FROM stdin;
    public          mirea_4dmin    false    228       3437.dat n          0    16431    content_in_collection 
   TABLE DATA           W   COPY public.content_in_collection (content_id, collection_id, film_number) FROM stdin;
    public          mirea_4dmin    false    229       3438.dat o          0    16434    countries_of_content 
   TABLE DATA           F   COPY public.countries_of_content (content_id, country_id) FROM stdin;
    public          mirea_4dmin    false    230       3439.dat p          0    16437    country 
   TABLE DATA           3   COPY public.country (country_id, name) FROM stdin;
    public          mirea_4dmin    false    231       3440.dat r          0    16443    genre 
   TABLE DATA           /   COPY public.genre (genre_id, name) FROM stdin;
    public          mirea_4dmin    false    233       3442.dat s          0    16449    genres_of_content 
   TABLE DATA           A   COPY public.genres_of_content (content_id, genre_id) FROM stdin;
    public          mirea_4dmin    false    234       3443.dat u          0    16453    news 
   TABLE DATA           S   COPY public.news (nid, description, title, news_date, uid, image_link) FROM stdin;
    public          mirea_4dmin    false    236       3445.dat           0    17302    opinion_classifier 
   TABLE DATA           ?   COPY public.opinion_classifier (oid, opinion_name) FROM stdin;
    public          mirea_4dmin    false    246       3455.dat w          0    16462    review 
   TABLE DATA           ]   COPY public.review (rid, description, rev_date, content_id, uid, title, opinion) FROM stdin;
    public          mirea_4dmin    false    238       3447.dat }          0    16946    role_classifier 
   TABLE DATA           8   COPY public.role_classifier (role_id, name) FROM stdin;
    public          mirea_4dmin    false    244       3453.dat y          0    16475 	   site_user 
   TABLE DATA           a   COPY public.site_user (uid, nickname, email, is_banned, ban_date, ban_reason, login) FROM stdin;
    public          mirea_4dmin    false    240       3449.dat {          0    16844 
   user_stars 
   TABLE DATA           B   COPY public.user_stars (sid, content_id, uid, rating) FROM stdin;
    public          mirea_4dmin    false    242       3451.dat ?           0    0    celebrity_cid_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('public.celebrity_cid_seq', 744, true);
          public          mirea_4dmin    false    243         ?           0    0    collection_collection_id_seq    SEQUENCE SET     K   SELECT pg_catalog.setval('public.collection_collection_id_seq', 16, true);
          public          mirea_4dmin    false    225         ?           0    0    content_content_id_seq    SEQUENCE SET     E   SELECT pg_catalog.setval('public.content_content_id_seq', 9, false);
          public          mirea_4dmin    false    227         ?           0    0    genre_genre_id_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('public.genre_genre_id_seq', 30, true);
          public          mirea_4dmin    false    232         ?           0    0    news_nid_seq    SEQUENCE SET     :   SELECT pg_catalog.setval('public.news_nid_seq', 6, true);
          public          mirea_4dmin    false    235         ?           0    0    opinion_classifier_oid_seq    SEQUENCE SET     H   SELECT pg_catalog.setval('public.opinion_classifier_oid_seq', 3, true);
          public          mirea_4dmin    false    245         ?           0    0    review_rid_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.review_rid_seq', 33, true);
          public          mirea_4dmin    false    237         ?           0    0    site_user_uid_seq    SEQUENCE SET     @   SELECT pg_catalog.setval('public.site_user_uid_seq', 22, true);
          public          mirea_4dmin    false    239         ?           0    0    user_stars_sid_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('public.user_stars_sid_seq', 56, true);
          public          mirea_4dmin    false    241         j           2606    16403    celebrity celebrity_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY public.celebrity
    ADD CONSTRAINT celebrity_pkey PRIMARY KEY (cid);
 B   ALTER TABLE ONLY public.celebrity DROP CONSTRAINT celebrity_pkey;
       public            mirea_4dmin    false    223         p           2606    16415    collection collection_pkey 
   CONSTRAINT     c   ALTER TABLE ONLY public.collection
    ADD CONSTRAINT collection_pkey PRIMARY KEY (collection_id);
 D   ALTER TABLE ONLY public.collection DROP CONSTRAINT collection_pkey;
       public            mirea_4dmin    false    226         r           2606    17042    content content_description_key 
   CONSTRAINT     a   ALTER TABLE ONLY public.content
    ADD CONSTRAINT content_description_key UNIQUE (description);
 I   ALTER TABLE ONLY public.content DROP CONSTRAINT content_description_key;
       public            mirea_4dmin    false    228         u           2606    16428    content content_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.content
    ADD CONSTRAINT content_pkey PRIMARY KEY (content_id);
 >   ALTER TABLE ONLY public.content DROP CONSTRAINT content_pkey;
       public            mirea_4dmin    false    228                    2606    17433    country country_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (country_id);
 >   ALTER TABLE ONLY public.country DROP CONSTRAINT country_pkey;
       public            mirea_4dmin    false    231         ?           2606    17422    genre genre_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.genre
    ADD CONSTRAINT genre_pkey PRIMARY KEY (genre_id);
 :   ALTER TABLE ONLY public.genre DROP CONSTRAINT genre_pkey;
       public            mirea_4dmin    false    233         ?           2606    16612    site_user login_unique 
   CONSTRAINT     R   ALTER TABLE ONLY public.site_user
    ADD CONSTRAINT login_unique UNIQUE (login);
 @   ALTER TABLE ONLY public.site_user DROP CONSTRAINT login_unique;
       public            mirea_4dmin    false    240         ?           2606    16460    news news_pkey 
   CONSTRAINT     M   ALTER TABLE ONLY public.news
    ADD CONSTRAINT news_pkey PRIMARY KEY (nid);
 8   ALTER TABLE ONLY public.news DROP CONSTRAINT news_pkey;
       public            mirea_4dmin    false    236         y           2606    16931    content_in_collection no_dupes 
   CONSTRAINT     n   ALTER TABLE ONLY public.content_in_collection
    ADD CONSTRAINT no_dupes UNIQUE (content_id, collection_id);
 H   ALTER TABLE ONLY public.content_in_collection DROP CONSTRAINT no_dupes;
       public            mirea_4dmin    false    229    229         ?           2606    17307 *   opinion_classifier opinion_classifier_pkey 
   CONSTRAINT     i   ALTER TABLE ONLY public.opinion_classifier
    ADD CONSTRAINT opinion_classifier_pkey PRIMARY KEY (oid);
 T   ALTER TABLE ONLY public.opinion_classifier DROP CONSTRAINT opinion_classifier_pkey;
       public            mirea_4dmin    false    246         ?           2606    16473    review review_pkey 
   CONSTRAINT     Q   ALTER TABLE ONLY public.review
    ADD CONSTRAINT review_pkey PRIMARY KEY (rid);
 <   ALTER TABLE ONLY public.review DROP CONSTRAINT review_pkey;
       public            mirea_4dmin    false    238         ?           2606    16950 $   role_classifier role_classifier_pkey 
   CONSTRAINT     g   ALTER TABLE ONLY public.role_classifier
    ADD CONSTRAINT role_classifier_pkey PRIMARY KEY (role_id);
 N   ALTER TABLE ONLY public.role_classifier DROP CONSTRAINT role_classifier_pkey;
       public            mirea_4dmin    false    244         ?           2606    16952 0   role_classifier role_classifier_role_id_name_key 
   CONSTRAINT     t   ALTER TABLE ONLY public.role_classifier
    ADD CONSTRAINT role_classifier_role_id_name_key UNIQUE (role_id, name);
 Z   ALTER TABLE ONLY public.role_classifier DROP CONSTRAINT role_classifier_role_id_name_key;
       public            mirea_4dmin    false    244    244         ?           2606    16486    site_user site_user_email_key 
   CONSTRAINT     Y   ALTER TABLE ONLY public.site_user
    ADD CONSTRAINT site_user_email_key UNIQUE (email);
 G   ALTER TABLE ONLY public.site_user DROP CONSTRAINT site_user_email_key;
       public            mirea_4dmin    false    240         ?           2606    16484     site_user site_user_nickname_key 
   CONSTRAINT     _   ALTER TABLE ONLY public.site_user
    ADD CONSTRAINT site_user_nickname_key UNIQUE (nickname);
 J   ALTER TABLE ONLY public.site_user DROP CONSTRAINT site_user_nickname_key;
       public            mirea_4dmin    false    240         ?           2606    16482    site_user site_user_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY public.site_user
    ADD CONSTRAINT site_user_pkey PRIMARY KEY (uid);
 B   ALTER TABLE ONLY public.site_user DROP CONSTRAINT site_user_pkey;
       public            mirea_4dmin    false    240         m           2606    17029 (   celebrity_in_content unique_combinations 
   CONSTRAINT     t   ALTER TABLE ONLY public.celebrity_in_content
    ADD CONSTRAINT unique_combinations UNIQUE (content_id, cid, role);
 R   ALTER TABLE ONLY public.celebrity_in_content DROP CONSTRAINT unique_combinations;
       public            mirea_4dmin    false    224    224    224         {           2606    17031 4   content_in_collection unique_combinations_collection 
   CONSTRAINT     ?   ALTER TABLE ONLY public.content_in_collection
    ADD CONSTRAINT unique_combinations_collection UNIQUE (content_id, collection_id);
 ^   ALTER TABLE ONLY public.content_in_collection DROP CONSTRAINT unique_combinations_collection;
       public            mirea_4dmin    false    229    229         }           2606    17033 2   countries_of_content unique_combinations_countries 
   CONSTRAINT        ALTER TABLE ONLY public.countries_of_content
    ADD CONSTRAINT unique_combinations_countries UNIQUE (content_id, country_id);
 \   ALTER TABLE ONLY public.countries_of_content DROP CONSTRAINT unique_combinations_countries;
       public            mirea_4dmin    false    230    230         ?           2606    17035 ,   genres_of_content unique_combinations_genres 
   CONSTRAINT     w   ALTER TABLE ONLY public.genres_of_content
    ADD CONSTRAINT unique_combinations_genres UNIQUE (content_id, genre_id);
 V   ALTER TABLE ONLY public.genres_of_content DROP CONSTRAINT unique_combinations_genres;
       public            mirea_4dmin    false    234    234         w           2606    17044    content unique_film 
   CONSTRAINT     i   ALTER TABLE ONLY public.content
    ADD CONSTRAINT unique_film UNIQUE (name, description, release_date);
 =   ALTER TABLE ONLY public.content DROP CONSTRAINT unique_film;
       public            mirea_4dmin    false    228    228    228         ?           2606    17159    review unique_reviews 
   CONSTRAINT     [   ALTER TABLE ONLY public.review
    ADD CONSTRAINT unique_reviews UNIQUE (uid, content_id);
 ?   ALTER TABLE ONLY public.review DROP CONSTRAINT unique_reviews;
       public            mirea_4dmin    false    238    238         ?           2606    16859 (   user_stars user_stars_content_id_uid_key 
   CONSTRAINT     n   ALTER TABLE ONLY public.user_stars
    ADD CONSTRAINT user_stars_content_id_uid_key UNIQUE (content_id, uid);
 R   ALTER TABLE ONLY public.user_stars DROP CONSTRAINT user_stars_content_id_uid_key;
       public            mirea_4dmin    false    242    242         ?           2606    16857    user_stars user_stars_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY public.user_stars
    ADD CONSTRAINT user_stars_pkey PRIMARY KEY (sid);
 D   ALTER TABLE ONLY public.user_stars DROP CONSTRAINT user_stars_pkey;
       public            mirea_4dmin    false    242         h           1259    17012    celebrity_name_index    INDEX     J   CREATE INDEX celebrity_name_index ON public.celebrity USING btree (name);
 (   DROP INDEX public.celebrity_name_index;
       public            mirea_4dmin    false    223         n           1259    17013    collection_name_index    INDEX     L   CREATE INDEX collection_name_index ON public.collection USING btree (name);
 )   DROP INDEX public.collection_name_index;
       public            mirea_4dmin    false    226         k           1259    17272    content_id_index    INDEX     W   CREATE INDEX content_id_index ON public.celebrity_in_content USING btree (content_id);
 $   DROP INDEX public.content_id_index;
       public            mirea_4dmin    false    224         s           1259    17014    content_name_index    INDEX     F   CREATE INDEX content_name_index ON public.content USING btree (name);
 &   DROP INDEX public.content_name_index;
       public            mirea_4dmin    false    228         ?           1259    17273    genre_index    INDEX     M   CREATE INDEX genre_index ON public.genres_of_content USING btree (genre_id);
    DROP INDEX public.genre_index;
       public            mirea_4dmin    false    234         ?           1259    17015    new_title_index    INDEX     A   CREATE INDEX new_title_index ON public.news USING btree (title);
 #   DROP INDEX public.new_title_index;
       public            mirea_4dmin    false    236         ?           1259    17016    review_title_index    INDEX     F   CREATE INDEX review_title_index ON public.review USING btree (title);
 &   DROP INDEX public.review_title_index;
       public            mirea_4dmin    false    238         ?           1259    17017    star_uid_index    INDEX     D   CREATE INDEX star_uid_index ON public.user_stars USING btree (uid);
 "   DROP INDEX public.star_uid_index;
       public            mirea_4dmin    false    242         ?           1259    17011    user_nickname_index    INDEX     M   CREATE INDEX user_nickname_index ON public.site_user USING btree (nickname);
 '   DROP INDEX public.user_nickname_index;
       public            mirea_4dmin    false    240         ?           2620    16801    site_user banned_delete    TRIGGER     ?   CREATE TRIGGER banned_delete AFTER INSERT OR UPDATE ON public.site_user FOR EACH STATEMENT EXECUTE FUNCTION public.banned_delete();
 0   DROP TRIGGER banned_delete ON public.site_user;
       public          mirea_4dmin    false    300    240         ?           2620    17009 "   user_stars calculate_rating_insert    TRIGGER     ?   CREATE TRIGGER calculate_rating_insert AFTER INSERT ON public.user_stars FOR EACH ROW EXECUTE FUNCTION public.calculate_rating_insert();
 ;   DROP TRIGGER calculate_rating_insert ON public.user_stars;
       public          mirea_4dmin    false    242    320         ?           2620    17003 )   user_stars calculate_rating_update_delete    TRIGGER     ?   CREATE TRIGGER calculate_rating_update_delete AFTER DELETE OR UPDATE ON public.user_stars FOR EACH ROW EXECUTE FUNCTION public.calculate_rating_update_delete();
 B   DROP TRIGGER calculate_rating_update_delete ON public.user_stars;
       public          mirea_4dmin    false    242    321         ?           2620    17190    site_user user_system_delete    TRIGGER     ~   CREATE TRIGGER user_system_delete AFTER DELETE ON public.site_user FOR EACH ROW EXECUTE FUNCTION public.user_system_delete();
 5   DROP TRIGGER user_system_delete ON public.site_user;
       public          mirea_4dmin    false    240    286         ?           2606    17053 #   celebrity_in_content celebrity_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.celebrity_in_content
    ADD CONSTRAINT celebrity_fkey FOREIGN KEY (cid) REFERENCES public.celebrity(cid) ON DELETE CASCADE;
 M   ALTER TABLE ONLY public.celebrity_in_content DROP CONSTRAINT celebrity_fkey;
       public          mirea_4dmin    false    223    224    3178         ?           2606    16953 3   celebrity_in_content celebrity_in_content_role_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.celebrity_in_content
    ADD CONSTRAINT celebrity_in_content_role_fkey FOREIGN KEY (role) REFERENCES public.role_classifier(role_id);
 ]   ALTER TABLE ONLY public.celebrity_in_content DROP CONSTRAINT celebrity_in_content_role_fkey;
       public          mirea_4dmin    false    244    224    3228         ?           2606    17063 %   content_in_collection collection_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.content_in_collection
    ADD CONSTRAINT collection_fkey FOREIGN KEY (collection_id) REFERENCES public.collection(collection_id) ON DELETE CASCADE;
 O   ALTER TABLE ONLY public.content_in_collection DROP CONSTRAINT collection_fkey;
       public          mirea_4dmin    false    226    3184    229         ?           2606    16532    collection collection_uid_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.collection
    ADD CONSTRAINT collection_uid_fkey FOREIGN KEY (uid) REFERENCES public.site_user(uid) ON UPDATE RESTRICT ON DELETE RESTRICT;
 H   ALTER TABLE ONLY public.collection DROP CONSTRAINT collection_uid_fkey;
       public          mirea_4dmin    false    3220    226    240         ?           2606    17048 !   celebrity_in_content content_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.celebrity_in_content
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;
 K   ALTER TABLE ONLY public.celebrity_in_content DROP CONSTRAINT content_fkey;
       public          mirea_4dmin    false    224    228    3189         ?           2606    17058 "   content_in_collection content_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.content_in_collection
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;
 L   ALTER TABLE ONLY public.content_in_collection DROP CONSTRAINT content_fkey;
       public          mirea_4dmin    false    3189    228    229         ?           2606    17068 !   countries_of_content content_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.countries_of_content
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;
 K   ALTER TABLE ONLY public.countries_of_content DROP CONSTRAINT content_fkey;
       public          mirea_4dmin    false    230    228    3189         ?           2606    17073    genres_of_content content_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.genres_of_content
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;
 H   ALTER TABLE ONLY public.genres_of_content DROP CONSTRAINT content_fkey;
       public          mirea_4dmin    false    234    228    3189         ?           2606    17088    review content_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.review
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;
 =   ALTER TABLE ONLY public.review DROP CONSTRAINT content_fkey;
       public          mirea_4dmin    false    238    228    3189         ?           2606    17098    user_stars content_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.user_stars
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;
 A   ALTER TABLE ONLY public.user_stars DROP CONSTRAINT content_fkey;
       public          mirea_4dmin    false    228    242    3189         ?           2606    17434 9   countries_of_content countries_of_content_country_id_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.countries_of_content
    ADD CONSTRAINT countries_of_content_country_id_fkey FOREIGN KEY (country_id) REFERENCES public.country(country_id) ON UPDATE RESTRICT ON DELETE RESTRICT;
 c   ALTER TABLE ONLY public.countries_of_content DROP CONSTRAINT countries_of_content_country_id_fkey;
       public          mirea_4dmin    false    3199    230    231         ?           2606    17423 1   genres_of_content genres_of_content_genre_id_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.genres_of_content
    ADD CONSTRAINT genres_of_content_genre_id_fkey FOREIGN KEY (genre_id) REFERENCES public.genre(genre_id) ON UPDATE RESTRICT ON DELETE RESTRICT;
 [   ALTER TABLE ONLY public.genres_of_content DROP CONSTRAINT genres_of_content_genre_id_fkey;
       public          mirea_4dmin    false    234    3201    233         ?           2606    17313    news news_uid_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.news
    ADD CONSTRAINT news_uid_fkey FOREIGN KEY (uid) REFERENCES public.site_user(uid) ON DELETE CASCADE;
 <   ALTER TABLE ONLY public.news DROP CONSTRAINT news_uid_fkey;
       public          mirea_4dmin    false    240    3220    236         ?           2606    17308    review review_opinion_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.review
    ADD CONSTRAINT review_opinion_fkey FOREIGN KEY (opinion) REFERENCES public.opinion_classifier(oid);
 D   ALTER TABLE ONLY public.review DROP CONSTRAINT review_opinion_fkey;
       public          mirea_4dmin    false    3232    246    238         ?           2606    17078    collection user_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.collection
    ADD CONSTRAINT user_fkey FOREIGN KEY (uid) REFERENCES public.site_user(uid) ON DELETE CASCADE;
 >   ALTER TABLE ONLY public.collection DROP CONSTRAINT user_fkey;
       public          mirea_4dmin    false    3220    240    226         ?           2606    17083    review user_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.review
    ADD CONSTRAINT user_fkey FOREIGN KEY (uid) REFERENCES public.site_user(uid) ON DELETE CASCADE;
 :   ALTER TABLE ONLY public.review DROP CONSTRAINT user_fkey;
       public          mirea_4dmin    false    238    3220    240         ?           2606    17093    user_stars user_fkey    FK CONSTRAINT     ?   ALTER TABLE ONLY public.user_stars
    ADD CONSTRAINT user_fkey FOREIGN KEY (uid) REFERENCES public.site_user(uid) ON DELETE CASCADE;
 >   ALTER TABLE ONLY public.user_stars DROP CONSTRAINT user_fkey;
       public          mirea_4dmin    false    3220    242    240         ^           3256    17282 #   site_user change_all_but_moderators    POLICY     ?   CREATE POLICY change_all_but_moderators ON public.site_user FOR UPDATE TO moderator USING ((((login)::text = CURRENT_USER) OR ( SELECT (pg_has_role((site_user.login)::name, 'moderator'::name, 'MEMBER'::text) = false))));
 ;   DROP POLICY change_all_but_moderators ON public.site_user;
       public          mirea_4dmin    false    240    240         G           0    16408 
   collection    ROW SECURITY     8   ALTER TABLE public.collection ENABLE ROW LEVEL SECURITY;          public          mirea_4dmin    false    226         e           3256    17397    collection create_collections    POLICY     ?   CREATE POLICY create_collections ON public.collection FOR INSERT TO ordinary_user, moderator WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));
 5   DROP POLICY create_collections ON public.collection;
       public          mirea_4dmin    false    240    240    226         Y           3256    16799    news create_news    POLICY     T   CREATE POLICY create_news ON public.news FOR INSERT TO moderator WITH CHECK (true);
 (   DROP POLICY create_news ON public.news;
       public          mirea_4dmin    false    236         `           3256    17381    review create_reviews    POLICY     ?   CREATE POLICY create_reviews ON public.review FOR INSERT TO ordinary_user, moderator WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));
 -   DROP POLICY create_reviews ON public.review;
       public          mirea_4dmin    false    240    238    240         d           3256    17396    user_stars create_stars    POLICY     ?   CREATE POLICY create_stars ON public.user_stars FOR INSERT TO ordinary_user, moderator WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));
 /   DROP POLICY create_stars ON public.user_stars;
       public          mirea_4dmin    false    242    240    240         S           3256    16793    collection delete_all    POLICY     T   CREATE POLICY delete_all ON public.collection FOR DELETE TO moderator USING (true);
 -   DROP POLICY delete_all ON public.collection;
       public          mirea_4dmin    false    226         V           3256    16796    news delete_all    POLICY     N   CREATE POLICY delete_all ON public.news FOR DELETE TO moderator USING (true);
 '   DROP POLICY delete_all ON public.news;
       public          mirea_4dmin    false    236         P           3256    16786    review delete_all    POLICY     P   CREATE POLICY delete_all ON public.review FOR DELETE TO moderator USING (true);
 )   DROP POLICY delete_all ON public.review;
       public          mirea_4dmin    false    238         \           3256    16928    user_stars delete_all    POLICY     T   CREATE POLICY delete_all ON public.user_stars FOR DELETE TO moderator USING (true);
 -   DROP POLICY delete_all ON public.user_stars;
       public          mirea_4dmin    false    242         g           3256    17419    site_user delete_ban_user    POLICY     ?   CREATE POLICY delete_ban_user ON public.site_user FOR DELETE TO not_login_user, ordinary_user, moderator USING ((((now() AT TIME ZONE 'Europe/Moscow'::text) - (ban_date)::timestamp without time zone) >= '1 mon'::interval));
 1   DROP POLICY delete_ban_user ON public.site_user;
       public          mirea_4dmin    false    240    240         R           3256    16791 !   collection delete_own_collections    POLICY     ?   CREATE POLICY delete_own_collections ON public.collection FOR DELETE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = collection.uid)))::text = CURRENT_USER));
 9   DROP POLICY delete_own_collections ON public.collection;
       public          mirea_4dmin    false    226    240    240    226         b           3256    17384    review delete_own_reviews    POLICY     ?   CREATE POLICY delete_own_reviews ON public.review FOR DELETE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = review.uid)))::text = CURRENT_USER));
 1   DROP POLICY delete_own_reviews ON public.review;
       public          mirea_4dmin    false    240    240    238    238         [           3256    16927    user_stars delete_own_stars    POLICY     ?   CREATE POLICY delete_own_stars ON public.user_stars FOR DELETE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = user_stars.uid)))::text = CURRENT_USER));
 3   DROP POLICY delete_own_stars ON public.user_stars;
       public          mirea_4dmin    false    240    242    242    240         T           3256    16794    collection edit_all    POLICY     R   CREATE POLICY edit_all ON public.collection FOR UPDATE TO moderator USING (true);
 +   DROP POLICY edit_all ON public.collection;
       public          mirea_4dmin    false    226         W           3256    16797    news edit_all    POLICY     L   CREATE POLICY edit_all ON public.news FOR UPDATE TO moderator USING (true);
 %   DROP POLICY edit_all ON public.news;
       public          mirea_4dmin    false    236         N           3256    16781    review edit_all    POLICY     N   CREATE POLICY edit_all ON public.review FOR UPDATE TO moderator USING (true);
 '   DROP POLICY edit_all ON public.review;
       public          mirea_4dmin    false    238         ]           3256    16929    user_stars edit_all    POLICY     R   CREATE POLICY edit_all ON public.user_stars FOR UPDATE TO moderator USING (true);
 +   DROP POLICY edit_all ON public.user_stars;
       public          mirea_4dmin    false    242         H           0    16453    news    ROW SECURITY     2   ALTER TABLE public.news ENABLE ROW LEVEL SECURITY;          public          mirea_4dmin    false    236         M           3256    16772    site_user register    POLICY     [   CREATE POLICY register ON public.site_user FOR INSERT TO not_login_user WITH CHECK (true);
 *   DROP POLICY register ON public.site_user;
       public          mirea_4dmin    false    240         I           0    16462    review    ROW SECURITY     4   ALTER TABLE public.review ENABLE ROW LEVEL SECURITY;          public          mirea_4dmin    false    238         U           3256    16795    collection see_all    POLICY     p   CREATE POLICY see_all ON public.collection FOR SELECT TO not_login_user, ordinary_user, moderator USING (true);
 *   DROP POLICY see_all ON public.collection;
       public          mirea_4dmin    false    226         X           3256    16798    news see_all    POLICY     j   CREATE POLICY see_all ON public.news FOR SELECT TO not_login_user, ordinary_user, moderator USING (true);
 $   DROP POLICY see_all ON public.news;
       public          mirea_4dmin    false    236         Q           3256    16788    site_user see_all    POLICY     _   CREATE POLICY see_all ON public.site_user FOR SELECT TO ordinary_user, moderator USING (true);
 )   DROP POLICY see_all ON public.site_user;
       public          mirea_4dmin    false    240         _           3256    17320    site_user see_as_nologin    POLICY     \   CREATE POLICY see_as_nologin ON public.site_user FOR SELECT TO not_login_user USING (true);
 0   DROP POLICY see_as_nologin ON public.site_user;
       public          mirea_4dmin    false    240         O           3256    16782    review see_reviews    POLICY     p   CREATE POLICY see_reviews ON public.review FOR SELECT TO not_login_user, ordinary_user, moderator USING (true);
 *   DROP POLICY see_reviews ON public.review;
       public          mirea_4dmin    false    238         Z           3256    16924    user_stars see_stars    POLICY     r   CREATE POLICY see_stars ON public.user_stars FOR SELECT TO not_login_user, ordinary_user, moderator USING (true);
 ,   DROP POLICY see_stars ON public.user_stars;
       public          mirea_4dmin    false    242         J           0    16475 	   site_user    ROW SECURITY     7   ALTER TABLE public.site_user ENABLE ROW LEVEL SECURITY;          public          mirea_4dmin    false    240         f           3256    17398 !   collection update_own_collections    POLICY     S  CREATE POLICY update_own_collections ON public.collection FOR UPDATE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = collection.uid)))::text = CURRENT_USER)) WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));
 9   DROP POLICY update_own_collections ON public.collection;
       public          mirea_4dmin    false    240    240    226    240    240    226         a           3256    17382    review update_own_reviews    POLICY     G  CREATE POLICY update_own_reviews ON public.review FOR UPDATE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = review.uid)))::text = CURRENT_USER)) WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));
 1   DROP POLICY update_own_reviews ON public.review;
       public          mirea_4dmin    false    238    238    240    240    240    240         c           3256    17394    user_stars update_own_stars    POLICY     M  CREATE POLICY update_own_stars ON public.user_stars FOR UPDATE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = user_stars.uid)))::text = CURRENT_USER)) WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));
 3   DROP POLICY update_own_stars ON public.user_stars;
       public          mirea_4dmin    false    242    242    240    240    240    240         L           3256    16765    site_user update_self    POLICY     r   CREATE POLICY update_self ON public.site_user FOR UPDATE TO ordinary_user USING (((login)::text = CURRENT_USER));
 -   DROP POLICY update_self ON public.site_user;
       public          mirea_4dmin    false    240    240         K           0    16844 
   user_stars    ROW SECURITY     8   ALTER TABLE public.user_stars ENABLE ROW LEVEL SECURITY;          public          mirea_4dmin    false    242                                                                                                                                                                                                                                                                3432.dat                                                                                            0000600 0004000 0002000 00000365056 14351347706 0014276 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        162	Джек Эйнджел	163	1930-10-24	2021-10-18	Модесто, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/16040.jpg
45	Джейсон Кац	\N	\N	\N	\N	Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/65210.jpg
30	Дональд Фуллилав	\N	1958-05-16	\N	Даллас, Техас, США	Актер, Продюсер, Монтажер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/37029.jpg
15	Гари Райли	\N	1963-11-19	\N	Сант-Луис, Миссури, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/87658.jpg
4	Дэвид Ойелоуо	172	1976-04-01	\N	Оксфорд, Англия, Великобритания	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/5300.jpg
53	Брук Смит	176	1967-05-22	\N	Нью-Йорк, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/22535.jpg
61	Кристофер Джоэль Айвз	183	1989-01-03	\N	Атланта, Джорджия, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24271.jpg
42	Тим Смит	\N	1967-07-07	\N	Уэйкросс, Джорджия, США	Режиссер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/677263.jpg
43	Ежи Новак	\N	1923-06-20	2013-03-26	Brzesko, Ma	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/107129.jpg
34	Оливия Дабровска	\N	1989-05-28	\N	Краков, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277045.jpg
2	Дитер Уиттинг	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277065.jpg
38	Агнешка Круковна	\N	1971-03-20	\N	Chorzów, Slaskie, Poland	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277067.jpg
59	Дариуш Шиманиак	\N	\N	\N	Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277075.jpg
17	Томас Моррис	190	1966-05-21	\N	Вена, Австрия	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/66160.jpg
51	Себастьян Конрад	\N	1971-12-11	\N	Оборники, Польша	Актер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277084.jpg
33	Равит Ферера	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277086.jpg
40	Эдвард Линде-Любашенко	178	1939-08-23	\N	Белосток, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/271664.jpg
47	Марта Бизонь	\N	1971-02-03	\N	Wadowice, Ma	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/404837.jpg
36	Джералд Р. Молен	\N	1935-01-06	\N	Грейт-Фоллс, Монтана, США	Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/35835.jpg
44	Анна Б. Шеппард	\N	1946-01-18	\N	Варшава, Польша	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1987343.jpg
31	Рени Блейн	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102273.jpg
63	Джон Р. Вудворд	196	\N	\N	\N	Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102285.jpg
58	Сэм Андерсон	178	1945-05-13	\N	Уопетон, Северная Дакота, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/11843.jpg
10	Кристин Сибрук	169	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/66907.jpg
13	Роб Ландри	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101666.jpg
39	Пит Остер	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101668.jpg
62	Афемо Омилами	191	1950-12-13	\N	Петербург, Вирджиния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/9280.jpg
35	Поли ДиКокко	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/86906.jpg
60	Ванесса Рот	\N	\N	\N	\N	Продюсер, Режиссер, Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101699.jpg
54	Джек Боуден	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101705.jpg
48	Джон Сайммит	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/543989.jpg
16	USC Trojan Marching Band	\N	\N	\N	\N	Группа: играют самих себя, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/103623.jpg
32	Стив Старки	\N	\N	\N	\N	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/9202.jpg
29	Томас Робинс	178	\N	\N	\N	Режиссер, Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/96102.jpg
8	Шэйн Ранги	193	1969-02-03	\N	Новая Зеландия	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/475885.jpg
57	Йорн Бензон	185	1973-07-12	\N	Svenborg, Denmark	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/736896.jpg
56	Майкл Линн	\N	1941-04-23	2019-03-24	Бруклин, Нью-Йорк, США	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32395.jpg
22	Марк Ордески	\N	1963-04-22	\N	Дэвис, Калифорния, США	Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/11577.jpg
12	Бенджамин Брэтт	187	1963-12-16	\N	Сан-Франциско, Калифорния, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2803.jpg
55	Габриэль Иглесиас	173	1976-07-15	\N	Сан-Диего, Калифорния, США	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/554693.jpg
21	София Эспиноса	\N	\N	\N	\N	Актриса, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1074073.jpg
52	Мэри Элис Драмм	\N	\N	\N	\N	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/242844.jpg
70	Сачи Паркер	\N	1956-09-01	\N	Лос-Анджелес, Калифорния, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/129103.jpg
89	Карен Петрасек	\N	1963-05-04	\N	Калифорния, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152915.jpg
102	Том Танген	\N	1961-09-08	\N	\N	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/23429.jpg
122	Дэвид Гяси	178	1980-01-02	\N	Хаммерсмит, Лондон, Англия	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/34831.jpg
84	Марк Казимир Дайневиц	187	1971-04-29	\N	Арлингтон Хайтс, Иллинойс, США	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1521952.jpg
105	Эмма Томас	\N	1970-11-30	\N	Лондон, Великобритания	Продюсер, Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41489.jpg
113	Джэйк Майерс	\N	\N	\N	\N	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/54455.jpg
101	Ив Брент	\N	1929-09-11	2011-08-27	Хьюстон, Техас, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24266.jpg
86	Томми Барнс	\N	\N	\N	\N	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/724791.jpg
75	Стивен Кинг	186	1947-09-21	\N	Портлэнд, Мэн, США	Сценарист, Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24263.jpg
77	Томас Ньюман	178	1955-10-20	\N	Лос-Анджелес, Калифорния, США	Композитор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/608629.jpg
74	Стивен Спилберг	172	1946-12-18	\N	Цинциннати, Огайо, США	Продюсер, Режиссер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/22260.jpg
116	Эмбет Дэвидц	173	1965-08-11	\N	Лафайетт, Индиана, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/21893.jpg
69	Людгер Пистор	194	1959-03-16	\N	Реклингхаузен, Северный Рейн — Вестфалия, Германия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/57259.jpg
125	Артус Мария Маттиссен	182	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277052.jpg
100	Эрвин Ледер	175	1951-07-30	\N	Санкт-Пёльтен, Австрия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/34368.jpg
68	Агнешка Вагнер	170	1970-12-17	\N	Варшава, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/86522.jpg
78	Петер Флехтнер	195	1963-01-18	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/118792.jpg
95	Ежи Саган	\N	1928-09-13	1998-04-14	Краков, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277074.jpg
112	Януш Камински	185	1959-06-27	\N	Зембице, Польша	Оператор, Режиссер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/9326.jpg
91	Скотт Манн	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102274.jpg
104	Джон Э. Саммерс	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102276.jpg
71	Роберт Хейли	\N	1942-08-16	\N	Didsbury, Alberta, Canada	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102281.jpg
127	Ники Марвин	\N	\N	\N	\N	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102293.jpg
82	Бен Ваддель	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101662.jpg
97	Джед Гиллин	183	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/62928.jpg
117	Аарон Избики	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101688.jpg
107	Джон Уильям Голт	171	1940-04-04	2022-01-29	Джексон, Миссисипи, США	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101694.jpg
72	Ричард Д’Алессандро	\N	1960-05-11	\N	Массапекуа, Нью-Йорк, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101698.jpg
76	Дик Стивелл	\N	1943-07-27	2002-11-23	Баффало, Нью-Йорк, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/22581.jpg
109	Лазарус Джексон	184	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101706.jpg
111	Ленни Херб	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/100747.jpg
96	Лонни Хэмилтон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/20457.jpg
98	Джинн Ханна	175	1931-03-05	\N	Нью-Йорк, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1109257.jpg
124	Шенн Джонсон	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/66703.jpg
92	Карл Урбан	185	1972-06-07	\N	Уэллингтон, Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32388.jpg
88	Брюс Филлипс	\N	1951-09-13	\N	Уэллингтон, Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41159.jpg
85	Алан Ховард	183	1937-08-05	2015-02-14	Лондон, Англия, Великобритания	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/51648.jpg
126	Пит Смит	\N	\N	2022-01-29	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/82239.jpg
128	Марк Робинс	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1986638.jpg
173	Мэтт Оспбэри	\N	\N	\N	\N	Оператор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4909780.jpg
142	Даниэль Фейнберг	\N	\N	\N	Боулдер, Колорадо, США	Оператор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4909781.jpg
132	Роберт Крантц	\N	\N	\N	\N	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/151822.jpg
147	Гранивиль’Дэнни’  Янг	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/133988.jpg
180	Хэл Гаусман	\N	1917-11-13	2003-06-14	Лос-Анджелес, Калифорния, США	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1994034.jpg
182	Дебора Линн Скотт	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1238244.jpg
135	Майкл Кейн	188	1933-03-14	\N	Лондон, Англия, Великобритания	Актер, Продюсер, Композитор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3711.jpg
158	Марлон Сандерс	191	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3729930.jpg
170	Мацей Орлос	\N	1960-07-16	\N	Варшава, Польша	Актер	\N
190	Эванн Драклер	157	1991-12-20	\N	Лос-Анджелес, Калифорния, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24272.jpg
137	Бэйли Драклер	157	1989-02-09	\N	Лос-Анджелес, Калифорния, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24273.jpg
139	Билл Крэддок	\N	\N	\N	\N	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1967572.jpg
185	Тед Холлис	\N	1947-04-26	\N	Понтиак, Мичиган, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/736956.jpg
140	Дора Тейт	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1670063.jpg
153	Михаэль Шнайдер	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/99578.jpg
130	Ури Аврахами	\N	1949-10-23	\N	Кирьят-Хаим, Хайфа, Израиль	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/255610.jpg
171	Хенрик Биста	\N	1934-03-12	1997-10-15	Kochlowice, Ruda Slaska, Slaskie, Poland	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/271311.jpg
144	Михаэль Ц. Хоффман	178	1943-02-25	\N	Мюнхен, Германия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277053.jpg
157	Марчин Гржимович	\N	1969-03-08	\N	Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277064.jpg
192	Памела Фишер	180	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/728531.jpg
129	Леопольд Роснер	\N	1918-06-26	2008-10-10	Польша	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277109.jpg
175	Лев Рывин	\N	1945-11-10	\N	Москва, СССР (Россия)	Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/20114.jpg
155	Корнелл Уоллес	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102278.jpg
176	Брайан Дилейт	191	1949-04-08	\N	Трентон, Нью-Джерси, США	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3470.jpg
189	Дональд Зинн	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102280.jpg
150	Роб Рейдер	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102288.jpg
146	Майкл Лайтси	180	1973-03-25	\N	Mentor, Ohio, USA	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/37935.jpg
164	Роберт Земекис	183	1951-05-14	\N	Чикаго, Иллинойс, США	Продюсер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2435.jpg
172	Брюс Луквиа	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101670.jpg
167	Пол А. ДиКокко мл.	\N	\N	2022-01-11	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/129431.jpg
178	Нора Данфи	\N	1915-12-25	1994-12-23	Belmont, Ohio, USA	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/95394.jpg
131	Джим Боэк	196	1938-11-11	2014-09-26	Акрон, Огайо, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/30998.jpg
174	Джеральд Форд	183	1913-07-14	2006-12-26	Омаха, Небраска, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/51038.jpg
133	Даррел Кук	179	1973-10-30	\N	Бофорта, Южная Каролина, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/683966.jpg
134	Уинстон Грум	\N	1944-03-23	2020-09-17	Вашингтон, округ Колумбия, США	Сценарист, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/7957.jpg
136	Уильям Джеймс Тигарден	\N	\N	\N	\N	Художник-постановщик, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/475273.jpg
159	Брэд Дуриф	175	1950-03-18	\N	Хантингтон, Западная Вирджиния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/7115.jpg
179	Тодд Риппон	\N	1964-05-30	\N	Уэллингтон, Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/87477.jpg
138	Барри М. Осборн	\N	1944-02-07	\N	Нью-Йорк, США	Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/23361.jpg
169	Аланна Юбак	160	1975-10-03	\N	Дауни, Калифорния, США	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32561.jpg
177	Селене Луна	117	1971-09-19	\N	Тихуана, Нижняя Калифорния, Мексика	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/34648.jpg
234	Дарла К. Андерсон	\N	\N	\N	\N	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/50573.jpg
196	Кристофер Ллойд	185	1938-10-22	\N	Стэмфорд, Коннектикут, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3514.jpg
210	Джордж ДиЧенцо	184	1940-04-21	2010-08-09	Нью-Хейвен, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/65045.jpg
230	Дж.Дж. Коэн	\N	1965-06-22	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/7237.jpg
243	Артур Тови	\N	1904-11-14	2000-10-20	Douglas, Arizona, USA	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/180249.jpg
200	Нил Кэнтон	\N	\N	\N	Нью-Йорк, США	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/12631.jpg
254	Кейси Аффлек	175	1975-08-12	\N	Фолмут, Массачусетс, США	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4.jpg
194	Джон Литгоу	193	1945-10-19	\N	Рочестер, Нью-Йорк, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/8837.jpg
225	Билл Ирвин	182	1950-04-11	\N	Санта-Моника, Калифорния, США	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/8925.jpg
220	Джефф Хефнер	188	1975-06-22	\N	Сэнд-Крик, Мичиган, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/40459.jpg
232	Расс Фега	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/47179.jpg
253	Арни Бьерн Хельгасон	\N	\N	\N	\N	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3737269.jpg
258	Гари Феттис	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1986331.jpg
206	Паула Малкомсон	166	1970-01-01	\N	Белфаст, Северная Ирландия, Великобритания	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/10082.jpg
245	Билл МакКинни	178	1931-09-12	2011-12-01	Чаттануга, Теннесси, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24275.jpg
227	Дэвид Э. Браунинг	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24281.jpg
257	Гарт Шоу	170	1949-05-16	\N	Оранж, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152828.jpg
198	Тодд Томпсон	191	1971-01-02	\N	Кливленд, Огайо, США	Продюсер, Режиссер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3483.jpg
228	Леопольд Козловски	\N	1918-11-26	2019-03-12	Przemyslany, Tar	Актер, Композитор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277042.jpg
241	Петер Аппиано	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277056.jpg
244	Анемона Кнут	\N	1980-11-15	\N	Варшава, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277068.jpg
223	Джереми Флинн	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277069.jpg
197	Леопольд Пфефферберг	\N	1913-03-20	2001-03-09	Польша	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/584910.jpg
236	Ларри Бранденбург	\N	1948-05-03	\N	Уашаба, Миннесота, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/63758.jpg
222	Джозеф Раньо	165	1936-03-11	\N	Бруклин, Нью-Йорк, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/56711.jpg
260	Морган Ланд	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3271.jpg
202	Дэвид Хехт	\N	\N	\N	\N	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102289.jpg
204	Харольд Дж. Хертэм	\N	1929-04-11	1998-07-04	Батон-Руж, Луизиана, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/69686.jpg
255	Марго Мурер	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/79518.jpg
249	Эд Дэвис	170	1952-07-18	\N	 Саванна, Джорджия, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101669.jpg
240	Марк Матисен	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/27714.jpg
238	Китти К. Грин	130	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101680.jpg
231	Майкл МакФол	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101690.jpg
214	Кевин Дэвис	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/79766.jpg
218	Питер Бэннон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101709.jpg
215	Джо Вашингтон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/75246.jpg
226	Лив Тайлер	178	1977-07-01	\N	Нью-Йорк, США	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/22383.jpg
235	Джон Ноубл	182	1948-08-20	\N	Порт-Пири, Южная Австралия, Австралия	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/18510.jpg
233	Брюс Спенс	200	1945-09-17	\N	Окленд, Северный остров, Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/31343.jpg
195	Ричард Эдж	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/475882.jpg
199	Павел Санаев	\N	1969-08-16	\N	Москва, СССР (Россия)	Сценарист, Переводчик, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/311065.jpg
203	Ли Анкрич	\N	1967-08-08	\N	Кливленд, Огайо, США	Режиссер, Монтажер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/23949.jpg
9	Гелена Пирогова	\N	1975-08-27	\N	Кишинёв, СССР (Молдавия)	Озвучка, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1692290.jpg
287	Уэнди Джо Спербер	157	1958-09-15	2005-11-29	Голливуд, Калифорния, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/45334.jpg
265	Джефф О’Хако	\N	1954-08-16	\N	США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/104780.jpg
272	Эрик Столц	\N	1961-09-30	\N	Уитьер, Калифорния, США	Актер, Режиссер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/7350.jpg
313	Уэс Бентли	180	1978-09-04	\N	Джонсборо, Арканзас, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3.jpg
310	Флора Нолан	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3730668.jpg
277	Александр Майкл Хелисек	179	\N	\N	\N	Продюсер, Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2998553.jpg
304	Мак Майлз	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24268.jpg
321	Скотти Левенуорф	170	1990-05-21	\N	Риверсайд, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24277.jpg
302	Дэвид Тэттерсолл	\N	1960-11-14	\N	Барроу-ин-Фернесс, Англия, Великобритания	Оператор, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/609246.jpg
315	Беатриче Макола	\N	1965-12-02	2001-12-13	Верона, Италия	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/252711.jpg
275	Альдона Грохал	\N	\N	\N	Пщина, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/240143.jpg
312	Шабтай Конорти	\N	\N	2002-05-27	Варна, Болгария	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/828966.jpg
273	Станислав Кочанович	\N	1921-05-05	1993-05-05	Tarnów, Ma	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277050.jpg
297	Ганс-Михаэль Реберг	178	1938-04-02	2017-11-07	Fürstenwalde, Brandenburg, Germany	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152265.jpg
300	Яцек Пуланеки	\N	1967-07-28	\N	Бондково, Польша	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277057.jpg
289	Осман Рагхеб	\N	1926-05-11	\N	Nablus, Palestine	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/155958.jpg
285	Этель Шиц	\N	1960-09-26	\N	Люблин, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/240147.jpg
261	Петр Кадличик	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277082.jpg
316	Сюзанна Липиек	\N	1970-08-11	\N	Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/390047.jpg
280	Эмили Шиндлер	\N	1907-10-22	2001-10-05	Альт Молетейн, Австро-Венгрия (Малетин, Чехия)	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277112.jpg
274	Клэнси Браун	191	1959-01-05	\N	Урбана, Огайо, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/20802.jpg
288	Джеймс Уитмор	173	1921-10-01	2009-02-06	Уайт-Плэйнс, Нью-Йорк, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/50162.jpg
322	Клер Слеммер	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/58052.jpg
266	Джеймс Кисики	\N	1938-04-14	2017-11-27	Чикаго, Иллинойс, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/10118.jpg
283	Чарли Кернс	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102287.jpg
279	Андрей Абакумов	\N	\N	\N	\N	Озвучка	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6507406.jpg
303	Дэниэл Дж. Гиллули	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101686.jpg
308	Байрон Миннс	\N	1962-12-31	\N	\N	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2959.jpg
267	Майкл Гарви	178	\N	\N	\N	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1962135.jpg
293	Марк А. Рич	\N	\N	\N	\N	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/569838.jpg
305	Уильям Шипман	193	1953-12-08	\N	Лонг-Айленд, Нью-Йорк, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1103546.jpg
318	Чарльз Невирт	\N	\N	\N	Нью-Йорк, США	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1549.jpg
264	Энди Серкис	\N	1964-04-20	\N	Руислип, Лондон, Англия, Великобритания	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/13176.jpg
268	Кейт Бланшетт	174	1969-05-14	\N	Мельбурн, Виктория, Австралия	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/112.jpg
282	Брюс Хопкинс	183	1955-11-25	\N	Invercargill, New Zealand	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41153.jpg
269	Эндрю Лесни	\N	1956-01-01	2015-04-27	Сидней, Новый Южный Уэльс, Австралия	Оператор, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/225142.jpg
263	Ройд Толкин	\N	\N	\N	\N	Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1835235.jpg
276	Джо Бликли	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2010661.jpg
314	Октавио Солис	\N	\N	\N	\N	Режиссер, Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/853480.jpg
549	Майкл Джаккино	173	1967-10-10	\N	Риверсайд, Нью-Джерси, США	Композитор, Актер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/257568.jpg
348	Пол Хэнсон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152916.jpg
383	Джанин Кинг	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2286966.jpg
345	Тодд Халлоуэлл	\N	1952-08-29	\N	Кембридж, Миннесота, США	Продюсер, Художник-постановщик, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/8977.jpg
353	Фрэнсис З. МакКарти	175	1942-02-15	\N	США	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/26791.jpg
363	Гриффен Фрейзер	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3730669.jpg
341	Брайан Стэмп	185	\N	\N	\N	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4324837.jpg
327	Джилл Кристенсен	\N	\N	\N	\N	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1229698.jpg
323	Джонатан Нолан	188	1976-06-06	\N	Лондон, Англия, Великобритания	Сценарист, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/55539.jpg
367	Кенделл Эллиотт	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1987056.jpg
331	Джаред Стовэлл	\N	\N	\N	\N	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3889715.jpg
351	Мири Фабиан	\N	1943-08-30	\N	Чехословакия	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277036.jpg
361	Магдалена Дандуриан	\N	1971-10-12	\N	Грудзёндзе., Куявско-Поморское воеводство,Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277044.jpg
362	Павел Делонг	187	1970-04-29	\N	Краков, Польша	Актер, Режиссер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/245561.jpg
377	Эугениуш Привезенцев	\N	1946-08-17	2005-07-08	Гданьск, Польша	Актер, Сценарист, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/140923.jpg
326	Томаш Дедек	182	1957-09-20	\N	Рава-Мазовецка, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277058.jpg
346	Магдалена Коморницка	\N	1968-10-10	\N	Лодзь, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277066.jpg
359	Александр Бучолич	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277092.jpg
329	Камиль Кравьек	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277104.jpg
372	Майкл Кан	\N	1935-12-08	\N	Нью-Йорк, США	Монтажер, Продюсер, Оператор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/172398.jpg
364	Нил Джунтоли	\N	1959-12-20	\N	Чикаго, Иллинойс, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/68803.jpg
380	Пол МакКрейн	170	1961-01-19	\N	Филадельфия, Пенсильвания, США	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/7363.jpg
328	Гордон Грин	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1667952.jpg
344	Рон Ньюэлл	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/98379.jpg
333	Кевин Мэнгэн	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/84739.jpg
339	Эмили Кэри	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101700.jpg
337	Джо Стефанелли	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101703.jpg
324	Халли Д’Амор	\N	1942-08-13	2006-12-14	Харви, Иллинойс, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101710.jpg
325	Боб Хоуп	178	1903-05-29	2003-07-27	Лондон, Англия, Великобритания	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/74873.jpg
376	Элвис Пресли	182	1935-01-08	1977-08-16	Тьюпело, Миссисипи, США	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/29799.jpg
381	Курт Рассел	176	1951-03-17	\N	Спрингфилд, Массачусетс, США	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/37652.jpg
347	Нэнси Хэй	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1997365.jpg
350	Пол Норелл	188	1952-02-11	\N	Лондон, Англия, Великобритания	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/235801.jpg
360	Шон Бин	179	1959-04-17	\N	Шеффилд, Йоркшир, Англия, Великобритания	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/28092.jpg
355	Роберт Поллок	\N	1951-09-16	\N	Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41160.jpg
332	Генри Мортенсен	188	1988-01-28	\N	Лос-Анджелес, Калифорния, США	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/94178.jpg
379	Боб Вайнштейн	178	1954-10-18	\N	Флашинг, Квинс, Нью-Йорк, США	Продюсер, Сценарист, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2434.jpg
354	Энтони Гонсалес	\N	2004-09-23	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3229588.jpg
343	Луис Вальдес	\N	1940-06-26	\N	Калифорния, США	Режиссер, Сценарист, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/145235.jpg
388	Михаил Черепнин	\N	\N	\N	\N	Переводчик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2434208.jpg
439	Марк МакКлюр	178	1957-03-31	\N	Сан-Матео, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2648.jpg
432	Фрэнсис Ли МакКейн	168	1944-07-28	\N	Йорк, Пенсильвания, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/22972.jpg
421	Элиес Габел	183	1983-05-08	\N	Лондон, Англия, Великобритания	Актер, Режиссер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1076719.jpg
427	Бенжамин Харди	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3316010.jpg
386	Кристиан Ван дер Хейден	185	\N	\N	\N	Продюсер, Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2679483.jpg
390	Ли Смит	\N	\N	\N	Сидней, Новый Южный Уэльс, Австралия	Монтажер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2354627.jpg
404	Гарри Дин Стэнтон	173	1926-07-14	2017-09-15	Вест Ирвин, Кентукки, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/22209.jpg
392	Арнольд Монти	\N	\N	\N	\N	Продюсер, Сценарист, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4321465.jpg
430	Уильям Крус	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1999763.jpg
438	Керин Вагнер	\N	\N	\N	\N	Художник-постановщик, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1107287.jpg
437	Малгоша Гебель	\N	1955-11-30	\N	Катовице, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152279.jpg
410	Альберт Мисак	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277038.jpg
389	Войцех Клата	\N	1976-01-27	\N	Варшава, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277047.jpg
447	Тадеуш Хук	187	1948-05-01	\N	Краков, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/164603.jpg
395	Пётр Цирвус	\N	1961-06-20	\N	Nowy Targ, Ma	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277060.jpg
416	Лидия Выробиец-Банк	\N	1927-09-21	1994-04-12	Варшава, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277085.jpg
442	Янек Дреснер	\N	1923-09-04	2016-04-18	Польша	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277100.jpg
401	Тим Роббинс	196	1958-10-16	\N	Уэст-Ковина, штат Калифорния, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/7987.jpg
440	Дон Макманус	\N	1959-11-08	\N	Силакога, Алабама, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6277.jpg
412	Рон Томас	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/36255.jpg
391	Джордж Макреди	185	1899-08-29	1973-07-02	Провиденс, Род-Айленд, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/186862.jpg
419	Джон Уоршэм	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101681.jpg
399	Тимоти Рекорд	\N	\N	2009-03-01	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101672.jpg
436	Боб Харкс	185	1927-09-20	2010-12-08	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101675.jpg
446	Дон Фишер	188	1959-04-18	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/30749.jpg
444	Бонни Энн Бёрджесс	\N	\N	\N	\N	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101692.jpg
433	Скотт Оливер	\N	\N	\N	Нэшвилл, Теннесси, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101693.jpg
394	Тим Перри	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/15166.jpg
435	Пол Рачовски	\N	\N	\N	\N	Актер, Продюсер, Оператор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101701.jpg
443	Алоизиус Гигл	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101704.jpg
429	Майкл Мэттисон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/38681.jpg
407	Джим Дэмрон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/35581.jpg
414	Стив Тиш	\N	\N	\N	Лейквуд, Нью-Джерси, США	Продюсер, Актер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/14914.jpg
425	Хьюго Уивинг	190	1960-04-04	\N	Ибадан, Нигерия	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1491.jpg
445	Йен Хьюз	175	1969-04-08	\N	Ванкувер, Британская Колумбия, Канада	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/53031.jpg
426	Сара МакЛауд	170	1971-07-18	\N	Путаруру, Новая Зеландия	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32393.jpg
408	Джарл Бензон	185	1976-09-22	\N	Svendborg, Denmark	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1083049.jpg
431	Джозеф Мика-Хант	178	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1852342.jpg
402	Эдриан Молина	\N	1985-08-23	\N	Юба-Сити, Калифорния, США	Сценарист, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4376631.jpg
400	Хайме Камиль	186	1973-07-22	\N	Мехико, Мексика	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/233381.jpg
486	Мэттью Олдрич	\N	\N	\N	\N	Сценарист, Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/383137.jpg
505	Стив Блум	\N	\N	\N	\N	Монтажер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2000584.jpg
461	Джеймс Толкан	168	1931-06-20	\N	Калумет, Мичиган, США	Актер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/88001.jpg
464	Билли Зейн	184	1966-02-24	\N	Чикаго, Иллинойс, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/45019.jpg
472	Норман Элден	180	1924-09-13	2012-07-27	Форт-Уорт, Техас, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/50731.jpg
454	Ричард Патрик	\N	\N	\N	\N	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/5337237.jpg
450	Том Виллетт	193	\N	\N	Ченолт, Кентукки, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/71277.jpg
484	Анна Шкуридина	\N	\N	\N	Рига, СССР (Латвия)	Переводчик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6373986.jpg
513	Дэвид Ф. Классен	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1986604.jpg
487	Брайан Либби	193	1949-04-20	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24274.jpg
499	Джуди Херрера	161	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1451.jpg
468	Йонатан Сэгаль	\N	1959-04-23	\N	Торонто, Онтарио, Канада	Актер, Режиссер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/157676.jpg
515	Гено Лехнер	176	\N	\N	Германия	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/92962.jpg
506	Славомир Холланд	\N	1958-04-14	\N	Варшава, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277059.jpg
503	Лех Нибильский	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277083.jpg
479	Дорит Сиадия	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277095.jpg
471	Катажина Смехович	163	1969-11-25	\N	Lódz, Lódzkie, Poland	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277113.jpg
478	Ирвинг Гловин	\N	\N	\N	\N	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277116.jpg
460	Джо Пекорато	\N	\N	\N	\N	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/80538.jpg
502	Харольд Э. Коуп мл.	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102279.jpg
494	Сержиу Като	180	1960-07-15	\N	Рио-де-Жанейро, Бразилия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/456211.jpg
473	Нил Ридэуэй	\N	\N	\N	\N	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4024080.jpg
451	Роджер Дикинс	178	1949-05-24	\N	Торки, Девон, Англия, Великобритания	Оператор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/258609.jpg
482	Шиван Фэллон	173	1961-05-13	\N	Сиракьюс, Нью-Йорк, США	Актриса, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/31636.jpg
498	Грэйди Боуман	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101663.jpg
504	Фэй Гененс	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101664.jpg
458	Кэлвин Гэдсден	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101687.jpg
457	Джо Аляски	\N	1949-05-26	2016-02-03	Трой, Нью-Йорк, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/8832.jpg
467	The Hallelujah Singers of Beaufort South Carolina	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3095349.jpg
466	Аарон Майкл Лэйси	180	1969-05-26	\N	Вашингтон, округ Колумбия, США	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/30564.jpg
470	Джордж Уоллес	170	1919-08-25	1998-09-13	Клио, Алабама, США	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/78490.jpg
511	Венди Файнерман	\N	\N	\N	Калифорния, США	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/44201.jpg
514	Орландо Блум	180	1977-01-13	\N	Кентербери, Кент, Великобритания	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/30875.jpg
462	Миранда Отто	165	1967-12-15	\N	Брисбен, Австралия	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32387.jpg
495	Джон Бах	188	1946-06-05	\N	Уэльс, Великобритания	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41149.jpg
453	Питер Тейт	180	\N	\N	\N	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/112147.jpg
488	Джед Брофи	175	1963-01-09	\N	Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41150.jpg
510	Филип Грив	178	1966-04-02	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/51664.jpg
512	Эмма Дикин	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1183842.jpg
507	Рик Поррас	\N	\N	\N	\N	Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32396.jpg
501	Дэн Хенна	\N	\N	\N	Hastings, New Zealand	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/569829.jpg
496	Карла Медина	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1932360.jpg
542	Джон Лассетер	170	1957-01-12	\N	Лос-Анджелес, Калифорния, США	Продюсер, Сценарист, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/23948.jpg
548	Лиза Фриман	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/133984.jpg
575	Джэми Эбботт	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152006.jpg
537	Чарльз Л. Кэмпбелл	\N	1930-08-17	2013-06-21	США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152920.jpg
563	Боб Гейл	\N	1951-05-25	\N	Юниверсити Сити, Миссури, США	Сценарист, Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/87813.jpg
557	Мэттью МакКонахи	182	1969-11-04	\N	Увалд, Техас, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/797.jpg
520	Тимоти Шаламе	178	1995-12-27	\N	Нью-Йорк, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1665224.jpg
570	Лена Георгас	\N	1979-07-05	\N	Нью-Йорк, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/742470.jpg
568	Уильям Патрик Браун	183	1980-01-05	\N	Мобил, Алабама, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2497048.jpg
538	Эдри Уорнер	\N	1933-04-07	2000-02-02	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24270.jpg
522	Рэйф Файнс	180	1962-12-22	\N	Ипсвич, Саффолк, Англия, Великобритания	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/22670.jpg
562	Шмуэль Леви	\N	1962-12-04	\N	Jaffa, Israel	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/829028.jpg
541	Мартин Земмельрогге	168	1955-12-08	\N	Bad Boll, Baden-Württemberg, Germany	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/28130.jpg
527	Марек Врона	\N	1967-09-17	\N	Trzebnica, Dolnoslaskie, Poland	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277062.jpg
559	Агнешка Коженёвска	\N	\N	\N	Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277087.jpg
550	Бонни Лоэв	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2074804.jpg
567	Харрисон Уайт	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/66344.jpg
519	Фрэнк Медрано	168	1958-05-20	\N	Манхэттэн, Нью-Йорк, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/456918.jpg
531	Гэри Ли Дэвис	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/85810.jpg
529	Фред Калбертсон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1866638.jpg
530	Питер Добсон	\N	1964-07-16	\N	Ред Бэнк, Нью-Джерси, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/29700.jpg
539	Сонни Шроер	189	1935-08-28	\N	Валдоста, Джорджия, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/53933.jpg
558	Мэтт Уоллес	191	\N	\N	Сант-Луис, Миссури, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101682.jpg
574	Майкл Кеммерлинг	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101684.jpg
571	Расс Уилсон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24399.jpg
576	Артур Бремер	\N	1950-08-21	\N	Милуоки, Висконсин, США	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/95950.jpg
545	Жаклин Ловелл	169	1974-12-09	\N	Лос-Анджелес, Калифорния, США	Актриса, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/16124.jpg
554	Майкл Оливер	183	1981-10-10	\N	Лос-Анджелес, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/96698.jpg
540	Джоанна Джонстон	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1087059.jpg
518	Билли Бойд	169	1968-08-28	\N	Глазго, Шотландия, Великобритания	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/28463.jpg
543	Джон Рис-Дэвис	185	1944-05-05	\N	Солсбери, Уилтшир, Англия, Великобритания	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/28519.jpg
524	Ноэль Эпплби	\N	1933-04-05	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/51649.jpg
553	Сэдвин Брофи	\N	\N	\N	Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/475881.jpg
556	Росс Дункан	172	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/242119.jpg
561	Ли Хартли	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/51661.jpg
564	Кэти Джексон	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41162.jpg
546	Филиппа Бойенс	\N	\N	\N	Новая Зеландия	Сценарист, Продюсер, Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32386.jpg
560	Грант Мейджор	185	\N	\N	Palmerston North, Manawatu, New Zealand	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/425445.jpg
552	Наталия Кордова-Бакли	168	1982-11-25	\N	Мехико, Мексика	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1651802.jpg
534	Бланка Арасели	169	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2095562.jpg
508	Харли Джессуп	\N	\N	\N	Корваллис, Орегон, США	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2000415.jpg
621	Айви Бетьюн	\N	1918-06-01	2019-07-19	Севастополь, Советская Россия (Украина)	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/31109.jpg
585	Кэтрин Бриттон	\N	1949-09-15	\N	США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152047.jpg
613	Ллойд Л. Толберт	\N	\N	\N	США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/133987.jpg
629	Ли Браунфилд	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152917.jpg
591	Коллетт Вульф	\N	1980-04-04	\N	Кинг Джордж, Вирджиния, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1094609.jpg
608	Гржегорц Квас	\N	1973-04-04	\N	Tarnowskie Góry, Slaskie, Poland	Актер	\N
623	Эндрю Борба	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/5635.jpg
597	Лиам Дикинсон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2948115.jpg
605	Кики Леа Кэмпбелл	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3726500.jpg
611	Кеван Вебер	\N	\N	\N	\N	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3890662.jpg
599	Мэри Зофрис	175	1964-03-23	\N	Флорида, США	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1529219.jpg
617	Гари Имхофф	\N	1952-08-27	\N	Милуоки, Висконсин, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24280.jpg
644	Анджей Северин	\N	1946-04-25	\N	Хайльбронн, Германия	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/81525.jpg
616	Анна Муха	163	1980-04-26	\N	Варшава, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277037.jpg
598	Яцек Вуйцицки	\N	1960-01-21	\N	Краков, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/125832.jpg
583	Эзра Дегэн	\N	1947-04-02	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152429.jpg
636	Август Шмёльцер	180	1958-06-27	\N	Грац, Австрия	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/53351.jpg
594	Хаймон Мария Буттингер	174	1953-05-03	\N	Вена, Австрия	Актер, Продюсер, Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/98785.jpg
635	Мачей Козловский	183	1957-09-08	2010-05-11	Каргова, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/270912.jpg
603	Люцина Забава	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/125833.jpg
631	Ганс Роснер	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277091.jpg
593	Разия Израэли	\N	1956-01-18	\N	Израиль	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/276493.jpg
641	Джон Хортон	\N	\N	\N	Китченер, Онтарио, Канада	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/75464.jpg
637	Юджин С. ДеПаскуале	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3331347.jpg
606	Крис Пейдж	\N	\N	\N	\N	Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3490100.jpg
630	Джодива Степп	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102291.jpg
588	Джон Рэндолл	\N	\N	1997-03-10	Новый Орлеан, Луизиана, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101659.jpg
640	Элизабет Хэнкс	\N	1982-05-17	\N	Лос-Анджелес, Калифорния, США	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/86908.jpg
602	Тайлер Лонг	\N	\N	\N	Айкен, Южная Каролина, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/7447.jpg
582	Гари Робинсон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101678.jpg
615	Мэтт Ребенкофф	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101708.jpg
618	Джеймс Энт	\N	1967-12-24	\N	Бремертон, Вашингтон, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/83247.jpg
622	Зэк Ханнер	188	1969-06-17	\N	Mt. Airy, North Carolina, USA	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/7436.jpg
600	Тедди Лэйн мл.	\N	\N	\N	Феникс, Аризона, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1533.jpg
643	Рональд Рейган	185	1911-02-06	2004-06-05	Тампико, Иллинойс, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/56948.jpg
584	Брендан Шэнэхэн	191	1969-01-23	\N	Мимико, Онтарио, Канада	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1238999.jpg
592	Лесли МакДональд	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1997364.jpg
625	Александра Эстин	163	1996-11-27	\N	Калифорния, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32389.jpg
627	Говард Шор	179	1946-10-18	\N	Торонто, Онтарио, Канада	Композитор, Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/142716.jpg
580	Рене Виктор	\N	1953-06-15	\N	\N	Актриса, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/27534.jpg
586	Сальвадор Рейс	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2472218.jpg
699	Берт Берри	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4045292.jpg
672	Кейси Семашко	173	1961-03-17	\N	Чикаго, Иллинойс, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/8569.jpg
669	Джон Грин мл.	\N	1972-01-25	\N	США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/134630.jpg
660	Томми Томас	\N	1957-03-31	1999-02-18	Мемфис, Теннесси, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/133986.jpg
693	Эллен Бёрстин	170	1932-12-07	\N	Детройт, Мичиган, США	Актриса, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6965.jpg
665	Линда Обст	\N	\N	\N	\N	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/31741.jpg
676	Хойте Ван Хойтема	\N	1971-10-04	\N	Хорген, Швейцария	Оператор, Композитор, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/758887.jpg
682	Джош Ласби	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3490096.jpg
675	Уильям Сэдлер	173	1950-04-13	\N	Баффало, Нью-Йорк, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24267.jpg
663	Рэйчел Сингер	157	1968-09-13	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24276.jpg
697	Фред Астер	175	1899-05-10	1987-06-22	Омаха, Небраска, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/154895.jpg
649	Сэмюэл Тейт	\N	\N	\N	\N	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1670066.jpg
684	Ади Ницан	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277035.jpg
689	Тадеуш Брадецкий	\N	1955-01-02	2022-01-24	Забже, Силезское воеводство, Польша	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277046.jpg
687	Беата Рыботицка	\N	\N	\N	Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277051.jpg
711	Александер Хельд	178	1958-10-19	\N	Мюнхен, Германия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/11014.jpg
702	Радослав Кжижовски	\N	\N	\N	Клучборк, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277077.jpg
662	Рышард Радвански	\N	\N	2019-12-20	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277081.jpg
694	Бен Талар	\N	\N	\N	\N	Режиссер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1906597.jpg
673	Дэвид Провал	169	1942-05-20	\N	Бруклин, Нью-Йорк, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/28987.jpg
654	В. Дж. Фостер	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/23749.jpg
656	Нед Беллами	183	1957-05-07	\N	Дейтон, Огайо, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/23488.jpg
688	Брайан Брофи	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/12710.jpg
661	Дэннис Бэйкер	\N	\N	\N	\N	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1104241.jpg
703	Михаил Ягодкин	\N	\N	\N	\N	Переводчик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4521580.jpg
698	Александр Земекис	\N	1985-12-11	\N	Лос-Анджелес, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/75322.jpg
659	Майкл Флэннери	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101677.jpg
704	Марлена Смоллз	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101679.jpg
652	Джеффри Виннер	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101685.jpg
646	Чиффони Кобб	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101711.jpg
685	Хуан Синглтон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101712.jpg
709	Кери-Энн Билотта	163	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277926.jpg
651	Клинт Кэлверт	180	1963-08-17	\N	США	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3199416.jpg
668	Джон Гленн Хардинг	180	1964-11-02	\N	Laurinburg, North Carolina, USA	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101716.jpg
647	Робб Скайлер	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/25460.jpg
657	Иэн Холм	\N	1931-09-12	2020-06-19	Гудмайес, Эссекс, Англия, Великобритания 	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2440.jpg
679	Мартон Чокаш	186	1966-06-30	\N	Инверкаргилл, Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/18503.jpg
707	Лоуренс Макор	193	1968-03-20	\N	Bastion Point, Auckland, New Zealand	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32392.jpg
667	Гарри Синклер	\N	\N	\N	Оклэнд, Новая Зеландия	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/51658.jpg
666	Кристиан Риверс	\N	\N	\N	Веллингтон, Новая Зеландия	Режиссер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3963573.jpg
650	Джэми Селкирк	\N	\N	\N	\N	Монтажер, Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41166.jpg
680	Херберт Сигенса	173	\N	\N	\N	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/82111.jpg
166	Тим Эватт	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4929492.jpg
734	Дэвид Харольд Браун	\N	\N	\N	США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/133985.jpg
725	Дебора Хэрмон	\N	1951-05-08	\N	Чикаго, Иллинойс, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152921.jpg
714	Дин Канди	\N	1946-03-12	\N	Альгамбра, Калифорния, США	Оператор, Актер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101732.jpg
718	Джозеф Оливейра	165	\N	\N	Бретанья, Сан-Мигел, Португалия	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3324459.jpg
678	Фрэнк Дарабонт	183	1959-01-28	\N	Монбельяр, Франция	Сценарист, Режиссер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24262.jpg
509	Том Хэнкс	183	1956-07-09	\N	Конкорд, Калифорния, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/9144.jpg
27	Дэвид Морс	193	1953-10-11	\N	Гамильтон, Массачусетс, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/12759.jpg
626	Бонни Хант	168	1961-09-22	\N	Чикаго, Иллинойс, США	Актриса, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/22527.jpg
726	Майкл Кларк Дункан	196	1957-12-10	2012-09-03	Чикаго, Иллинойс, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/677.jpg
535	Джеймс Кромуэлл	200	1940-01-27	\N	Лос-Анджелес, Калифорния, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/20664.jpg
186	Майкл Джитер	163	1952-08-26	2003-03-30	Лоуренсберг, Теннесси, США	Актер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/8989.jpg
141	Грэм Грин	179	1952-06-22	\N	Сикс-Нейшенс-Резерв, Онтарио, Канада	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1130.jpg
213	Даг Хатчисон	166	1960-05-26	\N	Довер, Делавэр, США	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/12761.jpg
114	Сэм Рокуэлл	173	1968-11-05	\N	Дэли-Сити, Калифорния, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/21496.jpg
208	Барри Пеппер	178	1970-04-04	\N	Кэмпбелл-Ривер, Британская Колумбия, Канада	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/7370.jpg
306	Джеффри ДеМанн	175	1947-04-25	\N	Баффало, Нью-Йорк, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24264.jpg
398	Патриша Кларксон	165	1959-12-29	\N	Новый Орлеан, Луизиана, США	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3420.jpg
357	Даббс Грир	179	1917-04-02	2007-04-28	Фэйрвью, Миссури, США.	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24265.jpg
369	Рай Таско	\N	1917-08-12	2011-07-02	Бостон, Массачусетс, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24269.jpg
73	Брент Бриско	175	1961-05-21	2017-10-18	Моберли, Миссури, США	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/13487.jpg
733	Ребекка Клингер	168	\N	\N	Кокомо, Индиана, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/660035.jpg
731	Лиам Нисон	192	1952-06-07	\N	Беллимен, Северная Ирландия, Великобритания	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6534.jpg
713	Петр Полк	176	1962-01-22	\N	Kalety, Slaskie, Poland	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277040.jpg
742	Беттина Купфер	168	1963-07-19	\N	Штутгарт, Германия	Актриса, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277048.jpg
716	Анджей Вельминский	\N	1952-04-30	\N	Краков, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277054.jpg
739	Йоахим Пауль Ассбёк	176	1965-10-26	\N	Кельн, ФРГ (Германия)	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2180.jpg
720	Ханна Коссовска	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277079.jpg
715	Морган Фриман	188	1937-06-01	\N	Мемфис, Теннесси, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6750.jpg
744	Алан Р. Кесслер	\N	1946-12-29	2011-10-17	Бекли, Западная Вирджиния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102277.jpg
736	Дэна Снайдер	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102282.jpg
719	Джордж Келли	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101658.jpg
722	Эрик Андервуд	\N	1968-06-03	1995-08-10	Мичиган, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101691.jpg
740	Тимоти МакНил	\N	\N	\N	Хьюстон, Техас, США	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/26935.jpg
737	Тереза Дентон	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101714.jpg
727	Элистэр Браунинг	180	1954-02-08	2019-06-02	Данидин, Отаго, Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/10331.jpg
721	Зо Хартли	158	\N	\N	Invercargill, New Zealand	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1055731.jpg
298	Гэри Синиз	173	1955-03-17	\N	Блу-Айленд, Иллинойс, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3100.jpg
692	Кэтлин Левенуорф	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24278.jpg
413	Билл Грэттон	\N	1939-07-07	2011-06-23	Портленд, Орегон, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/15235.jpg
547	Ди Крокстон	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24279.jpg
724	Ван Эпперсон	173	1957-02-21	\N	Sweetwater, Tennessee, USA	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/20718.jpg
664	Кристофер Гринвуд	\N	\N	\N	\N	Оператор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1586213.jpg
493	Уэс Холл	196	1979-12-14	\N	Ноксвилл, Теннесси, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1451292.jpg
151	Дэниэл Д. Харрис	\N	1947-03-19	2012-10-01	Колорадо-Спрингс, Колорадо, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/967709.jpg
207	Фил Хоун	173	1957-04-20	\N	Уорренсберг, Миссури, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/356.jpg
94	Дон Лэнгли	178	1947-10-01	\N	Weslaco, Texas, USA	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/379898.jpg
290	Роберт Мэлоун	175	1964-06-30	\N	Хантсвилл, Алабама, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/751695.jpg
18	Джинджер Роджерс	165	1911-07-16	1995-04-25	Индепенденс, Миссури, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/154905.jpg
211	Той Спирс	\N	1971-10-01	\N	Флорида, США	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/591747.jpg
474	Джеймс Маршалл Волчок	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24282.jpg
420	Дэвид Валдес	\N	1950-08-12	\N	Лос-Анджелес, Калифорния, США	Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24283.jpg
523	Леонид Белозорович	\N	1951-10-30	\N	Речица, Гомельская область, СССР (Белоруссия)	Озвучка, Актер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/290494.jpg
212	Теренс Марш	\N	1931-11-14	2018-01-09	Лондон, Англия, Великобритания	Художник-постановщик, Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/137866.jpg
581	Майкл Сейртон	\N	1937-11-01	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1997462.jpg
90	Ричард Фрэнсис-Брюс	\N	1948-12-10	\N	Сидней, Новый Южный Уэльс, Австралия	Монтажер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1986116.jpg
710	Бен Кингсли	\N	1943-12-31	\N	Скарборо, Йоркшир, Англия, Великобритания	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6846.jpg
624	Кэролайн Гудолл	173	1959-11-13	\N	Лондон, Англия, Великобритания	Актриса, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/12093.jpg
121	Марк Иванир	173	1968-09-06	\N	Черновцы, Черновицкая область, СССР (Украина)	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/54872.jpg
336	Фридрих фон Тун	188	1942-06-30	\N	Kwassitz, Protectorate of Bohemia and Moravia (Kvasice, Moravia, Czech 	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/44391.jpg
161	Кшиштоф Люфт	\N	1958-01-05	\N	Варшава, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277033.jpg
406	Хэрри Неринг	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277034.jpg
7	Норберт Вайссер	173	1946-07-09	\N	Ной-Изенбург, Германская империя (Германия)	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3603.jpg
441	Михаэль Гордон	186	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/172863.jpg
492	Беата Палух	\N	1961-09-04	\N	Бельско-Бьяла, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277039.jpg
165	Беата Дескур	\N	1972-04-29	\N	\N	Актриса, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1774656.jpg
730	Рэми Хьюбергер	\N	1963-01-12	\N	Израиль	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/141497.jpg
294	Адам Семён	\N	1981-03-10	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277043.jpg
311	Элина Ловенсон	\N	1966-07-11	\N	Бухарест, Румыния	Актриса, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/48282.jpg
601	Ева Колясиньска	\N	1951-07-13	\N	Познань, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/271633.jpg
352	Вили Матула	\N	1962-03-05	\N	Загреб, Хорватия, Югославия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/128134.jpg
163	Ханс-Йорг Ассманн	190	\N	\N	Берлин, Германия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/53713.jpg
156	Бранко Лустиг	\N	1932-06-10	2019-11-14	Осиек, Югославия (Хорватия)	Продюсер, Актер, Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/10046.jpg
723	Йохен Никель	186	1959-04-10	\N	Виттен, ФРГ (Германия)	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/26402.jpg
115	Даниэль Дель Понте	187	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/58897.jpg
296	Мариан Глинка	180	1943-07-01	2008-06-23	Варшава, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277055.jpg
686	Гжегож Даменцки	180	1967-11-15	\N	Варшава, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/231053.jpg
418	Станислав Брейдыгант	\N	1936-10-02	\N	Варшава, Польша	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/104725.jpg
609	Оляф Любашенко	180	1968-12-06	\N	Вроцлав, Польша	Актер, Режиссер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/240133.jpg
349	Збигнев Козловски	180	1976-04-08	\N	Плоцк, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277063.jpg
28	Ян Юревич	\N	1954-10-20	\N	Щецын, Западно-Поморское, Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/227102.jpg
118	Веслав Комаса	174	1949-01-15	\N	Nowy Wisnicz, Ma	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277070.jpg
428	Мартин Бергманн	\N	1913-02-15	2014-01-22	Прага, Австро-Венгрия (Чехия)	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/134701.jpg
291	Вильгельм Манске	185	1951-03-03	\N	Passau, Bavaria, Germany	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277071.jpg
536	Сигурд Бемме	178	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277072.jpg
671	Рут Фархи	\N	1927-08-13	2021-04-19	Вена, Австрия	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277073.jpg
633	Дирк Бендер	195	1944-02-28	\N	Quedlinburg, Germany	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277076.jpg
463	Мацей Уинклер	\N	\N	\N	Польша	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/38135.jpg
41	Яцек Ленчовски	\N	1953-02-21	2019-11-30	Краков, Польша	Режиссер, Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277078.jpg
271	Майя Осташевска	166	1972-09-03	\N	Краков, Польша	Актриса, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/38105.jpg
735	Себастьян Скалски	\N	\N	\N	\N	Композитор, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277080.jpg
24	Доминика Беднарчик	\N	1972-05-17	\N	Краков, Польша	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277088.jpg
465	Алисиа Кубашевская	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277089.jpg
80	Дэнни Марсу	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277090.jpg
382	Александр Штробеле	183	1953-05-06	\N	Вена, Австрия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/75068.jpg
708	Жорж Керн	185	\N	\N	Зальцбург, Австрия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/262537.jpg
712	Михаэль Шиллер	182	\N	\N	Ганновер, Нижняя Саксония, ФРГ (Германия)	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277093.jpg
642	Гёц Отто	198	1967-10-15	\N	Дитценбах, Германия	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/61377.jpg
544	Вольфганг Зайденберг	195	1962-03-18	\N	Зигбург, Германия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277094.jpg
14	Губерт Крамар	192	1948-05-27	\N	Шайбс, Нижняя Австрия, Австрия	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/377003.jpg
596	Эсти Ерушалми	\N	\N	\N	Тегеран, Иран	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277096.jpg
309	Мишель Кситос	170	1965-08-17	\N	South Lake Tahoe, California, USA	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/74998.jpg
37	Эрик Бруно Боргман	193	1970-06-23	\N	Челси, Массачусетс, США	Актер, Сценарист, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6189.jpg
491	Ришард Хоровиц	\N	1939-05-05	\N	Краков, Польша	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/277103.jpg
252	Мацей Ковалевски	\N	1969-07-30	\N	Коло, Польша	Актер, Сценарист, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1233739.jpg
191	Мария Пешек	\N	1973-09-09	\N	Вроцлав, Польша	Актриса, Композитор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/774415.jpg
695	Катажина Тлалька	\N	1970-09-24	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1715363.jpg
648	Кэтлин Кеннеди	161	1953-06-05	\N	Беркли, Калифорния, США	Продюсер, Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/20396.jpg
374	Роберт Рэймонд	\N	\N	\N	\N	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/153252.jpg
67	Ярослава Турылёва	\N	1932-09-02	2020-05-30	Москва, СССР (Россия)	Озвучка, Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1290076.jpg
497	Томас Кенилли	\N	1935-10-07	\N	Сидней, Новый Южный Уэльс, Австралия	Сценарист, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/251903.jpg
358	Стивен Зеллиан	175	1953-01-30	\N	Фресно, Калифорния, США	Сценарист, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41493.jpg
284	Джон Уильямс	182	1932-02-08	\N	Нью-Йорк, США	Композитор, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/225027.jpg
415	Аллан Старски	\N	1943-01-01	\N	Варшава, Польша	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1986387.jpg
533	Боб Гантон	187	1945-11-15	\N	Санта-Моника, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/23481.jpg
526	Гил Беллоуз	180	1967-06-28	\N	Ванкувер, Британская Колумбия, Канада	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/8531.jpg
248	Марк Ролстон	180	1956-12-07	\N	Балтимор, Мэриленд, США	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/47327.jpg
148	Джуд Чикколелла	178	1947-11-30	\N	Лонг-Айленд, Нью-Йорк, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/25914.jpg
66	Альфонсо Фриман	177	1959-09-13	\N	Лос-Анджелес, Калифорния, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6767.jpg
366	Нил Саммерс	\N	1944-04-28	\N	Лондон, Англия, Великобритания	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/93068.jpg
658	Дороти Сильвер	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/36269.jpg
224	Джон Д. Крэйг	\N	1935-11-10	2008-11-20	Brazil, Indiana, USA	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102283.jpg
717	Кен Маджи	\N	1946-12-11	2015-12-31	Техас, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/23042.jpg
295	Билл Болендер	\N	1940-11-14	\N	Чикаго, Иллинойс, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/27563.jpg
411	Чак Браухлер	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102286.jpg
143	Дион Андерсон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/39088.jpg
183	Пол Кеннеди	\N	1962-07-01	\N	Мэдисон, Висконсин, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/34895.jpg
476	Джеймс Бэбсон	\N	1974-10-24	\N	Уэст-Честер, Пенсильвания, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/29752.jpg
409	Ричард Дун	178	1948-12-04	\N	Коламбус, Огайо, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/27846.jpg
525	Рита Хэйворт	168	1918-10-17	1987-05-14	Бруклин, Нью-Йорк, США	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/106173.jpg
589	Алонсо Ф. Джонс	\N	1970-06-13	\N	Провиденс, Род-Айленд, США	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/33144.jpg
239	Гари Джонс	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3422395.jpg
696	Брэд Спенсер	\N	1969-05-10	\N	Мэнсфилд, Огайо, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/79727.jpg
23	Лиз Глоцер	\N	\N	\N	\N	Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102292.jpg
475	Дэвид В. Лестер	\N	\N	\N	\N	Продюсер, Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/25906.jpg
307	Диомид Виноградов	\N	1979-06-04	\N	Баку, СССР (Азербайджан)	Озвучка, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1781077.jpg
424	Питер Лэндсдаун Смит	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1998853.jpg
154	Элизабет МакБрайд	\N	1955-05-17	1997-06-16	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1999152.jpg
481	Робин Райт	168	1966-04-08	\N	Даллас, Техас, США	Актриса, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/8887.jpg
201	Салли Филд	\N	1946-11-06	\N	Пасадена, Калифорния, США	Актриса, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/13477.jpg
49	Майкелти Уильямсон	188	1960-03-04	\N	Сент-Луис, Миссури, США	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/519.jpg
292	Майкл Коннер Хэмпфри	\N	\N	\N	Миссиссиппи, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101657.jpg
683	Ханна Р. Холл	168	1984-07-09	\N	Денвер, Колорадо, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/24763.jpg
120	Ребекка Уильямс	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101656.jpg
628	Боб Пенни	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/8399.jpg
607	Иона М. Телек	163	1922-04-10	2015-10-23	Бофорта, Южная Каролина, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101660.jpg
368	Логан Ливингстон Гомес	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/913638.jpg
184	Кристофер Джонс	180	1982-02-04	\N	Миртл-Бич, Южная Каролина, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/49479.jpg
604	Фрэнк Гейер	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101665.jpg
20	Джейсон МакГуайр	188	1978-09-17	\N	Снелвилль, Джорджия, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101667.jpg
103	Бретт Райс	\N	1954-12-31	\N	Чаттануга, Теннесси, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/10149.jpg
691	Дэниэл С. Стрипик	\N	1930-10-08	\N	Сонома Каунти, Калифорния, США	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/18643.jpg
732	Дэвид Брисбин	\N	1952-06-26	\N	США	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/22099.jpg
480	Кирк Уорд	\N	\N	\N	\N	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/22629.jpg
221	Анджела Ломас	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101671.jpg
405	Дебора МакТир	\N	1953-05-03	\N	Атланта, Джорджия, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101673.jpg
551	Аль Хэррингтон	187	1935-12-12	2021-09-21	Паго-Паго, Западное Самоа (Самоа)	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101674.jpg
251	Кеннет Бевингтон	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101676.jpg
46	Данте МакКарти	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/96171.jpg
3	Майк Джолли	194	1959-11-29	\N	США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101683.jpg
205	Джон Волдстад	173	1951-02-20	\N	Осло, Норвегия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/14650.jpg
653	Майкл Берджесс	\N	1964-08-02	\N	Миртл-Бич, Южная Каролина, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/47446.jpg
123	Стивен Гриффит	\N	\N	\N	Гринвилл, Южная Каролина, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101689.jpg
356	Билл Роберсон	\N	1958-08-21	2017-11-18	Вашингтон, Северная Каролина, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/7430.jpg
638	Стив ДеРеллиан	191	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101715.jpg
385	Стефен Бриджуотер	188	1953-08-24	\N	Хатчинсон, Канзас, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32890.jpg
396	Хилари Чеплейн	\N	1956-06-17	\N	Бостон, Массачусетс, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101695.jpg
87	Изабель Роуз	\N	\N	\N	Манхэттэн, Нью-Йорк, США	Актриса, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101696.jpg
456	Джей Росс	\N	\N	\N	\N	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101697.jpg
246	Майкл Джейс	192	1962-07-13	\N	Патерсон, Нью-Джерси, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/9560.jpg
728	Джеффри Блейк	185	1962-08-20	\N	Балтимор, Мэриленд, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2783.jpg
1	Дик Каветт	169	1936-11-19	\N	Гиббон, Небраска, США	Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/55411.jpg
335	Тиффани Салерно	\N	\N	\N	\N	Актриса, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/18437.jpg
403	Марла Сухаретса	179	1965-05-20	\N	Нью-Йорк, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/9705.jpg
490	В. Бенсон Терри	\N	1921-08-10	1998-03-24	Чикаго, Иллинойс, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101707.jpg
160	Натали Хендрикс	\N	\N	\N	\N	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/21631.jpg
93	Бобби Ричардсон	175	1935-08-19	\N	Самтер, Южная Каролина, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101713.jpg
690	Чарльз Босуэлл	\N	1945-04-28	\N	Боссьер Сити, Луизиана, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/93060.jpg
342	Хэйли Джоэл Осмент	168	1988-04-10	\N	Лос-Анджелес, Калифорния, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/11381.jpg
578	Джозеф Эбби	175	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1330023.jpg
393	Роб Адамс	183	1970-02-27	\N	Сант-Луис, Миссури, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/15898.jpg
634	Маркус Александр	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/9650.jpg
83	Гранд Л. Буш	183	1955-12-24	\N	Лос-Анджелес, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/14948.jpg
301	Трой Кристиан	180	\N	\N	\N	Актер, Монтажер, Оператор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/68577.jpg
485	Дик Кларк	173	1929-11-30	2012-04-18	Маунт-Вернон, Нью-Йорк, США	Продюсер, Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/27729.jpg
152	Джон Коннали	\N	1917-02-27	1993-06-15	Floresville, Texas, USA	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/80414.jpg
262	Даррен В. Конрад	188	1968-02-10	\N	Гастония, Северная Каролина, США	Продюсер, Актер, Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1104436.jpg
674	Райан Дункан	175	1973-04-24	\N	Devens, Massachusetts, USA	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/476708.jpg
729	Кристофер Джеймс Холл	\N	1966-02-13	\N	Ньюкасл-апон-Тайн, Англия, Великобритания	Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/463959.jpg
632	Брайан Ханна	178	1962-11-10	\N	Лос-Анджелес, Калифорния, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1104339.jpg
188	Эллсворт Ханна	178	1923-10-15	2003-05-22	Alberhill, California, USA	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1216257.jpg
81	Джим Келлер	\N	1974-10-30	\N	Мэрилэнд, США	Режиссер, Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/101718.jpg
64	Роберт Ф. Кеннеди	175	1925-11-20	1968-06-06	Бруклайн, Массачусетс, США	Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/57428.jpg
521	Джон Леннон	\N	1940-10-09	1980-12-08	Ливерпуль, Англия, Великобритания	Актер, Композитор, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/46676.jpg
259	Ричард Никсон	\N	1913-01-09	1994-04-22	Йорба-Линда, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/28084.jpg
620	Шоун Майкл Перри	178	1966-04-27	\N	Сан-Диего, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/95251.jpg
639	Мэри Эллен Трейнор	170	1952-07-08	2015-05-20	Чикаго, Иллинойс, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/4378.jpg
455	Саймон Брайт	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2004690.jpg
423	Эрик Алан Уенделл	171	1973-06-12	\N	Вилинг, Западная Вирджиния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1482276.jpg
700	Чжуан Цзэдун	\N	1940-08-25	2013-02-10	Янчжоу, Цзянсу, Китай	\N	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/469587.jpg
149	Валерий Парфенов	\N	1984-02-06	\N	Москва, СССР (Россия)	Озвучка, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6413583.jpg
299	Эрик Рот	178	1945-03-22	\N	Нью-Йорк, США	Сценарист, Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/512.jpg
573	Дон Бёрджесс	\N	1956-05-28	\N	Санта-Моника, Калифорния, США	Оператор, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/85510.jpg
50	Алан Сильвестри	187	1950-03-26	\N	Нью-Йорк, США	Композитор, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/553348.jpg
384	Рик Картер	\N	\N	\N	Лос-Анджелес, Калифорния, США	Художник-постановщик, Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1074532.jpg
705	Артур Шмидт	\N	1937-06-17	\N	Лос-Анджелес, Калифорния, США	Монтажер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/722591.jpg
168	Питер Джексон	165	1961-10-31	\N	Пукеруа Бэй, Новая Зеландия	Продюсер, Режиссер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32383.jpg
247	Элайджа Вуд	165	1981-01-28	\N	Сидар-Рапидс, Айова, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/20287.jpg
469	Вигго Мортенсен	180	1958-10-20	\N	Манхэттэн, Нью-Йорк, США	Актер, Продюсер, Композитор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/10779.jpg
645	Шон Эстин	168	1971-02-25	\N	Санта-Моника, Калифорния, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/28426.jpg
110	Иэн Маккеллен	183	1939-05-25	\N	Бернли, Ланкашир, Англия, Великобритания	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/8215.jpg
378	Доминик Монахэн	170	1976-12-08	\N	Берлин, Германия	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/23240.jpg
317	Бернард Хилл	175	1944-12-17	\N	Манчестер, Англия, Великобритания	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/13143.jpg
532	Дэвид Уэнэм	178	1965-09-21	\N	Марриквилл, Новый Южный Уэльс, Австралия	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1492.jpg
477	Дэвид Эстон	\N	\N	\N	Окленд, Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/23337.jpg
338	Сала Бэйкер	188	1976-09-22	\N	Уэллингтон, Новая Зеландия	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32390.jpg
286	Кристофер Ли	196	1922-05-27	2015-06-07	Белгравия, Лондон, Англия, Великобритания	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/21465.jpg
229	Джейсон Фитч	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/475883.jpg
565	Брэт МакКензи	183	1976-06-29	\N	Веллингтон, Новая Зеландия	Актер, Композитор, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/51682.jpg
670	Мэйси МакЛеод-Риера	170	1999-05-25	\N	Уэллингтон, Новая Зеландия	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/475884.jpg
397	Джоэль Тобек	185	1971-06-02	\N	Оклэнд, Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/99484.jpg
459	Стефан Уре	172	1958-03-28	\N	Сент-Леонардс, Сидней, Новый Южный Уэльс, Австралия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32394.jpg
19	Билли Джексон	127	\N	\N	Уэллингтон, Новая Зеландия	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41164.jpg
566	Джино Асеведо	\N	1964-12-06	\N	Феникс, Аризона, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2762832.jpg
677	Эйдан Белл	183	\N	\N	Хартфордшир, Англия, Великобритания	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2324444.jpg
370	Майкл Элсворт	183	1932-06-12	\N	Великобритания	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/51651.jpg
219	Сандро Копп	191	1978-02-14	\N	Хайдельберг, Германия	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1096714.jpg
483	Крэйг Паркер	178	1970-11-12	\N	Сува, Фиджи	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41158.jpg
26	Ричард Тейлор	\N	\N	\N	\N	Актер, Художник-постановщик, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/917061.jpg
99	Джон Стефенсон	\N	1923-08-09	2015-05-15	Кеноша, Висконсин, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/54984.jpg
610	Фрэн Уолш	\N	1959-01-10	\N	Уэллингтон, Новая Зеландия	Сценарист, Продюсер, Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32385.jpg
373	Роберт Шей	183	1939-03-03	\N	Детройт, Мичиган, США	Продюсер, Актер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3023.jpg
569	Харви Вайнштейн	183	1952-03-19	\N	Флашинг, Квинс, Нью-Йорк, США	Продюсер, Актер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3205.jpg
422	Дж.Р.Р. Толкин	174	1892-01-03	1973-09-02	Блумфонтейн, Оранжевая Республика, Южная Африка	Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/32384.jpg
187	Филип Айви	\N	1969-03-12	\N	Оклэнд, Новая Зеландия	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2000618.jpg
517	Найла Диксон	\N	\N	\N	Данидин, Новая Зеландия	Художник-постановщик, Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1238318.jpg
25	Алан Ли	\N	1947-08-20	\N	Лондон, Англия, Великобритания	Художник-постановщик, Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/569830.jpg
555	Гаэль Гарсиа Берналь	170	1978-11-30	\N	Гвадалахара, Мексика	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/44234.jpg
334	Альфонсо Арау	175	1932-01-11	\N	Мехико, Мексика	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/10466.jpg
65	Ломбардо Бойяр	\N	1973-12-01	\N	Эль-Пасо, Техас, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/20214.jpg
387	Ана Офелиа Мургия	\N	1933-12-08	\N	Мехико, Мексика	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/145374.jpg
256	Эдвард Джеймс Олмос	175	1947-02-24	\N	Лос-Анджелес, Калифорния, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6582.jpg
5	Дайана Ортелли	\N	1961-05-01	\N	Nuevo Laredo, Tamaulipas, Mexico	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/12726.jpg
320	Чич Марин	168	1946-07-13	\N	Лос-Анджелес, Калифорния, США	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/12722.jpg
681	Джон Ратценбергер	177	1947-04-06	\N	Бриджпорт, Коннектикут, США	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/13821.jpg
516	Майкл Дж. Фокс	163	1961-06-09	\N	Эдмонтон, Альберта, Канада	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/181.jpg
119	Лиа Томпсон	160	1961-05-31	\N	Рочестер, Миннесота, США	Актриса, Режиссер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/77429.jpg
270	Криспин Гловер	184	1964-04-20	\N	Нью-Йорк, США	Актер, Режиссер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/3558.jpg
417	Томас Ф. Уилсон	189	1959-04-15	\N	Филадельфия, Пенсильвания, США	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/70021.jpg
281	Клаудия Уэллс	163	1966-07-05	\N	Куала-Лумпур, Малайзия	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152914.jpg
587	Гарри Уотерс мл.	\N	\N	\N	США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/76404.jpg
250	Кристен Кауффман	\N	1961-11-10	\N	США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/28317.jpg
614	Эльза Рэйвен	\N	1929-09-21	2020-11-02	Чарлстон, Южная Каролина, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/43095.jpg
738	Уилл Хэр	\N	1916-03-30	1997-08-31	Элкинс, Западная Вирджиния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/28991.jpg
371	Джейсон Марин	\N	1974-07-25	\N	Бруклин, Нью-Йорк, США	Актер, Оператор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/125647.jpg
319	Джейсон Херви	168	1972-04-06	\N	Лос-Анджелес, Калифорния, США	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/77937.jpg
209	Майа Брутон	\N	1977-09-30	\N	Лос-Анджелес, Калифорния, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/144736.jpg
741	Кортни Гейнс	180	1965-08-22	\N	Лос-Анджелес, Калифорния, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/39098.jpg
706	Ричард Л. Дюран	178	1948-07-26	2015-01-21	Лос-Анджелес, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/31656.jpg
619	Рид Морган	\N	1931-01-30	\N	Чикаго, Иллинойс, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/102506.jpg
577	Джордж «Бак» Флауэр	\N	1937-10-28	2004-06-18	Милтон-Фриуотер, Орегон, США	Актер, Сценарист, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/28593.jpg
365	Роберт ДеЛапп	\N	\N	\N	\N	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/152918.jpg
181	Кики Эбсен	175	1958-01-14	\N	Лос-Анджелес, Калифорния, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/456239.jpg
579	Хьюи Льюис	183	1950-07-05	\N	Нью-Йорк, США	Актер, Композитор	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/12592.jpg
500	Джон МакКук	184	1944-06-20	\N	Вентура, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/91904.jpg
449	Тони Поуп	\N	1947-03-22	2004-02-11	Кливленд, Огайо, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/396486.jpg
701	Этель Суэй	\N	1908-01-04	2000-07-18	Миннеаполис, Миннесота, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/209358.jpg
743	Фрэнк Маршалл	173	1946-09-13	\N	Лос-Анджелес, Калифорния, США	Продюсер, Режиссер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/20397.jpg
193	Александр Новиков	180	1959-01-14	\N	Калуга, СССР (Россия)	Озвучка, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/231213.jpg
340	Лоуренс Дж. Полл	\N	1938-04-13	2019-11-10	Чикаго, Иллинойс, США	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1712669.jpg
216	Гарри Керамидас	\N	\N	\N	Детройт, Мичиган, США	Монтажер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/886881.jpg
278	Кристофер Нолан	181	1970-07-30	\N	Лондон, Англия, Великобритания	Сценарист, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/41477.jpg
595	Энн Хэтэуэй	173	1982-11-12	\N	Бруклин, Нью-Йорк, США	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/38703.jpg
108	Джессика Честейн	163	1977-03-24	\N	Сакраменто, Калифорния, США	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1111242.jpg
6	Маккензи Фой	159	2000-11-10	\N	Лос-Анджелес, Калифорния, США	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2007922.jpg
79	Мэтт Дэймон	178	1970-10-08	\N	Бостон, Массачусетс, США	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/6458.jpg
242	Тофер Грейс	180	1978-07-12	\N	Нью-Йорк, США	Актер, Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/30099.jpg
612	Уильям Дивэйн	178	1937-09-05	\N	Олбани, Нью-Йорк, США	Актер, Сценарист, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/11971.jpg
528	Джош Стюарт	175	1977-02-06	\N	Диана, Западная Вирджиния, США	Актер, Продюсер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/546863.jpg
145	Леа Кейрнс	165	1974-06-02	\N	Северный Ванкувер, Британская Колумбия, Канада.	Актриса	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/547568.jpg
434	Райан Ирвинг	185	\N	\N	\N	Актер, Режиссер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2908355.jpg
375	Дерек Макинтайр	180	1988-08-30	2020-09-20	Апл-Велли, Калифорния, США	Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2657241.jpg
11	Стефани Фрайзер	\N	\N	\N	\N	Актриса, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1351462.jpg
217	Джордан Голдберг	\N	\N	\N	\N	Продюсер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1172899.jpg
655	Кип Торн	\N	1940-06-01	\N	Логан, Юта, США	Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/435723.jpg
452	Томас Тулл	\N	1970-06-09	\N	Эндуэлл, Нью-Йорк, США	Продюсер, Актер, Сценарист	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1074048.jpg
448	Всеволод Кузнецов	183	1970-02-25	\N	Алма-Ата, СССР (Казахстан)	Озвучка, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1616407.jpg
330	Иван Басов	\N	1984-12-01	\N	\N	Переводчик, Актер, Режиссер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/5258571.jpg
572	Ханс Циммер	178	1957-09-12	\N	Франкфурт-на-Майне, ФРГ (Германия)	Композитор, Актер, Продюсер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/50590.jpg
489	Нэйтан Краули	\N	\N	\N	Лондон, Англия, Великобритания	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1642584.jpg
590	Эггерт Кетилссон	\N	\N	\N	\N	Художник-постановщик, Продюсер, Актер	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1623430.jpg
106	Эрик Сандаль	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/2010855.jpg
237	Дин Уолкотт	\N	\N	\N	\N	Художник-постановщик	https://kinopoiskapiunofficial.tech/images/actor_posters/kp/1986015.jpg
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  3433.dat                                                                                            0000600 0004000 0002000 00000067411 14351347706 0014271 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        1	523	10	\N	61
2	731	4	Oskar Schindler	2
1	509	4	Paul Edgecomb	2
1	663	4	Cynthia Hammersmith	29
1	626	4	Jan Edgecomb	4
1	726	4	John Coffey	5
1	535	4	Warden Hal Moores	6
1	186	4	Eduard Delacroix	7
1	141	4	Arlen Bitterbuck	8
1	213	4	Percy Wetmore	9
1	114	4	«Wild Bill» Wharton	10
1	208	4	Dean Stanton	11
1	306	4	Harry Terwilliger	12
1	73	4	Bill Dodge	26
1	404	4	Toot-Toot	14
1	357	4	Old Paul Edgecomb	15
1	101	4	Elaine Connelly	16
1	675	4	Klaus Detterick	17
1	398	4	Melinda Moores	13
1	678	3	\N	63
1	538	4	Lady in Nursing Home	20
1	206	4	Marjorie Detterick	21
1	61	4	Howie Detterick (в титрах: Christopher Ives)	22
1	190	4	Kathe Detterick	23
1	137	4	Cora Detterick	24
1	487	4	Sheriff McGee	25
1	369	4	Man in Nursing Home	19
1	245	4	Jack Van Hay	27
1	298	4	Burt Hammersmith	28
1	75	3	роман	62
1	321	4	Hammersmith\\u0027s Son	30
1	692	4	Hammersmith\\u0027s Daughter	31
1	413	4	Earl the Plumber	32
1	547	4	Woman at Del\\u0027s Execution	33
1	733	4	Wife at Del\\u0027s Execution	34
1	617	4	Husband at Del\\u0027s Execution	35
1	724	4	Police Officer	36
1	697	4	actor in «Top Hat», хроника, в титрах не указан	38
1	86	4	Tower Guard, в титрах не указан	39
1	139	4	Inmate, в титрах не указан	40
1	664	4	Prison Guard, в титрах не указан	41
1	493	4	Prisoner, в титрах не указан	42
1	151	4	Prison Minister, в титрах не указан	43
1	207	4	Police Photographer, в титрах не указан	44
1	499	4	Bitterbuck\\u0027s Daughter, в титрах не указан	45
1	185	4	Coffee Execution Witness, в титрах не указан	46
1	94	4	Posse Member, в титрах не указан	47
1	290	4	Tower Guard, в титрах не указан	48
1	392	4	Prison Guard, в титрах не указан	49
1	18	4	Actress in «Top Hat», хроника, в титрах не указан	50
1	257	4	Inmate, в титрах не указан	51
1	42	4	Member of the Posse, в титрах не указан	52
1	211	4	Banker Posse Member, в титрах не указан	53
1	331	4	Inmate, в титрах не указан	54
1	140	4	Mourner, в титрах не указан	55
1	649	4	Mourner, в титрах не указан	56
1	198	4	Prison Guard, в титрах не указан	57
1	474	4	Prison Guard, в титрах не указан	58
1	420	2	продюсер (produced by)	60
1	77	7	\N	65
1	430	6	\N	67
1	304	4	Orderly Hector (в титрах: Mack C. Miles)	18
1	302	9	\N	64
1	90	8	\N	70
1	438	6	по костюмам	68
1	581	6	по декорациям	69
2	74	1	\N	1
1	27	4	Brutus «Brutal» Howell	3
1	212	6	постановщик	66
2	710	4	Itzhak Stern	3
2	522	4	Amon Goeth	4
2	624	4	Emilie Schindler	5
2	116	4	Helen Hirsch	6
2	468	4	Poldek Pfefferberg (в титрах: Jonathan Sagalle)	7
2	437	4	Wiktoria Klonowska (в титрах: Malgoscha Gebel)	8
2	121	4	Marcel Goldberg	10
2	315	4	Ingrid (в титрах: Beatrice Macola)	11
2	644	4	Julian Scherner	12
2	336	4	Rolf Czurda (в титрах: Friedrich Von Thun)	13
2	161	4	Herman Toffel	14
2	406	4	Leo John	15
2	7	4	Albert Hujar	16
2	684	4	Mila Pfefferberg	17
2	153	4	Juda Dresner	18
2	351	4	Chaja Dresner	19
2	616	4	Danka Dresner	20
2	410	4	Mordecai Wulkan	21
2	441	4	Mr. Nussbaum	22
2	275	4	Mrs. Nussbaum	23
2	598	4	Henry Rosner	24
2	713	4	Leo Rosner	26
2	583	4	Rabbi Menasha Levartov	27
2	165	4	Rebecca Tannenbaum (в титрах: Beata Nowak)	28
2	730	4	Josef Bau (в титрах: Rami Hauberger)	29
2	228	4	Investor	30
2	43	4	Investor	31
2	130	4	Chaim Nowak	32
2	294	4	O.D. / Chicken Boy	33
2	361	4	Nuisa Horowitz	34
2	362	4	Dolek Horowitz	35
2	312	4	Garage Mechanic	36
2	34	4	Red Genia	37
2	689	4	DEF Foreman	39
2	389	4	Lisiek	40
2	311	4	Diana Reiter	41
2	601	4	Irrational Woman	42
2	742	4	Regina Perlman	43
2	352	4	Investigator	44
2	273	4	Doorman	45
2	515	4	Majola	47
1	678	1	\N	1
2	69	4	Josef Liepold	49
2	687	4	Club Singer	50
2	125	4	Treblinka Commandant	52
2	567	4	Joe Louis Impersonator, в титрах не указан	147
2	297	4	Rudolph Hoss (в титрах: Hans Michael Rehberg)	53
2	377	4	Waiter (в титрах: Eugeniusz Priwiezencew)	54
2	144	4	Montelupich Colonel	55
2	100	4	SS Waffen Officer	56
2	723	4	Wilhelm Kunde	57
2	716	4	Dr. Blancke	58
2	115	4	Dr. Josef Mengele (в титрах: Daniel Del Ponte)	59
2	296	4	DEF SS Officer	60
2	686	4	SS Sgt. Kunder	61
2	418	4	DEF Guard	62
2	594	4	Auschwitz Guard	64
2	241	4	Auschwitz Guard	65
2	300	4	Brinnlitz Guard	66
2	326	4	Gestapo	67
2	506	4	Gestapo	68
2	541	4	SS Waffen Man	69
2	447	4	Gestapo Brinnlitz	70
2	711	4	SS Bureaucrat (в титрах: Gerald Alexander Held)	71
2	395	4	Ukrainian Guard	72
2	739	4	Gestapo Clerk Klaus Tauber	73
2	289	4	Border Guard	74
2	527	4	Toffel\\u0027s Secretary	75
2	349	4	Scherner\\u0027s Secretary	76
2	157	4	Czurda\\u0027s Secretary	77
2	2	4	Bosch	78
2	346	4	Goeth\\u0027s Girl	79
2	38	4	Czurda\\u0027s Girl (в титрах: Agnieszka Kruk)	80
2	244	4	Polish Girl	81
2	223	4	Brinnlitz Man	82
2	68	4	Brinnlitz Girl	83
2	28	4	Russian Officer	84
2	118	4	Plaszow Depot SS Guard	85
2	635	4	SS Guard Zablocie	86
2	428	4	SS NCO Zablocie	87
2	78	4	SS NCO - Ghetto	89
2	536	4	SS NCO - Ghetto	90
2	285	4	Ghetto Woman (в титрах: Ethel Szyc)	91
2	603	4	Ghetto Woman	92
2	671	4	Old Jewish Woman	93
2	95	4	Ghetto Old Man	94
2	59	4	Prisoner at Depot	95
2	633	4	Clerk at Depot	96
2	463	4	Black Marketeer	97
2	702	4	Black Marketeer	98
2	41	4	Black Marketeer	99
2	720	4	Ghetto Doctor	100
2	735	4	Stable Boy	102
2	662	4	Pankiewicz	103
2	261	4	Man in Pharmacy	104
2	17	4	Grun	106
2	51	4	Engineer Man	107
2	416	4	Clara Sternberg	108
2	33	4	Maria Mischel	109
2	559	4	Ghetto Girl	110
2	24	4	Ghetto Girl	111
2	465	4	Ghetto Girl	112
2	80	4	Ghetto Man	113
2	631	4	Ghetto Man	114
2	40	4	Brinnlitz Priest (в титрах: Edward Linde Lubaszenko)	115
2	382	4	Montelupich Prisoner	116
2	708	4	Depot Master (в титрах: Goerges Kern)	117
2	359	4	Plaszow SS Guard	118
2	712	4	Plaszow SS Guard	119
2	544	4	Plaszow SS Guard	121
2	14	4	Plaszow SS Guard (в титрах: Hubert Kramer)	122
2	593	4	Plaszow Jewish Girl	123
2	479	4	Plaszow Jewish Girl (в титрах: Dorit Ady Seadia)	124
2	596	4	Plaszow Jewish Girl	125
2	47	4	Dancer, в титрах не указан	126
2	309	4	в титрах не указан	127
2	491	4	играет самого себя - Schindler Mourner, в титрах не указан	130
2	252	4	Boy, в титрах не указан	131
2	192	4	Frances Langford Impersonator, в титрах не указан	132
2	329	4	Little Jewish Boy, в титрах не указан	133
2	316	4	Woman, в титрах не указан	134
2	191	4	Young Worker, в титрах не указан	135
2	197	4	играет самого себя - Schindler Mourner, в титрах не указан	136
2	550	4	Kate Smith Impersonator, в титрах не указан	137
2	129	4	играет самого себя - Schindler Mourner, в титрах не указан	138
2	280	4	играет саму себя - Schindler Mourner, в титрах не указан	139
2	471	4	German Girl, в титрах не указан	140
2	694	4	Jewish Boy, в титрах не указан	141
2	695	4	Worker in Factory, в титрах не указан	142
2	478	2	ассоциированный продюсер	143
2	648	2	исполнительный продюсер	144
2	156	2	продюсер (produced by)	145
2	636	4	Dieter Reeder (в титрах: August Schmolzer)	48
2	36	2	продюсер (produced by)	146
2	374	2	ассоциированный продюсер	148
2	175	2	сопродюсер	149
2	67	10	\N	151
2	497	3	книга	152
2	358	3	\N	153
2	112	9	\N	154
2	284	7	\N	155
2	415	6	постановщик	156
2	44	6	по костюмам	157
2	372	8	\N	158
3	401	4	Andy Dufresne	2
3	715	4	Ellis Boyd «Red» Redding	3
3	533	4	Warden Norton	4
3	675	4	Heywood	5
3	526	4	Tommy	7
4	201	4	Mrs. Gump	4
3	260	4	Laundry Truck Driver	28
3	222	4	Ernie	15
4	292	4	Young Forrest	7
3	77	7	\N	80
3	154	6	по костюмам	83
4	164	1	\N	1
4	509	4	Forrest Gump	2
3	288	4	Brooks Hatlen	9
3	236	4	Skeet	11
3	364	4	Jigger	12
3	487	4	Floyd	13
3	673	4	Snooze	14
3	148	4	Guard Mert	16
3	380	4	Guard Trout	17
3	91	4	Glenn Quentin	19
3	641	4	1946 Judge	20
3	66	4	Fresh Fish Con	22
3	104	4	New Fish Guard	24
3	519	4	Fat Ass	25
3	304	4	Tyrell	26
3	744	4	Laundry Bob	27
3	155	4	Laundry Leonard	29
3	531	4	Rooster	30
3	366	4	Pete	31
3	656	4	Guard Youngblood	32
3	460	4	Projectionist (в титрах: Joseph Pecoraro)	33
3	502	4	Hole Guard	34
3	176	4	Guard Dekins	35
3	440	4	Guard Wiley (в титрах: Don R. McManus)	36
3	189	4	Moresby Batter (в титрах: Donald E. Zinn)	37
3	658	4	1954 Landlady	38
3	71	4	1954 Food-Way Manager	39
3	736	4	1954 Food-Way Woman	40
3	224	4	1957 Parole Hearings Man	41
3	717	4	Ned Grimes	42
3	295	4	Elmo Blatch	44
3	344	4	Elderly Hole Guard	45
3	63	4	Bullhorn Tower Guard	46
3	411	4	Man Missing Guard	47
3	143	4	Head Bull Haig	48
3	322	4	Bank Teller	49
3	266	4	Bank Manager	50
3	412	4	Bugle Editor	51
3	283	4	1966 D.A.	52
3	150	4	Duty Guard	53
3	688	4	1967 Parole Hearings Man	54
3	183	4	1967 Food-Way Manager	55
3	661	4	Old Man on Bus, в титрах не указан	57
3	529	4	Police Officer, в титрах не указан	58
3	409	4	Con, в титрах не указан	59
3	525	4	Gilda Mundson Farrell, хроника, в титрах не указан	60
3	202	4	Bank Teller, в титрах не указан	61
3	589	4	Inmate, в титрах не указан	62
3	239	4	Convict, в титрах не указан	63
3	494	4	Inmate II, в титрах не указан	64
3	146	4	Con, в титрах не указан	65
3	391	4	Ballin Mundson, хроника, в титрах не указан	66
3	606	4	Traffic (driver), в титрах не указан	67
3	473	4	Con, в титрах не указан	68
3	630	4	New Fish Con, в титрах не указан	70
3	23	2	исполнительный продюсер	71
3	475	2	исполнительный продюсер	72
3	127	2	продюсер (produced by)	73
3	307	10	Sony Turbo	74
3	279	10	IVI	75
3	703	5	IVI	76
3	75	3	роман	77
3	248	4	Bogs Diamond	8
3	451	9	\N	79
3	424	6	\N	82
3	581	6	по декорациям	84
3	90	8	\N	85
4	481	4	Jenny Curran	3
4	298	4	Lieutenant Dan Taylor	5
4	49	4	Bubba Blue	6
4	683	4	Young Jenny Curran (в титрах: Hanna R. Hall)	8
4	58	4	Principal	9
4	120	4	Nurse at Park Bench	11
4	204	4	Doctor (в титрах: Harold Herthum)	12
4	719	4	Barber	13
4	628	4	Crony	14
4	588	4	Crony	15
4	255	4	Louise	16
4	607	4	Elderly Woman	17
4	10	4	Elderly Woman\\u0027s Daughter	18
4	419	4	Southern Gentleman / Landowner	19
4	530	4	Young Elvis Presley	20
4	698	4	School Bus Boy	21
4	368	4	School Bus Boy	22
4	82	4	School Bus Boy	23
4	640	4	School Bus Girl	24
4	602	4	Red Headed Boy	25
4	184	4	Boy with Cross	26
4	498	4	Fat Boy	27
4	333	4	Jenny\\u0027s Father	28
4	504	4	Jenny\\u0027s Grandmother	29
4	604	4	Police Chief	30
4	13	4	Red Headed Teen	31
4	20	4	Fat Teen	32
4	539	4	College Football Coach	34
4	103	4	High School Football Coach	35
4	249	4	High School Football Coach	36
4	691	4	Recruiter (в титрах: Daniel Striepeke)	37
4	172	4	Kick Off Return Player	38
4	732	4	Newscaster	39
4	480	4	Earl	40
4	221	4	Black Student	41
4	405	4	Woman with Child on Park Bench	43
4	240	4	Jenny\\u0027s Date	44
4	551	4	Local Anchor #1	45
4	97	4	President Kennedy, озвучка	46
4	436	4	University Dean	47
4	446	4	Army Recruiter	48
4	251	4	Army Bus Driver	49
4	659	4	Bus Recruit	50
4	582	4	Bus Recruit	51
4	558	4	Barracks Recruit	55
4	46	4	Topless Girl	56
4	167	4	Emcee	57
4	35	4	Emcee	58
4	3	4	Club Patron	59
4	574	4	Club Patron	60
4	205	4	Club Patron	61
4	652	4	Club Patron	62
4	571	4	Pick-up Truck Driver	63
4	303	4	Helicopter Gunman	64
4	117	4	Dallas	66
4	653	4	Cleveland	67
4	123	4	Tex	68
4	356	4	Fat Man at Bench	69
4	231	4	Army Hospital Male Nurse	70
4	722	4	Mail Call Soldier	71
4	308	4	Wounded Soldier	73
4	385	4	Hospital Officer (в титрах: Stephen Wesley Bridgewater)	74
4	444	4	Army Nurse	75
4	433	4	National Correspondent #1	76
4	107	4	President Johnson, озвучка	77
4	396	4	Hilary	78
4	87	4	Isabel	79
4	456	4	Veteran at War Rally	80
4	72	4	Abbie Hoffman	81
4	76	4	Policeman at War Rally	82
4	214	4	Black Panther	83
4	246	4	Black Panther	84
4	728	4	Wesley	85
4	394	4	Hippie at Commune	86
4	60	4	Hollywood Boulevard Girlfriend	87
4	339	4	Hollywood Boulevard Girlfriend	88
4	435	4	Man in VW Bug	89
4	1	4	Dick Cavett	90
4	335	4	Carla	92
4	403	4	Lenore	93
4	443	4	Musician Boyfriend	94
4	54	4	National Correspondent #4	95
4	457	4	President Nixon, озвучка	96
4	109	4	Discharge Officer	97
4	490	4	Stanley Loomis	98
4	615	4	Drugged Out Boyfriend	99
4	218	4	Local Correspondent #2	100
4	467	4	Church Choir	101
4	178	4	Elderly Southern Woman	103
4	160	4	Local Anchor #3 (в титрах: Nathalie Hendrix)	104
4	324	4	Waitress in Cafe	105
4	646	4	Hannibal Reporter	106
4	685	4	Hannibal Reporter	107
4	93	4	Hannibal Reporter	108
4	429	4	Taxi Driver	109
4	111	4	Young Man Running	110
4	690	4	Aging Hippie	111
4	342	4	Forrest Junior	113
4	96	4	The Minister	114
4	737	4	Lieutenant Dan\\u0027s Fiancée	115
4	578	4	Protestor on Stage, в титрах не указан	116
4	393	4	College Quarterback, в титрах не указан	117
4	634	4	Vietnam Veteran, в титрах не указан	118
4	709	4	Hippie, в титрах не указан	119
4	83	4	Black Panther Leader, в титрах не указан	122
4	651	4	Football Fan, в титрах не указан	123
4	301	4	Cheerleader, в титрах не указан	124
4	485	4	играет самого себя - New Year\\u0027s Eve, хроника, в титрах не указан	125
4	152	4	играет самого себя - with JFK in Dallas, хроника, в титрах не указан	126
4	262	4	Alabama Football Player, в титрах не указан	127
4	407	4	CBS Correspondent Cooper, в титрах не указан	128
4	674	4	Student Protester, в титрах не указан	129
4	618	4	Alabama College Photographer #1, в титрах не указан	130
4	174	4	играет самого себя - Assassination Attempt, хроника, в титрах не указан	131
4	267	4	Reporter, в титрах не указан	132
4	729	4	Governor, в титрах не указан	133
4	632	4	Football Coach, в титрах не указан	134
4	188	4	The Honeymooners, в титрах не указан	135
4	98	4	The Honeymooners, в титрах не указан	136
4	622	4	Barracks Recruit, в титрах не указан	137
4	668	4	Wounded Soldier, в титрах не указан	138
4	124	4	в титрах не указан	140
4	81	4	Hippie at Commune, в титрах не указан	141
4	64	4	играет самого себя - after 1968 California Primary, хроника, в титрах не указан	142
4	466	4	National Guardsman, в титрах не указан	143
4	600	4	Black Panther, в титрах не указан	144
4	521	4	играет самого себя, хроника, в титрах не указан	145
4	545	4	Football Fan, в титрах не указан	146
4	259	4	играет самого себя, хроника, в титрах не указан	147
4	133	4	Chorister, в титрах не указан	148
4	554	4	Red Headed Teen, в титрах не указан	149
4	620	4	в титрах не указан	150
4	376	4	играет самого себя, хроника, в титрах не указан	151
4	643	4	играет самого себя - Assassination Attempt, хроника, в титрах не указан	152
4	293	4	VA in Hospital, в титрах не указан	153
4	381	4	Elvis Presley, озвучка, в титрах не указан	154
4	584	4	Football Player, в титрах не указан	155
5	25	6	по декорациям	104
4	62	4	Drill Sergeant	54
4	647	4	Nicholas Katzenbach, в титрах не указан	158
4	639	4	Jenny\\u0027s Babysitter, в титрах не указан	159
4	16	4	Alabama Marching Band, в титрах не указан	160
4	470	4	играет самого себя, хроника, в титрах не указан	161
4	423	4	Student Protester, в титрах не указан	162
4	511	2	\N	164
4	32	2	\N	165
4	414	2	\N	166
4	318	2	сопродюсер	167
4	149	10	\N	168
4	134	3	роман	170
4	299	3	\N	171
4	573	9	\N	172
4	50	7	\N	173
5	168	1	\N	1
4	592	6	\N	175
4	136	6	\N	176
4	540	6	по костюмам	177
4	347	6	по декорациям	178
4	705	8	\N	179
4	48	4	Higgins - Protestor on Stage, в титрах не указан	157
5	269	4	Corsair of Umbar, в титрах не указан	67
5	26	4	Corsair of Umbar, в титрах не указан	74
5	560	6	постановщик	95
5	469	4	Aragorn	3
5	645	4	Sam	4
5	110	4	Gandalf	5
5	514	4	Legolas	6
5	378	4	Merry	7
5	518	4	Pippin	8
5	264	4	Gollum / Smeagol	9
5	462	4	Eowyn	10
5	317	4	Theoden	11
5	92	4	Eomer	12
5	532	4	Faramir	13
5	425	4	Elrond	14
5	268	4	Galadriel	15
5	226	4	Arwen	16
5	657	4	Bilbo	17
5	235	4	Denethor	18
5	543	4	Gimli	19
5	477	4	Gondorian Soldier 3	20
5	727	4	Damrod	21
5	679	4	Celeborn	22
5	282	4	Gamling	23
5	707	4	Witchking / Gothmog	24
5	350	4	King of the Dead	25
5	88	4	Grimbold	26
5	29	4	Deagol	27
5	85	4	Voice of the Ring, озвучка	28
5	338	4	Featured Orc	29
5	159	4	Wormtongue (extended edition)	30
5	286	4	Saruman (extended edition)	31
5	233	4	Black Lieutenant (extended edition)	32
5	524	4	Everard Proudfoot	33
5	495	4	Madril	35
5	360	4	Boromir	36
5	553	4	Eldarion	37
5	195	4	Gondorian Soldier 1	38
5	229	4	Uruk 2	39
5	445	4	Irolas	40
5	565	4	Elf Escort	41
5	426	4	Rosie Cotton	42
5	670	4	Baby Gamgee (в титрах: Maisie McLeod-Riera)	43
5	8	4	Harad Leader 2	44
5	179	4	Harad Leader 1	45
5	667	4	Isildur	46
5	453	4	Shagrat	47
5	397	4	Orc Lieutenant 1 (в титрах: Joel Tolbeck)	48
5	459	4	Gorbag	49
5	355	4	Featured Orc	50
5	556	4	Featured Orc	51
5	126	4	Featured Orc	52
5	488	4	Featured Orc	53
5	19	4	Featured Child	55
5	564	4	Featured Child	56
5	566	4	Corsair of Umbar, в титрах не указан	58
5	677	4	Orc, в титрах не указан	59
5	408	4	Coronation Elf, в титрах не указан	60
5	57	4	Rivendell Elf, в титрах не указан	61
5	512	4	Diamond of Long Cleave, в титрах не указан	62
5	370	4	Cirdan the Shipwright, в титрах не указан	63
5	721	4	Orc Helper, в титрах не указан	64
5	219	4	Coronation Elf, в титрах не указан	66
5	610	2	\N	79
5	269	9	\N	93
5	247	4	Frodo	2
5	332	4	Pelennor Orc, в титрах не указан	69
5	483	4	Gothmog / Orc Lieutenant 1, озвучка, в титрах не указан	70
5	666	4	Corsair of Umbar / Beacon Guard, в титрах не указан	72
4	384	6	постановщик	174
5	627	4	Drinking Rohan Soldier, в титрах не указан	73
5	276	6	\N	96
5	455	6	\N	97
5	373	2	исполнительный продюсер	84
5	431	4	Orc, в титрах не указан	68
5	263	4	Gondorian Ranger, в титрах не указан	76
5	168	2	\N	77
5	99	4	Witchking, озвучка, в титрах не указан	75
5	627	7	\N	94
5	501	6	по декорациям	98
5	22	2	исполнительный продюсер	81
5	507	2	сопродюсер	82
5	650	2	сопродюсер	83
5	379	2	исполнительный продюсер	85
5	569	2	исполнительный продюсер	86
5	199	5	\N	88
5	422	3	роман	89
5	546	3	\N	91
5	56	2	исполнительный продюсер	80
5	138	2	\N	78
5	187	6	\N	99
5	128	6	\N	100
5	517	6	по костюмам	101
7	196	4	Dr. Emmett Brown	3
7	119	4	Lorraine Baines	4
7	345	6	\N	71
8	557	4	Cooper	2
6	486	3	оригинал	34
6	203	1	\N	1
6	402	3	оригинал	35
6	354	4	Miguel, озвучка	3
6	508	6	постановщик	37
6	173	9	\N	38
6	555	4	Héctor, озвучка	4
6	169	4	Mamá Imelda, озвучка	6
6	580	4	Abuelita, озвучка (в титрах: Renée Victor)	7
6	400	4	Papá, озвучка	8
6	334	4	Papá Julio, озвучка	9
6	680	4	Tío Oscar / Tío Felipe, озвучка	10
6	55	4	Clerk, озвучка	11
6	387	4	Mamá Coco, озвучка	13
6	552	4	Frida Kahlo, озвучка	14
6	177	4	Tía Rosita, озвучка	15
6	256	4	Chicharrón, озвучка	16
6	21	4	Mamá, озвучка	17
6	496	4	Departures Agent, озвучка	18
6	5	4	Tía Victoria, озвучка	19
6	343	4	Tío Berto / Don Hidalgo, озвучка	20
6	534	4	Emcee, озвучка	21
6	586	4	Security Guard, озвучка	22
6	320	4	Corrections Officer, озвучка	23
6	314	4	Arrivals Agent, озвучка	24
6	681	4	Juan Ortodoncia, озвучка	25
6	162	4	Various, в титрах не указан	27
6	52	2	ассоциированный продюсер	28
6	542	2	исполнительный продюсер	29
6	9	10	\N	30
6	388	5	\N	31
6	203	3	оригинал	32
6	45	3	оригинал	33
7	743	2	исполнительный продюсер	63
7	50	7	\N	69
6	549	7	\N	36
6	142	9	\N	39
6	699	6	\N	40
6	166	6	\N	41
6	505	8	\N	42
7	164	1	\N	1
6	402	1	\N	2
7	516	4	Marty McFly	2
7	270	4	George McFly	5
7	417	4	Biff Tannen (в титрах: Thomas F. Wilson)	6
7	281	4	Jennifer Parker	7
7	439	4	Dave McFly	8
7	287	4	Linda McFly	9
7	210	4	Sam Baines	10
7	432	4	Stella Baines	11
7	461	4	Mr. Strickland	12
7	672	4	3-D	14
7	464	4	Match	15
7	587	4	Marvin Berry	16
7	30	4	Goldie Wilson	17
7	548	4	Babs	18
7	250	4	Betty	19
7	614	4	Clocktower Lady	20
7	738	4	Pa Peabody	21
7	621	4	Ma Peabody	22
7	371	4	Sherman Peabody	23
7	585	4	Peabody Daughter	24
7	319	4	Milton Baines	25
7	741	4	Dixon	27
7	706	4	Terrorist	28
7	265	4	Terrorist Van Driver	29
7	669	4	Scooter Kid #1	30
7	575	4	Scooter Kid #2	31
7	472	4	Lou	32
7	619	4	Cop	33
7	70	4	Bystander #1	34
7	15	4	Guy #1	36
7	89	4	Girl #1	37
7	577	4	Red the Bum (в титрах: Buck Flower)	38
7	660	4	Starlighter	39
7	147	4	Starlighter	40
7	734	4	Starlighter	41
7	613	4	Starlighter	42
7	348	4	Pinhead	43
7	629	4	Pinhead	44
7	365	4	Pinhead	45
7	181	4	Band Member, в титрах не указан	47
7	725	4	TV Newscaster, в титрах не указан	49
7	705	8	\N	75
8	278	1	\N	1
7	383	4	Student, в титрах не указан	50
7	579	4	High School Band Audition Judge, в титрах не указан	51
7	500	4	Surgeon, в титрах не указан	52
7	454	4	Teenager, в титрах не указан	53
7	449	4	1985 Radio Announcer, озвучка, в титрах не указан	54
7	272	4	Marty McFly, в титрах не указан	55
7	701	4	Wilbur\\u0027s Wife, в титрах не указан	56
7	102	4	Student, в титрах не указан	57
7	243	4	Wilbur, в титрах не указан	58
7	450	4	Pedestrian in Town Square, в титрах не указан	59
7	200	2	produced by	60
7	563	2	produced by	61
7	340	6	постановщик	70
7	193	10	\N	65
7	164	3	\N	66
7	714	9	\N	68
7	182	6	по костюмам	72
7	180	6	по декорациям	73
7	216	8	\N	74
8	595	4	Brand	3
8	108	4	Murph	4
8	6	4	Murph (10 Yrs.)	5
8	135	4	Professor Brand	6
8	122	4	Romilly	7
8	313	4	Doyle	8
8	254	4	Tom	9
8	194	4	Donald	10
8	79	4	Mann	11
8	242	4	Getty	12
8	693	4	Murph (Older)	13
5	650	8	\N	103
5	625	4	Elanor Gamgee (в титрах: Alexandra Astin)	34
2	609	4	Auschwitz Guard (в титрах: Olaf Linde Lubaszenko)	63
8	105	2	\N	45
8	11	2	trainee координирующий продюсер, в титрах не указан	47
5	523	10	\N	87
7	230	4	Skinhead (в титрах: Jeffrey Jay Cohen)	13
3	31	4	Andy Dufresne\\u0027s Wife	18
2	562	4	Wilek Chilowicz (в титрах: Shmulik Levy)	9
3	274	4	Captain Hadley	6
3	212	6	постановщик	81
4	482	4	School Bus Driver (в титрах: Siobhan J. Fallon)	10
5	610	3	\N	90
5	507	4	Corsair of Umbar / Beacon Guard, в титрах не указан	71
3	678	1	\N	1
8	520	4	Tom (15 Yrs.)	15
8	4	4	School Principal	16
8	591	4	Ms. Hanley	17
8	353	4	Boots (в титрах: Francis Xavier McCarthy)	18
8	225	4	TARS, озвучка	19
8	623	4	Smith	20
8	612	4	Williams	21
8	528	4	CASE, озвучка	22
8	145	4	Lois	23
8	597	4	Coop (в титрах: Liam Dickinson)	24
8	310	4	Girl on Truck	25
8	363	4	Boy on Truck	26
8	220	4	Doctor	27
8	570	4	Nurse Practitioner	28
8	53	4	Nurse	29
8	232	4	Crew Chief	30
8	605	4	NASA Scientist, в титрах не указан	32
8	84	4	NASA Inspector, в титрах не указан	33
8	427	4	NASA Scientist, в титрах не указан	34
8	277	4	Construction Boss, в титрах не указан	35
8	434	4	Popcorn Seller, в титрах не указан	36
8	375	4	Murph\\u0027s Relative, в титрах не указан	37
8	718	4	Astronaut, в титрах не указан	38
8	158	4	Jenkins, в титрах не указан	39
8	341	4	NASA Launch Technician, в титрах не указан	40
8	386	4	Scientist, в титрах не указан	41
8	611	4	Cooper Station Technician, в титрах не указан	42
8	278	2	\N	43
8	572	7	\N	59
8	489	6	постановщик	60
8	217	2	исполнительный продюсер	48
8	253	2	линейный продюсер: iceland	49
8	113	2	исполнительный продюсер	50
8	655	2	исполнительный продюсер	51
8	452	2	исполнительный продюсер	52
8	448	10	\N	53
8	330	5	\N	54
8	484	5	\N	55
8	323	3	\N	56
8	278	3	\N	57
8	665	2	\N	44
8	676	9	\N	58
8	367	6	\N	61
8	590	6	\N	62
8	513	6	\N	63
8	682	6	\N	64
8	106	6	\N	65
8	237	6	\N	66
8	599	6	по костюмам	67
8	258	6	по декорациям	68
8	390	8	\N	69
1	678	2	продюсер (produced by)	59
2	492	4	Manci Rosner	25
2	171	4	Mr. Löwenstein	38
2	163	4	Julius Madritsch (в титрах: Hans Jorg Assmann)	46
2	156	4	Nightclub Maitre d\\u0027	51
2	291	4	SS NCO - Ghetto	88
2	271	4	Frantic Woman	101
2	503	4	NCO Plaszow (в титрах: Bartek Niebielski)	105
2	642	4	Plaszow SS Guard (в титрах: Goetz Otto)	120
2	74	2	продюсер (produced by)	150
3	306	4	1946 D.A.	10
3	328	4	1947 Parole Hearings Man (в титрах: Gordon C. Greene)	21
3	637	4	Mail Caller (в титрах: Eugene C. De Pasquale)	43
3	476	4	Con, в титрах не указан	56
3	696	4	1957 Parole Hearings Guard, в титрах не указан	69
3	678	3	\N	78
4	39	4	Teen with Cross	33
4	399	4	Black Student	42
4	704	4	Bubba\\u0027s Mother	52
4	238	4	Bubba\\u0027s Great Grandmother	53
4	458	4	Sergeant Sims	65
4	638	4	Wounded Soldier (в титрах: Stephen Derelian)	72
4	337	4	John Lennon, озвучка	91
4	215	4	Local Anchor #2	102
4	740	4	Wild Eyed Man (в титрах: Tim McNeil)	112
4	131	4	University of Alabama Assistant Football Coach, в титрах не указан	120
4	325	4	играет самого себя - in Vietnam, хроника, в титрах не указан	139
4	305	4	Black Panther, в титрах не указан	156
4	700	4	играет самого себя - Chinese Ping Pong Player, хроника, в титрах не указан	163
4	703	5	\N	169
5	561	4	Featured Orc	54
5	510	4	Orc commander (Extended Edition) (в титрах: Phillip Grieve)	57
5	168	4	Corsair Bosun, в титрах не указан	65
5	168	3	\N	92
5	26	6	по костюмам	102
6	65	4	Plaza Mariachi / Gustavo, озвучка	12
6	234	2	продюсер (produced by) (p.g.a.)	26
6	203	8	\N	43
7	209	4	Sally Baines	26
8	421	4	Administrator	14
1	227	4	Reverend at Funeral (в титрах: Reverend David E. Browning)	37
2	37	4	Irving Berlin Impersonator, в титрах не указан	128
2	442	4	играет самого себя - Schindler Mourner, в титрах не указан	129
3	654	4	Hungry Fish Con (в титрах: V.J. Foster)	23
4	576	4	играет самого себя - Shooting George Wallace, хроника, в титрах не указан	121
6	12	4	Ernesto de la Cruz, озвучка	5
7	132	4	Bystander #2	35
7	537	4	1955 Radio Announcer, озвучка, в титрах не указан	46
7	180	4	Mayor Red Thomas, в титрах не указан	48
7	648	2	исполнительный продюсер	62
7	74	2	исполнительный продюсер	64
7	563	3	\N	67
8	568	4	NASA Employee, в титрах не указан	31
8	327	2	координирующий продюсер: alberta	46
\.


                                                                                                                                                                                                                                                       3435.dat                                                                                            0000600 0004000 0002000 00000000672 14351347706 0014267 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        1	Мировая классика	Всем известные фильмы	\N
6	Избранное		1
8	легенды джаза	легендарные кайфовые фильмы времён джаз бендов и ковбоев	11
10	Нравится	Для меня	19
12	Посмотрю потом		13
13	Фильмы для вечеринок		4
9	Для всей семьи	Кино для детей и взрослых	\N
\.


                                                                      3437.dat                                                                                            0000600 0004000 0002000 00000014532 14351347706 0014271 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        6	Тайна Коко	12-летний Мигель живёт в мексиканской деревушке в семье сапожников и тайно мечтает стать музыкантом. Тайно, потому что в его семье музыка считается проклятием. Когда-то его прапрадед оставил жену, прапрабабку Мигеля, ради мечты, которая теперь не даёт спокойно жить и его праправнуку. С тех пор музыкальная тема в семье стала табу. Мигель обнаруживает, что между ним и его любимым певцом Эрнесто де ла Крусом, ныне покойным, существует некая связь. Паренёк отправляется к своему кумиру в Страну Мёртвых, где встречает души предков. Мигель знакомится там с духом-скелетом по имени Гектор, который становится его проводником. Вдвоём они отправляются на поиски де ла Круса.	175000000	807082196	01:45:00	0.00	https://kinopoiskapiunofficial.tech/images/posters/kp/679486.jpg	2017-10-20
8	Интерстеллар	Когда засуха, пыльные бури и вымирание растений приводят человечество к продовольственному кризису, коллектив исследователей и учёных отправляется сквозь червоточину (которая предположительно соединяет области пространства-времени через большое расстояние) в путешествие, чтобы превзойти прежние ограничения для космических путешествий человека и найти планету с подходящими для человечества условиями.	165000000	677896797	02:49:00	8.75	https://kinopoiskapiunofficial.tech/images/posters/kp/258687.jpg	2014-10-26
7	Назад в будущее	Подросток Марти с помощью машины времени, сооружённой его другом-профессором доком Брауном, попадает из 80-х в далекие 50-е. Там он встречается со своими будущими родителями, ещё подростками, и другом-профессором, совсем молодым.	19000000	381109762	01:56:00	9.00	https://kinopoiskapiunofficial.tech/images/posters/kp/476.jpg	1985-07-03
5	Властелин колец: Возвращение короля	Повелитель сил тьмы Саурон направляет свою бесчисленную армию под стены Минас-Тирита, крепости Последней Надежды. Он предвкушает близкую победу, но именно это мешает ему заметить две крохотные фигурки — хоббитов, приближающихся к Роковой Горе, где им предстоит уничтожить Кольцо Всевластья.	94000000	1140682011	03:21:00	8.00	https://kinopoiskapiunofficial.tech/images/posters/kp/3498.jpg	2003-12-01
2	Список Шиндлера	Фильм рассказывает реальную историю загадочного Оскара Шиндлера, члена нацистской партии, преуспевающего фабриканта, спасшего во время Второй мировой войны почти 1200 евреев.	22000000	321306305	03:15:00	7.50	https://kinopoiskapiunofficial.tech/images/posters/kp/329.jpg	1993-11-30
3	Побег из Шоушенка	Бухгалтер Энди Дюфрейн обвинён в убийстве собственной жены и её любовника. Оказавшись в тюрьме под названием Шоушенк, он сталкивается с жестокостью и беззаконием, царящими по обе стороны решётки. Каждый, кто попадает в эти стены, становится их рабом до конца жизни. Но Энди, обладающий живым умом и доброй душой, находит подход как к заключённым, так и к охранникам, добиваясь их особого к себе расположения.	25000000	28418687	02:22:00	9.00	https://kinopoiskapiunofficial.tech/images/posters/kp/326.jpg	1994-09-10
1	Зеленая миля	Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора», каждый из узников которого однажды проходит «зеленую милю» по пути к месту казни. Пол повидал много заключённых и надзирателей за время работы. Однако гигант Джон Коффи, обвинённый в страшном преступлении, стал одним из самых необычных обитателей блока.	60000000	286801374	03:09:00	8.25	https://kinopoiskapiunofficial.tech/images/posters/kp/435.jpg	1999-12-06
4	Форрест Гамп	Сидя на автобусной остановке, Форрест Гамп — не очень умный, но добрый и открытый парень — рассказывает случайным встречным историю своей необыкновенной жизни.\r\n\r\nС самого малолетства парень страдал от заболевания ног, соседские мальчишки дразнили его, но в один прекрасный день Форрест открыл в себе невероятные способности к бегу. Подруга детства Дженни всегда его поддерживала и защищала, но вскоре дороги их разошлись.	55000000	677387716	02:22:00	8.00	https://kinopoiskapiunofficial.tech/images/posters/kp/448.jpg	1994-06-23
\.


                                                                                                                                                                      3438.dat                                                                                            0000600 0004000 0002000 00000000102 14351347706 0014256 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        7	1	1
2	1	2
1	1	3
5	1	4
7	6	1
2	8	1
8	6	2
6	9	1
8	10	1
7	9	2
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                              3439.dat                                                                                            0000600 0004000 0002000 00000000061 14351347706 0014263 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        1	4
2	4
3	4
4	4
5	4
5	6
6	4
7	5
8	4
8	5
8	7
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                               3440.dat                                                                                            0000600 0004000 0002000 00000000221 14351347706 0014251 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        1	Албания
2	Армения
3	Австралия
4	США
5	Канада
6	Новая зеландия
7	Великобритания
\.


                                                                                                                                                                                                                                                                                                                                                                               3442.dat                                                                                            0000600 0004000 0002000 00000001133 14351347706 0014256 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        2	Комедия
3	Мультфильм
4	Ужасы
5	Фантастика
6	Триллер
7	Боевик
8	Мелодрама
9	Детектив
10	Приключения
11	Фэнтези
12	Военный
13	Семейный
14	Аниме
15	Исторический
16	Драма
17	Документальный
18	Детский
19	Криминал
20	Биография
21	Вестерн
22	Нуар
23	Спортивный
24	Короткометражный
25	Музыкальный
26	Мюзикл
27	Игровой
28	Ток-шоу
1	Экспериментальный
30	Сериал
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                     3443.dat                                                                                            0000600 0004000 0002000 00000000212 14351347706 0014254 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        1	16
1	19
1	11
2	16
2	20
2	12
2	15
3	16
4	16
4	8
4	2
4	12
4	15
5	16
5	10
5	11
6	10
6	11
6	2
6	25
6	3
6	13
7	5
7	10
7	2
8	16
8	5
8	10
\.


                                                                                                                                                                                                                                                                                                                                                                                      3445.dat                                                                                            0000600 0004000 0002000 00000000541 14351347706 0014263 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        2	Привет! \nНаше приложение теперь работает!\nМы ждём Ваших оценок и рецензий на фильмы и надеемся, что Вам понравится пользоваться нашим сервисом!	Начало работы MovieStash	2022-12-23	4	https://i.imgur.com/fuNSKY0.jpg
\.


                                                                                                                                                               3455.dat                                                                                            0000600 0004000 0002000 00000000070 14351347706 0014261 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        1	Плохо
2	Нейтрально
3	Хорошо
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                        3447.dat                                                                                            0000600 0004000 0002000 00000025455 14351347706 0014300 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        18	Спасибо за детство	2022-12-20	1	1	Легенда	3
19	классные ковры морские дыры и кратеры, всё это могло бы быть нашим достоянием. но в результате третьей анимешной потасовки мы вынуждены только слушать о свершениях будущего.	2022-12-20	7	11	на будущем классно	2
20	Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Pretium vulputate sapien nec sagittis aliquam malesuada bibendum arcu. Enim lobortis scelerisque fermentum dui. Mollis aliquam ut porttitor leo a diam sollicitudin tempor id. Mattis pellentesque id nibh tortor. Etiam erat velit scelerisque in dictum non. Bibendum est ultricies integer quis auctor elit. Tortor at risus viverra adipiscing at in tellus integer feugiat. Nulla at volutpat diam ut. Integer enim neque volutpat ac tincidunt vitae semper quis. Etiam sit amet nisl purus in mollis nunc sed id. Est ultricies integer quis auctor. Fringilla est ullamcorper eget nulla facilisi etiam dignissim diam. Eu feugiat pretium nibh ipsum. Consectetur purus ut faucibus pulvinar elementum integer enim. Cursus turpis massa tincidunt dui ut ornare lectus. Non quam lacus suspendisse faucibus interdum posuere lorem ipsum. Eu ultrices vitae auctor eu. Eget nunc lobortis mattis aliquam faucibus purus in massa.\n\nPotenti nullam ac tortor vitae purus faucibus ornare. Nisl condimentum id venenatis a condimentum vitae. Eget felis eget nunc lobortis mattis aliquam faucibus. Luctus accumsan tortor posuere ac. Bibendum enim facilisis gravida neque convallis a. Dignissim cras tincidunt lobortis feugiat vivamus at. Id cursus metus aliquam eleifend mi in nulla posuere sollicitudin. Mauris pharetra et ultrices neque ornare aenean euismod elementum nisi. Bibendum neque egestas congue quisque egestas diam in arcu. Faucibus interdum posuere lorem ipsum dolor sit amet consectetur adipiscing. Quis enim lobortis scelerisque fermentum dui faucibus in ornare quam. Ut diam quam nulla porttitor massa. Volutpat maecenas volutpat blandit aliquam etiam erat velit scelerisque. Mi eget mauris pharetra et ultrices neque ornare aenean euismod. Molestie a iaculis at erat pellentesque adipiscing. Sit amet facilisis magna etiam tempor orci. Odio eu feugiat pretium nibh ipsum consequat nisl vel. Ornare arcu dui vivamus arcu felis bibendum ut tristique.\n\nLorem ipsum dolor sit amet consectetur adipiscing elit ut. Dis parturient montes nascetur ridiculus. Commodo sed egestas egestas fringilla phasellus faucibus. Lectus sit amet est placerat. Sed vulputate mi sit amet mauris commodo. Sed adipiscing diam donec adipiscing tristique. Netus et malesuada fames ac turpis. Phasellus egestas tellus rutrum tellus pellentesque eu tincidunt tortor. Enim sit amet venenatis urna cursus eget nunc. Dictum at tempor commodo ullamcorper a lacus vestibulum sed.\n\nQuisque non tellus orci ac auctor. Massa vitae tortor condimentum lacinia quis vel eros donec ac. Aliquam sem et tortor consequat id porta nibh venenatis cras. Pharetra convallis posuere morbi leo urna molestie at. In eu mi bibendum neque egestas congue quisque. Tempor orci eu lobortis elementum nibh tellus molestie nunc. Sociis natoque penatibus et magnis dis. Commodo sed egestas egestas fringilla phasellus faucibus scelerisque eleifend donec. Hendrerit gravida rutrum quisque non. Lectus arcu bibendum at varius vel pharetra vel. Urna porttitor rhoncus dolor purus non enim praesent elementum.\n\nTurpis massa tincidunt dui ut ornare lectus sit amet est. Elementum integer enim neque volutpat ac tincidunt vitae semper. Massa tincidunt nunc pulvinar sapien et ligula ullamcorper malesuada proin. Turpis egestas maecenas pharetra convallis posuere morbi. Lectus arcu bibendum at varius vel pharetra. Ultrices dui sapien eget mi. Nibh sit amet commodo nulla facilisi nullam vehicula. Tortor dignissim convallis aenean et tortor. Elementum tempus egestas sed sed risus. Aenean vel elit scelerisque mauris pellentesque. Ultrices mi tempus imperdiet nulla malesuada pellentesque elit eget gravida. In hendrerit gravida rutrum quisque. Pretium nibh ipsum consequat nisl vel. Magnis dis parturient montes nascetur ridiculus mus mauris. Dui id ornare arcu odio ut sem nulla pharetra. Lorem ipsum dolor sit amet consectetur adipiscing elit pellentesque habitant. Vitae turpis massa sed elementum tempus egestas. Commodo nulla facilisi nullam vehicula ipsum a arcu cursus vitae.\n\nVulputate dignissim suspendisse in est ante in nibh mauris. Venenatis tellus in metus vulputate eu scelerisque felis. Cras fermentum odio eu feugiat pretium nibh. Facilisis leo vel fringilla est ullamcorper eget nulla facilisi etiam. Enim ut tellus elementum sagittis vitae et leo duis. At auctor urna nunc id cursus metus aliquam. At augue eget arcu dictum. Aliquam sem et tortor consequat id porta. Sed arcu non odio euismod lacinia at quis risus sed. Egestas quis ipsum suspendisse ultrices gravida. Suspendisse in est ante in nibh mauris cursus.\n\nRidiculus mus mauris vitae ultricies leo integer. Consequat id porta nibh venenatis cras sed. Ultrices sagittis orci a scelerisque purus semper eget duis. Porttitor massa id neque aliquam vestibulum morbi blandit. Pulvinar mattis nunc sed blandit. Tincidunt tortor aliquam nulla facilisi. Dolor sed viverra ipsum nunc aliquet bibendum. Convallis aenean et tortor at risus viverra adipiscing at. Euismod quis viverra nibh cras pulvinar mattis nunc sed blandit. Vitae aliquet nec ullamcorper sit amet risus. Et leo duis ut diam quam nulla porttitor. Elit pellentesque habitant morbi tristique senectus.\n\nAliquam ultrices sagittis orci a scelerisque. Arcu dui vivamus arcu felis bibendum. Faucibus et molestie ac feugiat sed lectus vestibulum mattis. Diam volutpat commodo sed egestas. Id donec ultrices tincidunt arcu non sodales. Euismod elementum nisi quis eleifend quam adipiscing vitae proin. Mattis vulputate enim nulla aliquet porttitor lacus luctus accumsan tortor. Aliquam malesuada bibendum arcu vitae elementum curabitur vitae. Faucibus pulvinar elementum integer enim neque volutpat. Nulla facilisi cras fermentum odio eu feugiat. Purus in mollis nunc sed. Tristique sollicitudin nibh sit amet commodo nulla facilisi nullam. Vestibulum mattis ullamcorper velit sed ullamcorper.\n\nMauris commodo quis imperdiet massa tincidunt nunc pulvinar sapien et. Neque vitae tempus quam pellentesque nec. Volutpat blandit aliquam etiam erat velit scelerisque in dictum. Tristique senectus et netus et malesuada. Sed tempus urna et pharetra pharetra massa massa ultricies. Blandit volutpat maecenas volutpat blandit aliquam etiam erat velit. Hac habitasse platea dictumst quisque. Vitae turpis massa sed elementum tempus egestas sed. Velit euismod in pellentesque massa. Quam pellentesque nec nam aliquam sem et.\n\nSemper quis lectus nulla at volutpat diam ut venenatis. Et tortor consequat id porta nibh venenatis cras. Elit duis tristique sollicitudin nibh sit amet commodo. Quam pellentesque nec nam aliquam. Suspendisse ultrices gravida dictum fusce ut. Nulla malesuada pellentesque elit eget. A pellentesque sit amet porttitor eget dolor morbi non. Ac auctor augue mauris augue neque gravida in. Facilisis magna etiam tempor orci eu lobortis elementum nibh. Tempus imperdiet nulla malesuada pellentesque elit eget gravida cum sociis. Egestas erat imperdiet sed euismod nisi porta. Amet nisl purus in mollis. Eget nullam non nisi est sit amet facilisis magna etiam. Bibendum neque egestas congue quisque. Ullamcorper velit sed ullamcorper morbi. Amet luctus venenatis lectus magna fringilla urna. Mattis vulputate enim nulla aliquet porttitor lacus luctus accumsan. A iaculis at erat pellentesque adipiscing.	2022-12-20	8	5	Спам обзор	1
21	Не смотрел :(	2022-12-20	1	19	Хороший фильм	3
26	Норм	2022-12-22	2	20	Понравился фильм	3
27	Нормально	2022-12-22	8	20	Хорошо	1
28	Интересный	2022-12-23	2	19	Хороший фильм	3
11	Нолан гений 🧐🤯	2022-12-19	8	1	Отличный фильм	3
29	Кристофер Нолан, как никто другой умеет удивлять. Я не шибко люблю фильмы про космос и первооткрывателей, но этот один из любимых!\n\nВы и без меня знаете, насколько красиво снят этот фильм, но это только благодаря тщательной подготовке. Нолан проделывает огромный труд для того, чтобы годами углубляться в изучение деталей, прежде чем снимает свои шедевры. В этом он неподражаем.\n\nЯ для себя каждый раз, как в первый, отмечаю новые смыслы в картине: это же как огромный сборник самых потрясающих философских произведений о космосе и путешествиям в неём.\n\nЧеловечество изо дня в день находится в поиске и изучении новых благоприятных условий, но только не на Земле, а изучая новые пространства. При этом в «Интерстелларе» затронута тема общественности, где раскрываются обычные земные проблемы.\n\nБезгранично много снято кинокартин о космосе, но уровень этой-непостижим! Музыка, которая отправляет в прямом смысле в космос. И реалистично снять графику до малейших деталей-это ж надо постараться!\n\nСюжет совершенно индивидуален и неповторим. Главный герой Купер из глубинки, где живет со своей семьей и ведет хозяйство (что в целом и заставило его изучать новые места из-за засухи), совершает невероятное путешествие во благо всего населения мира. Его любовь к семье и людям чувствуется через экран и придаёт особых ощущений.\n\nПосле просмотра каждый раз остаются неизгладимые впечатления, а самое главное, что фильм лишний раз дарит надежду, что в сложном и утопающем современном мире, еще есть шанс на спасение.	2022-12-23	8	4	Фантастически красиво	3
\.


                                                                                                                                                                                                                   3453.dat                                                                                            0000600 0004000 0002000 00000000327 14351347706 0014264 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        1	Режиссер
2	Продюсер
3	Сценарист
6	Художник
7	Композитор
8	Монтажер
9	Оператор
4	Актер
5	Переводчик
10	Режиссер дубляжа
\.


                                                                                                                                                                                                                                                                                                         3449.dat                                                                                            0000600 0004000 0002000 00000001136 14351347706 0014270 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        13	err404	email@example.com	f	\N	\N	anonymous
15	arder	vadim6@ya.ru	f	\N	\N	Ardithel
16	sampleNick	email@gmail.com	f	\N	\N	nweuser
4	Герман	german1828@example.org	f	\N	\N	german18
14	Emus	example@email.com	f	\N	\N	emus
20	AlModerator	mbthf@mail.ru	f	\N	\N	flmod
5	Аноним	anonymous@bad.com	t	2022-12-22	Спам	BadUser
21	zoom	myemail@email.com	f	\N	\N	Zoomiks
19	Alexander	kristnagaeva@gmail.com	f	\N	\N	flame
1	Alexey	SharpRainbow@yandex.ru	f	\N	\N	SharpRainbow
11	николай	nickolay@mail.ru	t	2022-12-23	Спам	николай
22	MovieEnjoyer	critic@email.com	f	\N	\N	filmenjoyer
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                  3451.dat                                                                                            0000600 0004000 0002000 00000000244 14351347706 0014260 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        30	2	11	7
31	8	11	10
37	3	11	10
38	1	11	5
39	7	14	8
40	1	14	8
41	7	1	10
29	8	1	7
42	8	5	8
44	3	4	8
45	1	1	10
47	1	20	10
48	4	4	8
49	5	4	8
50	2	20	8
56	8	22	10
\.


                                                                                                                                                                                                                                                                                                                                                            restore.sql                                                                                         0000600 0004000 0002000 00000247024 14351347706 0015407 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        --
-- NOTE:
--
-- File paths need to be edited. Search for $$PATH$$ and
-- replace it with the path to the directory containing
-- the extracted data files.
--
--
-- PostgreSQL database dump
--

-- Dumped from database version 15.0
-- Dumped by pg_dump version 15.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE cinema_u49e;
--
-- Name: cinema_u49e; Type: DATABASE; Schema: -; Owner: mirea_4dmin
--

CREATE DATABASE cinema_u49e WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.UTF8';


ALTER DATABASE cinema_u49e OWNER TO mirea_4dmin;

\connect cinema_u49e

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: cinema_u49e; Type: DATABASE PROPERTIES; Schema: -; Owner: mirea_4dmin
--

ALTER DATABASE cinema_u49e SET "TimeZone" TO 'utc';


\connect cinema_u49e

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: mirea_4dmin
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO mirea_4dmin;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: mirea_4dmin
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: add_celebrity(character varying, smallint, date, date, text, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.add_celebrity(IN cel_name character varying, IN cel_height smallint, IN cel_birthday date, IN cel_death date, IN cel_birthplace text, IN cel_career character varying, IN cel_img character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO celebrity(name, height, birthday, death, birthplace, career, img_link)
                        VALUES (cel_name, cel_height, cel_birthday, cel_death, cel_birthplace, cel_career, cel_img);
END; $$;


ALTER PROCEDURE public.add_celebrity(IN cel_name character varying, IN cel_height smallint, IN cel_birthday date, IN cel_death date, IN cel_birthplace text, IN cel_career character varying, IN cel_img character varying) OWNER TO mirea_4dmin;

--
-- Name: add_collection(character varying, text, integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.add_collection(IN coll_name character varying, IN coll_description text, IN coll_uid integer)
    LANGUAGE plpgsql
    AS $$
DECLARE user_status bool;
BEGIN
    SELECT is_banned INTO user_status FROM site_user WHERE uid = coll_uid;
    IF user_status IS true THEN
        RAISE EXCEPTION 'Ваш аккаунт заблокирован!';
    END IF;
    INSERT INTO collection(name, description, uid)
                        VALUES (coll_name, coll_description, coll_uid);
END; $$;


ALTER PROCEDURE public.add_collection(IN coll_name character varying, IN coll_description text, IN coll_uid integer) OWNER TO mirea_4dmin;

--
-- Name: add_content(character varying, text, bigint, bigint, time without time zone, character varying, date); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.add_content(IN film_name character varying, IN film_description text, IN film_budget bigint, IN film_box_office bigint, IN film_duration time without time zone, IN film_image_link character varying, IN film_release_date date)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO content(name, description, budget, box_office, duration, image_link, release_date)
                        VALUES (film_name, film_description, film_budget,film_box_office,film_duration, film_image_link, film_release_date);

END; $$;


ALTER PROCEDURE public.add_content(IN film_name character varying, IN film_description text, IN film_budget bigint, IN film_box_office bigint, IN film_duration time without time zone, IN film_image_link character varying, IN film_release_date date) OWNER TO mirea_4dmin;

--
-- Name: add_film_to_collection(integer, integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.add_film_to_collection(IN coll_id integer, IN film_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF (SELECT count(*) FROM content_in_collection WHERE collection_id = coll_id AND content_id = film_id) > 0 THEN
        RAISE EXCEPTION 'Фильм уже добавлен!';
    END IF;
    INSERT INTO content_in_collection(content_id, collection_id, film_number)
                        VALUES (film_id, coll_id, ((SELECT COUNT(*) FROM content_in_collection WHERE collection_id = coll_id)+1));
END; $$;


ALTER PROCEDURE public.add_film_to_collection(IN coll_id integer, IN film_id integer) OWNER TO mirea_4dmin;

--
-- Name: add_genre(character varying); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.add_genre(IN genre_name character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO genre(name)
                        VALUES (genre_name);
END; $$;


ALTER PROCEDURE public.add_genre(IN genre_name character varying) OWNER TO mirea_4dmin;

--
-- Name: add_new(character varying, text, integer, character varying); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.add_new(IN new_title character varying, IN new_description text, IN new_uid integer, IN image character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO news(title, description, news_date, uid, image_link)
                        VALUES (new_title, new_description, CURRENT_DATE, new_uid, image);
                                        
                                        
END; $$;


ALTER PROCEDURE public.add_new(IN new_title character varying, IN new_description text, IN new_uid integer, IN image character varying) OWNER TO mirea_4dmin;

--
-- Name: add_review(character varying, text, integer, integer, smallint); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.add_review(IN review_title character varying, IN review_description text, IN review_content_id integer, IN review_uid integer, IN review_rating smallint)
    LANGUAGE plpgsql
    AS $$
DECLARE user_status bool;
BEGIN
    SELECT is_banned INTO user_status FROM site_user WHERE uid = review_uid;
    IF user_status IS true THEN
        RAISE EXCEPTION 'Ваш аккаунт заблокирован!';
    END IF;
    INSERT INTO review(title, description, rev_date, content_id, uid, opinion)
                        VALUES (review_title, review_description, CURRENT_DATE, review_content_id, review_uid, review_rating);
    COMMIT;
END; $$;


ALTER PROCEDURE public.add_review(IN review_title character varying, IN review_description text, IN review_content_id integer, IN review_uid integer, IN review_rating smallint) OWNER TO mirea_4dmin;

--
-- Name: add_star(integer, integer, smallint); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.add_star(IN star_content_id integer, IN star_uid integer, IN star_rating smallint)
    LANGUAGE plpgsql
    AS $$
DECLARE user_status bool;
BEGIN
    SELECT is_banned INTO user_status FROM site_user WHERE uid = star_uid;
    IF user_status IS true THEN
        RAISE EXCEPTION 'Ваш аккаунт заблокирован!';
    END IF;
    INSERT INTO user_stars(content_id, uid, rating)
                        VALUES (star_content_id, star_uid, star_rating);                 
END; $$;


ALTER PROCEDURE public.add_star(IN star_content_id integer, IN star_uid integer, IN star_rating smallint) OWNER TO mirea_4dmin;

--
-- Name: assign_celebrity_to_content(integer, integer[], integer[]); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.assign_celebrity_to_content(IN film_id integer, IN cel_id integer[], IN role_id integer[])
    LANGUAGE plpgsql
    AS $$
BEGIN
    FOR i in 1..(array_length(cel_id, 1)) LOOP
        INSERT INTO celebrity_in_content(content_id, cid, role, priority)
                                VALUES (film_id,  cel_id[i], role_id[i], ((SELECT COUNT(*) FROM celebrity_in_content WHERE content_id = film_id)+1));
    END LOOP;
    COMMIT;
END; $$;


ALTER PROCEDURE public.assign_celebrity_to_content(IN film_id integer, IN cel_id integer[], IN role_id integer[]) OWNER TO mirea_4dmin;

--
-- Name: assign_country_to_film(integer, integer[]); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.assign_country_to_film(IN film_id integer, IN country_id integer[])
    LANGUAGE plpgsql
    AS $$
    DECLARE c integer;
BEGIN
    FOREACH c IN ARRAY country_id
    LOOP
        INSERT INTO countries_of_content(content_id, country_id)
                            VALUES (film_id,  c);
    END LOOP ;
    COMMIT;
END; $$;


ALTER PROCEDURE public.assign_country_to_film(IN film_id integer, IN country_id integer[]) OWNER TO mirea_4dmin;

--
-- Name: assign_genre_to_film(integer[], integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.assign_genre_to_film(IN gen_id integer[], IN film_id integer)
    LANGUAGE plpgsql
    AS $$
DECLARE g integer;
BEGIN
    FOREACH g IN ARRAY gen_id
    LOOP
        INSERT INTO genres_of_content(content_id, genre_id)
                            VALUES (film_id,  g);
    END LOOP ;
    COMMIT;
END; $$;


ALTER PROCEDURE public.assign_genre_to_film(IN gen_id integer[], IN film_id integer) OWNER TO mirea_4dmin;

--
-- Name: ban_user(character varying, text); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.ban_user(IN input_login character varying, IN reason_to_ban text)
    LANGUAGE plpgsql
    AS $$
DECLARE user_ban_status bool;
BEGIN
    IF (pg_has_role(input_login, 'moderator', 'MEMBER')) THEN
        RAISE EXCEPTION 'Нельзя забанить модератора!';
    END IF;
    SELECT is_banned INTO user_ban_status FROM site_user WHERE login = input_login FOR NO KEY UPDATE ;
    IF (user_ban_status) THEN
        RAISE EXCEPTION 'Пользователь уже заблокирован!';
    END IF;
    UPDATE site_user SET is_banned = true, ban_reason = reason_to_ban, ban_date = CURRENT_DATE WHERE login = input_login;
END; $$;


ALTER PROCEDURE public.ban_user(IN input_login character varying, IN reason_to_ban text) OWNER TO mirea_4dmin;

--
-- Name: ban_user_by_id(integer, text); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.ban_user_by_id(IN user_id integer, IN reason text)
    LANGUAGE plpgsql
    AS $$
DECLARE user_ban_status bool;
BEGIN
    IF (pg_has_role((SELECT login FROM site_user WHERE uid = user_id), 'moderator', 'MEMBER')) THEN
        RAISE EXCEPTION 'Нельзя забанить модератора!';
    END IF;
    SELECT is_banned INTO user_ban_status FROM site_user WHERE uid = user_id FOR NO KEY UPDATE;
    IF (user_ban_status) THEN
        RAISE EXCEPTION 'Пользователь уже заблокирован!';
    END IF;
    UPDATE site_user SET is_banned = true, ban_reason = reason, ban_date = CURRENT_DATE WHERE uid = user_id;
END; $$;


ALTER PROCEDURE public.ban_user_by_id(IN user_id integer, IN reason text) OWNER TO mirea_4dmin;

--
-- Name: banned_delete(); Type: FUNCTION; Schema: public; Owner: mirea_4dmin
--

CREATE FUNCTION public.banned_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        DELETE FROM site_user WHERE NOW() - site_user.ban_date > interval '30 days' ;
        RETURN NULL;
    END;
    $$;


ALTER FUNCTION public.banned_delete() OWNER TO mirea_4dmin;

--
-- Name: calculate_rating_insert(); Type: FUNCTION; Schema: public; Owner: mirea_4dmin
--

CREATE FUNCTION public.calculate_rating_insert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE rating_total decimal(4, 2); id integer;
BEGIN
    SELECT SUM(user_stars.rating::numeric)/COUNT(user_stars.rating::numeric) INTO rating_total FROM user_stars WHERE content_id = NEW.content_id;
    id = NEW.content_id;
    UPDATE content SET rating = rating_total WHERE content.content_id = id;
    RETURN NEW;
END; $$;


ALTER FUNCTION public.calculate_rating_insert() OWNER TO mirea_4dmin;

--
-- Name: calculate_rating_update_delete(); Type: FUNCTION; Schema: public; Owner: mirea_4dmin
--

CREATE FUNCTION public.calculate_rating_update_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE rating_total decimal(4, 2); id integer;
BEGIN
   SELECT SUM(user_stars.rating::numeric)/COUNT(user_stars.rating::numeric) INTO rating_total FROM user_stars WHERE content_id = OLD.content_id;
    id = OLD.content_id;
   IF rating_total is null THEN
       rating_total = 0;
   end if;
    UPDATE content SET rating = rating_total WHERE content.content_id = id;
    RETURN OLD;
END; $$;


ALTER FUNCTION public.calculate_rating_update_delete() OWNER TO mirea_4dmin;

--
-- Name: delete_celebrity(integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.delete_celebrity(IN cel_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM celebrity WHERE cid = cel_id; 
END; $$;


ALTER PROCEDURE public.delete_celebrity(IN cel_id integer) OWNER TO mirea_4dmin;

--
-- Name: delete_collection(integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.delete_collection(IN coll_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM collection WHERE collection_id = coll_id;
END; $$;


ALTER PROCEDURE public.delete_collection(IN coll_id integer) OWNER TO mirea_4dmin;

--
-- Name: delete_content(integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.delete_content(IN con_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM content WHERE content_id = con_id; 
END; $$;


ALTER PROCEDURE public.delete_content(IN con_id integer) OWNER TO mirea_4dmin;

--
-- Name: delete_film_from_collection(integer, integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.delete_film_from_collection(IN coll_id integer, IN film_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM content_in_collection WHERE content_id = film_id AND collection_id = coll_id;
    COMMIT;
END; $$;


ALTER PROCEDURE public.delete_film_from_collection(IN coll_id integer, IN film_id integer) OWNER TO mirea_4dmin;

--
-- Name: delete_genre(character varying); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.delete_genre(IN genre_name character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM genre WHERE name = genre_name; 
END; $$;


ALTER PROCEDURE public.delete_genre(IN genre_name character varying) OWNER TO mirea_4dmin;

--
-- Name: delete_new(integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.delete_new(IN new_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM news WHERE nid = new_id; 
END; $$;


ALTER PROCEDURE public.delete_new(IN new_id integer) OWNER TO mirea_4dmin;

--
-- Name: delete_review(integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.delete_review(IN review_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM review WHERE rid = review_id;
END; $$;


ALTER PROCEDURE public.delete_review(IN review_id integer) OWNER TO mirea_4dmin;

--
-- Name: delete_star(integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.delete_star(IN star_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM user_stars WHERE sid = star_id; 
END; $$;


ALTER PROCEDURE public.delete_star(IN star_id integer) OWNER TO mirea_4dmin;

--
-- Name: hash_pass_match(character varying, character varying); Type: FUNCTION; Schema: public; Owner: mirea_4dmin
--

CREATE FUNCTION public.hash_pass_match(input_login character varying, password character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN(SELECT (password_hash = crypt(password, password_hash)) AS pswmatch FROM site_user where login = input_login);

END; $$;


ALTER FUNCTION public.hash_pass_match(input_login character varying, password character varying) OWNER TO mirea_4dmin;

--
-- Name: hash_password(character varying, character varying); Type: FUNCTION; Schema: public; Owner: mirea_4dmin
--

CREATE FUNCTION public.hash_password(password character varying, login character varying) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
    hashed_password varchar;
BEGIN
    hashed_password = encode(digest(concat(password, login), 'sha256'), 'hex');
    RETURN hashed_password;
END; $$;


ALTER FUNCTION public.hash_password(password character varying, login character varying) OWNER TO mirea_4dmin;

--
-- Name: register_moderator(character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.register_moderator(IN username character varying, IN email character varying, IN u_login character varying, IN password character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    hashed_password varchar;
BEGIN
    IF (SELECT COUNT(*) FROM pg_roles WHERE rolname=u_login) THEN
        RAISE EXCEPTION 'User Already Exists!';
    ELSE
        hashed_password = encode(digest(concat(password, u_login), 'sha256'), 'hex');
        EXECUTE format('INSERT INTO site_user(nickname, email, is_banned, ban_date, ban_reason, login)
                        VALUES (%L, %L, false, null, null, %L);', username, email, u_login);
        EXECUTE format('CREATE USER %I PASSWORD %L IN ROLE moderator;',
                        u_login, hashed_password);
    END IF;
END; $$;


ALTER PROCEDURE public.register_moderator(IN username character varying, IN email character varying, IN u_login character varying, IN password character varying) OWNER TO mirea_4dmin;

--
-- Name: register_user(character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.register_user(IN username character varying, IN email character varying, IN u_login character varying, IN password character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    decrypted_pass varchar;
    hashed_password varchar;
    rand_num char(16);
BEGIN
    IF (SELECT COUNT(*) FROM pg_roles WHERE rolname=u_login) THEN
        RAISE EXCEPTION 'Пользователь с таким логином уже существует!';
    ELSE
        IF (NOT exists(SELECT FROM reg_table WHERE login = u_login)) THEN
            RAISE EXCEPTION 'Ошибка при регистрации. Попробуйте позже!';
        END IF;
        rand_num = (SELECT r_num FROM reg_table WHERE login = u_login);
        decrypted_pass = encode(decrypt(decode(password, 'base64'), rand_num::bytea, 'aes-cbc'), 'escape');
        hashed_password = encode(digest(concat(decrypted_pass, u_login), 'sha256'), 'hex');
        EXECUTE format('INSERT INTO site_user(nickname, email, is_banned, ban_date, ban_reason, login)
                        VALUES (%L, %L, false, null, null, %L);', username, email, u_login);
        EXECUTE format('CREATE USER %I PASSWORD %L IN ROLE ordinary_user;',
                        u_login, hashed_password);
    END IF;
END; $$;


ALTER PROCEDURE public.register_user(IN username character varying, IN email character varying, IN u_login character varying, IN password character varying) OWNER TO mirea_4dmin;

--
-- Name: remove_genre_from_film(integer, integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.remove_genre_from_film(IN gen_id integer, IN film_id integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM genres_of_content WHERE content_id =  film_id AND genre_id = gen_id;
END; $$;


ALTER PROCEDURE public.remove_genre_from_film(IN gen_id integer, IN film_id integer) OWNER TO mirea_4dmin;

--
-- Name: start_registration(character varying, character varying); Type: FUNCTION; Schema: public; Owner: mirea_4dmin
--

CREATE FUNCTION public.start_registration(u_login character varying, p_hash character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
    DECLARE
        rand_num char(16);
    BEGIN
        rand_num = left((random()::text), 16);
        CREATE TEMP TABLE IF NOT EXISTS reg_table(login varchar(20) PRIMARY KEY, r_num char(16));
        DELETE FROM reg_table WHERE reg_table.login = u_login;
        INSERT INTO reg_table(login, r_num) VALUES(u_login, rand_num);
        RETURN encode((encrypt(rand_num::bytea, decode(p_hash, 'hex'), 'aes-cbc'))::bytea, 'base64');
    END;
$$;


ALTER FUNCTION public.start_registration(u_login character varying, p_hash character varying) OWNER TO mirea_4dmin;

--
-- Name: unban_user(character varying); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.unban_user(IN input_login character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE user_ban_status bool;
BEGIN
    SELECT is_banned INTO user_ban_status FROM site_user WHERE login = input_login FOR NO KEY UPDATE ;
    IF (NOT user_ban_status) THEN
        RAISE EXCEPTION 'Пользователь не заблокирован!';
    ELSE
    UPDATE site_user SET is_banned = false, ban_reason = null, ban_date = null WHERE login = input_login;
    END IF;
END; $$;


ALTER PROCEDURE public.unban_user(IN input_login character varying) OWNER TO mirea_4dmin;

--
-- Name: unban_user_by_id(integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.unban_user_by_id(IN id integer)
    LANGUAGE plpgsql
    AS $$
DECLARE user_ban_status bool;
BEGIN
    SELECT is_banned INTO user_ban_status FROM site_user WHERE uid = id FOR NO KEY UPDATE ;
    IF (NOT user_ban_status) THEN
        RAISE EXCEPTION 'Пользователь не заблокирован!';
    ELSE
    UPDATE site_user SET is_banned = false, ban_reason = null, ban_date = null WHERE uid = id;
    END IF;
END; $$;


ALTER PROCEDURE public.unban_user_by_id(IN id integer) OWNER TO mirea_4dmin;

--
-- Name: update_celebrity(character varying, integer, smallint, date, date, text, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.update_celebrity(IN cel_name character varying, IN id integer, IN cel_height smallint, IN cel_birthday date, IN cel_death date, IN cel_birthplace text, IN cel_career character varying, IN cel_img character varying)
    LANGUAGE plpgsql
    AS $$
    BEGIN
        UPDATE celebrity SET name = cel_name, height = cel_height, birthday = cel_birthday, death = cel_death, birthplace = cel_birthplace, 
                             career = cel_career, img_link = cel_img WHERE cid = id;
    END $$;


ALTER PROCEDURE public.update_celebrity(IN cel_name character varying, IN id integer, IN cel_height smallint, IN cel_birthday date, IN cel_death date, IN cel_birthplace text, IN cel_career character varying, IN cel_img character varying) OWNER TO mirea_4dmin;

--
-- Name: update_collection(character varying, text, integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.update_collection(IN nm character varying, IN descr text, IN id integer)
    LANGUAGE plpgsql
    AS $$
    BEGIN
        UPDATE collection SET name = nm, description = descr WHERE collection_id = id;
    END $$;


ALTER PROCEDURE public.update_collection(IN nm character varying, IN descr text, IN id integer) OWNER TO mirea_4dmin;

--
-- Name: update_content(character varying, text, bigint, bigint, time without time zone, character varying, date, integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.update_content(IN nm character varying, IN descr text, IN budg bigint, IN boffice bigint, IN dur time without time zone, IN image character varying, IN release date, IN id integer)
    LANGUAGE plpgsql
    AS $$
    BEGIN
        UPDATE content SET name = nm, description = descr , budget = budg, box_office = boffice, duration = dur, image_link = image, release_date = release WHERE
                                                                                                                                                                content_id = id;
    END $$;


ALTER PROCEDURE public.update_content(IN nm character varying, IN descr text, IN budg bigint, IN boffice bigint, IN dur time without time zone, IN image character varying, IN release date, IN id integer) OWNER TO mirea_4dmin;

--
-- Name: update_new(text, character varying, integer, character varying); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.update_new(IN descr text, IN tit character varying, IN id integer, IN image character varying)
    LANGUAGE plpgsql
    AS $$
    BEGIN
        UPDATE news SET title = tit, image_link = image, description = descr, news_date = CURRENT_DATE WHERE nid = id;
    END $$;


ALTER PROCEDURE public.update_new(IN descr text, IN tit character varying, IN id integer, IN image character varying) OWNER TO mirea_4dmin;

--
-- Name: update_review(character varying, text, smallint, integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.update_review(IN review_title character varying, IN review_description text, IN review_rating smallint, IN review_id integer)
    LANGUAGE plpgsql
    AS $$
DECLARE user_status bool;
BEGIN
    SELECT is_banned INTO user_status FROM site_user WHERE uid = (SELECT uid FROM review WHERE rid = review_id);
    IF user_status IS true THEN
        RAISE EXCEPTION 'Ваш аккаунт заблокирован!';
    END IF;
    UPDATE review SET title = review_title, description = review_description, opinion = review_rating WHERE rid = review_id;
    COMMIT;
END; $$;


ALTER PROCEDURE public.update_review(IN review_title character varying, IN review_description text, IN review_rating smallint, IN review_id integer) OWNER TO mirea_4dmin;

--
-- Name: update_star(integer, smallint); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.update_star(IN id integer, IN rate smallint)
    LANGUAGE plpgsql
    AS $$
DECLARE user_status bool;
BEGIN
    SELECT is_banned INTO user_status FROM site_user WHERE uid = (SELECT uid FROM user_stars WHERE sid = id);
    IF user_status IS true THEN
        RAISE EXCEPTION 'Ваш аккаунт заблокирован!';
    END IF;
    UPDATE user_stars SET rating = rate WHERE sid = id;
END $$;


ALTER PROCEDURE public.update_star(IN id integer, IN rate smallint) OWNER TO mirea_4dmin;

--
-- Name: update_user(character varying, character varying, integer); Type: PROCEDURE; Schema: public; Owner: mirea_4dmin
--

CREATE PROCEDURE public.update_user(IN nname character varying, IN mail character varying, IN id integer)
    LANGUAGE plpgsql
    AS $$
    BEGIN
        UPDATE site_user SET nickname = nname, email = mail WHERE uid = id;
    END $$;


ALTER PROCEDURE public.update_user(IN nname character varying, IN mail character varying, IN id integer) OWNER TO mirea_4dmin;

--
-- Name: user_system_delete(); Type: FUNCTION; Schema: public; Owner: mirea_4dmin
--

CREATE FUNCTION public.user_system_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    EXECUTE format('DROP USER %I;', OLD.login);
    RETURN NULL;
END; $$;


ALTER FUNCTION public.user_system_delete() OWNER TO mirea_4dmin;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: content; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.content (
    content_id integer NOT NULL,
    name character varying(50) NOT NULL,
    description text NOT NULL,
    budget bigint,
    box_office bigint,
    duration time without time zone NOT NULL,
    rating numeric(10,2) DEFAULT 0 NOT NULL,
    image_link character varying(100),
    release_date date NOT NULL,
    CONSTRAINT content_soundtrack_rating_check CHECK ((rating <= (10)::numeric)),
    CONSTRAINT valid_values3 CHECK ((rating >= (0)::numeric))
);


ALTER TABLE public.content OWNER TO mirea_4dmin;

--
-- Name: TABLE content; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.content IS 'Объект, представляющий собой фильм, сериал и другой видеоконтент.';


--
-- Name: COLUMN content.content_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.content.content_id IS 'Уникальный код видеоконтента в цифровом формате';


--
-- Name: COLUMN content.name; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.content.name IS 'Название контента';


--
-- Name: COLUMN content.description; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.content.description IS 'Краткое описание сюжета';


--
-- Name: COLUMN content.budget; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.content.budget IS 'Потраченные средства';


--
-- Name: COLUMN content.box_office; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.content.box_office IS 'Заработанные средства';


--
-- Name: COLUMN content.rating; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.content.rating IS 'Рейтинг саундтрека по польщовательским оценкам';


--
-- Name: best_films; Type: VIEW; Schema: public; Owner: mirea_4dmin
--

CREATE VIEW public.best_films AS
 SELECT content.content_id,
    content.name,
    content.description,
    content.budget,
    content.box_office,
    content.duration,
    content.rating,
    content.image_link,
    content.release_date
   FROM public.content
  WHERE (content.rating >= (8)::numeric)
  ORDER BY content.rating DESC;


ALTER TABLE public.best_films OWNER TO mirea_4dmin;

--
-- Name: celebrity; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.celebrity (
    cid integer NOT NULL,
    name character varying(50) NOT NULL,
    height smallint,
    birthday date,
    death date,
    birthplace text,
    career character varying(100),
    img_link character varying(100),
    CONSTRAINT valid_review CHECK (((name)::text ~ '^[A-Za-zА-Яа-я]'::text))
);


ALTER TABLE public.celebrity OWNER TO mirea_4dmin;

--
-- Name: TABLE celebrity; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.celebrity IS 'Объект, содержащий информацию об актере, режиссере, или других участниках сьемочного процесса.';


--
-- Name: COLUMN celebrity.cid; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.celebrity.cid IS 'Уникальный идентификатор знаменитости';


--
-- Name: COLUMN celebrity.name; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.celebrity.name IS 'Имя знаменитости';


--
-- Name: celebrity_cid_seq; Type: SEQUENCE; Schema: public; Owner: mirea_4dmin
--

CREATE SEQUENCE public.celebrity_cid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.celebrity_cid_seq OWNER TO mirea_4dmin;

--
-- Name: celebrity_cid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mirea_4dmin
--

ALTER SEQUENCE public.celebrity_cid_seq OWNED BY public.celebrity.cid;


--
-- Name: celebrity_in_content; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.celebrity_in_content (
    content_id integer NOT NULL,
    cid integer NOT NULL,
    role smallint NOT NULL,
    description character varying(100),
    priority smallint
);


ALTER TABLE public.celebrity_in_content OWNER TO mirea_4dmin;

--
-- Name: TABLE celebrity_in_content; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.celebrity_in_content IS 'Знаменитость в фильме';


--
-- Name: COLUMN celebrity_in_content.content_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.celebrity_in_content.content_id IS 'Уникальный код видеоконтента в цифровом формате';


--
-- Name: COLUMN celebrity_in_content.cid; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.celebrity_in_content.cid IS 'Уникальный идентификатор знаменитости';


--
-- Name: collection; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.collection (
    collection_id integer NOT NULL,
    name character varying(30) NOT NULL,
    description text,
    uid integer,
    CONSTRAINT valid_name CHECK (((name)::text ~ '^[A-Za-zА-Яа-я0-9!".,]'::text))
);


ALTER TABLE public.collection OWNER TO mirea_4dmin;

--
-- Name: TABLE collection; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.collection IS 'Фильмы, отобранные по определенному признаку';


--
-- Name: COLUMN collection.collection_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.collection.collection_id IS 'Идентификатор коллекции';


--
-- Name: COLUMN collection.name; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.collection.name IS 'Название подборки';


--
-- Name: COLUMN collection.description; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.collection.description IS 'Текстовое описание подборки';


--
-- Name: COLUMN collection.uid; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.collection.uid IS 'Идентификатор пользователя';


--
-- Name: collection_collection_id_seq; Type: SEQUENCE; Schema: public; Owner: mirea_4dmin
--

CREATE SEQUENCE public.collection_collection_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.collection_collection_id_seq OWNER TO mirea_4dmin;

--
-- Name: collection_collection_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mirea_4dmin
--

ALTER SEQUENCE public.collection_collection_id_seq OWNED BY public.collection.collection_id;


--
-- Name: content_content_id_seq; Type: SEQUENCE; Schema: public; Owner: mirea_4dmin
--

CREATE SEQUENCE public.content_content_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.content_content_id_seq OWNER TO mirea_4dmin;

--
-- Name: content_content_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mirea_4dmin
--

ALTER SEQUENCE public.content_content_id_seq OWNED BY public.content.content_id;


--
-- Name: content_in_collection; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.content_in_collection (
    content_id integer NOT NULL,
    collection_id integer NOT NULL,
    film_number integer NOT NULL
);


ALTER TABLE public.content_in_collection OWNER TO mirea_4dmin;

--
-- Name: TABLE content_in_collection; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.content_in_collection IS 'Фильм в подборке';


--
-- Name: COLUMN content_in_collection.content_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.content_in_collection.content_id IS 'Уникальный код видеоконтента в цифровом формате';


--
-- Name: COLUMN content_in_collection.collection_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.content_in_collection.collection_id IS 'Идентификатор коллекции';


--
-- Name: countries_of_content; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.countries_of_content (
    content_id integer NOT NULL,
    country_id integer NOT NULL
);


ALTER TABLE public.countries_of_content OWNER TO mirea_4dmin;

--
-- Name: TABLE countries_of_content; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.countries_of_content IS 'Страна проиводства конкретного фильма/сериала/другого контента';


--
-- Name: COLUMN countries_of_content.content_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.countries_of_content.content_id IS 'Уникальный код видеоконтента в цифровом формате';


--
-- Name: COLUMN countries_of_content.country_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.countries_of_content.country_id IS 'Уникальный цифровой код страны производства';


--
-- Name: country; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.country (
    country_id smallint NOT NULL,
    name character varying(20) NOT NULL
);


ALTER TABLE public.country OWNER TO mirea_4dmin;

--
-- Name: TABLE country; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.country IS 'Страна производства видеоконтента';


--
-- Name: COLUMN country.country_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.country.country_id IS 'Уникальный цифровой код страны производства';


--
-- Name: COLUMN country.name; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.country.name IS 'Название страны-производителя видеоконтента';


--
-- Name: genre; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.genre (
    genre_id smallint NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE public.genre OWNER TO mirea_4dmin;

--
-- Name: TABLE genre; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.genre IS 'Жанр, к которому относится видеоконтент';


--
-- Name: COLUMN genre.genre_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.genre.genre_id IS 'Уникальный цифровой идентификатор жанра';


--
-- Name: COLUMN genre.name; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.genre.name IS 'Текстовое наименование жанра';


--
-- Name: genre_genre_id_seq; Type: SEQUENCE; Schema: public; Owner: mirea_4dmin
--

CREATE SEQUENCE public.genre_genre_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.genre_genre_id_seq OWNER TO mirea_4dmin;

--
-- Name: genre_genre_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mirea_4dmin
--

ALTER SEQUENCE public.genre_genre_id_seq OWNED BY public.genre.genre_id;


--
-- Name: genres_of_content; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.genres_of_content (
    content_id integer NOT NULL,
    genre_id integer NOT NULL
);


ALTER TABLE public.genres_of_content OWNER TO mirea_4dmin;

--
-- Name: TABLE genres_of_content; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.genres_of_content IS 'Жанр конкретного фильма/сериала/другого контента';


--
-- Name: COLUMN genres_of_content.content_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.genres_of_content.content_id IS 'Уникальный код видеоконтента в цифровом формате';


--
-- Name: COLUMN genres_of_content.genre_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.genres_of_content.genre_id IS 'Уникальный цифровой идентификатор жанра';


--
-- Name: news; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.news (
    nid integer NOT NULL,
    description text NOT NULL,
    title character varying(50) NOT NULL,
    news_date date NOT NULL,
    uid integer NOT NULL,
    image_link character varying(100),
    CONSTRAINT valid_new CHECK ((description ~ '^[A-Za-zА-Яа-я0-9!".,-;:*()]'::text)),
    CONSTRAINT valid_title CHECK (((title)::text ~ '^[A-Za-zА-Яа-я0-9!".,]'::text))
);


ALTER TABLE public.news OWNER TO mirea_4dmin;

--
-- Name: TABLE news; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.news IS 'Объект, представляющий собой новость';


--
-- Name: COLUMN news.nid; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.news.nid IS 'Уникальный идентификатор новости';


--
-- Name: COLUMN news.description; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.news.description IS 'Текстовая информация, представляющая новость';


--
-- Name: COLUMN news.title; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.news.title IS 'Заголовок новости, отображающий ее основную тематику';


--
-- Name: COLUMN news.news_date; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.news.news_date IS 'Дата публикации новости';


--
-- Name: news_nid_seq; Type: SEQUENCE; Schema: public; Owner: mirea_4dmin
--

CREATE SEQUENCE public.news_nid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.news_nid_seq OWNER TO mirea_4dmin;

--
-- Name: news_nid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mirea_4dmin
--

ALTER SEQUENCE public.news_nid_seq OWNED BY public.news.nid;


--
-- Name: opinion_classifier; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.opinion_classifier (
    oid integer NOT NULL,
    opinion_name character varying(10) NOT NULL
);


ALTER TABLE public.opinion_classifier OWNER TO mirea_4dmin;

--
-- Name: opinion_classifier_oid_seq; Type: SEQUENCE; Schema: public; Owner: mirea_4dmin
--

CREATE SEQUENCE public.opinion_classifier_oid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.opinion_classifier_oid_seq OWNER TO mirea_4dmin;

--
-- Name: opinion_classifier_oid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mirea_4dmin
--

ALTER SEQUENCE public.opinion_classifier_oid_seq OWNED BY public.opinion_classifier.oid;


--
-- Name: recent_films; Type: VIEW; Schema: public; Owner: mirea_4dmin
--

CREATE VIEW public.recent_films AS
 SELECT content.content_id,
    content.name,
    content.description,
    content.budget,
    content.box_office,
    content.duration,
    content.rating,
    content.image_link,
    content.release_date
   FROM public.content
  WHERE ((now() - (content.release_date)::timestamp with time zone) < '30 days'::interval);


ALTER TABLE public.recent_films OWNER TO mirea_4dmin;

--
-- Name: recent_news; Type: VIEW; Schema: public; Owner: mirea_4dmin
--

CREATE VIEW public.recent_news AS
 SELECT news.nid,
    news.description,
    news.title,
    news.news_date,
    news.uid,
    news.image_link
   FROM public.news
  WHERE ((now() - (news.news_date)::timestamp with time zone) < '7 days'::interval)
 LIMIT 5;


ALTER TABLE public.recent_news OWNER TO mirea_4dmin;

--
-- Name: review; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.review (
    rid integer NOT NULL,
    description text,
    rev_date date NOT NULL,
    content_id integer,
    uid integer,
    title character varying(100) DEFAULT 'Рецензия'::character varying NOT NULL,
    opinion smallint NOT NULL,
    CONSTRAINT censor_review CHECK ((description !~ similar_to_escape('%блять|пиздец|ебать|хуй%'::text))),
    CONSTRAINT valid_review CHECK ((description ~ '^[A-Za-zА-Яа-я0-9!".,]'::text))
);


ALTER TABLE public.review OWNER TO mirea_4dmin;

--
-- Name: TABLE review; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.review IS 'Содержит информацию об обзорах фильма';


--
-- Name: COLUMN review.rid; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.review.rid IS 'Уникальный идентификатор рецензии';


--
-- Name: COLUMN review.description; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.review.description IS 'Текстовая рецензия фильма/сериала и тд.';


--
-- Name: COLUMN review.rev_date; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.review.rev_date IS 'Дата написания рецензии';


--
-- Name: COLUMN review.content_id; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.review.content_id IS 'Уникальный код видеоконтента в цифровом формате';


--
-- Name: COLUMN review.uid; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.review.uid IS 'Идентификатор пользователя';


--
-- Name: review_rid_seq; Type: SEQUENCE; Schema: public; Owner: mirea_4dmin
--

CREATE SEQUENCE public.review_rid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.review_rid_seq OWNER TO mirea_4dmin;

--
-- Name: review_rid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mirea_4dmin
--

ALTER SEQUENCE public.review_rid_seq OWNED BY public.review.rid;


--
-- Name: role_classifier; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.role_classifier (
    role_id smallint NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE public.role_classifier OWNER TO mirea_4dmin;

--
-- Name: site_user; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.site_user (
    uid integer NOT NULL,
    nickname character varying(20) NOT NULL,
    email character varying(50) NOT NULL,
    is_banned boolean NOT NULL,
    ban_date date,
    ban_reason text,
    login character varying(20) NOT NULL,
    CONSTRAINT valid_ban_reason CHECK ((ban_reason ~ '^[A-Za-zА-Яа-я0-9!".,]'::text)),
    CONSTRAINT valid_email CHECK (((email)::text ~ '^(?!\.)(?:(?:[A-Za-z0-9!#$%&''*+/=?^_`{|}~]|-(?!-)|\.(?!\.)))+(?<!\.)@(?:(?!-)(?:[a-zA-Z\d]|-(?!-))+(?<!-)\.)+[a-zA-Z]{2,}$'::text)),
    CONSTRAINT valid_nickname CHECK (((nickname)::text ~ '^[A-Za-zА-Яа-я0-9]'::text))
);


ALTER TABLE public.site_user OWNER TO mirea_4dmin;

--
-- Name: TABLE site_user; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON TABLE public.site_user IS 'Объект, представляющий пользователя сервиса';


--
-- Name: COLUMN site_user.uid; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.site_user.uid IS 'Идентификатор пользователя';


--
-- Name: COLUMN site_user.nickname; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.site_user.nickname IS 'Ник, выбранный пользователем';


--
-- Name: COLUMN site_user.email; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.site_user.email IS 'Электронная почта для оповещений ';


--
-- Name: COLUMN site_user.is_banned; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.site_user.is_banned IS '0 - не забанен
1 - забанен';


--
-- Name: COLUMN site_user.ban_date; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.site_user.ban_date IS 'Дата бана пользователя';


--
-- Name: COLUMN site_user.ban_reason; Type: COMMENT; Schema: public; Owner: mirea_4dmin
--

COMMENT ON COLUMN public.site_user.ban_reason IS 'Причина бана пользователя - указывается модератором';


--
-- Name: site_user_uid_seq; Type: SEQUENCE; Schema: public; Owner: mirea_4dmin
--

CREATE SEQUENCE public.site_user_uid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.site_user_uid_seq OWNER TO mirea_4dmin;

--
-- Name: site_user_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mirea_4dmin
--

ALTER SEQUENCE public.site_user_uid_seq OWNED BY public.site_user.uid;


--
-- Name: user_stars; Type: TABLE; Schema: public; Owner: mirea_4dmin
--

CREATE TABLE public.user_stars (
    sid integer NOT NULL,
    content_id integer NOT NULL,
    uid integer NOT NULL,
    rating smallint NOT NULL,
    CONSTRAINT user_stars_user_visual_rating_check CHECK ((rating <= 10)),
    CONSTRAINT user_stars_user_visual_rating_check1 CHECK ((rating >= 0))
);


ALTER TABLE public.user_stars OWNER TO mirea_4dmin;

--
-- Name: user_stars_sid_seq; Type: SEQUENCE; Schema: public; Owner: mirea_4dmin
--

CREATE SEQUENCE public.user_stars_sid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_stars_sid_seq OWNER TO mirea_4dmin;

--
-- Name: user_stars_sid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mirea_4dmin
--

ALTER SEQUENCE public.user_stars_sid_seq OWNED BY public.user_stars.sid;


--
-- Name: celebrity cid; Type: DEFAULT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.celebrity ALTER COLUMN cid SET DEFAULT nextval('public.celebrity_cid_seq'::regclass);


--
-- Name: collection collection_id; Type: DEFAULT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.collection ALTER COLUMN collection_id SET DEFAULT nextval('public.collection_collection_id_seq'::regclass);


--
-- Name: content content_id; Type: DEFAULT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.content ALTER COLUMN content_id SET DEFAULT nextval('public.content_content_id_seq'::regclass);


--
-- Name: news nid; Type: DEFAULT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.news ALTER COLUMN nid SET DEFAULT nextval('public.news_nid_seq'::regclass);


--
-- Name: opinion_classifier oid; Type: DEFAULT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.opinion_classifier ALTER COLUMN oid SET DEFAULT nextval('public.opinion_classifier_oid_seq'::regclass);


--
-- Name: review rid; Type: DEFAULT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.review ALTER COLUMN rid SET DEFAULT nextval('public.review_rid_seq'::regclass);


--
-- Name: site_user uid; Type: DEFAULT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.site_user ALTER COLUMN uid SET DEFAULT nextval('public.site_user_uid_seq'::regclass);


--
-- Name: user_stars sid; Type: DEFAULT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.user_stars ALTER COLUMN sid SET DEFAULT nextval('public.user_stars_sid_seq'::regclass);


--
-- Data for Name: celebrity; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.celebrity (cid, name, height, birthday, death, birthplace, career, img_link) FROM stdin;
\.
COPY public.celebrity (cid, name, height, birthday, death, birthplace, career, img_link) FROM '$$PATH$$/3432.dat';

--
-- Data for Name: celebrity_in_content; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.celebrity_in_content (content_id, cid, role, description, priority) FROM stdin;
\.
COPY public.celebrity_in_content (content_id, cid, role, description, priority) FROM '$$PATH$$/3433.dat';

--
-- Data for Name: collection; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.collection (collection_id, name, description, uid) FROM stdin;
\.
COPY public.collection (collection_id, name, description, uid) FROM '$$PATH$$/3435.dat';

--
-- Data for Name: content; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.content (content_id, name, description, budget, box_office, duration, rating, image_link, release_date) FROM stdin;
\.
COPY public.content (content_id, name, description, budget, box_office, duration, rating, image_link, release_date) FROM '$$PATH$$/3437.dat';

--
-- Data for Name: content_in_collection; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.content_in_collection (content_id, collection_id, film_number) FROM stdin;
\.
COPY public.content_in_collection (content_id, collection_id, film_number) FROM '$$PATH$$/3438.dat';

--
-- Data for Name: countries_of_content; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.countries_of_content (content_id, country_id) FROM stdin;
\.
COPY public.countries_of_content (content_id, country_id) FROM '$$PATH$$/3439.dat';

--
-- Data for Name: country; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.country (country_id, name) FROM stdin;
\.
COPY public.country (country_id, name) FROM '$$PATH$$/3440.dat';

--
-- Data for Name: genre; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.genre (genre_id, name) FROM stdin;
\.
COPY public.genre (genre_id, name) FROM '$$PATH$$/3442.dat';

--
-- Data for Name: genres_of_content; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.genres_of_content (content_id, genre_id) FROM stdin;
\.
COPY public.genres_of_content (content_id, genre_id) FROM '$$PATH$$/3443.dat';

--
-- Data for Name: news; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.news (nid, description, title, news_date, uid, image_link) FROM stdin;
\.
COPY public.news (nid, description, title, news_date, uid, image_link) FROM '$$PATH$$/3445.dat';

--
-- Data for Name: opinion_classifier; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.opinion_classifier (oid, opinion_name) FROM stdin;
\.
COPY public.opinion_classifier (oid, opinion_name) FROM '$$PATH$$/3455.dat';

--
-- Data for Name: review; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.review (rid, description, rev_date, content_id, uid, title, opinion) FROM stdin;
\.
COPY public.review (rid, description, rev_date, content_id, uid, title, opinion) FROM '$$PATH$$/3447.dat';

--
-- Data for Name: role_classifier; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.role_classifier (role_id, name) FROM stdin;
\.
COPY public.role_classifier (role_id, name) FROM '$$PATH$$/3453.dat';

--
-- Data for Name: site_user; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.site_user (uid, nickname, email, is_banned, ban_date, ban_reason, login) FROM stdin;
\.
COPY public.site_user (uid, nickname, email, is_banned, ban_date, ban_reason, login) FROM '$$PATH$$/3449.dat';

--
-- Data for Name: user_stars; Type: TABLE DATA; Schema: public; Owner: mirea_4dmin
--

COPY public.user_stars (sid, content_id, uid, rating) FROM stdin;
\.
COPY public.user_stars (sid, content_id, uid, rating) FROM '$$PATH$$/3451.dat';

--
-- Name: celebrity_cid_seq; Type: SEQUENCE SET; Schema: public; Owner: mirea_4dmin
--

SELECT pg_catalog.setval('public.celebrity_cid_seq', 744, true);


--
-- Name: collection_collection_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mirea_4dmin
--

SELECT pg_catalog.setval('public.collection_collection_id_seq', 16, true);


--
-- Name: content_content_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mirea_4dmin
--

SELECT pg_catalog.setval('public.content_content_id_seq', 9, false);


--
-- Name: genre_genre_id_seq; Type: SEQUENCE SET; Schema: public; Owner: mirea_4dmin
--

SELECT pg_catalog.setval('public.genre_genre_id_seq', 30, true);


--
-- Name: news_nid_seq; Type: SEQUENCE SET; Schema: public; Owner: mirea_4dmin
--

SELECT pg_catalog.setval('public.news_nid_seq', 6, true);


--
-- Name: opinion_classifier_oid_seq; Type: SEQUENCE SET; Schema: public; Owner: mirea_4dmin
--

SELECT pg_catalog.setval('public.opinion_classifier_oid_seq', 3, true);


--
-- Name: review_rid_seq; Type: SEQUENCE SET; Schema: public; Owner: mirea_4dmin
--

SELECT pg_catalog.setval('public.review_rid_seq', 33, true);


--
-- Name: site_user_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: mirea_4dmin
--

SELECT pg_catalog.setval('public.site_user_uid_seq', 22, true);


--
-- Name: user_stars_sid_seq; Type: SEQUENCE SET; Schema: public; Owner: mirea_4dmin
--

SELECT pg_catalog.setval('public.user_stars_sid_seq', 56, true);


--
-- Name: celebrity celebrity_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.celebrity
    ADD CONSTRAINT celebrity_pkey PRIMARY KEY (cid);


--
-- Name: collection collection_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.collection
    ADD CONSTRAINT collection_pkey PRIMARY KEY (collection_id);


--
-- Name: content content_description_key; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.content
    ADD CONSTRAINT content_description_key UNIQUE (description);


--
-- Name: content content_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.content
    ADD CONSTRAINT content_pkey PRIMARY KEY (content_id);


--
-- Name: country country_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (country_id);


--
-- Name: genre genre_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.genre
    ADD CONSTRAINT genre_pkey PRIMARY KEY (genre_id);


--
-- Name: site_user login_unique; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.site_user
    ADD CONSTRAINT login_unique UNIQUE (login);


--
-- Name: news news_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.news
    ADD CONSTRAINT news_pkey PRIMARY KEY (nid);


--
-- Name: content_in_collection no_dupes; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.content_in_collection
    ADD CONSTRAINT no_dupes UNIQUE (content_id, collection_id);


--
-- Name: opinion_classifier opinion_classifier_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.opinion_classifier
    ADD CONSTRAINT opinion_classifier_pkey PRIMARY KEY (oid);


--
-- Name: review review_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.review
    ADD CONSTRAINT review_pkey PRIMARY KEY (rid);


--
-- Name: role_classifier role_classifier_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.role_classifier
    ADD CONSTRAINT role_classifier_pkey PRIMARY KEY (role_id);


--
-- Name: role_classifier role_classifier_role_id_name_key; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.role_classifier
    ADD CONSTRAINT role_classifier_role_id_name_key UNIQUE (role_id, name);


--
-- Name: site_user site_user_email_key; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.site_user
    ADD CONSTRAINT site_user_email_key UNIQUE (email);


--
-- Name: site_user site_user_nickname_key; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.site_user
    ADD CONSTRAINT site_user_nickname_key UNIQUE (nickname);


--
-- Name: site_user site_user_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.site_user
    ADD CONSTRAINT site_user_pkey PRIMARY KEY (uid);


--
-- Name: celebrity_in_content unique_combinations; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.celebrity_in_content
    ADD CONSTRAINT unique_combinations UNIQUE (content_id, cid, role);


--
-- Name: content_in_collection unique_combinations_collection; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.content_in_collection
    ADD CONSTRAINT unique_combinations_collection UNIQUE (content_id, collection_id);


--
-- Name: countries_of_content unique_combinations_countries; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.countries_of_content
    ADD CONSTRAINT unique_combinations_countries UNIQUE (content_id, country_id);


--
-- Name: genres_of_content unique_combinations_genres; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.genres_of_content
    ADD CONSTRAINT unique_combinations_genres UNIQUE (content_id, genre_id);


--
-- Name: content unique_film; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.content
    ADD CONSTRAINT unique_film UNIQUE (name, description, release_date);


--
-- Name: review unique_reviews; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.review
    ADD CONSTRAINT unique_reviews UNIQUE (uid, content_id);


--
-- Name: user_stars user_stars_content_id_uid_key; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.user_stars
    ADD CONSTRAINT user_stars_content_id_uid_key UNIQUE (content_id, uid);


--
-- Name: user_stars user_stars_pkey; Type: CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.user_stars
    ADD CONSTRAINT user_stars_pkey PRIMARY KEY (sid);


--
-- Name: celebrity_name_index; Type: INDEX; Schema: public; Owner: mirea_4dmin
--

CREATE INDEX celebrity_name_index ON public.celebrity USING btree (name);


--
-- Name: collection_name_index; Type: INDEX; Schema: public; Owner: mirea_4dmin
--

CREATE INDEX collection_name_index ON public.collection USING btree (name);


--
-- Name: content_id_index; Type: INDEX; Schema: public; Owner: mirea_4dmin
--

CREATE INDEX content_id_index ON public.celebrity_in_content USING btree (content_id);


--
-- Name: content_name_index; Type: INDEX; Schema: public; Owner: mirea_4dmin
--

CREATE INDEX content_name_index ON public.content USING btree (name);


--
-- Name: genre_index; Type: INDEX; Schema: public; Owner: mirea_4dmin
--

CREATE INDEX genre_index ON public.genres_of_content USING btree (genre_id);


--
-- Name: new_title_index; Type: INDEX; Schema: public; Owner: mirea_4dmin
--

CREATE INDEX new_title_index ON public.news USING btree (title);


--
-- Name: review_title_index; Type: INDEX; Schema: public; Owner: mirea_4dmin
--

CREATE INDEX review_title_index ON public.review USING btree (title);


--
-- Name: star_uid_index; Type: INDEX; Schema: public; Owner: mirea_4dmin
--

CREATE INDEX star_uid_index ON public.user_stars USING btree (uid);


--
-- Name: user_nickname_index; Type: INDEX; Schema: public; Owner: mirea_4dmin
--

CREATE INDEX user_nickname_index ON public.site_user USING btree (nickname);


--
-- Name: site_user banned_delete; Type: TRIGGER; Schema: public; Owner: mirea_4dmin
--

CREATE TRIGGER banned_delete AFTER INSERT OR UPDATE ON public.site_user FOR EACH STATEMENT EXECUTE FUNCTION public.banned_delete();


--
-- Name: user_stars calculate_rating_insert; Type: TRIGGER; Schema: public; Owner: mirea_4dmin
--

CREATE TRIGGER calculate_rating_insert AFTER INSERT ON public.user_stars FOR EACH ROW EXECUTE FUNCTION public.calculate_rating_insert();


--
-- Name: user_stars calculate_rating_update_delete; Type: TRIGGER; Schema: public; Owner: mirea_4dmin
--

CREATE TRIGGER calculate_rating_update_delete AFTER DELETE OR UPDATE ON public.user_stars FOR EACH ROW EXECUTE FUNCTION public.calculate_rating_update_delete();


--
-- Name: site_user user_system_delete; Type: TRIGGER; Schema: public; Owner: mirea_4dmin
--

CREATE TRIGGER user_system_delete AFTER DELETE ON public.site_user FOR EACH ROW EXECUTE FUNCTION public.user_system_delete();


--
-- Name: celebrity_in_content celebrity_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.celebrity_in_content
    ADD CONSTRAINT celebrity_fkey FOREIGN KEY (cid) REFERENCES public.celebrity(cid) ON DELETE CASCADE;


--
-- Name: celebrity_in_content celebrity_in_content_role_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.celebrity_in_content
    ADD CONSTRAINT celebrity_in_content_role_fkey FOREIGN KEY (role) REFERENCES public.role_classifier(role_id);


--
-- Name: content_in_collection collection_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.content_in_collection
    ADD CONSTRAINT collection_fkey FOREIGN KEY (collection_id) REFERENCES public.collection(collection_id) ON DELETE CASCADE;


--
-- Name: collection collection_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.collection
    ADD CONSTRAINT collection_uid_fkey FOREIGN KEY (uid) REFERENCES public.site_user(uid) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: celebrity_in_content content_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.celebrity_in_content
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;


--
-- Name: content_in_collection content_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.content_in_collection
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;


--
-- Name: countries_of_content content_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.countries_of_content
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;


--
-- Name: genres_of_content content_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.genres_of_content
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;


--
-- Name: review content_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.review
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;


--
-- Name: user_stars content_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.user_stars
    ADD CONSTRAINT content_fkey FOREIGN KEY (content_id) REFERENCES public.content(content_id) ON DELETE CASCADE;


--
-- Name: countries_of_content countries_of_content_country_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.countries_of_content
    ADD CONSTRAINT countries_of_content_country_id_fkey FOREIGN KEY (country_id) REFERENCES public.country(country_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: genres_of_content genres_of_content_genre_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.genres_of_content
    ADD CONSTRAINT genres_of_content_genre_id_fkey FOREIGN KEY (genre_id) REFERENCES public.genre(genre_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: news news_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.news
    ADD CONSTRAINT news_uid_fkey FOREIGN KEY (uid) REFERENCES public.site_user(uid) ON DELETE CASCADE;


--
-- Name: review review_opinion_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.review
    ADD CONSTRAINT review_opinion_fkey FOREIGN KEY (opinion) REFERENCES public.opinion_classifier(oid);


--
-- Name: collection user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.collection
    ADD CONSTRAINT user_fkey FOREIGN KEY (uid) REFERENCES public.site_user(uid) ON DELETE CASCADE;


--
-- Name: review user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.review
    ADD CONSTRAINT user_fkey FOREIGN KEY (uid) REFERENCES public.site_user(uid) ON DELETE CASCADE;


--
-- Name: user_stars user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE ONLY public.user_stars
    ADD CONSTRAINT user_fkey FOREIGN KEY (uid) REFERENCES public.site_user(uid) ON DELETE CASCADE;


--
-- Name: site_user change_all_but_moderators; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY change_all_but_moderators ON public.site_user FOR UPDATE TO moderator USING ((((login)::text = CURRENT_USER) OR ( SELECT (pg_has_role((site_user.login)::name, 'moderator'::name, 'MEMBER'::text) = false))));


--
-- Name: collection; Type: ROW SECURITY; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE public.collection ENABLE ROW LEVEL SECURITY;

--
-- Name: collection create_collections; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY create_collections ON public.collection FOR INSERT TO ordinary_user, moderator WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));


--
-- Name: news create_news; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY create_news ON public.news FOR INSERT TO moderator WITH CHECK (true);


--
-- Name: review create_reviews; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY create_reviews ON public.review FOR INSERT TO ordinary_user, moderator WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));


--
-- Name: user_stars create_stars; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY create_stars ON public.user_stars FOR INSERT TO ordinary_user, moderator WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));


--
-- Name: collection delete_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY delete_all ON public.collection FOR DELETE TO moderator USING (true);


--
-- Name: news delete_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY delete_all ON public.news FOR DELETE TO moderator USING (true);


--
-- Name: review delete_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY delete_all ON public.review FOR DELETE TO moderator USING (true);


--
-- Name: user_stars delete_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY delete_all ON public.user_stars FOR DELETE TO moderator USING (true);


--
-- Name: site_user delete_ban_user; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY delete_ban_user ON public.site_user FOR DELETE TO not_login_user, ordinary_user, moderator USING ((((now() AT TIME ZONE 'Europe/Moscow'::text) - (ban_date)::timestamp without time zone) >= '1 mon'::interval));


--
-- Name: collection delete_own_collections; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY delete_own_collections ON public.collection FOR DELETE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = collection.uid)))::text = CURRENT_USER));


--
-- Name: review delete_own_reviews; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY delete_own_reviews ON public.review FOR DELETE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = review.uid)))::text = CURRENT_USER));


--
-- Name: user_stars delete_own_stars; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY delete_own_stars ON public.user_stars FOR DELETE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = user_stars.uid)))::text = CURRENT_USER));


--
-- Name: collection edit_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY edit_all ON public.collection FOR UPDATE TO moderator USING (true);


--
-- Name: news edit_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY edit_all ON public.news FOR UPDATE TO moderator USING (true);


--
-- Name: review edit_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY edit_all ON public.review FOR UPDATE TO moderator USING (true);


--
-- Name: user_stars edit_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY edit_all ON public.user_stars FOR UPDATE TO moderator USING (true);


--
-- Name: news; Type: ROW SECURITY; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE public.news ENABLE ROW LEVEL SECURITY;

--
-- Name: site_user register; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY register ON public.site_user FOR INSERT TO not_login_user WITH CHECK (true);


--
-- Name: review; Type: ROW SECURITY; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE public.review ENABLE ROW LEVEL SECURITY;

--
-- Name: collection see_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY see_all ON public.collection FOR SELECT TO not_login_user, ordinary_user, moderator USING (true);


--
-- Name: news see_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY see_all ON public.news FOR SELECT TO not_login_user, ordinary_user, moderator USING (true);


--
-- Name: site_user see_all; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY see_all ON public.site_user FOR SELECT TO ordinary_user, moderator USING (true);


--
-- Name: site_user see_as_nologin; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY see_as_nologin ON public.site_user FOR SELECT TO not_login_user USING (true);


--
-- Name: review see_reviews; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY see_reviews ON public.review FOR SELECT TO not_login_user, ordinary_user, moderator USING (true);


--
-- Name: user_stars see_stars; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY see_stars ON public.user_stars FOR SELECT TO not_login_user, ordinary_user, moderator USING (true);


--
-- Name: site_user; Type: ROW SECURITY; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE public.site_user ENABLE ROW LEVEL SECURITY;

--
-- Name: collection update_own_collections; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY update_own_collections ON public.collection FOR UPDATE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = collection.uid)))::text = CURRENT_USER)) WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));


--
-- Name: review update_own_reviews; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY update_own_reviews ON public.review FOR UPDATE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = review.uid)))::text = CURRENT_USER)) WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));


--
-- Name: user_stars update_own_stars; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY update_own_stars ON public.user_stars FOR UPDATE TO ordinary_user USING (((( SELECT site_user.login
   FROM public.site_user
  WHERE (site_user.uid = user_stars.uid)))::text = CURRENT_USER)) WITH CHECK ((( SELECT site_user.is_banned
   FROM public.site_user
  WHERE ((site_user.login)::text = CURRENT_USER)) = false));


--
-- Name: site_user update_self; Type: POLICY; Schema: public; Owner: mirea_4dmin
--

CREATE POLICY update_self ON public.site_user FOR UPDATE TO ordinary_user USING (((login)::text = CURRENT_USER));


--
-- Name: user_stars; Type: ROW SECURITY; Schema: public; Owner: mirea_4dmin
--

ALTER TABLE public.user_stars ENABLE ROW LEVEL SECURITY;

--
-- Name: DATABASE cinema_u49e; Type: ACL; Schema: -; Owner: mirea_4dmin
--

GRANT CONNECT ON DATABASE cinema_u49e TO ordinary_user;


--
-- Name: FUNCTION banned_delete(); Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT ALL ON FUNCTION public.banned_delete() TO moderator;


--
-- Name: FUNCTION user_system_delete(); Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT ALL ON FUNCTION public.user_system_delete() TO moderator;
GRANT ALL ON FUNCTION public.user_system_delete() TO ordinary_user;
GRANT ALL ON FUNCTION public.user_system_delete() TO not_login_user;


--
-- Name: TABLE content; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,UPDATE ON TABLE public.content TO moderator;
GRANT SELECT ON TABLE public.content TO ordinary_user;
GRANT SELECT ON TABLE public.content TO not_login_user;


--
-- Name: COLUMN content.rating; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT UPDATE(rating) ON TABLE public.content TO ordinary_user;


--
-- Name: TABLE best_films; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT ON TABLE public.best_films TO moderator;
GRANT SELECT ON TABLE public.best_films TO ordinary_user;
GRANT SELECT ON TABLE public.best_films TO not_login_user;


--
-- Name: TABLE celebrity; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,UPDATE ON TABLE public.celebrity TO moderator;
GRANT SELECT ON TABLE public.celebrity TO ordinary_user;
GRANT SELECT ON TABLE public.celebrity TO not_login_user;


--
-- Name: SEQUENCE celebrity_cid_seq; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,USAGE ON SEQUENCE public.celebrity_cid_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.celebrity_cid_seq TO ordinary_user;


--
-- Name: TABLE celebrity_in_content; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,UPDATE ON TABLE public.celebrity_in_content TO moderator;
GRANT SELECT ON TABLE public.celebrity_in_content TO ordinary_user;
GRANT SELECT ON TABLE public.celebrity_in_content TO not_login_user;


--
-- Name: TABLE collection; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.collection TO moderator;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.collection TO ordinary_user;
GRANT SELECT ON TABLE public.collection TO not_login_user;


--
-- Name: COLUMN collection.name; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT UPDATE(name) ON TABLE public.collection TO ordinary_user;


--
-- Name: COLUMN collection.description; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT UPDATE(description) ON TABLE public.collection TO ordinary_user;


--
-- Name: SEQUENCE collection_collection_id_seq; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,USAGE ON SEQUENCE public.collection_collection_id_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.collection_collection_id_seq TO ordinary_user;


--
-- Name: SEQUENCE content_content_id_seq; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,USAGE ON SEQUENCE public.content_content_id_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.content_content_id_seq TO ordinary_user;


--
-- Name: TABLE content_in_collection; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.content_in_collection TO moderator;
GRANT SELECT,INSERT,DELETE ON TABLE public.content_in_collection TO ordinary_user;
GRANT SELECT ON TABLE public.content_in_collection TO not_login_user;


--
-- Name: TABLE countries_of_content; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,UPDATE ON TABLE public.countries_of_content TO moderator;
GRANT SELECT ON TABLE public.countries_of_content TO ordinary_user;
GRANT SELECT ON TABLE public.countries_of_content TO not_login_user;


--
-- Name: TABLE country; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,UPDATE ON TABLE public.country TO moderator;
GRANT SELECT ON TABLE public.country TO ordinary_user;
GRANT SELECT ON TABLE public.country TO not_login_user;


--
-- Name: TABLE genre; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,UPDATE ON TABLE public.genre TO moderator;
GRANT SELECT ON TABLE public.genre TO ordinary_user;
GRANT SELECT ON TABLE public.genre TO not_login_user;


--
-- Name: SEQUENCE genre_genre_id_seq; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,USAGE ON SEQUENCE public.genre_genre_id_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.genre_genre_id_seq TO ordinary_user;


--
-- Name: TABLE genres_of_content; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,UPDATE ON TABLE public.genres_of_content TO moderator;
GRANT SELECT ON TABLE public.genres_of_content TO ordinary_user;
GRANT SELECT ON TABLE public.genres_of_content TO not_login_user;


--
-- Name: TABLE news; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.news TO moderator;
GRANT SELECT ON TABLE public.news TO ordinary_user;
GRANT SELECT ON TABLE public.news TO not_login_user;


--
-- Name: SEQUENCE news_nid_seq; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,USAGE ON SEQUENCE public.news_nid_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.news_nid_seq TO ordinary_user;


--
-- Name: TABLE opinion_classifier; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT ON TABLE public.opinion_classifier TO not_login_user;
GRANT SELECT ON TABLE public.opinion_classifier TO ordinary_user;
GRANT SELECT ON TABLE public.opinion_classifier TO moderator;


--
-- Name: TABLE recent_news; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT ON TABLE public.recent_news TO moderator;
GRANT SELECT ON TABLE public.recent_news TO ordinary_user;
GRANT SELECT ON TABLE public.recent_news TO not_login_user;


--
-- Name: TABLE review; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.review TO moderator;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.review TO ordinary_user;
GRANT SELECT ON TABLE public.review TO not_login_user;


--
-- Name: COLUMN review.description; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT UPDATE(description) ON TABLE public.review TO ordinary_user;


--
-- Name: COLUMN review.content_id; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT UPDATE(content_id) ON TABLE public.review TO ordinary_user;


--
-- Name: SEQUENCE review_rid_seq; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,USAGE ON SEQUENCE public.review_rid_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.review_rid_seq TO ordinary_user;


--
-- Name: TABLE role_classifier; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT ON TABLE public.role_classifier TO not_login_user;
GRANT SELECT,DELETE,UPDATE ON TABLE public.role_classifier TO moderator;
GRANT SELECT ON TABLE public.role_classifier TO ordinary_user;


--
-- Name: TABLE site_user; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,DELETE,UPDATE ON TABLE public.site_user TO moderator;
GRANT SELECT,INSERT,DELETE ON TABLE public.site_user TO not_login_user;
GRANT SELECT,DELETE,UPDATE ON TABLE public.site_user TO ordinary_user;


--
-- Name: COLUMN site_user.nickname; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT UPDATE(nickname) ON TABLE public.site_user TO moderator;
GRANT UPDATE(nickname) ON TABLE public.site_user TO ordinary_user;


--
-- Name: COLUMN site_user.email; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT UPDATE(email) ON TABLE public.site_user TO ordinary_user;


--
-- Name: COLUMN site_user.is_banned; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT UPDATE(is_banned) ON TABLE public.site_user TO moderator;


--
-- Name: COLUMN site_user.ban_date; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT UPDATE(ban_date) ON TABLE public.site_user TO moderator;


--
-- Name: COLUMN site_user.ban_reason; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT UPDATE(ban_reason) ON TABLE public.site_user TO moderator;


--
-- Name: SEQUENCE site_user_uid_seq; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,USAGE ON SEQUENCE public.site_user_uid_seq TO not_login_user;
GRANT USAGE ON SEQUENCE public.site_user_uid_seq TO moderator;
GRANT USAGE ON SEQUENCE public.site_user_uid_seq TO ordinary_user;


--
-- Name: TABLE user_stars; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT ON TABLE public.user_stars TO not_login_user;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.user_stars TO ordinary_user;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.user_stars TO moderator;


--
-- Name: SEQUENCE user_stars_sid_seq; Type: ACL; Schema: public; Owner: mirea_4dmin
--

GRANT SELECT,USAGE ON SEQUENCE public.user_stars_sid_seq TO moderator;
GRANT SELECT,USAGE ON SEQUENCE public.user_stars_sid_seq TO ordinary_user;


--
-- PostgreSQL database dump complete
--

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            