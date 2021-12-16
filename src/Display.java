import java.util.ArrayList;
import java.util.Scanner;

//todo check for duplicates before add thing
//todo add songs to albums and songs and alubms to artists
//https://musicbrainz.org/ws/2/artist?query=beatles&fmt=xml
public class Display {
    Loader loader = new Loader();

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
            showAllSongs();
            menu();
        }else if(intResponse == 2){
            showAllAlbums();
            menu();
        }else if(intResponse == 3){
            showAllArtists();
            menu();
        }else if(intResponse == 4){
            //show playlists
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
            //create playlist
            menu();
        }else if(intResponse == 9){
            //create playlist with musicbrainz

        }else if(intResponse == 10){
            //play new playlist
            menu();
        }else if(intResponse == 11){
            //exit
        }else{
            //could throw error instead
            System.out.println("error in intResponse");
            menu();
        }
    }

    public void showAllSongs(){
        for(int i = 0; i < loader.listOfSongs.size(); i++){
            System.out.println(loader.listOfSongs.get(i));
        }
    }

    public void showAllAlbums(){
        for(int i = 0; i < loader.listOfAlbums.size(); i++){
            System.out.println(loader.listOfAlbums.get(i));
        }
    }

    public void showAllArtists(){
        for(int i = 0; i < loader.listOfArtists.size(); i++){
            System.out.println(loader.listOfArtists.get(i));
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
