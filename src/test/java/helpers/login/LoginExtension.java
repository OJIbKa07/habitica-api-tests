package helpers.login;
import api.AccountApiSteps;
import models.LoginResponse;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LoginExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        boolean isWithLogin = context.getTestClass()
                .map(WithLogin.class::isAssignableFrom)
                .orElse(false);

        if (isWithLogin) {
            LoginResponse response = AccountApiSteps.loginWithApi();
            System.out.println("Авторизация прошла успешно. User ID: " + response.getUserID());
        }
    }
}

