
CREATE OR REPLACE FUNCTION time_diff_milliSecs (aTimestamp timestamp, bTimestamp timestamp)
  RETURN NUMBER
as
  vMS NUMBER DEFAULT 0;
BEGIN
  select
    (extract(day from bTimestamp - aTimestamp)*86400+
    extract(hour from bTimestamp - aTimestamp)*3600+
    extract(minute from bTimestamp - aTimestamp)*60+
    extract(second from bTimestamp - aTimestamp)) * 1000 into vMS
    from dual;
  RETURN vMS;
END;
/

CREATE OR REPLACE FUNCTION time_diff (DATE_1 IN DATE, DATE_2 IN DATE) RETURN NUMBER IS
 NDATE_1   NUMBER;
 NDATE_2   NUMBER;
 NSECOND_1 NUMBER(5,0);
 NSECOND_2 NUMBER(5,0);
BEGIN
  -- return Julian date number from first date (DATE_1)
  NDATE_1 := TO_NUMBER(TO_CHAR(DATE_1, 'J'));

  -- return Julian date number from second date (DATE_2)
  NDATE_2 := TO_NUMBER(TO_CHAR(DATE_2, 'J'));

  -- return seconds since midnight from first date (DATE_1)
  NSECOND_1 := TO_NUMBER(TO_CHAR(DATE_1, 'SSSSS'));

  -- return seconds since midnight from second date (DATE_2)
  NSECOND_2 := TO_NUMBER(TO_CHAR(DATE_2, 'SSSSS'));

  RETURN (((NDATE_2 - NDATE_1) * 86400)+(NSECOND_2 - NSECOND_1));
END;
/
