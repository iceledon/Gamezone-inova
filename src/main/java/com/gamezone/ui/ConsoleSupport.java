package com.gamezone.ui;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Scanner;
import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.PersistenceException;
import com.gamezone.exceptions.ResourceNotFoundException;

/** Shared console input and error presentation. Does not close the injected scanner. */
public final class ConsoleSupport {
    private final Scanner scanner;

    public ConsoleSupport(Scanner scanner) {
        this.scanner = scanner;
    }

    public String ask(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int askInt(String prompt) {
        String value = ask(prompt);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("numero entero",
                    "debe contener un numero entero valido");
        }
    }

    public double askDouble(String prompt) {
        String value = ask(prompt);
        try {
            double number = Double.parseDouble(value);
            if (!Double.isFinite(number)) {
                throw new InvalidDataException("numero",
                        "debe contener un numero decimal finito");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new InvalidDataException("numero",
                    "debe contener un numero decimal valido");
        }
    }

    public LocalDate askDate(String prompt) {
        String value = ask(prompt);
        try {
            return LocalDate.parse(value);
        } catch (java.time.format.DateTimeParseException e) {
            throw new InvalidDataException("fecha",
                    "debe contener una fecha valida en formato aaaa-mm-dd");
        }
    }

    public void showError(RuntimeException error) {
        try {
            throw error;

        } catch (ResourceNotFoundException e) {
            System.out.println(
                    "No se encontro el recurso solicitado: " + e.getMessage()
            );

        } catch (BusinessRuleException e) {
            System.out.println(
                    "No se puede completar la operacion: " + e.getMessage()
            );

        } catch (InvalidDataException e) {
            System.out.println(
                    "Los datos ingresados no son validos: " + e.getMessage()
            );

        } catch (PersistenceException e) {
            System.out.println(
                    "Ocurrio un problema al acceder a los datos del sistema. "
                    + "Contacte al administrador."
            );

        } catch (NumberFormatException e) {
            System.out.println(
                    "Los datos ingresados no son validos: "
                    + "se esperaba un valor numerico."
            );

        } catch (java.time.format.DateTimeParseException e) {
            System.out.println(
                    "Los datos ingresados no son validos: "
                    + "se esperaba una fecha en formato aaaa-mm-dd."
            );

        } catch (NoSuchElementException e) {
            System.out.println(
                    "No se encontro el recurso solicitado. "
                    + "Revise el identificador ingresado."
            );

        } catch (IllegalArgumentException e) {
            // Supports existing modules that still use legacy exceptions.
            System.out.println(
                    "No se puede completar la operacion. "
                    + "Revise los datos y las reglas del modulo."
            );

        } catch (RuntimeException e) {
            System.out.println(
                    "Ocurrio un error inesperado. "
                    + "Intente nuevamente o contacte al administrador."
            );
        }
    }

}
