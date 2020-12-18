package htwb.ai.ALIS.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.ALIS.model.User;
import htwb.ai.ALIS.model.UserBuilder;
import htwb.ai.ALIS.repository.UserDAO;
import htwb.ai.ALIS.repository.UserDAOImpl;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AuthControllerTest {
    private MockMvc mockMvc;
    private UserDAO mockedUserDAO;
    private List<User> listOfUsers;
    private User authenticatedUser, nonAuthenticatedUser, userDoesNotExist;

    @BeforeEach
    public void setUp() throws Exception {
        User mmuster = new UserBuilder().setPassword("pass1234").setUserId("mmuster").setFirstName("Maxime").setLastName("Muster").createUser();
        User eschuler = new UserBuilder().setPassword("pass1234").setUserId("eschuler").setFirstName("Elena").setLastName("Schuler").createUser();
        nonAuthenticatedUser = new UserBuilder().setUserId("mamamia").setPassword("marioboi19").createUser();
        authenticatedUser = new UserBuilder().setUserId("mmuster").setPassword("pass1234").createUser();
        userDoesNotExist = new UserBuilder().setUserId("Noooo").setPassword("ooooo").createUser();
        listOfUsers = new ArrayList<>();
        listOfUsers.add(mmuster);
        listOfUsers.add(eschuler);
        mockedUserDAO = Mockito.mock(UserDAOImpl.class);
        Mockito.when(mockedUserDAO.authenticateUser(authenticatedUser.getUserId(), authenticatedUser.getPassword())).thenReturn(true);
        Mockito.when(mockedUserDAO.authenticateUser(nonAuthenticatedUser.getUserId(), nonAuthenticatedUser.getPassword())).thenReturn(false);
        Mockito.when(mockedUserDAO.authenticateUser(userDoesNotExist.getUserId(), userDoesNotExist.getPassword())).thenThrow(NotFoundException.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(mockedUserDAO)).build();
    }

    @Test
    public void testAuthenticationWithValidUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(authenticatedUser);
        MvcResult result = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andReturn();

        //validate random token, 15 characters long
        result.getResponse().setCharacterEncoding("utf-8");
        System.out.println(result.getResponse().getContentAsString());
        assertTrue(result.getResponse().getContentAsString().matches("^[a-zA-Z0-9]{15}$"));
        Mockito.verify(mockedUserDAO, Mockito.times(1)).authenticateUser(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testAuthenticationWithInvalidUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(nonAuthenticatedUser);
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isUnauthorized());

        Mockito.verify(mockedUserDAO, Mockito.times(1)).authenticateUser(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testAuthenticationWithNonExistingUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(userDoesNotExist);
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isUnauthorized());

        Mockito.verify(mockedUserDAO, Mockito.times(1)).authenticateUser(Mockito.anyString(), Mockito.anyString());
    }
}