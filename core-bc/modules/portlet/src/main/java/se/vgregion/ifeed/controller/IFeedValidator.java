package se.vgregion.ifeed.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import se.vgregion.ifeed.types.IFeed;

public class IFeedValidator implements Validator {

    public boolean supports(Class<?> klass) {
        return IFeed.class.isAssignableFrom(klass);
    }

    public void validate(Object target, Errors errors) {
        IFeed iFeed = (IFeed) target;

        // TODO add validation rules
    }
}
