package testfiles;

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
	}
	
	//{ x < −4 }
	public static int calc1(int x) {
		int y = 2 * x + 1;
		if(y > 0)
			return 0;
		else {
			x = 2 * y;
		}
		return y;
	}
	//{ y > x + 5 }
	public static int calc2(int a) {
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

}
