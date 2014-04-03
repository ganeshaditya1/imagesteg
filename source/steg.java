

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import java.nio.charset.Charset;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


class image{
    private BufferedImage target;
    private String location;
    image(String location) throws IOException{
        this.location = location;
        target = ImageIO.read(new File(location));
    }

    public void setChar( char letter, int x, int y){
        int color = target.getRGB(x,y);
        int mask = letter;
        mask = ( (mask & 0xE0) << 11 ) | ( ( mask & 0x1C ) << 6 ) | ( mask & 3 ) ;
        color = color & 0xfff8f8fc;
        color = color | mask;
        target.setRGB(x, y, color);

    }

    public char getChar( int x, int y){
        int color = target.getRGB(x,y);
        color = color & 0x070703;
        color = ( (color & 0x070000) >> 11 ) | ( ( color & 0x0700 ) >> 6 ) | ( color & 0x3 ) ;
        return (char)(color);
    }
    public void setString( String input ){
        input = "$" + input.length() + "$" + input;
        int x = 0, y = 0;
        for ( int i = 0; i < input.length(); i++ ){
            setChar( input.charAt(i), x, y );
            if ( x++ >= target.getWidth() ){
                x = 0; y++;
            }
        }
        try{ImageIO.write(target,"png",new File(location));}catch(Exception e){}
    }
    public String getString() throws Exception{

        if( getChar(0, 0) != '$' )
            return "";
        int x = 1, y = 0;
        String length = "";
        while (true){
            char temp = getChar(x, y);
            if (temp == '$'){
                x++;
                break;
            }
            length += temp;
            x++;
        }
        int limit = Integer.parseInt(length);
        int count = 0;
        String result = "";
        while (count < limit){
            char temp = getChar( x, y );
            if ( x++ >= target.getWidth() ){
                x = 0; y++;
            }
            result += temp;
            count++;
        }
        return result;
    }
}
public class steg {

    /**
     * @param args the command line arguments
     */

    public static String aes(String[] args) throws Exception {
        String location = args[0];
        location =  location.replaceAll("\\\\","\\\\\\\\");

       image i = new image( location );
       if ( args.length == 3 ) {
             String message = GlobalMembersAes.encrypt("This is a valid string" + args[1], args[2] );
             i.setString( message);
             return "";
       }
       else {
             String encrypt = GlobalMembersAes.decrypt( i.getString(), args[1] );
             String validString = "This is a valid string";
             if ( i.getString().equals("") ){
                return "No message in Image";
             }
             if ( !encrypt.substring( 0, validString.length() ).equals(validString) ){
                return "Invalid Key";
             }
             return encrypt.substring( validString.length() );
             }
    }
    public static void main(String[] args) throws Exception {

    }
}
