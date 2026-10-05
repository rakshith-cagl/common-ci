
CREATE OR REPLACE FUNCTION DateDiff(units VARCHAR(30), start_t TIMESTAMP, end_t TIMESTAMP)
     RETURNS INT AS $$
   DECLARE
     diff_interval INTERVAL;
     diff INT = 0;
   BEGIN
     diff_interval = end_t - start_t;
     diff = diff + DATE_PART('day', diff_interval);
     IF units IN ('week') THEN
       diff = diff/7;
       RETURN diff;
     END IF;
     IF units IN ('day') THEN
       RETURN diff;
     END IF;
     diff = diff * 24 + DATE_PART('hour', diff_interval);
     IF units IN ('hour') THEN
        RETURN diff;
     END IF;
     diff = diff * 60 + DATE_PART('minute', diff_interval);
     IF units IN ('minute') THEN
        RETURN diff;
     END IF;
     diff = diff * 60 + DATE_PART('second', diff_interval);
     RETURN diff;
   END;
   $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION to_milliseconds(start_t TIMESTAMP,end_t TIMESTAMP)
 RETURNS INT AS $$
 DECLARE
     diff INT = 0;
 BEGIN
     diff=round((EXTRACT(EPOCH FROM end_t)-EXTRACT(EPOCH FROM start_t))*1000);
     RETURN diff;
 END;
$$ LANGUAGE plpgsql;
