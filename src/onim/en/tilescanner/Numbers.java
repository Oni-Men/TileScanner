package onim.en.tilescanner;

public class Numbers {

  public static int parseInt(String s, int i) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return i;
    }
  }
 
  public static double parseDouble(String s, double d) {
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      return d;
    }
  }
  
}
