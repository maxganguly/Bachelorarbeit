package testfiles;

import java.util.Arrays;

public class Test {

	public static void condition(boolean con) {
		if(con) {
			con = true;
		}else {
			con = false;
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Beginning");
		int x = 2;
		double b = 12.3;
		int c = (int)b;
		System.out.println(Math.pow(b, x));
		switch(x) {
			case 2-> System.out.println(2);
			case 1-> System.out.println(1);
			case 0-> System.out.println(0);
		
		}
		while (x > 0) {
			System.out.println(x);
			x--;
		}
		for(double y = b;b > 1; b /=2) {
			System.out.println("Y is:" + b);
		}
		try {
			int a = 0;
			double d = b/a;
			
		}catch (ArithmeticException ae) {
			ae.printStackTrace();
		}finally {
			System.out.println("Whack");
		}
		calc1(12);
		calc2(12);
		calc3(12);
		calc4(12);
		calc5(1,2);
	}
	
	public static int calc1(int x) {
		int y = 2 * x + 1;
		if(y > 0)
			return 0;
		else {
			x = 2 * y;
		}
		return y;
	}
	
	public static int calc2(int a) {
		if(Math.random()>0.5)
			return -1;
		if (a > 0) {
			return a;
		} else {
			a = -1*a;
		}
		return a;
	}
	
	public static int calc3(int a) {
		int sum = a;
		for (int i = 0; i < a*a;i++) {
			if(a < 0) {
				sum -=1;
			}else {
				sum++;
			}
		}
		return sum;
	}
	public static int calc4(int a) {
		int sum = a;
		for (int i = 0; i < a*a;i++) {
			if(a < 0) {
				sum -=1;
			}else {
				sum++;
			}
		}
		return sum;
	}
	
	public static int calc5(int a, int b) {
		return (a*b)/(a+b);
	}
	
	public static void print(float c) {
		System.out.println("float: " + c);
	}
	
	public static int[] dimensions(int[][] arr) {
		return new int[] {arr.length, arr[0].length};
	}
	
	public static int[] random() {
		if(Math.random() > 0.9)
			return null;
		int[] a = new int[(int)(Math.random()*11)];
		for(int i = 0; i < a.length;i++) {
			a[i] = (int) (Math.random()*100);
		}
		return a;
	}
	
	public static int sum(int[][][] s) {
		return s.length + s[0].length + s[0][0].length;
	}
	
	public static int sum(int[] arr) {
		return Arrays.stream(arr).sum();
	}
}
