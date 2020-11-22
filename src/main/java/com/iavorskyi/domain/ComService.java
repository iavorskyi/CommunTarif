package com.iavorskyi.domain;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Entity
public class ComService {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @NotEmpty(message = "Укажите название сервиса")
    @Length(min = 1, max = 25, message = "Название сервиса должно содержать от 1 до 25 символов")
    private String name;
    @NotNull(message = "Тариф должен быть больше нуля")
    private double tariff;
    private int startIndex;
    private int lastIndex;
    private int delta;
    private double cost;
    private int year;
    private Months month;
    private boolean isCounter;
    private double area;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private User curUser;

    public ComService() {
    }
    public ComService(String name, double tariff, double area) {
        this.name = name;
        this.tariff = tariff;
        this.area = area;
    }
    public ComService(String name, double tariff) {
        this.name = name;
        this.tariff = tariff;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTariff() {
        return tariff;
    }

    public void setTariff(double tariff) {
        this.tariff = tariff;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public double getCost() {
        BigDecimal result = new BigDecimal(cost);
        result = result.setScale(2, RoundingMode.UP);
        return result.doubleValue();
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Months getMonth() {
        return month;
    }

    public void setMonth(Months month) {
        this.month = month;
    }
    public boolean isCounter() {
        return isCounter;
    }
    public void setCounter(boolean counter) {
        isCounter = counter;
    }

    public User getCurUser() {
        return curUser;
    }

    public void setCurUser(User curUser) {
        this.curUser = curUser;
    }


}
