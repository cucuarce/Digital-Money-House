package com.digital_money_house.security_service.service;

import java.util.List;

public interface IService<T, E>{

    public void update(E e);

    public T findById(Long id);

    public void deleteById(Long id);

    public List<T> listAll();

}
