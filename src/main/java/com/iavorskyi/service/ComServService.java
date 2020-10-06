package com.iavorskyi.service;

import com.iavorskyi.domain.ComService;
import com.iavorskyi.repos.ComServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ComServService {

    final
    ComServiceRepo comServiceRepo;

    public ComServService(ComServiceRepo comServiceRepo) {
        this.comServiceRepo = comServiceRepo;
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

}
