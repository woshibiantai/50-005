import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;


public class TextEncryption {
    public static void main(String[] args) throws Exception {
        String data = "";
        String line;
        BufferedReader bufferedReader = new BufferedReader( new FileReader(args[0])); //args[0] is the file you are going to encrypt. 
        while((line= bufferedReader.readLine())!=null){
            data = data +"\n" + line;
        }
       
//TODO: Print to screen contents of the file
        System.out.println("|||||||||||||||| ORIGINAL FILE ||||||||||||||||");
        System.out.println(data);

//TODO: generate secret key using DES algorithm
        SecretKey key = KeyGenerator.getInstance("DES").generateKey();
        
//TODO: create cipher object, initialize the ciphers with the given key, choose encryption mode as DES
        Cipher encryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE,key);

//TODO: do encryption, by calling method Cipher.doFinal().
        byte[] encryptedBytes = encryptCipher.doFinal(data.getBytes());
        System.out.println(new String(encryptedBytes));

//TODO: print the length of output encrypted byte[], compare the length of file smallSize.txt and largeSize.txt
        System.out.println("|||||||||||||||| FILE LENGTH ||||||||||||||||");
        System.out.println(encryptedBytes.length);

//TODO: do format conversion. Turn the encrypted byte[] format into base64format String using DatatypeConverter
        String encryptedString = DatatypeConverter.printBase64Binary(encryptedBytes);

//TODO: print the encrypted message (in base64format String format)
        System.out.println("|||||||||||||||| ENCRYPTED FILE ||||||||||||||||");
        System.out.println(encryptedString);

//TODO: create cipher object, initialize the ciphers with the given key, choose decryption mode as DES
        Cipher decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE,key);

//TODO: do decryption, by calling method Cipher.doFinal().
        byte[] decryptedBytes = decryptCipher.doFinal(encryptedBytes);

//TODO: do format conversion. Convert the decrypted byte[] to String, using "String a = new String(byte_array);"
        String decryptedString = new String(decryptedBytes);

//TODO: print the decrypted String text and compare it with original text
        System.out.println("|||||||||||||||| DECRYPTED FILE ||||||||||||||||");
        System.out.println(decryptedString);
    }
}
