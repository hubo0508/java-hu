import java.util.StringTokenizer;


public class Examples {
	public static void main(String[] args) {
		
		
		String sql = "select * from user";
		
		sql = sql.toUpperCase();
		
		StringTokenizer st_dest = new StringTokenizer(sql, " ");
		int size = st_dest.countTokens();
		for (int i = 0; i < size; i++) {
			System.out.println(st_dest.nextToken());
		}
		
		
	}
}
