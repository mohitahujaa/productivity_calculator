package src;


import java.io.*;
import java.util.*;

public class LoginLogic {
    private Map<String, String> userMap = new HashMap<>();
    public static void main(String[] args){

    }
    public void Reader(){
        try {
            BufferedReader fin = new BufferedReader(new FileReader("user_data/users.txt"));
            String line;
            while((line = fin.readLine()) != null){
            String cred[] = line.split(",");
                if(cred.length >= 2){
                    String true_username = cred[0].trim();
                    String true_password = cred[1].trim();
                    userMap.put(true_username, true_password);
                }
            }
            fin.close();
        } catch (Exception e) {
            System.out.println("Error Reading file: +" + e.getMessage());
        }
    }

    public Result validateLogin(String user, char [] pass){
        Reader();
        String password = new String(pass);
        Result r = new Result();
        if(userMap.containsKey(user)){
            if(userMap.get(user).equals(password)){
                password = null;
                r.username = user;
                r.res = 1;
                return r;
            }
            else{
                password = null;
                r.res = 10;
                return r;
            }
        }
        else{
            password = null;
            r.res =  100;
            return r;
        }
    }
}
