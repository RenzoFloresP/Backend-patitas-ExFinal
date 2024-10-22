package pe.edu.cibertec.patitas_backend_a.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.cibertec.patitas_backend_a.dto.LoginResponseDTO;
import pe.edu.cibertec.patitas_backend_a.dto.LoginResquestDTO;
import pe.edu.cibertec.patitas_backend_a.service.AutenticacionService;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/autenticacion")

public class AutenticacionController {

    @Autowired
  AutenticacionService autenticacionService;



  @PostMapping("/login")
  public LoginResponseDTO login(@RequestBody LoginResquestDTO loginResquestDTO) {

      try {
         // Thread.sleep(Duration.ofSeconds(60));
          String[] datosUsuario = autenticacionService.validarUsuario(loginResquestDTO);
          System.out.println("Resultado :");
          if (datosUsuario == null) {
              return new LoginResponseDTO("01", "Error: El usuario no ha sido registrado", "", "");
          }

          return  new LoginResponseDTO("00","",datosUsuario[0],datosUsuario[1]);
      } catch (Exception e) {
          return new LoginResponseDTO("99", "Error: Ocurrio un Problema", "", "");
      }


  }
    @PostMapping("/cerrar-async")
    public Mono<ResponseEntity<Map<String, String>>> logout(@RequestBody LoginResquestDTO loginRequestDTO) {
        System.out.println("Datos recibidos: " + loginRequestDTO);
        return autenticacionService.cerrarSeccion(loginRequestDTO)
                .map(response -> {
                    Map<String, String> responseBody = new HashMap<>();
                    responseBody.put("message", "Sesión finalizada.");
                    return ResponseEntity.ok(responseBody); // Devuelve un objeto JSON
                })
                .onErrorResume(e -> {
                    Map<String, String> errorBody = new HashMap<>();
                    errorBody.put("message", "Error al finaliza la sesión");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody)); // Devuelve un error en formato JSON
                });
    }
}
