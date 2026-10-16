package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Person;

import java.util.List;

/**
 * Validation rules for customers and sellers, called by the services before any business operation.
 */
public final class PersonValidator {

    private PersonValidator() {
    }

    /**
     * Checks the data of a person before it is registered.
     * The email is only checked when it is given, because sellers do not have one.
     *
     * @param id    unique identifier of the person
     * @param name  full name of the person
     * @param email contact email, or null for people without email
     * @param phone contact phone number
     * @throws InvalidDataException if a required field is empty or the email format is not valid
     */
    public static void validatePersonData(String id, String name, String email, String phone) {
        requireText(id, "identificacion");
        requireText(name, "nombre");
        requireText(phone, "telefono");
        if (email != null && !hasValidEmailFormat(email)) {
            throw new InvalidDataException("correo", "debe tener un formato valido, por ejemplo nombre@dominio.com");
        }
    }

    /**
     * Checks that a person lookup found something.
     *
     * @param person   result of the lookup, may be null
     * @param personId identifier that was searched
     * @throws ResourceNotFoundException if the person is null
     */
    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("una persona", personId);
        }
    }

    /**
     * Checks that no registered person already uses the new identifier.
     *
     * @param existing people already registered (customers or sellers)
     * @param newId    identifier of the person about to be registered
     * @throws BusinessRuleException if the identifier is already in use
     */
    public static void validateUniquePerson(List<? extends Person> existing, String newId) {
        for (Person person : existing) {
            if (person.getId().equals(newId)) {
                throw new BusinessRuleException("Ya existe una persona con el identificador " + newId + ".");
            }
        }
    }

    private static boolean hasValidEmailFormat(String email) {
        int at = email.indexOf('@');
        int lastDot = email.lastIndexOf('.');
        return at > 0 && lastDot > at + 1 && lastDot < email.length() - 1;
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(fieldName, "no puede estar vacio");
        }
    }
}
