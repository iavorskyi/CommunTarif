package com.iavorskyi.service;

import com.iavorskyi.domain.ComService;
import com.iavorskyi.domain.Months;
import com.iavorskyi.domain.User;
import com.iavorskyi.repos.ComServiceRepo;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ComServService {

    final ComServiceRepo comServiceRepo;
    final UserService userService;

    public ComServService(ComServiceRepo comServiceRepo, UserService userService) {
        this.comServiceRepo = comServiceRepo;
        this.userService = userService;
    }

    public void calculDelta(ComService comService) {
        comService.setDelta(comService.getLastIndex() - comService.getStartIndex());
    }

    public  void calculCost(ComService comService) {
        if (comService.isCounter()) {
            comService.setCost(comService.getDelta() * comService.getTariff());

        } else comService.setCost(comService.getArea() * comService.getTariff());
    }

    public ComService cloneComService(ComService comService){
        ComService comServiceClone;
        if(comService.isCounter()) {
            comServiceClone = new ComService(comService.getName(), comService.getTariff());
            comServiceClone.setCounter(true);
        }
        else{
            comServiceClone = new ComService(comService.getName(), comService.getTariff(), comService.getArea());
        }
        comServiceClone.setCurUser(comService.getCurUser());
        return comServiceClone;
    }

    public ComService findById(long id){
        return comServiceRepo.findOneById(id);
    }

    public void save(ComService comService){
       comServiceRepo.save(comService);
    }

    public void delete (ComService comService){
        comServiceRepo.delete(comService);
    }
    public List<ComService> findAll(){
        return comServiceRepo.findAll();
    }

    public void deleteServ(long id, User user) {
        ComService comService = user.getComServiceList().stream().filter(cs -> cs.getId() == id).collect(Collectors.toList()).get(0);
        user.getComServiceList().remove(comService);// удаляем из списка сервисов пользователя
        User currentUserDb = (User) userService.loadUserByUsername(user.getUsername());// находим текущего User в DB
        userService.addUser(currentUserDb); // сохраняем текущего пользователя из DB
        delete(comService); // удаляем его из базы данных
    }

    public void addService(User user, String name, String counter, Double tariff, Double area, Integer filteryear,
                           Months filtermonth, boolean notDefaultService) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        if (filteryear == null) {//если год в фильтре на главной странице пустой
            filteryear = calendar.get(Calendar.YEAR);//передает текущий год
        }
        if (filtermonth == null) {//если месяц в фильтре на главной странице пустой
            filtermonth = Months.values()[calendar.get(Calendar.MONTH)];//передает текущий месяц(взят порядковый элемент энама)
        }
        if (counter != null && counter.contentEquals("yes")) {//если выбран счетчик, то поле area - не обязательно
            ComService comService = new ComService(name, tariff);
            if (notDefaultService) {
                comService.setYear(filteryear);
                comService.setMonth(filtermonth);
            }
            comService.setCounter(true);
            comService.setCurUser(user);
            save(comService);
            user.getComServiceList().add(comService);
            User currentUserDB = (User) userService.loadUserByUsername(user.getUsername());
            userService.addUser(currentUserDB);
        } else {
            ComService comService = new ComService(name, tariff, area);
            if (notDefaultService) {
                comService.setYear(filteryear);
            }
            if (notDefaultService) {
                comService.setMonth(filtermonth);
            }
            comService.setCounter(false);
            comService.setCurUser(user);
            calculCost(comService);
            save(comService);
            user.getComServiceList().add(comService);
            User currentUserDB = (User) userService.loadUserByUsername(user.getUsername());
            userService.addUser(currentUserDB);
        }
    }

    public void updateIndexes(Integer startIndex, Integer lastIndex, long id, User user) {
        if ((id != 0)) {
            ComService comService = user.getComServiceList().stream().filter(cs -> cs.getId() == id).collect(Collectors.toList()).get(0);
            comService.setStartIndex(startIndex);
            comService.setLastIndex(lastIndex);
            calculDelta(comService);
            calculCost(comService);
            save(comService);
        }
    }
}
