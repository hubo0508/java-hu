package examples.service;

import com.easysql.orm.EntityEngine;

import examples.domain.CopyOfUser;
import examples.domain.User;

public class Test {
	public static void main(String[] args) {
		
		
		
		EntityEngine ref = new EntityEngine(User.class);
		
		System.out.println(ref.getCanonicalName());

		String[] files = ref.getRowField();
		for (String s : files) {
			System.out.println(s);
		}
	}
}
