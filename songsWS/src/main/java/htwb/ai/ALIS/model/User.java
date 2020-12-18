package htwb.ai.ALIS.model;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
@JacksonXmlRootElement
public class User {

    @Id
    @NotNull
    @Column
    @Length(max = 50)
    private String userId;

    @NotNull
    @Column
    @Length(max = 50)
    private String password;

    @NotNull
    @Column
    @Length(max = 50)
    private String firstName;

    @NotNull
    @Column
    @Length(max = 50)
    private String lastName;

    @Column
    @Length(max = 20)
    private String accessToken;

    public User(@NotNull @Min(3) @Max(50) String userId, @NotNull @Min(3) @Max(50) String password, @NotNull @Min(3) @Max(50) String firstName, @NotNull @Min(3) @Max(50) String lastName) {
        this.userId = userId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
