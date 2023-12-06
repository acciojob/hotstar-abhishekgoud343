package com.driver.services;


import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {
    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto) throws  Exception {
        //Add a webSeries to the database and update the ratings of the productionHouse
        //In case the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Don't forget to save the production and webseries Repo
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();

        if (webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName()) != null)
            throw new Exception("Series is already present");

        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setProductionHouse(productionHouse);

        productionHouse.getWebSeriesList().add(webSeries);

        double ratingPH = 0;
        for (WebSeries series : productionHouse.getWebSeriesList())
            ratingPH += series.getRating();
        ratingPH /= productionHouse.getWebSeriesList().size();

        productionHouse.setRatings(ratingPH);

        productionHouseRepository.save(productionHouse);

        return webSeries.getId();
    }
}