SET SERVEROUTPUT ON SIZE 1000000
DECLARE
i NUMBER DEFAULT 1;
BEGIN 
WHILE i<=10 LOOP
    DBMS_OUTPUT.put_line(
          i
    );
    i:=i+1;
END LOOP;
END;
/