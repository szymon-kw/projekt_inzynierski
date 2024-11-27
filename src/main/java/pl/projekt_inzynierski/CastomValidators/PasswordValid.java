package pl.projekt_inzynierski.CastomValidators;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.LengthRule;
import org.passay.PasswordValidator;


public class PasswordValid implements ConstraintValidator<Password, String> {



    @Override
    public void initialize(Password arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

 //       PasswordValidator validator = new PasswordValidator(new LengthRule(8, 30));
//                new LengthRule(8, 30),
//                new UppercaseCharacterRule(1),
//                new LowercaseCharacterRule(1),
//                new DigitCharacterRule(1),
//                new SpecialCharacterRule(1),
//                new WhitespaceRule());
 //       RuleResult result = validator.validate(new PasswordData(password));
        return false;
    }


}
