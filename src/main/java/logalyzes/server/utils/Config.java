package logalyzes.server.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private  Map<String,String> env = new HashMap<String,String>();

    public static int PORT;


    public  Config(String path) {
        this.readEnv(path);

        Config.PORT = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 50051;
    }

    public   Map<String, String> readEnv(String path) {
        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            String line = reader.readLine();
            while (line != null){
                String[] parts = line.split("=");
                System.out.println(parts[0] + " " + parts[1]);
                if(parts.length == 2){
                    this.env.put(parts[0].trim(), parts[1].trim());
                }
                line = reader.readLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return  env;
    }

}
