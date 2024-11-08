CREATE OR REPLACE FUNCTION parameters_referenced_in(table_name TEXT, parameters_id INTEGER)
    RETURNS BOOLEAN AS
$$
DECLARE
    result BOOLEAN;
BEGIN
    EXECUTE format(
        'SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM %I WHERE parameters_id = $1',
        table_name
            )
        INTO result
        USING parameters_id;

    return result;
END;
$$ LANGUAGE plpgsql;