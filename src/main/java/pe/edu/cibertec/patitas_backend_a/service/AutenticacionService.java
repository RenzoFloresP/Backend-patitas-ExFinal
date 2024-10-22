package pe.edu.cibertec.patitas_backend_a.service;

import org.springframework.http.ResponseEntity;
import pe.edu.cibertec.patitas_backend_a.dto.LoginResquestDTO;
import reactor.core.publisher.Mono;

import javax.imageio.IIOException;
import java.io.IOException;

public interface AutenticacionService {

    String[] validarUsuario(LoginResquestDTO loginResquestDTO) throws IOException;
    Mono<ResponseEntity<String>> cerrarSeccion(LoginResquestDTO loginResquestDTO);
}
