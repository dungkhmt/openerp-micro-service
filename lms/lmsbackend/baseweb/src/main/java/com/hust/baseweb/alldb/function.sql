CREATE FUNCTION set_last_updated_stamp()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    NEW.last_updated_stamp := now();
    RETURN NEW;
END;
$$;


-- CREATE OR REPLACE FUNCTION public.uuid_generate_v1()
--  RETURNS uuid
--  LANGUAGE c
--  PARALLEL SAFE STRICT
-- AS '$libdir/uuid-ossp', $function$uuid_generate_v1$function$
-- ;
