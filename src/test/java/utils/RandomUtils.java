package utils;

import com.github.javafaker.Faker;

import java.util.Locale;

public class RandomUtils {

    Faker faker = new Faker(new Locale("ru"));

    public String getTitle() {
        return faker.book().title();
    }
}