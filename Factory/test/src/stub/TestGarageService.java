package stub;

/**
 * TODO documentation
 */
public class TestGarageService {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(new UserCall()).start();
        }
    }
}
