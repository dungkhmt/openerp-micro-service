package wms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OpenerpResourceServerApplicationTests {
	Calculator underTest = new Calculator();
	@Test
	void isShouldAddNumbers() {
		int numberOne = 20;
		int numberTwo = 30;
		int result = underTest.add(numberOne, numberTwo);
		int expected = 60;
		assertThat(result).isEqualTo(expected);
	}
	class Calculator {
		int add (int a, int b) {
			return a + b;
		}
	}
	@Test
	void contextLoads() {
	}

}
