import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//todo check for duplicates before add thing
//todo add songs to albums and songs and alubms to artists
//https://musicbrainz.org/ws/2/artist?query=beatles&fmt=xml
public class Display {
    static Loader loader = new Loader();
    static ArrayList<Playlist> playlists = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        loader.loadSQL();
        menu();
    }

    /**
     * menu
     * The main user interface of Display. Prompts user for input, calls the appropriate
     * function, then calls itself again unless exited.
     * @throws IOException
     */
    public static void menu() throws IOException {
        Scanner inputGetter = new Scanner(System.in);
        int intResponse;
        String strResponse;

        //display all artists or all albums
        System.out.println("1. Display all songs\n2. Display all albums\n" +
                "3. Display all artists\n4. Display playlists\n5. Add song\n" +
                "6. Add artist\n7. Add album\n8. Create playlist" +
                "\n9. Enter alternate name to see if artist is in database" +
                "\n10. Play playlist\n11. Exit");
        //add a delete option?
        intResponse = inputGetter.nextInt();

        while( intResponse < 1 || intResponse > 11) {
            System.out.println("Not a valid entry. Please enter a number from the menu: ");
            intResponse = inputGetter.nextInt();
        }

        if(intResponse == 1){
            showAllSongs(loader.listOfSongs);
            menu();
        }else if(intResponse == 2){
            showAllAlbums();
            menu();
        }else if(intResponse == 3){
            showAllArtists();
            menu();
        }else if(intResponse == 4){
            showAllPlaylists();
            menu();
        }else if(intResponse == 5){
            addSong();
            menu();
        }else if(intResponse == 6){
            addArtist();
            menu();
        }else if(intResponse == 7){
            addAlbum();
            menu();
        }else if(intResponse == 8){
            createPlaylist();
            menu();
        }else if(intResponse == 9){
            menuItem9();
            menu();
        }else if(intResponse == 10){
            playPlaylist();
            menu();
        }else if(intResponse == 11){
            saveSQL();
            System.out.println("Goodbye!");
        }else{
            //could throw error instead?
            System.out.println("error in intResponse");
            menu();
        }
    }

    /**
     * menuItem9
     * a shell for the useMusicBrainz function, which takes the name of an artist and
     * uses the online database to see if it is an alias for an artist already in our
     * program
     */
    public static void menuItem9(){
        Scanner inputGetter = new Scanner(System.in);
        System.out.println("Enter name of artist you would like to check for:");
        String artistName = inputGetter.nextLine();

        Artist artist = useMusicBrainz(artistName);
        if(artist != null) {
            System.out.println(artistName + " found in database as " + artist.name + ".");
        }else{
            System.out.println(artistName + "Not found in database.");
        }
    }

    /**
     * saveSQL
     * updates the music.db file with songs, albums and artists added by the user
     */
    public static void saveSQL(){
        Connection connection = null;
        Song song;
        Album album;
        Artist artist;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src/music.db");   //renamed that but not sure it took
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            for(int i = 0; i < loader.listOfSongs.size(); i ++) {
                song = loader.listOfSongs.get(i);
                statement.executeUpdate("insert or ignore into songs values("
                + song.entityID + ", \'" + song.name + "\', " + song.performer.entityID +
                        ", " + song.album.entityID +")");
            }
            for(int i = 0; i < loader.listOfAlbums.size(); i ++) {
                album = loader.listOfAlbums.get(i);
                statement.executeUpdate("insert or ignore into songs values("
                        + album.entityID + ", \'" + album.name + "\', " +
                        album.artist.entityID + ")");
            }
            for(int i = 0; i < loader.listOfArtists.size(); i ++) {
                artist = loader.listOfArtists.get(i);
                statement.executeUpdate("insert or ignore into songs values("
                        + artist.entityID + ", \'" + artist.name + "\')");
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

    /**
     * writeXML
     * writes an arraylist (from a playlist) to an XML document
     * @param songs
     * @throws IOException
     */
    public static void writeXML(ArrayList<Song> songs)throws IOException {
        FileWriter fileWriter = new FileWriter("Playlist" + playlists.size());
        PrintWriter printWriter =new PrintWriter(fileWriter);
        printWriter.print("<?xml version=\"1.0\"  ?>\n<library>\n<songs>");
        for(int x = 0; x < songs.size(); x++){
            printWriter.print(SongtoXML(songs.get(x)));
        }

        printWriter.print("\n</albums>\n</library>");

        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * SongtoXML
     * takes a song and writes it and all of its parameters to a string
     * @param s
     * @return
     */
    public static String SongtoXML(Song s){
        return ("\n<song id=\"" + s.entityID + "\">\n" + "<title>\n" + s.name +
                "</title>\n" + "\n<artist id=\"" + s.performer.entityID  + "\">\n" + s.performer.name
                + "\n</artist>\n<album id=\"" + s.album.entityID + "\">\n" + s.album.name +
                "</album>\n</song>");
    }

    /**
     * getNamesMB
     * using the XML file, gets name from specific artist
     * @param artist
     * @return
     */
    public static String getNamesMB(NodeList artist){    //pass in childNodes of artists
        NodeList name = artist.item(0).getChildNodes();
        return name.item(0).getNodeValue();
    }

    /**
     * useMusicBrainz
     * Uses MusicBrainz database to search all alternate names for an artist to see if
     * they match the name the user is checking for
     * @param artistName
     * @return
     */
    public static Artist useMusicBrainz(String artistName){
        String name;
        String url = "https://musicbrainz.org/ws/2/artist?query=" + artistName
                + "&fmt=xml";
        //String url = "https://musicbrainz.org/ws/2/artist?query=the%20beatles&fmt=xml";

        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(false);
            f.setValidating(false);
            DocumentBuilder b = f.newDocumentBuilder();
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.addRequestProperty("Accept", "application/xml");
            Document doc = b.parse(urlConnection.getInputStream());
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList LibraryItem = root.getChildNodes();
            NodeList artists = LibraryItem.item(0).getChildNodes();
            for(int j = 0; j < loader.listOfArtists.size(); j++) {
                name = loader.listOfArtists.get(j).name;
                for (int i = 0; i < artists.getLength(); i++) {
                    if (getNamesMB(artists.item(i).getChildNodes()).equals(name)) {
                        return loader.listOfArtists.get(j);
                    }
                    //does that work?
                }
            }
        } catch (Exception e) {
            System.out.println("Parsing error:" + e);
        }
        return null;
    }

    /**
     * playPlaylist
     * prints names of all songs in a playlist
     */
    public static void playPlaylist(){
        Scanner inputGetter = new Scanner(System.in);
        System.out.println("Enter number of playlist: ");
        int playlist = inputGetter.nextInt();
        boolean check = false;

        for(int a = 0; a < playlists.size(); a++){
            if(a == playlist){
                check = true;
                showAllSongs(playlists.get(a).listOfSongs);
            }
        }
        if(check == false){
            System.out.println("No such playlist.");
        }
    }

    /**
     * createPlaylist
     * lets the user add songs one at a time to create a playlist
     * @throws IOException
     */
    public static void createPlaylist() throws IOException {
        Scanner inputGetter = new Scanner(System.in);
        Playlist pl = new Playlist();
        boolean check = false;

        System.out.println("Enter id of first song to add to playlist: ");
        int song = inputGetter.nextInt();

        while(song != 0) {
            for (int i = 0; i < loader.listOfSongs.size(); i++) {
                if (loader.listOfSongs.get(i).entityID == song) {
                    check = true;
                    pl.listOfSongs.add(loader.listOfSongs.get(i));
                }
            }
            if(check = false){
                System.out.println("Song does not exist.");
            }
            System.out.println("Enter id of next song to add to playlist, or 0 to finish: ");
            song = inputGetter.nextInt();
            //allows for duplicates because what if you want a song twice
        }
        playlists.add(pl);
        //writeXML(pl.listOfSongs); todo null
    }

    /**
     * showAllPlaylists
     * prints names of all playlists, which should just be Playlist 1, Playlist 2, etc
     */
    public static void showAllPlaylists(){
        Playlist pl = new Playlist();
        for(int i = 0; i < playlists.size(); i++){
            pl = playlists.get(i);
            System.out.println("Playlist " + i +":");
            for(int j = 0; j < pl.listOfSongs.size(); j++){
                System.out.println(pl.listOfSongs.get(j).name);
            }
            System.out.println("\n");
        }
    }

    /**
     * showAllSongs
     * prints names of all songs in a specific list, either all in library or a playlist
     * @param songs
     */
    public static void showAllSongs(ArrayList<Song> songs){
        for(int i = 0; i < songs.size(); i++){
            System.out.println(songs.get(i).name);
        }
    }

    /**
     * showAllAlbums
     * prints names of all albums in library
     */
    public static void showAllAlbums(){
        for(int i = 0; i < loader.listOfAlbums.size(); i++){
            System.out.println(loader.listOfAlbums.get(i).name);
        }
    }

    /**
     * showAllArtists
     * prints names of all artists in library
     */
    public static void showAllArtists(){
        for(int i = 0; i < loader.listOfArtists.size(); i++){
            System.out.println(loader.listOfArtists.get(i).name);
        }
    }

    /**
     * addSong
     * lets user add a song to the library
     */
    public static void addSong(){
        Artist artist = null;
        Album album = null;
        Boolean check = false;
        int intResponse;

        Scanner inputGetter = new Scanner(System.in);
        System.out.println("Enter name of song: ");
        String songName = inputGetter.nextLine();

        System.out.println("Enter name of artist: ");
        String artistName = inputGetter.nextLine();
        for(int a = 0; a < loader.listOfArtists.size(); a++){
            if(loader.listOfArtists.get(a).name.equals(
                    artistName.toLowerCase(Locale.ROOT))){
                artist = loader.listOfArtists.get(a);
                check = true;
            }
        }
        if(check == false) {
            artist = new Artist(artistName);
            loader.listOfArtists.add(artist);
        }
        System.out.println("Enter name of album: ");
        String albumName = inputGetter.nextLine();
        for(int a = 0; a < loader.listOfAlbums.size(); a++){
            if(loader.listOfAlbums.get(a).name.equals(
                    albumName.toLowerCase(Locale.ROOT))){
                album = loader.listOfAlbums.get(a);
                check = true;
            }
            /*if(check == false){

                album = new Album(albumName);
                loader.listOfAlbums.add(album);
                album.setArtist(artist);
            }*/
        }
        Song song = new Song(songName);
        song.setPerformer(artist);
        song.setAlbum(album);
        loader.listOfSongs.add(song);
    }

    /**
     * addAlbum
     * lets user add an album to the library
     */
    public static void addAlbum(){
        Scanner inputGetter = new Scanner(System.in);
        Artist artist = null;
        Boolean check = false;

        System.out.println("Enter name of album: ");
        String albumName = inputGetter.nextLine();

        System.out.println("Enter name of artist: ");
        String artistName = inputGetter.nextLine();
        for(int a = 0; a < loader.listOfArtists.size(); a++){
            if(loader.listOfArtists.get(a).name.equals(
                    artistName.toLowerCase(Locale.ROOT))){
                artist = loader.listOfArtists.get(a);
                check = true;
            }
        }
        if(check == false) {
            artist = new Artist(artistName);
            loader.listOfArtists.add(artist);
        }
        Album album = new Album(albumName);
        album.setArtist(artist);
        loader.listOfAlbums.add(album);
    }

    /**
     * addArtist
     * lets user add an artist to the library
     */
    public static void addArtist(){
        Scanner inputGetter = new Scanner(System.in);
        System.out.println("Enter name of artist: ");
        String artist = inputGetter.nextLine();

        loader.listOfArtists.add(new Artist(artist));
    }
}
