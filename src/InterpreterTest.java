import org.testng.annotations.Test;
import org.junit.jupiter.api.BeforeEach;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
//just trying to generate this broke everything, I had to copy it over from another project

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.*;

class LoadTest {
    Loader L;
    Display d;

    @BeforeEach
    void setUp() {
        L = new Loader();
        d = new Display();
    }

    @org.junit.jupiter.api.Test
    void testAddSong(){
        L.loadSQL();
        d.addSong();
    }

    @org.junit.jupiter.api.Test
    void testLoadSQL(){
        L.loadSQL();
        System.out.println(L.listOfSongs.get(0).name);
    }

    @org.junit.jupiter.api.Test
    void testURL(){
        String url = "https://musicbrainz.org/ws/2/artist?query=the%20beatles&fmt=xml";

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
        NodeList AlsoLibraryItem = LibraryItem.item(0).getChildNodes();
        NodeList artistStuff = AlsoLibraryItem.item(0).getChildNodes();

        NodeList name = artistStuff.item(0).getChildNodes();
        System.out.println(AlsoLibraryItem.item(0).getNodeName());
        System.out.println(name.item(0).getNodeValue());
        //so alsoLibraryItem is list of artists, pass into thing and then get artistStuff and name

        //System.out.println ("Root element: " +  root.getNodeName());
        } catch (Exception e) {
            System.out.println("Parsing error:" + e);
        }
    }
}