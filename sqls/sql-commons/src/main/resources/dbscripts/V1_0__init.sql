CREATE ALIAS ADDDATE FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.addDate";
CREATE ALIAS ADDTIME FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.addTime";
CREATE ALIAS TIME FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.time";
CREATE ALIAS UTC_TIMESTAMP FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.utcTimestamp";
CREATE ALIAS UTC_DATE FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.utcDate";
CREATE ALIAS UTC_TIME FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.utcTime";
CREATE ALIAS FROM_DAYS FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.fromDays";
CREATE ALIAS TO_DAYS FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.toDays";
CREATE ALIAS TO_SECONDS FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.toSeconds";
CREATE ALIAS TIME_TO_SEC FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.timeToSeconds";
CREATE ALIAS DATE_FORMAT FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.dateFormat";
CREATE ALIAS TIME_FORMAT FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.timeFormat";
CREATE ALIAS LAST_DAY FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.lastDay";
CREATE ALIAS MAKEDATE FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.makeDate";
CREATE ALIAS MAKETIME FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.makeTime";
CREATE ALIAS SEC_TO_TIME FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.secondsToTime";
CREATE ALIAS SLEEP FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.sleep";
CREATE ALIAS STR_TO_DATE FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.strToDate";
CREATE ALIAS SUBDATE FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.subDate";
CREATE ALIAS SUBTIME FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.subTime";
CREATE ALIAS YEARWEEK FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.yearWeek";
CREATE ALIAS WEEKOFYEAR FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.weekOfYear";
CREATE ALIAS WEEKDAY FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.weekDay";
CREATE ALIAS MICROSECOND FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.microSecond";
CREATE ALIAS CONVERT_TZ FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.convertTZ";
CREATE ALIAS PERIOD_ADD FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.periodAdd";
CREATE ALIAS PERIOD_DIFF FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.periodDiff";
CREATE ALIAS TIMEDIFF FOR "org.mvnsearch.h2.mysql.DateTimeFunctions.timeDiff";

CREATE TABLE sequence
(
    name VARCHAR(50) NOT NULL UNIQUE,
    val INTEGER,
    PRIMARY KEY (name)
);

INSERT INTO sequence values ('global_version', 1);