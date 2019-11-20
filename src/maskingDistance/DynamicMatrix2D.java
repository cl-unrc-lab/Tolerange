package maskingDistance;
import java.util.*;

public class DynamicMatrix2D<E> {
    private ArrayList<ArrayList<E>> matrix;
    private int rows = 200;
    private int cols = 200;

    public DynamicMatrix2D(){
        matrix = new ArrayList<ArrayList<E>>(rows);
        for (int i=0;i<rows;i++){
            matrix.add(i,new ArrayList<E>(cols));
            for (int j=0;j<cols;j++){
                matrix.get(i).add(j,null);  
            }
        }
    }

    public void set(int x, int y, E value) {
        //System.out.println("x:"+x+"  y:"+y);
        /*if (x >= matrix.size()) {
            ArrayList<ArrayList<E>> tmp = copyMatrix();
            matrix = new ArrayList<ArrayList<E>>(x + 10);
            matrix.addAll(0,tmp);
            for (int i = x; i < x + 10; i++) {
                matrix.add(i,new ArrayList<E>(x+10));
            }
        }

        if (y >= matrix.get(0).size()) {
            for (int i = 0; i < matrix.size(); i++) {
                ArrayList<E> tmp = (ArrayList<E>)matrix.get(i).clone();
                matrix.add(i, new ArrayList<E>(y + 10));
                matrix.get(i).addAll(0,tmp);
            }
        }*/
        /*if (x == rows-1){
            //System.out.println("bien");
            matrix.add(x,new ArrayList<E>(cols));
            for (int i=0;i<cols;i++){
                matrix.get(x).add(i,null);  
            }
            rows++;
        }*/
        /*if (x == rows){
            rows++;
            matrix.add(x,new ArrayList<E>(cols));
            for (int i=0;i<cols;i++){
                matrix.get(x).add(i,null);  
            }
        }
        if (y == cols){
            System.out.println("h");
            cols++;
        }*/
        matrix.get(x).add(y,value);
    }

    public E get(int x, int y) {
        //System.out.println("x:"+rows+"  y:"+cols);
        /*if (x == rows - 1){
            System.out.println("bien2");
            matrix.add(rows,new ArrayList<E>(cols));
            for (int i=0;i<cols;i++){
                matrix.get(rows).add(i,null);  
            }
            rows++;
        }*/
        return x >= matrix.size() || y >= matrix.get(x).size() ? null : matrix.get(x).get(y);
        //return matrix.get(x).get(y);
    }

    public int getRowLength(){
        return rows;
    }

    public int getColLength(){
        return cols;
    }

    public String toString(){
        return matrix.toString();
    }

    private ArrayList<ArrayList<E>> copyMatrix(){
        ArrayList<ArrayList<E>> res = new ArrayList<ArrayList<E>>(matrix.size());
        for (ArrayList<E> e : matrix){
            ArrayList<E> tmp = new ArrayList<E>(e.size());
            for (E e_ : e){
                tmp.add(e_);
            }
            res.add(tmp);
        }
        return res;
    }

}