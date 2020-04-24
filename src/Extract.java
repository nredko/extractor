import java.io.*;
import java.sql.*;
import com.sybase.jdbcx.*;

public class Extract {
    static String _user = "";
    static String _password = "";
    static String _server = "";
    static String _db = "";
    static String _query = "";
    static String _propValue = "";

    static Connection _con;
    static SybDriver _sybDriver = null;

    public static void main(String[] args) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        InputStream is = null;
        OutputStream os = null;

        try {
            _sybDriver = (SybDriver) Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance();
            DriverManager.registerDriver((Driver) _sybDriver);
            
            // check parameters    
            if (!processCommandline(args) || _user == "" || _password == "" || _server == "" || _db == ""
                    || _query == "") {
                System.out.println("Syntax:\n"
                        + "\tjava Extract -u <username> -p <password> -s <servername> -d <database> -q <query with 2 columns>");
                return;
            }

            //register deriver
            Class.forName("com.sybase.jdbc4.jdbc.SybDriver");
            _con = DriverManager.getConnection("jdbc:sybase:Tds:" + _server, _user, _password);

            // change db
            st = _con.createStatement();
            st.executeUpdate("use " + _db);
            checkForWarning(st.getWarnings());
            st.close();
            
            // execute query
            st = _con.createStatement();
            rs = st.executeQuery(_query);
            
            //iterate over records
            while (rs.next()) {
                is = rs.getBinaryStream(2);
                String filePath = rs.getString(1);
                
                File file = new File(filePath);
                file.getParentFile().mkdirs();
                System.out.println(filePath);
                
                // write content
                os = new FileOutputStream(file);
                byte[] content = new byte[1024];
                int size = 0;
                while ((size = is.read(content)) != -1) {
                    os.write(content, 0, size);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (os != null) os.close();
                if (st != null) st.close();
                if (_con != null) _con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static int parseArguments(String argv[], int pos) {
        int argc = argv.length - 1; // # arguments specified
        String arg = argv[pos].substring(1);
        int argLen = arg.length(); // Length of arg
        int incrementValue = 0;

        if (argLen > 1) {
            // The argument value follows (i.e. -Uusername)
            _propValue = arg.substring(1);
        } else {
            if (pos == argc || argv[pos + 1].regionMatches(0, "-", 0, 1)) {
                // We are either at the last argument or the next option
                // starts with '-'.
                _propValue = null;
            } else {
                // The argument value is the next argument (i.e. -U username)
                _propValue = argv[pos + 1];
                incrementValue = 1;
            }
        }

        return (incrementValue);
    }
    // END -- parseArguments()

    static private boolean processCommandline(String args[]) {
        // * DONE
        String arg;
        int errorCount = 0;
        for (int i = 0; i < args.length; i++) {
            arg = args[i];
            if (arg.regionMatches(0, "-", 0, 1)) {
                try {
                    switch (arg.charAt(1)) {
                        case 'd':
                            i += parseArguments(args, i);
                            if (_propValue != null) {
                                _db = _propValue;
                            } else {
                                errorCount++;
                            }
                            break;
                        case 'u':
                            i += parseArguments(args, i);
                            if (_propValue != null) {
                                _user = _propValue;
                            } else {
                                errorCount++;
                            }
                            break;
                        case 'p':
                            i += parseArguments(args, i);
                            _password = (_propValue == null ? "" : _propValue);
                            break;
                        case 's':
                            i += parseArguments(args, i);
                            if (_propValue != null) {
                                _server = _propValue;
                            } else {
                                errorCount++;
                            }
                            break;
                        case 'q':
                            i += parseArguments(args, i);
                            if (_propValue != null) {
                                _query = _propValue;
                            } else {
                                errorCount++;
                            }
                            break;
                        default:
                            System.out.println("Invalid command line option: " + arg);
                            errorCount++;
                            break;
                    }
                } catch (ArrayIndexOutOfBoundsException aioobe) {
                    System.out.println("missing option argument");
                    errorCount++;
                }
            } else {
                // The syntax has no non "-" arguments
                errorCount++;
            }
        }
        return (errorCount == 0);
    }

    static boolean checkForWarning(SQLWarning warn) throws SQLException {
        boolean rc = false;

        // If a SQLWarning object was given, display the warning messages.
        // Note that there could be multiple warnings chained together

        if (warn != null) {
            System.out.println("\n *** Warning ***\n");
            rc = true;
            while (warn != null) {
                System.out.println("SQLState: " + warn.getSQLState() + "\n");
                System.out.println("Message:  " + warn.getMessage() + "\n");
                System.out.println("Vendor:   " + warn.getErrorCode() + "\n");
                warn = warn.getNextWarning();
            }
        }
        return rc;
    }

}