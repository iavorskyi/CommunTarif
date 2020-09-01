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
            System.out.println(year);
            System.out.println(month);
            System.out.println(startIndex);
            System.out.println(lastIndex);

            ComService comService = comServiceRepo.findByYearAndMonthAndAndName(Integer.parseInt(year), Months.valueOf(month), name);
            comService.setStartIndex(Integer.parseInt(startIndex));
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
    public String addService (@RequestParam String yearCur,
                              @RequestParam String monthCur,
                              @RequestParam String name,
                              @RequestParam(required = false) String counter,
                              @RequestParam String tariff,
                              @RequestParam (required = false) String area){

        Months currentMonth = Months.valueOf(monthCur);
        System.out.println(counter);
        if(counter!=null && !counter.isEmpty() && counter.contentEquals("yes")){//если выбран счетчик, то поле area - не обязательно
            ComService comService = new ComService(name, Double.parseDouble(tariff), Integer.parseInt(yearCur), currentMonth);
            comService.setCounter(true);
            comServiceRepo.save(comService);

        }
        else {
            ComService comService = new ComService(name, Double.parseDouble(tariff), Double.parseDouble(area), Integer.parseInt(yearCur), currentMonth);
            comService.setCounter(false);
            comServiceRepo.save(comService);
        }
        return "redirect:/";
    }


}
