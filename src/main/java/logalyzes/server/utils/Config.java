package logalyzes.server.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private  Map<String,String> env = new HashMap<String,String>();

    public static int PORT;
    public static  String ELK_HOST;
    public static int ELK_PORT = 9200;
    public  static String ELK_PASSWORD;

    // Notification Config
    public static String MAIL_HOST;
    public static String MAIL_PORT;
    public  static  String MAIL_USERNAME;
    public  static  String MAIL_PASSWORD;
    public  static  String MAIL_FROM;

    public static String PREDICTOR_API_URL;


    public  Config(String path) {
        this.readEnv(path);

        Config.PORT = System.getenv("PORT") == null ?
                Integer.parseInt(env.get("PORT"))
                :Integer.parseInt(System.getenv("PORT"));

        Config.ELK_HOST = System.getenv("ELK_HOST") == null ?
                env.get("ELK_HOST")
                : System.getenv("ELK_HOST");

        Config.ELK_PORT = System.getenv("ELK_PORT") == null ?
                Integer.parseInt(env.get("ELK_PORT"))
                : Integer.parseInt(System.getenv("ELK_PORT"));

        Config.ELK_PASSWORD = System.getenv("ELK_PASSWORD") == null ?
                env.get("ELK_PASSWORD")
                : System.getenv("ELK_PASSWORD") ;

        // Notification config
        Config.MAIL_HOST = System.getenv("MAIL_HOST") == null ?
                env.get("MAIL_HOST")
                : System.getenv("MAIL_HOST") ;

        Config.MAIL_PORT = System.getenv("MAIL_PORT") == null ?
                env.get("MAIL_PORT")
                :System.getenv("MAIL_PORT");

        Config.MAIL_USERNAME = System.getenv("MAIL_USERNAME") == null ?
                env.get("MAIL_USERNAME")
                : System.getenv("MAIL_USERNAME") ;

        Config.MAIL_PASSWORD = System.getenv("MAIL_PASSWORD") == null ?
                env.get("MAIL_PASSWORD")
                : System.getenv("MAIL_PASSWORD") ;

        Config.MAIL_FROM = System.getenv("MAIL_FROM") == null ?
                env.get("MAIL_FROM")
                : System.getenv("MAIL_FROM") ;

        Config.PREDICTOR_API_URL = System.getenv("PREDICTOR_API_URL") == null ?
                env.get("PREDICTOR_API_URL")
                : System.getenv("PREDICTOR_API_URL");

    }

    public   void readEnv(String path) {
        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            String line = reader.readLine();
            while (line != null){
                String[] parts = line.split("=");
                //System.out.println(parts[0] + " " + parts[1]);
                if(parts.length == 2){
                    this.env.put(parts[0].trim(), parts[1].trim());
                }
                line = reader.readLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return ;
    }

}
