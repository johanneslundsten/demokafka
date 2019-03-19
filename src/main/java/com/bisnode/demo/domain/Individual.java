package com.bisnode.demo.domain;

import java.util.Random;
import java.util.UUID;

/**
 * Created by johlun
 * on 2019-03-18.
 */
public class Individual {

    private static Random random = new Random();
    private static String[] countries = {"SE", "DK", "NO"};
    private static String[] genders = {"M", "F"};

    private String gedi;
    private int age;
    private String gender;
    private String countryCode;



    public static Individual buildARandom(){
        Individual individual = new Individual();
        individual.setAge(random.nextInt(100));
        individual.setCountryCode(countries[random.nextInt(countries.length)]);
        individual.setGender(genders[random.nextInt(genders.length)]);
        individual.setGedi(UUID.randomUUID().toString());
        return individual;
    }

    public String getGedi() {
        return gedi;
    }

    public Individual setGedi(String gedi) {
        this.gedi = gedi;
        return this;
    }

    public int getAge() {
        return age;
    }

    public Individual setAge(int age) {
        this.age = age;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public Individual setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Individual setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    @Override
    public String toString() {
        return "Individual{" +
                "gedi='" + gedi + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}
