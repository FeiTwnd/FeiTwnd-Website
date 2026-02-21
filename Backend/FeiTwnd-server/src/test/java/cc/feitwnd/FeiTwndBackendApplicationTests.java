package cc.feitwnd;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

class FeiTwndBackendApplicationTests {

    @Test
    public void testPassword() throws Exception {
        String password = "";
        String salt = "";

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String combined = password + salt;
        byte[] hash = md.digest(combined.getBytes(StandardCharsets.UTF_8));

        // 转换为十六进制
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        System.out.println(hexString.toString());
    }

}
