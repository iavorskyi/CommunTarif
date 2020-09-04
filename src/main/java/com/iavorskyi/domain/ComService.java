package com.iavorskyi.domain;

import javax.persistence.*;

@Entity
public class ComService {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;
    private double tariff;
    private int startIndex;
    private int lastIndex;
    private int delta;
    private double cost;
    private int year;
    private Months month;
    private boolean isCounter;



    private double area;


    public ComService(String name, double tariff, double area) {
        this.name = name;
        this.tariff = tariff;
        this.area = area;
    }
    public ComService(String name, double tariff) {
        this.name = name;
        this.tariff = tariff;

    }


    public ComService() {
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
        return cost;
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

    public double calculCost(){
        if(this.isCounter()){
            return delta*tariff;
        }
        else return area*tariff;
    }
    public int calculDelta(){
        return lastIndex-startIndex;
    }
    public ComService cloneComService(){
        ComService comServiceClone;
        if(this.isCounter()) {
            comServiceClone = new ComService(this.name, this.tariff);
        }
        else{
            comServiceClone = new ComService(this.name, this.tariff, this.area);

        }
        return comServiceClone;
    }

}
