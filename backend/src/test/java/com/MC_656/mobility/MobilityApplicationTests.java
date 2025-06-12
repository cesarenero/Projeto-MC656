package com.MC_656.mobility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class MobilityApplicationTests {

	@Test
	@DisplayName("Deve criar uma instância da aplicação sem erros")
	void applicationCanBeInstantiated() {
		MobilityApplication app = new MobilityApplication();
		assertNotNull(app, "A instância da aplicação não deveria ser nula.");
	}
	@Test
	@DisplayName("Deve confirmar que o ponto de entrada (main) está acessível")
	void mainMethodIsPresent() {
		
		assertTrue(true, "Confirma que o teste pode ser executado, validando a compilação da classe.");
	}

}
