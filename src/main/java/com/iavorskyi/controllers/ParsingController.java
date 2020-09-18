package com.iavorskyi.controllers;

import com.iavorskyi.domain.ParsingObj;
import com.iavorskyi.domain.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

@Controller
@RequestMapping("/tariff")
public class ParsingController {


    @GetMapping
    public String getTariffTables(Model model,
                                  @AuthenticationPrincipal User user) throws IOException {
        ParsingObj parsingObjElectro = new ParsingObj();
        ParsingObj parsingObjGaz = new ParsingObj();
        ParsingObj parsingObjVoda = new ParsingObj();

        parsingObjElectro.setDocument(Jsoup.connect("https://index.minfin.com.ua/tariff/electric/").get());
        parsingObjElectro.setTableElements(parsingObjElectro.getDocument().getElementsByAttributeValue("class", "grid"));

        parsingObjVoda.setDocument(Jsoup.connect("https://index.minfin.com.ua/tariff/water/").get());
        parsingObjVoda.setTableElements(parsingObjVoda.getDocument().getElementsByAttributeValue("class", "grid"));

        parsingObjGaz.setDocument(Jsoup.connect("https://index.minfin.com.ua/tariff/gas/").get());
        parsingObjGaz.setTableElements(parsingObjGaz.getDocument().getElementsByAttributeValue("class", "grid"));

        model.addAttribute("parsingObjElectro", parsingObjElectro);
        model.addAttribute("parsingObjGaz", parsingObjGaz);
        model.addAttribute("parsingObjVoda", parsingObjVoda);
        model.addAttribute("user", user);

        return "tariff_info";
    }
}
