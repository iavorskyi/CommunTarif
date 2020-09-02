package com.iavorskyi.controllers;

import com.iavorskyi.domain.ComService;
import com.iavorskyi.domain.Months;
import com.iavorskyi.repos.ComServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FirstController {
    @Autowired
    private ComServiceRepo comServiceRepo;



    @GetMapping ("/")
    public String showList(@RequestParam(required = false) String filteryear,
                           @RequestParam(required = false) String filtermonth, Model model) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            int currentYear = calendar.get(Calendar.YEAR);
            String currentMonth = String.valueOf(Months.values()[calendar.get(Calendar.MONTH)]);
        List<ComService> comServiceList;

        if((filteryear != null && !filteryear.isEmpty()) && (filtermonth==null || filtermonth.isEmpty())){
            comServiceList = comServiceRepo.findAllByYear(Integer.parseInt(filteryear));
        }
        else if((filteryear == null || filteryear.isEmpty()) && (filtermonth!=null && !filtermonth.isEmpty())){
            comServiceList = comServiceRepo.findAllByMonth(Months.valueOf(filtermonth));
        }
        else if((filteryear!=null && !filteryear.isEmpty()) && (filtermonth!=null && !filtermonth.isEmpty())){
            comServiceList = comServiceRepo.findAllByYearAndMonth(Integer.parseInt(filteryear), Months.valueOf(filtermonth));
            if(comServiceList.size()==0){ // если результат фильтрации даст пустой список, то выдается набор дефолтных Сервисов и клонирует их
                System.out.println("enter in Defoult");
                comServiceList = comServiceRepo.findAll();
                comServiceList = comServiceList.stream().filter(comService -> comService.getYear()==0).collect(Collectors.toList());
                comServiceList.forEach(comService -> {
                    comService = comService.cloneComService();
                        });
                comServiceList.forEach(a->{a.setYear(Integer.parseInt(filteryear));
                                            a.setMonth(Months.valueOf(filtermonth));
                                            comServiceRepo.save(a);
                    System.out.println("Saving Services with year is comlited");
                });
            }
        }
        else {
            comServiceList = comServiceRepo.findAllByYearAndMonth(currentYear, Months.valueOf(currentMonth));// если фильтр пустой или не при первом заходе, то берутся значения текущей даты
        }
        model.addAttribute("comServiceList", comServiceList);
        model.addAttribute("filteryear", filteryear);
        model.addAttribute("filtermonth", filtermonth);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("monthEnum", Months.JANUARY);
        return "mainPage";
    }
    @PostMapping("/")
    public String addIndexes(@RequestParam String startIndex,
                             @RequestParam String lastIndex,
                             @RequestParam String year,
                             @RequestParam String month,
                             @RequestParam String name){
        if((year!=null && !year.isEmpty()) && (month!=null && !month.isEmpty())) {
            System.out.println("enter in addIndex");
            System.out.println(year + " " + month + " " + name);
            System.out.println(startIndex + "------" + lastIndex);
            ComService comService = comServiceRepo.findByYearAndMonthAndName(Integer.parseInt(year), Months.valueOf(month), name);
            System.out.println(comService);
            comService.setStartIndex(Integer.parseInt(startIndex));//!!!!!!!!!!!!!!!!!!!
            comService.setLastIndex(Integer.parseInt(lastIndex));
            comService.setDelta(comService.calculDelta());

            comServiceRepo.save(comService);
        }
        return "redirect:/";
    }



    @GetMapping("/add_service")
    public String addService(@RequestParam String year,
                             @RequestParam String month,
                             Model model) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        if(year==null||year.isEmpty()){//если год в фильтре на главной странице пустой
            year = String.valueOf(calendar.get(Calendar.YEAR));//передает текущий год
        }
        if(month==null||month.isEmpty()){//если месяц в фильтре на главной странице пустой
            month = String.valueOf(Months.values()[calendar.get(Calendar.MONTH)]);//передает текущий месяц(взят порядковый элемент энама)
        }
        model.addAttribute("year", Integer.parseInt(year));
        model.addAttribute("month", month);

        return "addService";
    }
    @PostMapping ("/add_service")
    public String addService (@RequestParam String name,
                              @RequestParam(required = false) String counter,
                              @RequestParam String tariff,
                              @RequestParam (required = false) String area){
        if(counter!=null && !counter.isEmpty() && counter.contentEquals("yes")){//если выбран счетчик, то поле area - не обязательно
            ComService comService = new ComService(name, Double.parseDouble(tariff));
            comService.setCounter(true);
            comServiceRepo.save(comService);
        }
        else {
            ComService comService = new ComService(name, Double.parseDouble(tariff), Double.parseDouble(area));
            comService.setCounter(false);
            comServiceRepo.save(comService);
        }
        return "redirect:/";
    }


}