package edu.wvup.cs460.db;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * "Code early, Code often."
 */
final class DBSetupSQLStrings {
    final String DB_CREATE;// = "CREATE DATABASE "+ DBContext.dbName+";";
    final String DB_DROP; //= "DROP DATABASE "+ DBContext.dbName+";";
    final String DB_EXISTS;
    final String RW_USER_DROP;
    final String RW_USER_CREATE;
    final String RW_USER_GRANT;
    final String CREATE_COURSE_INSTANCE_TABLE =
            "CREATE TABLE IF NOT EXISTS COURSE_INSTANCE ("+
                    "CRN VARCHAR(12) NOT NULL,"+ //CRN of course, like 5609
                    "TYPE VARCHAR(8) NOT NULL,"+ //Short type, like "E-C" or "Lec"
                    "CROSSLISTED BOOLEAN NOT NULL DEFAULT FALSE,"+ //Boolean
                    "SUBJECT VARCHAR(8) NOT NULL,"+ //Subject, like ECON
                    "COURSE_NUMBER VARCHAR(8) NOT NULL,"+
                    "COURSE_TITLE VARCHAR(255) NOT NULL,"+
                    "CREDITS SMALLINT NOT NULL,"+
                    "DAYS VARCHAR(16) NOT NULL,"+
                    "TIME VARCHAR(32) NOT NULL,"+
                    "INSTRUCTOR VARCHAR(64) NOT NULL,"+
                    "ROOM VARCHAR(32) NOT NULL,"+
                    "START_DATE timestamp NOT NULL,"+
                    "END_DATE timestamp NOT NULL,"+
                    "SEATS_AVAILABLE SMALLINT NOT NULL,"+
                    "TERM_LENGTH VARCHAR(64) NOT NULL,"+
                    "CAMPUS VARCHAR(32) NOT NULL," +
                    "TERM VARCHAR(8) NOT NULL,"+
                    "YEAR VARCHAR(4) NOT NULL,"+//making this a string on purpose.
                    "PRIMARY KEY (CRN));";


    final String CREATE_COURSE_META_TABLE =
            "CREATE TABLE IF NOT EXISTS COURSE_META ("+
                    "SUBJECT VARCHAR(8) NOT NULL,"+ //Subject, like ECON
                    "COURSE_NUMBER VARCHAR(8) NOT NULL,"+
                    "HUMANITIES BOOLEAN NOT NULL DEFAULT FALSE,"+
                    "NATSCI BOOLEAN NOT NULL DEFAULT FALSE,"+
                    "SOCSCI BOOLEAN NOT NULL DEFAULT FALSE,"+
                    "MATH BOOLEAN NOT NULL DEFAULT FALSE,"+
                    "COMMUNICATIONS BOOLEAN NOT NULL DEFAULT FALSE,"+
                    "COMPLIT BOOLEAN NOT NULL DEFAULT FALSE,"+
                    "UPPERDIV BOOLEAN NOT NULL DEFAULT FALSE," +
                    "PRIMARY KEY (SUBJECT, COURSE_NUMBER))";

    final String CREATE_TERMS_TABLE =
            "CREATE TABLE IF NOT EXISTS SCHOOL_TERMS ("+
                    "SEASON VARCHAR(32) NOT NULL, " +
                    "YEAR smallint NOT NULL);";

    final String CREATE_URL_CACHE_TABLE =
            "create table if not exists URL_CACHE (URL VARCHAR(255) NOT NULL, LASTMODIFIED TIMESTAMP WITH TIME ZONE NOT NULL, "+
            "PRIMARY KEY (URL));";

    DBSetupSQLStrings(DBContext context){
        DB_CREATE = constructDBCreate(context);
        DB_DROP = constructDBDrop(context);
        DB_EXISTS = constructDoesDBExist(context);
        RW_USER_DROP =  constructReadWriteUserDrop(context);
        RW_USER_CREATE = constructReadWriteUserCreate(context);
        RW_USER_GRANT = constructReadWriteUserGrant(context);
    }

    private String constructDBCreate(DBContext context){
        StringBuilder sb = new StringBuilder("CREATE DATABASE ");
        sb.append(context.DB_NAME).append(";");
        return sb.toString();
    }
    private String constructDBDrop(DBContext context){
        StringBuilder sb = new StringBuilder("DROP DATABASE ");
        sb.append(context.DB_NAME).append(";");
        return sb.toString();
    }
    private  String constructReadWriteUserDrop(DBContext context){
        StringBuilder sb = new StringBuilder("DROP USER ");
        sb.append(context.DB_READ_WRITE_USERNAME).append(";");
        return sb.toString();
    }
    private String constructReadWriteUserCreate(DBContext context){
        StringBuilder sb = new StringBuilder("CREATE USER ");
        sb.append(context.DB_READ_WRITE_USERNAME).append(" WITH PASSWORD '").append(context.DB_READ_WRITE_PASSWORD);
        sb.append("';");
        return sb.toString();

    }
    private String constructReadWriteUserGrant(DBContext context){
        StringBuilder sb = new StringBuilder("GRANT ALL ON DATABASE ");
        sb.append(context.DB_NAME).append(" TO ").append(context.DB_READ_WRITE_USERNAME);
        sb.append(";");
        return sb.toString();

    }

    private String constructDoesDBExist(DBContext context){
        StringBuilder sb = new StringBuilder("SELECT count(*) from pg_database WHERE datname='");
        sb.append(context.DB_NAME).append("';");
        return sb.toString();
    }

}
