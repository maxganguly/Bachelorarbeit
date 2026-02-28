package testfiles;

import java.util.Random;

/*
Aufgabe 3) Zweidimensionale Arrays - Sortieren und Filtern
*/
public class Aufgabe3 {

private static double[][] genCircleFilter(int n, double radius) {
    // TODO: Implementieren Sie hier Ihre Lösung für die Methode
    //solution::

    //check if correct input
    if ((n % 2) == 0 || n < 1) {
        return null;
    }
    Random r = new Random();
    r.nextInt();
    double[][] myFilter = new double[n][n];

    int midPoint;
    midPoint = n / 2;

    OuterLoop:
    for (int x = 0; x < n; x++) {
        InnerLoop:
    	for (int y = 0; y < n; y++) {
            if (Math.sqrt(Math.pow(x - midPoint, 2) + Math.pow(y - midPoint, 2)) < radius) {
                myFilter[x][y] = 1;
            }
        }
    }
    return myFilter;
    //::solution
    /*spec::
    return null; //Zeile kann geändert oder entfernt werden.
    */
}

private static double[][] applyFilter(double[][] workArray, double[][] filterArray) {
    // TODO: Implementieren Sie hier Ihre Lösung für die Methode
    //solution::
    double[][] resultArray = new double[workArray.length][workArray[0].length];
    double temp_result;

    //iterate through inner array, such that filter values have corresponding values in workArray (no edge cases occur)
    int filterSideLength = filterArray.length / 2;
    for (int x = filterSideLength; x < workArray.length - filterSideLength; x++) {
        for (int y = filterSideLength; y < workArray[0].length - filterSideLength; y++) {
            //at every array position, compute filter result
            temp_result = 0;
            for (int xx = -filterSideLength; xx <= filterSideLength; xx++) {
                for (int yy = -filterSideLength; yy <= filterSideLength; yy++) {
                    temp_result += workArray[x + xx][y + yy] * filterArray[xx + filterSideLength][yy + filterSideLength];
                }
            }
            resultArray[x][y] = temp_result;
        }
    }
    return resultArray;
    //::solution
    /*spec::
    return null; //Zeile kann geändert oder entfernt werden.
    */
}

private static void print(double[][] workArray) {
    if (workArray != null) {
        for (int y = 0; y < workArray.length; y++) {
            for (int x = 0; x < workArray[y].length; x++) {
                System.out.printf("%.2f", workArray[y][x]);
                System.out.print("\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}

public static void main(String[] args) {
    double[][] myResultArray;

    double[][] myFilter1 = genCircleFilter(3, 1.2);
    System.out.println("Output -> myFilter1 (genCircleFilter mit size = 3 und radius = 1.2):");
    print(myFilter1);

    double[][] myFilter2 = genCircleFilter(7, 2.5);
    System.out.println("Output -> myFilter2 (genCircleFilter mit size = 7 und radius = 2.5):");
    print(myFilter2);

    double[][] myArray1 = {{0, 0, 0, 0, 0}, {0, 1, 1, 1, 0}, {0, 2, 2, 2, 0}, {0, 3, 3, 3, 0}, {0, 0, 0, 0, 0}};
    System.out.println("Output -> myArray1:");
    print(myArray1);

    myResultArray = applyFilter(myArray1, myFilter1);
    System.out.println("Output -> myFilter1 applied to myArray1:");
    print(myResultArray);

    myResultArray = applyFilter(myArray1, myFilter2);
    System.out.println("Output -> myFilter2 applied to myArray1:");
    print(myResultArray);

    //Beispiel aus Aufgabenblatt
    double[][] myArray3 = {{0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}};
    double[][] myFilter3 = {{1, 0, 0}, {1, 2, 0}, {0, 0, 3}};
    System.out.println("Output -> myArray3:");
    print(myArray3);
    System.out.println("Output -> myFilter3:");
    print(myFilter3);
    myResultArray = applyFilter(myArray3, myFilter3);
    System.out.println("Output -> myFilter3 applied to myArray3:");
    print(myResultArray);

    double[][] myArray4 = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 1, 2, 3, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
    System.out.println("Output -> myArray4:");
    print(myArray4);
    //TODO: Erstellen Sie den Filter aus dem Aufgabenblatt, wenden Sie ihn auf myArray4 an und geben Sie das Ergebnis mittels print() aus
    //solution::
    double[][] shiftFilter = {{0, 0, 0}, {0, 0, 0}, {0, 0.5, 0}};
    myResultArray = applyFilter(myArray4, shiftFilter);
    System.out.println("Output -> myFilter4 applied to myArray4:");
    print(myResultArray);
    //::solution
}
}