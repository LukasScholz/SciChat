import org.client.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.Arrays;
import java.util.List;

public class TestClient {
    public static Client client;

    @BeforeAll
    public static void init() throws Exception{
        client = new Client(new String[]{"localhost", "8080"});
    }

    @Test
    public void testEncryption() {
        List<String[]> values = Arrays.asList(new String[][]{
                {"ABCDEFGH", "ABCDEFGH"},
                {"12345", "12345"},
                {"!?:,.", "!?:,."},
                {"ABC23e7!", "ABC23e7!"}
        });
        values.forEach(value -> {
            String input = value[0];
            String expected = value[1];
            try {
                String encrypted = client.encrypt(input);
                System.out.println(encrypted);
                String decrypted = client.decrypt(encrypted);
                System.out.println(decrypted);

                Assertions.assertEquals(decrypted, expected);
            } catch (Exception ex) {ex.printStackTrace();}
        });
    }
}
