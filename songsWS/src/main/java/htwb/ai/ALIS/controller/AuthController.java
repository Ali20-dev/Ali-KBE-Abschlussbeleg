package htwb.ai.ALIS.controller;

import htwb.ai.ALIS.model.User;
import htwb.ai.ALIS.repository.UserDAO;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDAO userDAO;

    public AuthController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthController() {

    }


    private String generateToken() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 15;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        System.out.println(generatedString);
        return generatedString;
    }

    @PostMapping(produces = {"text/plain"})
    public ResponseEntity<?> authenticate(@RequestBody User user) {
        try {
            if (user.getUserId().equals("") || user.getPassword().equals(""))
                return ResponseEntity.badRequest().build();
            boolean authenticated = userDAO.authenticateUser(user.getUserId(), user.getPassword());
            if (authenticated) {
                String token = generateToken();
                return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

//
//    @GetMapping(value = "/generateUsers")
//    public ResponseEntity<?> generateUsers() {
//        try {
//            User mmuster = new UserBuilder().setFirstName("Maxime").setLastName("Muster").setUserId("mmuster").setPassword("pass1234").createUser();
//            User eschuler = new UserBuilder().setFirstName("Elena").setLastName("Schuler").setUserId("eschuler").setPassword("pass1234").createUser();
//            userDAO.registerUser(mmuster);
//            userDAO.registerUser(eschuler);
//            return ResponseEntity.ok("Yeah boiiii");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().build();
//        }
//    }
}
