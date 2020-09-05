package com.iavorskyi.controllers;

import com.iavorskyi.domain.ComService;
import com.iavorskyi.domain.Months;
import com.iavorskyi.repos.ComServiceRepo;
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

    public FirstController(ComServiceRepo comServiceRepo) {
        this.comServiceRepo = comServiceRepo;
    }


    @GetMapping("/")
    public String showList(@RequestParam(required = false) String filteryear,
                           @RequestParam(required = false) String filtermonth, Model model) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        int currentYear = calendar.get(Calendar.YEAR);
        String currentMonth = String.valueOf(Months.values()[calendar.get(Calendar.MONTH)]);
        List<ComService> comServiceList;

        if ((filteryear != null && !filteryear.isEmpty()) && (filtermonth == null || filtermonth.isEmpty())) {
            comServiceList = comServiceRepo.findAllByYear(Integer.parseInt(filteryear));
        } else if ((filteryear == null || filteryear.isEmpty()) && (filtermonth != null && !filtermonth.isEmpty())) {
            comServiceList = comServiceRepo.findAllByMonth(Months.valueOf(filtermonth));
        } else if (filteryear != null && !filteryear.isEmpty()) {
            comServiceList = comServiceRepo.findAllByYearAndMonth(Integer.parseInt(filteryear), Months.valueOf(filtermonth));
        } else {
            comServiceList = comServiceRepo.findAllByYearAndMonth(currentYear, Months.valueOf(currentMonth));// если фильтр пустой или не при первом заходе, то берутся значения текущей даты
        }
        if (comServiceList.size() == 0) { // если результат фильтрации даст пустой список, то выдается набор дефолтных Сервисов и клонирует их
            comServiceList = comServiceRepo.findAll();
            comServiceList = comServiceList.stream().filter(comService -> comService.getYear() == 0).collect(Collectors.toList());
            comServiceList.forEach(comService -> {
                comServiceRepo.save(comService.cloneComService());
            });
            comServiceList.forEach(a -> {
                if (filteryear != null && !filteryear.isEmpty()) a.setYear(Integer.parseInt(filteryear));
                else a.setYear(Integer.parseInt(String.valueOf(currentYear)));
                if (filtermonth != null && !filtermonth.isEmpty()) a.setMonth(Months.valueOf(filtermonth));
                else a.setMonth(Months.valueOf(currentMonth));
                comServiceRepo.save(a);
            });
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
                             @RequestParam Long id) {
        if ((id != 0)) {
            ComService comService = comServiceRepo.findOneById(id);
            comService.setStartIndex(Integer.parseInt(startIndex));
            comService.setLastIndex(Integer.parseInt(lastIndex));
            comService.setDelta(comService.calculDelta());
            comServiceRepo.save(comService);
        }
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteService(@RequestParam Long id) {
        comServiceRepo.deleteById(id);
        return "redirect:/";
    }


    @GetMapping("/add_service")
    public String addService(@RequestParam(required = false) String year,
                             @RequestParam(required = false) String month,
                             @RequestParam(required = false) boolean notDefaultService,
                             Model model) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        boolean isYearNotNull = true;
        boolean isMonthNotNull = true;
        List<ComService> listOfDefaultServices = comServiceRepo.findAll().stream().filter(comService -> comService.getYear()==0).collect(Collectors.toList());
        if (year == null || year.isEmpty()) {//если год в фильтре на главной странице пустой
            isYearNotNull = false;
            year = String.valueOf(calendar.get(Calendar.YEAR));//передает текущий год
        }
        if (month == null || month.isEmpty()) {//если месяц в фильтре на главной странице пустой
            isMonthNotNull = false;
            month = String.valueOf(Months.values()[calendar.get(Calendar.MONTH)]);//передает текущий месяц(взят порядковый элемент энама)
        }
        model.addAttribute("year", Integer.parseInt(year));
        model.addAttribute("month", month);
        model.addAttribute("notDefaultService", notDefaultService);
        model.addAttribute("listOfDefaultServices", listOfDefaultServices);
        model.addAttribute("isYearNotNull", isYearNotNull);
        model.addAttribute("isMonthNotNull", isMonthNotNull);

        return "addService";
    }

    @PostMapping("/add_service")
    public String addService(@RequestParam String name,
                             @RequestParam(required = false) String counter,
                             @RequestParam String tariff,
                             @RequestParam(required = false) String area,
                             @RequestParam(required = false) String filteryear,
                             @RequestParam(required = false) String filtermonth,
                             @RequestParam(required = false) boolean notDefaultService) { //передает true, если добавляет пустую форму для нового сервиса
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        if (filteryear == null || filteryear.isEmpty()) {//если год в фильтре на главной странице пустой
            filteryear = String.valueOf(calendar.get(Calendar.YEAR));//передает текущий год
        }
        if (filtermonth == null || filtermonth.isEmpty()) {//если месяц в фильтре на главной странице пустой
            filtermonth = String.valueOf(Months.values()[calendar.get(Calendar.MONTH)]);//передает текущий месяц(взят порядковый элемент энама)
        }
        System.out.println("Notdefault" + notDefaultService);

        if (counter != null && !counter.isEmpty() && counter.contentEquals("yes")) {//если выбран счетчик, то поле area - не обязательно
            ComService comService = new ComService(name, Double.parseDouble(tariff));
            if (notDefaultService) {
                comService.setYear(Integer.parseInt(filteryear));
                comService.setMonth(Months.valueOf(filtermonth));
            }
            comService.setCounter(true);
            comServiceRepo.save(comService);
        } else {
            ComService comService = new ComService(name, Double.parseDouble(tariff), Double.parseDouble(area));
            if (notDefaultService) {
                comService.setYear(Integer.parseInt(filteryear));
            }
            if (notDefaultService) {
                comService.setMonth(Months.valueOf(filtermonth));
            }
            comService.setCounter(false);
            comServiceRepo.save(comService);
        }
        return "redirect:/add_service";
    }
    @PostMapping("add_service/delete")
    public String deleteDefaultService(@RequestParam Long id) {
        System.out.println("delete");
        comServiceRepo.deleteById(id);
        return "redirect:/add_service";
    }

    @GetMapping("/greating")
    public String greating() {
        return "/greating";
    }


}
