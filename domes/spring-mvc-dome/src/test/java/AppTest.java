import java.net.URL;

public class AppTest {

	
	public static void main(String[] args) {
		
		URL url = AppTest.class.getClassLoader().getResource("");
		
		System.out.println("toString = " + url.toString());
		System.out.println("getPath = " + url.getPath());
		
		System.out.println("getFile = " + url.getFile());
		
	}
	
	
	
}
