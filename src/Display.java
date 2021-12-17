import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

//todo check for duplicates before add thing
//todo add songs to albums and songs and alubms to artists
//https://musicbrainz.org/ws/2/artist?query=beatles&fmt=xml
public class Display {
    Loader loader = new Loader();
    ArrayList<Playlist> playlists = new ArrayList<>();

    public void menu(){
        Scanner inputGetter = new Scanner(System.in);
        int intResponse;
        String strResponse;

        //display all artists or all albums
        System.out.println("1. Display all songs\n2. Display all albums\n3. Display all artists" +
                "\n4. Display playlists\n5. Add song\n6. Add artist\n7. Add album\n8. Create playlist" +
                "\n9. Create playlist based on artist\n10. Play playlist\n11. Exit");
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
            //create playlist with musicbrainz

        }else if(intResponse == 10){
            playPlaylist();
            menu();
        }else if(intResponse == 11){
            //save
            //exit
        }else{
            //could throw error instead?
            System.out.println("error in intResponse");
            menu();
        }
    }

    public String getNamesMB(NodeList artist){    //pass in childNodes of artists
        NodeList name = artist.item(0).getChildNodes();
        return name.item(0).getNodeValue();
    }

    public boolean useMusicBrainz(Artist check){
        Scanner inputGetter = new Scanner(System.in);

        System.out.println("Enter name of artist you would like to check for:");
        String artistName = inputGetter.next();

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
                        return true;
                    }
                    //does that work?
                }
            }
        } catch (Exception e) {
            System.out.println("Parsing error:" + e);
        }
        return false;
    }

    public void playPlaylist(){
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

    public void createPlaylist(){
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
    }

    public void showAllPlaylists(){
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

    public void showAllSongs(ArrayList<Song> songs){
        for(int i = 0; i < songs.size(); i++){
            System.out.println(songs.get(i).name);
        }
    }

    public void showAllAlbums(){
        for(int i = 0; i < loader.listOfAlbums.size(); i++){
            System.out.println(loader.listOfAlbums.get(i).name);
        }
    }

    public void showAllArtists(){
        for(int i = 0; i < loader.listOfArtists.size(); i++){
            System.out.println(loader.listOfArtists.get(i).name);
        }
    }

    public boolean checkForDuplicates(){
        return false;
    }

    public void addSong(){
        Scanner inputGetter = new Scanner(System.in);
        System.out.println("Enter name of song: ");
        String name = inputGetter.next();
        /*System.out.println("Enter name of album: ");
        String album = inputGetter.next();
        System.out.println("Enter name of artist: ");
        String artist = inputGetter.next();*/
        System.out.println("Enter song id: ");
        //todo need to have check for duplicate, a lot
        int id = inputGetter.nextInt();
        loader.listOfSongs.add(new Song(name, id));
    }

    public void addAlbum(){
        Scanner inputGetter = new Scanner(System.in);
        System.out.println("Enter name of album: ");
        String album = inputGetter.next();
        /*System.out.println("Enter name of artist: ");
        String artist = inputGetter.next();*/
        System.out.println("Enter album id: ");
        int id = inputGetter.nextInt();
        loader.listOfAlbums.add(new Album(album, id));
        //need to find album
        //loader.listOfAlbums.get(loader.listOfAlbums.size()-1).setArtist(new Artist(artist));
    }

    public void addArtist(){
        Scanner inputGetter = new Scanner(System.in);
        System.out.println("Enter name of artist: ");
        String artist = inputGetter.next();
        System.out.println("Enter artist id: ");
        int id = inputGetter.nextInt();
        loader.listOfArtists.add(new Artist(artist, id));
        //need to find album
    }
}
