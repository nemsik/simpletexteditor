import java.util.ArrayList;

/**
 * Created by bartek on 15.08.2017.
 */
public class FindMethod {

    private ArrayList<Integer> pos;


    public FindMethod(String text, String pattern){

        text = text.toUpperCase();
        pattern = pattern.toUpperCase();

        pos = new ArrayList<>();

        int index = text.indexOf(pattern);
        pos.clear();
        while (index >= 0) {
            //System.out.println(index);
            pos.add(index);
            index = text.indexOf(pattern, index + 1);
        }

    }

    public ArrayList<Integer> getPos() {
        return pos;
    }

}
