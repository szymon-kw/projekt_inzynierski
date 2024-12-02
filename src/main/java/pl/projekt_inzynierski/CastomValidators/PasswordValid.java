package pl.projekt_inzynierski.CastomValidators;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;


public class PasswordValid implements ConstraintValidator<Password, String> {



    @Override
    public void initialize(Password arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 30),
                new CharacterRule(PolishCharacterData.UpperCase, 1), //Co najmniej jedna wielka litera
                new CharacterRule(PolishCharacterData.LowerCase, 1), //Co najmniej jedna mała litera
                new CharacterRule(EnglishCharacterData.Digit, 1), //Co najmniej jedna cyfra
                new CharacterRule(EnglishCharacterData.Special, 1), //Co najmniej jeden znak specjalny
                new WhitespaceRule() //Nie może być spacji

        ));

        RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) {
            return true;
        }
        List<String> messages = validator.getMessages(result);

        context.disableDefaultConstraintViolation();
        for (String message : messages) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return false;

    }


}
