import java.lang.Object;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.image.BufferedImage;
import java.nio.*;
import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;


public class ImageEncryption {
    public static void main(String[] args) throws Exception{
        int image_width;
        int image_length;

        // select encryption mode
        String enMode = args[1];

        // read image file and save pixel value into int[][] imageArray
        BufferedImage img = ImageIO.read(new File(args[0])); // pass the image globe.bmp as first command-line argument. 
        image_width = img.getWidth();
        image_length = img.getHeight();
        // byte[][] imageArray = new byte[image_width][image_length];
        int[][] imageArray = new int[image_width][image_length];
        for(int idx = 0; idx < image_width; idx++) {
            for(int idy = 0; idy < image_length; idy++) {
                int color = img.getRGB(idx, idy);
                imageArray[idx][idy] = color;            
            }
        } 
// TODO: generate secret key using DES algorithm
        SecretKey key = KeyGenerator.getInstance("DES").generateKey();

// TODO: Create cipher object, initialize the ciphers with the given key, choose encryption algorithm/mode/padding,
//you need to try both ECB and CBC mode, use PKCS5Padding padding method
        Cipher encryptCipher = Cipher.getInstance("DES/" + enMode + "/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE,key);

        // define output BufferedImage, set size and format
        BufferedImage outImage = new BufferedImage(image_width,image_length, BufferedImage.TYPE_3BYTE_BGR);



        for(int idx = 0; idx < image_width; idx++) {
        // convert each column int[] into a byte[] (each_width_pixel)
            byte[] each_width_pixel = new byte[4*image_length];
            for(int idy = 0; idy < image_length; idy++) {
                ByteBuffer dbuf = ByteBuffer.allocate(4);
                dbuf.putInt(imageArray[idx][idy]);
                byte[] bytes = dbuf.array();
                System.arraycopy(bytes, 0, each_width_pixel, idy*4, 4);
            }
// TODO: encrypt each column or row bytes 
            byte[] encryptedImageBytes = encryptCipher.doFinal(each_width_pixel);

// TODO: convert the encrypted byte[] back into int[] and write to outImage (use setRGB)
            byte[] encryptedPixel = new byte[4];
            for (int idy = 0; idy < image_length; idy++) {
                System.arraycopy(encryptedImageBytes,idy*4,encryptedPixel,0,4);
                ByteBuffer wrapped = ByteBuffer.wrap(encryptedPixel);
                int newColor = wrapped.getInt();
                outImage.setRGB(idx,idy,newColor);
            }

        }
//write outImage into file
        ImageIO.write(outImage, "BMP",new File(enMode + ".bmp"));//for the ECB mode output
    }
}
