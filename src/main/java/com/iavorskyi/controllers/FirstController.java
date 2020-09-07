package com.iavorskyi.controllers;

import com.iavorskyi.domain.ComService;
import com.iavorskyi.domain.Months;
import com.iavorskyi.domain.User;
import com.iavorskyi.repos.ComServiceRepo;
import com.iavorskyi.repos.UserRepo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FirstController {
    private final ComServiceRepo comServiceRepo;// конструкция вместо @Autowired
    private final UserRepo userRepo;
    public FirstController(ComServiceRepo comServiceRepo, UserRepo userRepo) {
        this.comServiceRepo = comServiceRepo;
        this.userRepo = userRepo;
    }



    @GetMapping("/main")
    public String showList(@RequestParam(required = false) Integer filteryear,
                           @RequestParam(required = false) Months filtermonth,
                           Model model,
                           @AuthenticationPrincipal User user) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        int currentYear = calendar.get(Calendar.YEAR);
        String currentMonth = String.valueOf(Months.values()[calendar.get(Calendar.MONTH)]);
        List<ComService> comServiceList = user.getComServiceList();

        if (filteryear != null && filtermonth == null) {
            comServiceList = comServiceList.stream().filter(comService -> comService.getYear()==filteryear).collect(Collectors.toList());
        } else if (filteryear == null && filtermonth != null ) {
            comServiceList = comServiceList.stream().filter(comService -> comService.getMonth()==filtermonth).collect(Collectors.toList());
        } else if (filteryear != null) {
            comServiceList = comServiceList.stream().filter(comService -> comService.getYear()==filteryear&&comService.getMonth()==filtermonth).collect(Collectors.toList());
        } else {
            comServiceList = comServiceList.stream().filter(comService -> comService.getYear()==currentYear&&comService.getMonth()==Months.valueOf(currentMonth)).collect(Collectors.toList());
        }
        if (comServiceList.size() == 0) { // если результат фильтрации даст пустой список, то выдается набор дефолтных Сервисов и клонирует их
            comServiceList = user.getComServiceList();
            comServiceList = comServiceList.stream().filter(comService -> comService.getYear() == 0).collect(Collectors.toList());
            comServiceList.forEach(comService -> {
                ComService comServiceClone = comService.cloneComService();
                comServiceRepo.save(comServiceClone);
                user.getComServiceList().add(comServiceClone);
                userRepo.save(user);
            });
            comServiceList.forEach(comService -> {
                if (filteryear != null ) comService.setYear(filteryear);
                else comService.setYear(Integer.parseInt(String.valueOf(currentYear)));
                if (filtermonth != null) comService.setMonth(filtermonth);
                else comService.setMonth(Months.valueOf(currentMonth));
                comServiceRepo.save(comService);
            });
        }
        model.addAttribute("comServiceList", comServiceList);
        model.addAttribute("filteryear", filteryear);
        model.addAttribute("filtermonth", filtermonth);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("monthEnum", Months.JANUARY);
        model.addAttribute("user", user);
        return "mainPage";
    }

    @PostMapping("/main")
    public String addIndexes(@RequestParam String startIndex,
                             @RequestParam String lastIndex,
                             @RequestParam Long id) {
        if ((id != 0)) {
            ComService comService = comServiceRepo.findOneById(id);
            comService.setStartIndex(Integer.parseInt(startIndex));
            comService.setLastIndex(Integer.parseInt(lastIndex));
            comService.setDelta(comService.calculDelta());
            comServiceRepo.save(comService);
        }
        return "redirect:/main";
    }

    @PostMapping("/delete")
    public String deleteService(@RequestParam Long id,
                                @AuthenticationPrincipal User user) {
        user.getComServiceList().remove(comServiceRepo.findOneById(id));// удаляем из списка сервисов пользователя
        comServiceRepo.deleteById(id);
        userRepo.save(user);
        return "redirect:/";
    }


    @GetMapping("/add_service")
    public String addService(@RequestParam(required = false) Integer year,
                             @RequestParam(required = false) Months month,
                             @RequestParam(required = false) boolean notDefaultService,
                             Model model,
                             @AuthenticationPrincipal User user) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        boolean isYearNotNull = true;
        boolean isMonthNotNull = true;
          List<ComService> listOfDefaultServices = user.getComServiceList().stream().filter(comService -> comService.getYear()==0).collect(Collectors.toList());
        if (year == null) {//если год в фильтре на главной странице пустой
            isYearNotNull = false;
            year = calendar.get(Calendar.YEAR);//передает текущий год
        }
        if (month == null) {//если месяц в фильтре на главной странице пустой
            isMonthNotNull = false;
            month = (Months.values()[calendar.get(Calendar.MONTH)]);//передает текущий месяц(взят порядковый элемент энама)
        }
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("notDefaultService", notDefaultService);
        model.addAttribute("listOfDefaultServices", listOfDefaultServices);
        model.addAttribute("isYearNotNull", isYearNotNull);
        model.addAttribute("isMonthNotNull", isMonthNotNull);
        model.addAttribute("user", user);

        return "addService";
    }

    @PostMapping("/add_service")
    public String addService(@AuthenticationPrincipal User user,
                             @RequestParam String name,
                             @RequestParam(required = false) String counter,
                             @RequestParam Double tariff,
                             @RequestParam(required = false) Double area,
                             @RequestParam(required = false) Integer filteryear,
                             @RequestParam(required = false) Months filtermonth,
                             @RequestParam(required = false) boolean notDefaultService) { //передает true, если добавляет пустую форму для нового сервиса
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
            comServiceRepo.save(comService);
            user.getComServiceList().add(comService);
            userRepo.save(user);
        } else {
            ComService comService = new ComService(name, tariff, area);
            if (notDefaultService) {
                comService.setYear(filteryear);
            }
            if (notDefaultService) {
                comService.setMonth(filtermonth);
            }
            comService.setCounter(false);
            comServiceRepo.save(comService);
            user.getComServiceList().add(comService);
            userRepo.save(user);
        }

        return "redirect:/add_service";
    }
    @PostMapping("add_service/delete")
    public String deleteDefaultService(@RequestParam Long id,
                                       @AuthenticationPrincipal User user) {
        user.getComServiceList().remove(comServiceRepo.findOneById(id));// удаляем из списка сервисов пользователя
        comServiceRepo.deleteById(id);
        userRepo.save(user);
        return "redirect:/add_service";
    }

    @GetMapping("/")
    public String greating(@AuthenticationPrincipal User user,
                           Model model) {
        model.addAttribute("user", user);
        return "/greating";
    }


}
