package com.iavorskyi.domain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ParsingObj {
    Document document;
    Elements tableElements;

    public ParsingObj() {
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Elements getTableElements() {
        return tableElements;
    }

    public void setTableElements(Elements tableElements) {
        this.tableElements = tableElements;
    }

    {
        try {
            document = Jsoup.connect("https://index.minfin.com.ua/tariff/electric/")
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
            Elements tableElements = document.getElementsByAttributeValue("class", "grid");


        } catch (IOException e) {
            System.out.println("Connection to http-resource is failed");
        }
    }
}
