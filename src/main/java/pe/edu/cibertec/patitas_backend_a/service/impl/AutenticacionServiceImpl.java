package pe.edu.cibertec.patitas_backend_a.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.patitas_backend_a.dto.LoginResquestDTO;
import pe.edu.cibertec.patitas_backend_a.service.AutenticacionService;
import reactor.core.publisher.Mono;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@Service
 public class AutenticacionServiceImpl implements AutenticacionService {

     @Autowired
     ResourceLoader resourceLoader;


    @Override
    public String[] validarUsuario(LoginResquestDTO loginResquestDTO) throws IOException {
        String[] datosUsuario = null;
        Resource resource = resourceLoader.getResource("classpath:usuarios.txt");

        try(BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))){

            //ocurre algo aqui
            String linea;
            while ((linea = br.readLine()) !=null){
                String[] datos = linea.split(";");
                if(loginResquestDTO.tipoDocumento().equals(datos[0]) &&
                        loginResquestDTO.numeroDocumento().equals(datos[1]) &&
                        loginResquestDTO.password().equals(datos[2])){

                    datosUsuario = new String[2];
                    datosUsuario[0] = datos[3]; //recuperar nombre
                    datosUsuario[1] = datos[4];  //recuperar correo
                    break;
                }
            }

        }catch (IIOException e){

            datosUsuario = null;
            throw new IOException(e);
        }

        return datosUsuario;
    }

    @Override
    public Mono<ResponseEntity<String>> cerrarSeccion(LoginResquestDTO loginResquestDTO) {
        return Mono.just(loginResquestDTO)
                .flatMap(request -> {
                    // Formato de los datos de cierre de sesión
                    String logData = String.format("Tipo Documento: %s, Número Documento: %s, Fecha de Cierre: %s%n",
                            request.tipoDocumento(),
                            request.numeroDocumento(),
                            LocalDateTime.now());

                    // Especificar la ruta del archivo de registro
                    Path filePath = Paths.get("cerrar_sesion.txt");

                    try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
                        writer.write(logData);
                    } catch (IOException e) {
                        return Mono.error(new RuntimeException("Error al registrar el cierre de sesión", e));
                    }

                    return Mono.just(ResponseEntity.ok("Cierre de sesión registrado exitosamente."));
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al registrar el cierre de sesión.")));
    }
    }

