CREATE TRIGGER status_last_updated_stamp
    BEFORE UPDATE
    ON status
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER party_type_last_updated_stamp
    BEFORE UPDATE
    ON party_type
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER party_last_updated_stamp
    BEFORE UPDATE
    ON party
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER person_last_updated_stamp
    BEFORE UPDATE
    ON person
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER user_login_last_updated_stamp
    BEFORE UPDATE
    ON user_login
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER security_group_last_updated_stamp
    BEFORE UPDATE
    ON security_group
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER security_permission_last_updated_stamp
    BEFORE UPDATE
    ON security_permission
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER security_group_permission_last_updated_stamp
    BEFORE UPDATE
    ON security_group_permission
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER user_login_security_group_last_updated_stamp
    BEFORE UPDATE
    ON user_login_security_group
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER application_type_last_updated_stamp
    BEFORE UPDATE
    ON application_type
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER application_last_updated_stamp
    BEFORE UPDATE
    ON application
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER track_locations_last_updated_stamp
    BEFORE UPDATE
    ON track_locations
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER content_type_last_updated_stamp
    BEFORE UPDATE
    ON content_type
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER content_last_updated_stamp
    BEFORE UPDATE
    ON content
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
CREATE TRIGGER product_content_last_updated_stamp
    BEFORE UPDATE
    ON product_content
    FOR EACH ROW
EXECUTE PROCEDURE set_last_updated_stamp();
