package htwb.ai.ALIS.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UserBuilder {
    private @NotNull @Min(3) @Max(50) String userId;
    private @NotNull @Min(3) @Max(50) String password;
    private @NotNull @Min(3) @Max(50) String firstName;
    private @NotNull @Min(3) @Max(50) String lastName;
    private String accessToken;

    public UserBuilder setUserId(@NotNull @Min(3) @Max(50) String userId) {
        this.userId = userId;
        return this;
    }

    public UserBuilder setPassword(@NotNull @Min(3) @Max(50) String password) {
        this.password = password;
        return this;
    }

    public UserBuilder setFirstName(@NotNull @Min(3) @Max(50) String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder setLastName(@NotNull @Min(3) @Max(50) String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder setAccessToken(@NotNull String accessToken){
        this.accessToken = accessToken;
        return this;
    }

    public User createUser() {
        return new User(userId, password, firstName, lastName, accessToken);
    }
}