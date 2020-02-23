package ib.edu.shoppingList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JsonService {



    /**
     * default constructor
     */
    private JsonService() {

    }

    public static <T> List<T> readFromReaderStream(Reader reader, Class<T[]> classObj){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        T[] array = gson.fromJson(reader, classObj);
        return Arrays.asList(array);
    }

    /**
     * method to save to file
     * @param file file name
     * @param list arraylist
     * @param <T> type of arraylist
     */
    public static <T> void saveToFile(File file, ArrayList<T> list) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(list, fileWriter);
        } catch (IOException e) {
        }
    }


    public static <T> String parseDataToSend(ArrayList<T> arrayList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String data = gson.toJson(arrayList);
        return data;
    }

    public static <T> ArrayList<T> deparseData(String data, Class<T[]> type) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        T[] list = gson.fromJson(data, type);
        return new ArrayList<>(Arrays.asList(list));
    }

    /**
     * parses single shopping list
     * @param shoppingList
     * @return
     */
    public static String parseSingleList(ShoppingList shoppingList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String data = gson.toJson(shoppingList);
        return data;
    }

    public static ShoppingList deparseSingleList(String data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map map = gson.fromJson(data, Map.class);
        String innerlist = gson.toJson(map.get("productsList"));
        ArrayList<Product> arrProd = deparseData(innerlist, Product[].class);
        return new ShoppingList((String) map.get("listName"), (int)map.get("id"), arrProd);
    }



}
