package testfiles;

import java.util.Arrays;

public class Test2 {

    //TODO: Schreiben Sie hier die geforderten Methoden.
    
    private static int[][] summate(int[][] arr1, int[][] arr2) {
    int[][] result = new int[arr1.length][];
    int max = 0;
    for(int i = 0; i < arr1.length; i++) {
        max = Math.max(arr1[i].length, arr2[i].length);
        result[i] = new int[max];
        max = 0;
    }
    for(int i = 0; i < result.length; i++) {
        for(int j = 0; j < result[i].length; j++) {
            if(arr1[i].length > j) {
                result[i][j] += arr1[i][j];
            }
            if(arr2[i].length > j) {
                result[i][j] += arr2[i][j];
            }
        }
    }


    return result;
    }

    private static void centralize(int[][] input) {
    int max = 0;
        for (int i = 0; i < input.length; i++) {
            if(input[i].length > max) max = input[i].length;
        }
    for(int i = 0; i < input.length;i++) {
        if((max - input[i].length != 0) ) {
            int[] temp = input[i];
            input[i] = new int[max];
            int counter = 0;
            int mid = 0;
            int diff = input[i].length - temp.length;
            if(temp.length %2==0){
                mid = max/2 - 1;
            } else {
                mid = max/2;
            }
            if(diff%2==0) {
                    for (int j = 0; j <= input[i].length && j < temp.length; j++) {
                    input[i][mid + j] = temp[j];

                }
            } else {
                mid = input[i].length/2 - 1;
                for (int j = 0; j <= input[i].length && j < temp.length; j++) {
                    input[i][mid + j] = temp[j];

                }
            }

        }

    }


    }

    private static String keep(String seq, String elements) {
        if (seq.length() == 0 || elements.length() == 0) {
            return "";
        }
            char first = seq.charAt(0);
            char compare = elements.charAt(0);
            String rest = seq.substring(1);

            if (first == compare) {
                return keep(rest, elements);
            } else {
                return keep(seq, elements.substring(1));
            }


    }

    public static void main(String[] args) {
        //TODO: Erweitern Sie "main" laut Angabe.

        int[][] test1 = {{5},
                         {3, 5, 7, 9},
                         {1},
                         {3, 4, 5, 6, 7}} ;

        int[][] test2 = {{1, 2},
                         {3, 4, 5},
                         {6, 7, 8, 9},
                         {4}};

        String text = "14678";

        int[][] result1 = summate(test1,test2);
        System.out.println(Arrays.deepToString(result1));
        int[][] result2 = summate(test2,test2);
        System.out.println(Arrays.deepToString(result2));

        centralize(test1);
        System.out.println(Arrays.deepToString(test1));
        centralize(test2);
        System.out.println(Arrays.deepToString(test2));

        String s1 = keep(text, "456");
        System.out.println(s1);
    }

}



