package org.assist.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReplaceText {
    public static void main(String[] args) {
        String searchText = "AZC";
        int count = 17;


        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\dev\\projects\\erfassistent-2.0\\aa.txt"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String text = line.split(";")[4];

                text = text.split("\"")[1].trim();
                System.out.println(text);
                System.out.println(replace(text, searchText, count));
                System.out.println("-------------------------------------------------");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String replace(String text, String searchText, int count) {
        int firstIndex = text.indexOf(searchText);
        if (firstIndex == -1) {
            return text;
        }
        if (firstIndex + count >= text.length())
            return text.substring(0, firstIndex);

        text = text.substring(firstIndex + count, text.length());

        return replace(text.trim(),searchText,count);
    }


}
