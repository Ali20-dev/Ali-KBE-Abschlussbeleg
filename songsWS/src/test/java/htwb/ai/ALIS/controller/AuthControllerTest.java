package htwb.ai.ALIS.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.ALIS.model.User;
import htwb.ai.ALIS.model.UserBuilder;
import htwb.ai.ALIS.service.UserService;
import htwb.ai.ALIS.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockedUserService;

    private User authenticatedUser, nonAuthenticatedUser, userDoesNotExist;

    @BeforeEach
    public void setUp() throws Exception {
        User mmuster = new UserBuilder().setPassword("pass1234").setUserId("mmuster").setFirstName("Maxime").setLastName("Muster").createUser();
        User eschuler = new UserBuilder().setPassword("pass1234").setUserId("eschuler").setFirstName("Elena").setLastName("Schuler").createUser();
        nonAuthenticatedUser = new UserBuilder().setUserId("mamamia").setPassword("marioboi19").createUser();
        authenticatedUser = new UserBuilder().setUserId("mmuster").setPassword("pass1234").createUser();
        userDoesNotExist = new UserBuilder().setUserId("Noooo").setPassword("ooooo").createUser();
        mockedUserService = Mockito.mock(UserServiceImpl.class);
        Mockito.when(mockedUserService.authenticateUser(authenticatedUser.getUserId(), authenticatedUser.getPassword())).thenReturn(true);
        Mockito.when(mockedUserService.authenticateUser(nonAuthenticatedUser.getUserId(), nonAuthenticatedUser.getPassword())).thenReturn(false);
        Mockito.when(mockedUserService.authenticateUser(userDoesNotExist.getUserId(), userDoesNotExist.getPassword())).thenReturn(false);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(mockedUserService)).build();
    }

    @Test
    public void testAuthenticationWithValidUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(authenticatedUser);
        MvcResult result = mockMvc.perform(post("/rest/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andReturn();

        //validate random token, 15 characters long
        result.getResponse().setCharacterEncoding("utf-8");
        System.out.println(result.getResponse().getContentAsString());
        assertTrue(result.getResponse().getContentAsString().matches("^[a-zA-Z0-9]{15}$"));
        Mockito.verify(mockedUserService, Mockito.times(1)).authenticateUser(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testAuthenticationWithInvalidUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(nonAuthenticatedUser);
        mockMvc.perform(post("/rest/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isUnauthorized());

        Mockito.verify(mockedUserService, Mockito.times(1)).authenticateUser(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testAuthenticationWithNonExistingUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(userDoesNotExist);
        mockMvc.perform(post("/rest/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isUnauthorized());

        Mockito.verify(mockedUserService, Mockito.times(1)).authenticateUser(Mockito.anyString(), Mockito.anyString());
    }
}