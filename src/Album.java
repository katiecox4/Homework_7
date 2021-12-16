import java.util.ArrayList;

public class Album extends Entity {
    protected ArrayList<Song> songs;
    protected Artist artist;

    public Album(String name, int id) {
        super(name, id);
    }

    public String getName() {
        System.out.println("this is an album" + super.getName());
        return name;
    }

    public boolean equals(Album otherAlbum) {
        try {
            if ((this.artist.equals(otherAlbum.getArtist())) &&
                    (this.name.equals(otherAlbum.getName()))) {
                return true;
            } else {
                return false;
            }
        }catch (NullPointerException n){
            return false;
        }
    }

    protected ArrayList<Song> getSongs() {
        return songs;
    }

    protected void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
