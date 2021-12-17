import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Loader {
    ArrayList<Song> listOfSongs = new ArrayList<>();
    ArrayList<Artist> listOfArtists = new ArrayList<>();
    ArrayList<Album> listOfAlbums = new ArrayList<>();

    //Set<Song> finalSongs = new HashSet<>();

    //todo not done
    public void loadSQL(){
            Connection connection = null;
            int[][] songLinks = new int[3][100];
            int[][] albumLinks = new int[2][100];
            try {
                //do not lose this
                connection = DriverManager.getConnection("jdbc:sqlite:src/music.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                //statement.executeUpdate("insert or ignore into songs values(1, 'leo')");
                //statement.executeUpdate("insert or ignore into songs values(2, 'yui')");
                ResultSet rs = statement.executeQuery("select * from songs");

                int index = 0;
                while (rs.next()) {
                    // read the result set
                    listOfSongs.add(new Song(rs.getString("name")));
                    songLinks[0][index] = rs.getInt("id");
                    if(rs.getObject("artist") != null ){
                        songLinks[1][index] = rs.getInt("artist");
                    }
                    if(rs.getObject("album") != null ){
                        songLinks[2][index] = rs.getInt("album");
                    }
                }
                rs = statement.executeQuery("select * from albums");

                index = 0;
                while (rs.next()) {
                    listOfAlbums.add(new Album(rs.getString("name")));
                    albumLinks[0][index] = rs.getInt("id");
                    if(rs.getObject("artist") != null ){
                        albumLinks[1][index] = rs.getInt("artist");
                    }
                }
                rs = statement.executeQuery("select * from artists");

                index = 0;
                while (rs.next()) {
                    listOfArtists.add(new Artist(rs.getString("name")));
                }
                fillSQL(songLinks, albumLinks);
                findDuplicates();
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

    public boolean intIsNumber(int a){
        if(a > 0){
            return true;
        }else{
            return false;
        }
    }

    public String yn(){
        Scanner inputGetter = new Scanner(System.in);
        //String yn = inputGetter.next();
        String yn = "y";    //only for testing! cant make junit to test thing that needs input
        while(!(yn.toLowerCase(Locale.ROOT).equals("y") ||yn.toLowerCase(Locale.ROOT).equals("n"))){
            System.out.println("Please enter y or n: ");
            yn = inputGetter.next();
        }
        return yn.toLowerCase(Locale.ROOT);
    }

    public void findDuplicates(){
        String songname;
        String check;


        String answer;

        for(int i = 0; i < listOfSongs.size(); i++){
            songname = listOfSongs.get(i).name
                    .replaceAll("[^\\w\\s]", "").toLowerCase(Locale.ROOT);
            for(int x = 0; x < listOfSongs.size(); x++){
                check = listOfSongs.get(x).name
                        .replaceAll("[^\\w\\s]", "").toLowerCase(Locale.ROOT);


                if(x != i && songname.equals(check)) {
                    if ((listOfSongs.get(x).performer.equals(listOfSongs.get(i).performer) ||
                            listOfSongs.get(x).album.equals(listOfSongs.get(i).album))) {
                        //why does that print this is an album
                        answer = yn();
                        if (answer.equals("y")) {
                            listOfSongs.remove(listOfSongs.get(x));
                        }
                    }else if((listOfSongs.get(x).album.name == null) && (listOfSongs.get(x).performer.name == null)){
                        //if all fields are null and theres another thing with the same name
                        listOfSongs.remove(listOfSongs.get(x));
                    }else if((listOfSongs.get(x).album.name.equals("")) && (listOfSongs.get(x).performer.name.equals(""))){
                        listOfSongs.remove(listOfSongs.get(x));
                    }
                }

            }
        }
    }

    public void addSong(String title, int id){
        //Song s = new Song(title);

        listOfSongs.add(new Song(title));
    }

    public void addArtist(String name, int id){
        Artist a = new Artist(name);

        listOfArtists.add(a);
    }

    public void addAlbum(String name, int id){
        Album a = new Album(name);
        listOfAlbums.add(a);
    }

    protected void print(){
        for(int i = 0; i < listOfSongs.size(); i++){
            System.out.println(listOfSongs.get(i).name);
        }
    }

    public void loadXML() {
        String filename = "music-library.xml";
        String buffer = new String();
        String attribute[][] =  new String[100][3];

        int index = 0;

        try {
            URL fileURL = this.getClass().getResource(filename);
            File inputFile = new File(fileURL.getPath());
            //wasn't finding file

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(inputFile);

            Element root = doc.getDocumentElement();

            NodeList LibraryItem = root.getChildNodes();

            NodeList songs = LibraryItem.item(1).getChildNodes();
            int index1 = 0;

            //don't care about id here, this is for reading playlists not the library
            for (int a = 0; a < songs.getLength(); a++) {
                if ((songs.item(a).getNodeType() == songs.item(1).getNodeType())) {

                    attribute[index] = getSongThing(songs, a);

                    addSong(attribute[index][0],a);  //creates song in list
                    index++;
                }
            }

            NodeList albums = LibraryItem.item(5).getChildNodes();
            for (int a = 0; a < albums.getLength(); a++) {
                if ((albums.item(a).getNodeType() == albums.item(1).getNodeType())) {
                    addAlbum(getAttribute(albums, a),a);
                }
            }

            NodeList artists = LibraryItem.item(3).getChildNodes();
            for (int a = 0; a < artists.getLength(); a++) {
                if ((artists.item(a).getNodeType() == artists.item(1).getNodeType())) {
                    String artist = (getAttribute(artists, a));
                    addArtist(artist,a);
                }
            }
            fill(attribute);
        } catch (Exception e) {
            System.out.println("Parsing error:" + e);
        }
        findDuplicates();
    }

    public String getAttribute(NodeList Artists, int i) {
        NodeList artistAttributes = Artists.item(i).getChildNodes();

        for (int b = 0; b < artistAttributes.getLength(); b++) {
            if ((artistAttributes.item(b).getNodeType()
                    == artistAttributes.item(1).getNodeType())) {
                NodeList thing = artistAttributes.item(b).getChildNodes();

                return thing.item(0).getNodeValue().replaceAll("\\W", "");
            }
        }
        return null;
    }

    public String[] getSongThing(NodeList songs, int i){
        //didn't let me just pass in the node for some reason
        String buffer = new String();
        String attribute[] =  new String[3];
        NodeList songAttributes = songs.item(i).getChildNodes();

        int index = 0;
        int check = 1;

        for (int b = 0; b < songAttributes.getLength(); b++) {
            if ((songAttributes.item(b).getNodeType()
                    == songAttributes.item(1).getNodeType())) {
                NodeList thing = songAttributes.item(b).getChildNodes();

                buffer = thing.item(0).getNodeValue();
                //todo spaces, need to use regex and stuff
                buffer = buffer.replaceAll("  ", "").replaceAll("\n", "");
                if(buffer.charAt(0) == ' '){
                    buffer = buffer.substring(1);
                }
                attribute[index] = buffer.replaceAll("  ", "").replaceAll("\n", "");
                index++;
            }
        }
        return attribute;
    }

    public void fillSQL(int[][] songAttribute, int[][] albumAttribute) {
        //i know this would all be faster with streams but i can't handle that right now
        for(int a = 0; a < songAttribute[0].length; a++){
            if(intIsNumber(songAttribute[1][a])){
                //attach artist
                for(int b = 0; b < listOfArtists.size(); b++){
                    if(listOfArtists.get(b).entityID == songAttribute[1][a]){
                        listOfSongs.get(a).setPerformer(listOfArtists.get(b));
                    }
                }
            }
            if(intIsNumber(albumAttribute[1][a])){
                //attach album
                for(int b = 0; b < listOfAlbums.size(); b++){
                    if(listOfAlbums.get(b).entityID == albumAttribute[1][a]){
                        listOfAlbums.get(a).setArtist(listOfArtists.get(b));
                    }
                }
            }
        }
        for(int a = 0; a < songAttribute[0].length; a++) {
            if (intIsNumber(songAttribute[1][a])) {
                //attach artist
                for (int b = 0; b < listOfArtists.size(); b++) {
                    if (listOfArtists.get(b).entityID == songAttribute[1][a]) {
                        listOfSongs.get(a).setPerformer(listOfArtists.get(b));
                    }
                }
            }
        }
    }

    public void fill(String[][] attribute){
        //list of songs, albums, and artist complete, now connect those things
        //print();
        ArrayList<Song> albumSongs;// = new ArrayList<>();
        ArrayList<Song> artistSongs;// = new ArrayList<>();
        ArrayList<Album> artistAlbums;// = new ArrayList<>();
        boolean check = false;
        Album newAlbum;
        Artist newArtist;

        //sets artist and album for song each song
        for(int z = 0; z < listOfSongs.size(); z++){
            check = false;
            for(int y = 0; y < listOfArtists.size(); y++){
                if(listOfArtists.get(y).name.equals(attribute[z][1])){  //found right artist
                    listOfSongs.get(z).setPerformer(listOfArtists.get(y));
                    check = true;
                }
            }
            if(check == false){
                newArtist = new Artist(attribute[z][1]);
                listOfArtists.add(newArtist);
                listOfSongs.get(z).setPerformer(newArtist);
            }
            check = false;
            for(int x = 0; x < listOfAlbums.size(); x++){
                //System.out.println(attribute[z][2]);
                if(listOfAlbums.get(x).name.equals(attribute[z][2])){  //found right album
                    listOfSongs.get(z).setAlbum(listOfAlbums.get(x));
                    check = true;
                }
            }
            if(check == false){
                newAlbum = new Album(attribute[z][2]);
                listOfAlbums.add(newAlbum);
                listOfSongs.get(z).setAlbum(newAlbum);
            }
            //System.out.println(listOfSongs.get(0).performer.name);
            //System.out.println(listOfSongs.get(0).album.name);
        }

        //uses fields of songs to set fields for albums
        for(int z = 0; z < listOfAlbums.size(); z++){
            albumSongs = new ArrayList<>();
            check = false;
            for(int y = 0; y < listOfSongs.size(); y++){
                if(listOfSongs.get(y).album.name.equals(listOfAlbums.get(z).name)){
                    albumSongs.add(listOfSongs.get(y));
                }
            }
            if(albumSongs.size() > 0) {
                listOfAlbums.get(z).setArtist(albumSongs.get(0).performer);
                listOfAlbums.get(z).setSongs(albumSongs);
            }else{
                listOfAlbums.remove(listOfAlbums.get(z));
            }
        }

        //use fields of songs to create list of songs and list of albums for artists
        for(int z = 0; z < listOfArtists.size(); z++) {
            artistSongs = new ArrayList<>();
            artistAlbums = new ArrayList<>();
            for (int y = 0; y < listOfSongs.size(); y++) {
                if (listOfSongs.get(y).performer.name.equals(listOfArtists.get(z).name)) {
                    //found artist
                    artistSongs.add(listOfSongs.get(y));
                }
            }
            for (int x = 0; x < listOfAlbums.size(); x++) {
                if (listOfAlbums.get(x).artist.name.equals(listOfArtists.get(z).name)) {
                    artistAlbums.add(listOfAlbums.get(x));
                }
            }
            if (artistSongs.size() > 0) {
                listOfArtists.get(z).setSongs(artistSongs);
                listOfArtists.get(z).setAlbums(artistAlbums);
            } else {
                listOfArtists.remove(listOfArtists.get(z));
            }
        }
    }

    }