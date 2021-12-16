import org.testng.annotations.Test;
import org.junit.jupiter.api.BeforeEach;
//just trying to generate this broke everything, I had to copy it over from another project

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
    void testLoadSQL(){
        L.loadSQL();
        System.out.println(L.listOfSongs.get(0).name);
    }
}