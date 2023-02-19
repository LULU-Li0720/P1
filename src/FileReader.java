import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// read pitcher and goal in file
public class FileReader {
    public int[] pitchers;   // first line of pitchers
    public int goal;         // second line of the goal

    // read file
    public void readFile(String FileName) throws Exception{
        ClassLoader cLer = getClass().getClassLoader();
        InputStream iSer = cLer.getResourceAsStream(FileName);
        try(InputStreamReader iSR = new InputStreamReader(iSer, StandardCharsets.UTF_8);
            BufferedReader bR = new BufferedReader(iSR)) {
            // read pitchers
            String[] numArr = bR.readLine().split(",");
            pitchers = new int[numArr.length];
            for (int i = 0; i < numArr.length; i++) {
                pitchers[i] = Integer.valueOf(numArr[i]);
            }
            // read goal 
            String str = bR.readLine();
            goal = Integer.valueOf(str);
        }
    }
}
