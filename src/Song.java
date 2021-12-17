import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Song extends Entity {
        protected Album album;
        protected Artist performer;

        //added those nulls in
        public Song(String name) {
            super(name);
            //album = new Album("");
            //performer = new Artist("");
        }

        /*public Song(String name, String Artist, String Albumm) {
            super(name,);
            album = new Album(Albumm);
            performer = new Artist(Artist);
        }*/

        protected Album getAlbum() {
            return album;
        }

        protected void setAlbum(Album album) {
            this.album = album;
        }

        public Artist getPerformer() {
            return performer;
        }

        public void setPerformer(Artist performer) {
            this.performer = performer;
        }

        public String toString() {
            return super.toString() + " " + this.performer + " " + this.album;

        }

        //todo should go in load
    /*public static void main(String[] args) {

        Connection connection = null;
        try {
            // create a database connection
            //todo do not lose this
            connection = DriverManager.getConnection("jdbc:sqlite:src/music.db");   //renamed that but not sure it took
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("insert or ignore into songs values(1, 'leo')");
            statement.executeUpdate("insert or ignore into songs values(2, 'yui')");
            ResultSet rs = statement.executeQuery("select * from songs");
            while (rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }*/

    public static void main(String[] args) {

        Connection connection = null;
        try {
            // create a database connection
            //todo do not lose this
            connection = DriverManager.getConnection("jdbc:sqlite:src/music.db");   //renamed that but not sure it took
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("insert or ignore into artists values(1, 'Beatles')");
            statement.executeUpdate("insert or ignore into artists values(2, 'David Bowie')");
            ResultSet rs = statement.executeQuery("select * from artists");
            while (rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

}
