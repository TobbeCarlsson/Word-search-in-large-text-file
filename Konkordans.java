import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner; 
import java.time.Duration;
import java.time.Instant;
import java.lang.Math;

public class Konkordans{
    RandomAccessFile korpus = new RandomAccessFile("/afs/kth.se/misc/info/kurser/DD2350/adk22/labb1/korpus", "r");
    Scanner userInput = new Scanner(System.in);
    Mio mio = new Mio();
    public static void main(String[] args) throws IOException{
        if (args.length == 1) {
            Konkordans konkordans = new Konkordans(args[0].toLowerCase());
        }else if(args.length > 1){
            System.out.print("Mata bara in 1 argument");
            return;
        }else{
            System.out.println("Du måste köra filen med ett argument");
            return;
        }
        
    }

    public Konkordans(String word) throws IOException{
       search(word);
    }

    public void search(String word) throws IOException{
        Instant inst1 = Instant.now();
        Hash hash = new Hash();
        int pos = 0;
        pos = hash.getHash(word);

        String filePath = "lazyindex.txt"; 
        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        filePath = "/afs/kth.se/misc/info/kurser/DD2350/adk22/labb1/rawindex.txt"; 
        RandomAccessFile file2 = new RandomAccessFile(filePath, "r");
        // Creates the lower bound for the interval being searched
        
        file.seek(pos);
        long b = file.readLong();
        if(b == 0 && !word.equals("a")){
            System.out.println("Ordet finns inte");
            return;
        }
        
        int lowerBound = (int)b;
        int upperPos = pos;
        long b2;
        int upperBound;
        //Finding the next existing 3 letter combination to retrieve upperbound
        while(true){
            upperPos += 8;
            if(upperPos > 215992){
                upperBound = 1607306794;
                break;
            }
            file2.seek(upperPos);
            b2 = file.readLong();
            if(b2 != 0){
                upperBound = (int)b2;
                break;
            }
        }
        
        // End of file
        file2.seek(lowerBound);
        if(lowerBound > 1607306794){
            lowerBound = 1607306779;
            upperBound = 1607306794;
        }
        
        
        // Looking for one occurance of the searched word
        String line = "";
        Boolean foundWord = false;
        while(!foundWord){
            int middle = lowerBound/2 + upperBound/2;
            goToStartOfWord(file2, middle);
            file2.readLine();
            line = file2.readLine();
            if(line == null){
                System.out.println("Ordet finns inte");
                return;
            }
            String currentWord = line.split(" ")[0];
            int comparison = word.compareTo(currentWord);
            if(comparison > 0){
                lowerBound = middle;
            }else if(comparison < 0){
                upperBound = middle;
            }else if(comparison == 0){
                foundWord = true;
            }
            if((upperBound-lowerBound)<2){
                System.out.println("Ordet finns inte");
                return;
            }
        }

        //Finding the first occurance of that word
        Boolean firstOccurance = false;
        Boolean prep = false;
        long pointer = file2.getFilePointer()-2;
        Byte by;
        char ch;
        while(!prep){
            if(pointer == 0){
                break;
            }
            file2.seek(pointer);
            by = file2.readByte();
            ch = new String(new byte[]{by} , "ISO-8859-1").charAt(0);
            if(ch == '\n'){
                prep = true;
            }
            pointer--;
        }
        while(!firstOccurance){
            if(pointer == 0){
                break;
            }
            pointer -= 1;
            file2.seek(pointer);
            by = file2.readByte();
            ch = new String(new byte[]{by} , "ISO-8859-1").charAt(0);
            if(ch == '\n'){
                String nextWord = file2.readLine();
                if(nextWord.split(" ")[0].compareTo(word) != 0){
                    pointer = file2.getFilePointer();
                    firstOccurance = true;
                }
            }
        }
        
        
        int occurances = 0;
        file2.seek(pointer);
        while(true){
            String w = file2.readLine();
            if(w == null){
                break;
            }
            if(w.split(" ")[0].compareTo(word) == 0){
                occurances++;
            }else{
                break;
            }

        }
        Instant inst2 = Instant.now();
        // System.out.println("Duration (in seconds):");
        // System.out.println(Duration.between(inst1, inst2).toSeconds());
        System.out.println("Antal förekomster: " + occurances);
        
        // Iterate through all occurances and print them to the user
        Boolean foundAllWords = false;
        file2.seek(pointer);
        int counter = 0;
        while(!foundAllWords){
            String currentWord = file2.readLine();
            if(currentWord == null){
                break;
            }
            if(currentWord.split(" ")[0].compareTo(word) == 0){
                showWord(currentWord);
            }else{
                foundAllWords = true;
                System.out.println("Alla ord visade");
                break;
            }
            counter++;
            if(counter > 25){
                String answer = getInput();
                if(answer.equals("n")){
                    break;
                }else if(answer.equals("j")){
                    counter = Integer.MIN_VALUE;
                }
            }
        }
        
        
        file2.close();
    }

    public String getInput(){
        System.out.println("\nVill du se fler förekomster av ordet?\nj = ja\tn = nej");
        String answer = userInput.nextLine();
        System.out.println();
        if(!answer.equals("j") && !answer.equals("n")){
            System.out.println("Snälla svara med j eller n");
            return getInput();
        }
        return answer;
    }

    public void showWord(String word) throws IOException{
        byte b[] = new byte[60+word.length()];
        int pos = Integer.valueOf(word.split(" ")[1]);
        korpus.seek(Math.max(pos-30, 0));
        byte by;
        char ch;
        StringBuilder sb1 = new StringBuilder("");
        for(int i = 0; i < b.length;i++){
            by = korpus.readByte();
            ch = new String(new byte[]{by} , "ISO-8859-1").charAt(0);
            if(mio.IsWhitespace(ch)){
                ch = ' ';
            }
            sb1.append(ch);
        }
        System.out.println(sb1);
    }

    public void goToStartOfWord(RandomAccessFile file, int start) throws IOException{
        Byte by;
        char ch;
        int pointer = start;
        while(true){
            if(pointer == 0){
                break;
            }
            file.seek(pointer);
            by = file.readByte();
            ch = new String(new byte[]{by} , "ISO-8859-1").charAt(0);
            if(ch == '\n'){
                pointer--;
                break;
            }
            pointer--;
        }
        file.seek(pointer);
    }
}