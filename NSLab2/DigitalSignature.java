import javax.xml.bind.DatatypeConverter;
import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;


public class DigitalSignature {

    public static void main(String[] args) throws Exception {
   //Read the text file and save to String data
    String data = "";
    String line;
    BufferedReader bufferedReader = new BufferedReader( new FileReader(args[0]));
    while((line= bufferedReader.readLine())!=null){
        data = data +"\n" + line;
    }
    

//TODO: generate a RSA keypair, initialize as 1024 bits, get public key and private key from this keypair.
        KeyPairGenerator key = KeyPairGenerator.getInstance("RSA");
        key.initialize(1024);
        KeyPair keyPair = key.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();

//TODO: Calculate message digest, using MD5 hash function
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());

//TODO: print the length of output digest byte[], compare the length of file smallSize.txt and largeSize.txt
        byte[] digest = md.digest();
        System.out.println("Digest size: " + digest.length);
           
//TODO: Create RSA("RSA/ECB/PKCS1Padding") cipher object and initialize is as encrypt mode, use PRIVATE key.
        Cipher cipherEn = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherEn.init(Cipher.ENCRYPT_MODE,privateKey);

//TODO: encrypt digest message
        byte[] encryptedBytes = cipherEn.doFinal(digest);
        System.out.println("Signed message digest size: " + encryptedBytes.length);

//TODO: print the encrypted message (in base64format String using DatatypeConverter)
        String encryptedString = DatatypeConverter.printBase64Binary(digest);
        System.out.println("|||||||||||||||| ENCRYPTED FILE ||||||||||||||||");
        System.out.println(encryptedString);

//TODO: Create RSA("RSA/ECB/PKCS1Padding") cipher object and initialize is as decrypt mode, use PUBLIC key.
        Cipher cipherDe = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherDe.init(Cipher.DECRYPT_MODE,publicKey);

//TODO: decrypt message
        byte[] decryptedBytes = cipherDe.doFinal(encryptedBytes);

//TODO: print the decrypted message (in base64format String using DatatypeConverter), compare with origin digest 
        String decryptedString = DatatypeConverter.printBase64Binary(decryptedBytes);
        System.out.println("|||||||||||||||| DECRYPTED MESSAGE ||||||||||||||||");
        System.out.println(decryptedString);
    }

}
