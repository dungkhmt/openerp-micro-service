create or replace procedure insert_bay_data (id uuid, x_bay integer, y_bay integer, x_gap integer, y_gap integer)
as $$
declare
x_warehouse integer;
	y_warehouse integer;
	insert_sql varchar(200);
	codes text array default array['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'] ;
	code_index integer;
	code varchar(50);
begin
execute 'select ww.width from wms_warehouse ww where ww.warehouse_id = $1 ' into x_warehouse using id;
execute 'select ww.length from wms_warehouse ww where ww.warehouse_id = $1 ' into y_warehouse using id;
code_index = 1;
for x in 0 .. x_warehouse by x_bay + x_gap loop
		if code_index < 10 then code = codes[code_index] || '0' || text(code_index);
else code = codes[code_index] || text(code_index);
end if;
for y in 0 .. y_warehouse by y_bay + y_gap loop
			exit when x + x_bay > x_warehouse;
			exit when y + y_bay > y_warehouse;
			insert_sql = 'insert into wms_bay (bay_id, warehouse_id, code, x, y, x_long, y_long) VALUES (uuid_generate_v1(), $1, $2, $3, $4, $5, $6) ';
execute insert_sql using id, code, x, y, x_bay, y_bay;
end loop;
		code_index = code_index + 1;
end loop;
end; $$
language plpgsql;

-- call insert_bay_data('bc220690-08dd-4825-b250-a4b2b4997756', 10, 10, 5, 5);
-- call insert_bay_data('f59b6a4d-9be0-40ce-ada1-d0ce78fe5fde', 10, 10, 5, 5);
-- call insert_bay_data('e108d675-04ce-4e14-a921-50326b295be6', 10, 10, 5, 5);
-- call insert_bay_data('b2990344-3fab-419f-ad9c-32ba44948f03', 10, 10, 5, 5);
-- call insert_bay_data('5f35ac00-dfc9-4181-a933-defcde2a8728', 10, 10, 5, 5);
-- call insert_bay_data('86cfebf3-f366-4f07-ba68-49d94989ebf7', 10, 10, 5, 5);
-- call insert_bay_data('fe8e941c-cb5b-42c9-b101-199981778975', 10, 10, 5, 5);
-- call insert_bay_data('f9ad52f5-141f-42d6-af13-7ac40e9b0587', 10, 10, 5, 5);
-- call insert_bay_data('893c33d0-76bb-45e1-b789-a77c4c098e68', 10, 10, 5, 5);
-- call insert_bay_data('f59b6a4d-9be0-40ce-ada1-d0ce78fe5fde', 10, 10, 5, 5);
